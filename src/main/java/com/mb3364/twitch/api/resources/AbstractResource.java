package com.mb3364.twitch.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mb3364.twitch.api.handlers.BaseFailureHandler;
import com.mb3364.twitch.api.models.Error;
import com.mrivanplays.twitch.api.AsyncHttpClient;
import com.mrivanplays.twitch.api.ChannelNameToID;
import com.mrivanplays.twitch.api.IdHttpResponseHandler;
import com.mrivanplays.twitch.api.StringHttpResponseHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * AbstractResource is the abstract base class of a Twitch resource. A resource provides the functionality to access the
 * REST endpoints of the Twitch API.
 *
 * @author Matthew Bell
 */
public abstract class AbstractResource {

    protected final ObjectMapper objectMapper; // can reuse
    protected final AsyncHttpClient http; // can reuse
    private final String baseUrl; // Base url for twitch rest api
    private final ChannelNameToID channelNameToID;

    /**
     * Construct a resource using the Twitch API base URL and specified API version.
     *
     * @param httpClient   http client
     * @param objectMapper object mapper
     * @param baseUrl      the base URL of the Twitch API
     * @param apiVersion   the requested version of the Twitch API
     */
    protected AbstractResource(AsyncHttpClient httpClient, ObjectMapper objectMapper, ChannelNameToID channelNameToID, String baseUrl, int apiVersion) {
        this.http = httpClient;
        this.objectMapper = objectMapper;
        this.channelNameToID = channelNameToID;
        this.baseUrl = baseUrl;
        http.setHeader("Accept", "application/vnd.twitchtv.v" + apiVersion + "+json"); // Specify API version
        configureObjectMapper();
    }

    /**
     * Configure the Jackson JSON {@link ObjectMapper} to properly parse the API responses.
     */
    private void configureObjectMapper() {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    /**
     * Sets the authentication access token to be included in the HTTP headers of each API request.
     *
     * @param accessToken the user's authentication access token
     */
    public void setAuthAccessToken(String accessToken) {
        if (accessToken != null && accessToken.length() > 0) {
            http.setHeader("Authorization", String.format("OAuth %s", accessToken));
        } else {
            http.removeHeader("Authorization");
        }
    }

    /**
     * Sets the application's client ID to be included in the HTTP headers of each API request.
     *
     * @param clientId the application's client ID
     */
    public void setClientId(String clientId) {
        if (clientId != null && clientId.length() > 0) {
            http.setHeader("Client-ID", clientId);
        } else {
            http.removeHeader("Client-ID");
        }
    }

    /**
     * Get the base URL to the Twitch API. Intended to be called by subclasses when generating their resource URL
     * endpoint.
     *
     * @return the base URL to the Twitch API
     */
    protected String getBaseUrl() {
        return baseUrl;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public AsyncHttpClient getHttpClient() {
        return http;
    }

    public void getId(String username, IdHttpResponseHandler responseHandler) {
        channelNameToID.getId(username, objectMapper, responseHandler);
    }

    /**
     * Handles HTTP response's from the Twitch API.
     * <p>Since all Http failure logic is the same, we handle it all in one place: here.</p>
     */
    public static abstract class TwitchHttpResponseHandler extends StringHttpResponseHandler {

        private BaseFailureHandler apiHandler;
        private ObjectMapper objectMapper;

        public TwitchHttpResponseHandler(BaseFailureHandler apiHandler, ObjectMapper objectMapper) {
            this.apiHandler = apiHandler;
            this.objectMapper = objectMapper;
        }

        @Override
        public abstract void onSuccess(int statusCode, Map<String, List<String>> headers, String content);

        @Override
        public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
            try {
                if (content.length() > 0) {
                    Error error = objectMapper.readValue(content, Error.class);
                    apiHandler.onFailure(statusCode, error.getStatusText(), error.getMessage());
                } else {
                    apiHandler.onFailure(statusCode, "", "");
                }
            } catch (IOException e) {
                apiHandler.onFailure(e);
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            apiHandler.onFailure(throwable);
        }
    }
}
