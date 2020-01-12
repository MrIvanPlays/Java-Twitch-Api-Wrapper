package com.mrivanplays.twitch.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.Response;

public abstract class HttpResponseHandler {

    public void onSuccess(int statusCode, Map<String, List<String>> headers, Reader content) {
        try (BufferedReader reader = new BufferedReader(content)) {
            onSuccess(statusCode, headers, reader.lines().collect(Collectors.joining()));
        } catch (IOException e) {
            onFailure(e);
        }
    }

    public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {

    }

    public void onFailure(int statusCode, Map<String, List<String>> headers, Reader content) {
        try (BufferedReader reader = new BufferedReader(content)) {
            onFailure(statusCode, headers, reader.lines().collect(Collectors.joining()));
        } catch (IOException e) {
            onFailure(e);
        }
    }

    public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {

    }

    public abstract void onFailure(Throwable throwable);

    protected void processResponse(Response response) {
        // Response
        int responseCode = response.code();
        Map<String, List<String>> responseHeaders = response.headers().toMultimap();

        Reader charStream = response.body().charStream();

        if (responseCode >= 200 && responseCode < 300) {
            onSuccess(responseCode, responseHeaders, charStream);
        } else {
            onFailure(responseCode, responseHeaders, charStream);
        }
    }
}
