package com.mb3364.twitch.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb3364.twitch.api.handlers.IngestsResponseHandler;
import com.mb3364.twitch.api.models.Ingests;
import com.mrivanplays.twitch.api.AsyncHttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The {@link IngestsResource} provides the functionality
 * to access the <code>/ingests</code> endpoints of the Twitch API.
 *
 * @author Matthew Bell
 */
public class IngestsResource extends AbstractResource {

    public IngestsResource(AsyncHttpClient httpClient, ObjectMapper objectMapper, String baseUrl, int apiVersion) {
        super(httpClient, objectMapper, baseUrl, apiVersion);
    }

    /**
     * Returns a list of ingest objects.
     *
     * @param handler the response handler
     */
    public void get(final IngestsResponseHandler handler) {
        String url = String.format("%s/ingests", getBaseUrl());

        http.get(url, new TwitchHttpResponseHandler(handler, objectMapper) {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    Ingests value = objectMapper.readValue(content, Ingests.class);
                    handler.onSuccess(value.getIngests());
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }
}
