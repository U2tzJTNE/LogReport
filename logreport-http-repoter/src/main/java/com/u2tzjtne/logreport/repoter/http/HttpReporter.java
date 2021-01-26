package com.u2tzjtne.logreport.repoter.http;


import android.content.Context;

import com.u2tzjtne.logrepoter.core.upload.BaseUpload;

import java.io.File;


/**
 * HTTP的post请求方式发送
 *
 * @author u2tzjtne
 */
public class HttpReporter extends BaseUpload {

    private String url;
    private String fileParam;


    public HttpReporter(Context context) {
        super(context);
    }

    @Override
    protected void sendReport(String title, String body, File file, OnUploadFinishedListener onUploadFinishedListener) {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
