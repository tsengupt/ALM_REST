package com.aig.reporting.sync.qc.rest;

import util.data.mime.MIME;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Objects;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import com.aig.reporting.sync.BasicHttpClient;

public class QCRestHttpClient extends BasicHttpClient {

    
    public static final String LWSSO_COOKIE_KEY = "LWSSO_COOKIE_KEY";
    Object LOGIN_KEY;
    Header[] COOKIES = new Header[]{};

    public QCRestHttpClient(URL urL, String uN, String pW) {
        super(urL, uN, pW);
    }

    @Override
    public void setHeader(HttpPost req) {
        addCookies(req);
        setAccept(req);
    }

    @Override
    public void setHeader(HttpPut req) {
        addCookies(req);
        setAccept(req);
    }

    /**
     * custom header for respective client
     *
     * @param req
     */
    @Override
    public void setHeader(HttpGet req) {
        addCookies(req);
        setAccept(req);
    }

    public void addCookies(HttpUriRequest req) {
        if (Objects.nonNull(COOKIES)) {
            for (Header h : COOKIES) {
                req.addHeader(h);
            }
        }
    }

    public void setAccept(HttpUriRequest req) {
        req.addHeader("Accept", "application/xml");
    }

    @Override
    public void setPostEntity(String xmlstr, HttpPost httppost) throws UnsupportedEncodingException {
        StringEntity input = new StringEntity(xmlstr);
        if (xmlstr != null && !xmlstr.isEmpty()) {
            input.setContentType("application/xml");
        }
        httppost.setEntity(input);
    }

    @Override
    public void setPutEntity(String xmlstr, HttpPut httpput) throws UnsupportedEncodingException {
        StringEntity input = new StringEntity(xmlstr);
        if (xmlstr != null && !xmlstr.isEmpty()) {
            input.setContentType("application/xml");
        }
        httpput.setEntity(input);
    }

    @Override
    public void setPostEntity(File file, HttpPost httppost) {
        httppost.setHeader("Content-Type", "application/octet-stream");
        httppost.setHeader("Slug", file.getName());
        HttpEntity e = new FileEntity(file, ContentType.create(MIME.getType(file)));
        httppost.setEntity(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject parseResponse(HttpResponse response) throws Exception {
        JSONObject jobj = new JSONObject();
        HttpEntity entity = response.getEntity();
        String resp;
        try {
            if (entity != null) {
                resp = EntityUtils.toString(entity);
                jobj.put("res", resp);
                jobj.put("status", response.getStatusLine().getStatusCode());
                if (LOGIN_KEY == null) {
                    setLoginCookie(jobj, response);
                }
                EntityUtils.consume(entity);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return jobj;
    }

    /**
     * set login/session cookie from a request
     *
     * @param jobj
     * @param response
     */
    @SuppressWarnings("unchecked")
    private void setLoginCookie(JSONObject jobj, HttpResponse response) {
        jobj.put("COOKIE", getCookies(response));
    }

    public Header[] getCookies(HttpResponse response) {
        return response.getHeaders("Set-Cookie");
    }

}
