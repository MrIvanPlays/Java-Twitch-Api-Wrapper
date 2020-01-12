package com.mrivanplays.twitch.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb3364.twitch.api.resources.AbstractResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a simple way to convert channel name to twitch v5 api id
 */
public class ChannelNameToID {

    private AsyncHttpClient httpClient;

    private Map<String, String> cache;

    public ChannelNameToID(AsyncHttpClient httpClient) {
        this.httpClient = httpClient;
        cache = new ConcurrentHashMap<>();
    }

    public void getId(String channelName, ObjectMapper objectMapper, IdHttpResponseHandler responseHandler) {
        if (cache.containsKey(channelName)) {
            responseHandler.onSuccess(200, new HashMap<>(), cache.get(channelName));
            return;
        }
        RequestParams requestParams = new RequestParams();
        requestParams.put("login", channelName);
        httpClient.get("https://api.twitch.tv/helix/users", requestParams, new AbstractResource.TwitchHttpResponseHandler(responseHandler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    JsonNode node = objectMapper.readTree(content);
                    String id = node.get("data").get(0).get("id").asText();
                    cache.put(channelName, id);
                    responseHandler.onSuccess(statusCode, headers, id);
                } catch (IOException e) {
                    responseHandler.onFailure(e);
                }
            }
        });
    }
}
