package com.mrivanplays.twitch.api;

import java.util.List;
import java.util.Map;

public abstract class StringHttpResponseHandler extends HttpResponseHandler {

    @Override
    public abstract void onSuccess(int statusCode, Map<String, List<String>> headers, String content);

    @Override
    public abstract void onFailure(int statusCode, Map<String, List<String>> headers, String content);

    @Override
    public abstract void onFailure(Throwable throwable);
}
