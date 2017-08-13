package com.aig.reporting.sync.qc.rest;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import com.aig.reporting.sync.Sync;
import com.aig.reporting.sync.qc.rest.util.ServerException;
import util.data.KeyMap;


public class QCRestSync implements Sync {

    QCRestClient client;
    
    private static final String TEMP_PATH
            = "/com/aig/reporting/sync/qc/rest/entity/";
    private static final String TEMP_NEW_RUN
            = TEMP_PATH.concat("run.post.xml");
    private final Properties vMap;

    /**
     * @throws java.lang.Exception
     * @since 5.x
     * @param ops
     */
    public QCRestSync() throws Exception {
        System.out.println(("Initializing TM integration with QC Rest API"));
        
        String QCUrl,QCUserName,QCPassword,QCDomain,QCProject;
        QCUrl = "http://ctsc00315511501:8080/qcbin";
        QCUserName ="550361";
        QCPassword = "550361";
        QCDomain = "DEFAULT";
        QCProject = "LS_OFF";
        client = new QCRestClient(QCUrl,
                QCUserName,QCPassword,QCDomain,QCProject);
        vMap = new Properties();
        client.login();
        init();
    }

    @Override
    public String getModule() {
        return "QC_REST";
    }

    @Override
    public boolean isConnected() {
        return client.isLoggedIn();
    }

    private void init() throws Exception {

        vMap.put("user.name", client.usr);
        if (client.isLoggedIn()) {
            try {
                String tsPath = "Root\\TestALM";
                String tsName = "Test";
                tsPath = KeyMap.resolveContextVars(tsPath, vMap);
                tsName = KeyMap.resolveContextVars(tsName, vMap);

                String testset_folderId, testsetId;
                testset_folderId = findTestSetFolderIdFromPath(tsPath);
                testsetId = client.getTestSetId(testset_folderId, tsName);
                vMap.put("testset.id", testsetId);
                vMap.put("os.name", System.getProperty("os.name"));
                vMap.put("host.name", InetAddress.getLocalHost().getHostName());
            } catch (Exception ex) {
                System.err.println((ex.getMessage()));
            }
        }
    }

    private String findTestSetFolderIdFromPath(String tsPath) throws Exception {

        String[] folders = tsPath.split("\\\\|/", 0);
        String testset_folderId = "0";
        for (String folder : folders) {
            if ("Root".equals(folder) || folder.isEmpty()) {
                continue;
            }
            testset_folderId = client.getTestSetFolderId(folder, testset_folderId);
        }
        return testset_folderId;
    }

    @Override
    public boolean updateResults(String testCase, String status, List<File> files) {
        try {
            System.out.println("Conneting qc to update results");
            String testFolderId, testId, instanceId, runId;
            String testScenario = "Test";
            testFolderId = client.getTestFolderId(testScenario);
            testId = client.getTestId(testFolderId, testCase);
            instanceId = client.getTestInstanceId(
                    vMap.getProperty("testset.id"), testId);
            if (!StringUtils.isNumeric(instanceId)) {
                System.out.print(
                        String.format("Instance not found for test //%s/%s(id:%s) ",
                                testScenario, testCase, testId));
                return false;
            }
            vMap.put("run.name", String.format("%s@%s_%s", testCase, "08-11-2017", "16:36:42"));
            vMap.put("instance.id", instanceId);
            vMap.put("testcae.id", testId);
            vMap.put("run.status", "Not Completed");
            vMap.put("run.time", "3");
            vMap.put("run.desc", "Test Description");
            vMap.put("run.iteration", "1");
            vMap.put("run.platform", "Windows");
            String  runTemplate = KeyMap.readStream(
                    QCRestSync.class.getResourceAsStream(TEMP_NEW_RUN));
            System.out.print(String.format("Status : %s | ", status));
            runId = client.createNewRun(payload(runTemplate, vMap));
            vMap.put("run.status", status);
            client.updateRun(payload(runTemplate, vMap), runId);
            if (StringUtils.isNumeric(runId)) {
                System.out.print(String.format(" New RunId : %s | ", runId));
                if (files != null && !files.isEmpty()) {
                    System.out.println(String.format(" Attachments : %s ", files.size()));
                    for (File file : files) {
                        try {
                            client.uploadAttachment(runId, file);
                        } catch (ServerException ex) {
                            
                            System.err.println("error uploading {0}, see log {1}");
                        }
                    }
                }
                return true;
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return false;
    }

    @Override
    public String createIssue(JSONObject issue, List<File> attach) {
        return "Not Supported yet!!!!";
    }

    @Override
    public void disConnect() {
        try {
            client.logout();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

   private String payload(String tmpl, Map map) {
        return KeyMap.resolveContextVars(tmpl, map);
    }
}
