package com.mrivanplays.twitch.api;

import com.mb3364.twitch.api.models.Error;

public class ChannelData {

    private String channelId;
    private int statusCode;
    private Error error;
    private Throwable exception;

    public ChannelData(int statusCode, String channelId) {
        this(channelId, statusCode, null, null);
    }

    public ChannelData(int statusCode, Error error) {
        this(null, statusCode, error, null);
    }

    public ChannelData(int statusCode, Throwable exception) {
        this(null, statusCode, null, exception);
    }

    private ChannelData(String channelId, int statusCode, Error error, Throwable exception) {
        this.statusCode = statusCode;
        this.channelId = channelId;
        this.error = error;
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return error == null && exception == null;
    }

    public String getChannelId() {
        return channelId;
    }

    public boolean isHttpError() {
        return error != null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Error getHttpError() {
        return error;
    }

    public Throwable getException() {
        return exception;
    }
}
