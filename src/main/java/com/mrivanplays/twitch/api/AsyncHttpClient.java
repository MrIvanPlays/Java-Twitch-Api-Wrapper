package com.mrivanplays.twitch.api;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncHttpClient {

    private OkHttpClient okHttpClient;
    private Headers.Builder headersBuilder;

    public AsyncHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        headersBuilder = new Headers.Builder();
    }

    private void requestCall(Request request, HttpResponseHandler responseHandler) {
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                responseHandler.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseHandler.processResponse(response);
            }
        });
    }

    private Request.Builder createNoPostOrPutRequest(String url, RequestParams requestParams) {
        if (requestParams.size() > 0) {
            url = url + "?" + requestParams.toEncodedString();
        }

        return createRequest(url);
    }

    private Request.Builder createRequest(String url) {
        return new Request.Builder()
                .url(url)
                .headers(headersBuilder.build());
    }

    public String getHeader(String key) {
        return headersBuilder.get(key);
    }

    public void setHeader(String key, String value) {
        headersBuilder.set(key, value);
    }

    public void removeHeader(String key) {
        headersBuilder.removeAll(key);
    }

    public String getUserAgent() {
        return getHeader("User-Agent");
    }

    public void setUserAgent(String userAgent) {
        setHeader("User-Agent", userAgent);
    }

    public void get(String url, RequestParams requestParams, HttpResponseHandler responseHandler) {
        requestCall(createNoPostOrPutRequest(url, requestParams).get().build(), responseHandler);
    }

    public void get(String url, HttpResponseHandler responseHandler) {
        get(url, new RequestParams(), responseHandler);
    }

    public void put(String url, HttpResponseHandler responseHandler) {
        put(url, new RequestParams(), responseHandler);
    }

    public void put(String url, RequestParams requestParams, HttpResponseHandler responseHandler) {
        Request.Builder requestBuilder = createRequest(url);
        if (requestParams.hasFiles()) {
            requestBuilder.put(new FileRequestBody(requestParams));
        } else {
            byte[] content = requestParams.toEncodedString().getBytes();
            requestBuilder.header("Content-Type", "application/x-www-form-urlencoded;charset=" + requestParams.getCharset().name())
                    .header("Content-Length", Long.toString(content.length));
            requestBuilder.put(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), content));
        }
        requestCall(requestBuilder.build(), responseHandler);
    }

    public void post(String url, RequestParams requestParams, HttpResponseHandler responseHandler) {
        Request.Builder requestBuilder = createRequest(url);
        if (requestParams.hasFiles()) {
            requestBuilder.post(new FileRequestBody(requestParams));
        } else {
            byte[] content = requestParams.toEncodedString().getBytes();
            requestBuilder.header("Content-Type", "application/x-www-form-urlencoded;charset=" + requestParams.getCharset().name())
                    .header("Content-Length", Long.toString(content.length));
            requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), content));
        }
        requestCall(requestBuilder.build(), responseHandler);
    }

    public void delete(String url, HttpResponseHandler responseHandler) {
        requestCall(createRequest(url).delete().build(), responseHandler);
    }
}
