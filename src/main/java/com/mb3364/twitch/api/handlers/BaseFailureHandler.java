package com.mb3364.twitch.api.handlers;

public interface BaseFailureHandler {

    void onFailure(int statusCode, String statusMessage, String errorMessage);

    void onFailure(Throwable throwable);
}
