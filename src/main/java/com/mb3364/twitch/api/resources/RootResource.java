package com.mb3364.twitch.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb3364.twitch.api.handlers.TokenResponseHandler;
import com.mb3364.twitch.api.models.Root;
import com.mrivanplays.twitch.api.AsyncHttpClient;
import com.mrivanplays.twitch.api.ChannelNameToID;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The {@link RootResource} provides the functionality to access the root <code>/</code> endpoints of the Twitch API.
 *
 * @author Matthew Bell
 */
public class RootResource extends AbstractResource {

    public RootResource(AsyncHttpClient httpClient, ObjectMapper objectMapper, ChannelNameToID channelNameToID, String baseUrl, int apiVersion) {
        super(httpClient, objectMapper, channelNameToID, baseUrl, apiVersion);
    }

    /**
     * Authentication status. If you are authenticated, the response includes the status of your token and links to
     * other related resources.
     *
     * @param handler the response handler
     */
    public void get(final TokenResponseHandler handler) {
        String url = String.format("%s/", getBaseUrl());

        http.get(url, new TwitchHttpResponseHandler(handler, objectMapper) {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    Root value = objectMapper.readValue(content, Root.class);
                    handler.onSuccess(value.getToken());
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }
}
