package com.aig.reporting.sync;

import java.io.File;
import java.util.List;

import org.json.simple.JSONObject;

public interface Sync {

    public String getModule();

    public boolean isConnected();

    public boolean updateResults(String TestCase, String status,
            List<File> attach);

    public String createIssue(JSONObject issue, List<File> attach);

    public void disConnect();
}
