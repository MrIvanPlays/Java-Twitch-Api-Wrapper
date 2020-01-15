package com.mrivanplays.twitch.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb3364.twitch.api.models.Error;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

    public CompletableFuture<ChannelData> getId(String channelName, ObjectMapper objectMapper) {
        CompletableFuture<ChannelData> future = new CompletableFuture<>();
        if (cache.containsKey(channelName)) {
            future.complete(new ChannelData(200, cache.get(channelName)));
            return future;
        }
        RequestParams requestParams = new RequestParams();
        requestParams.put("login", channelName);
        httpClient.get("https://api.twitch.tv/helix/users", requestParams, new StringHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    JsonNode node = objectMapper.readTree(content);
                    String id = node.get("data").get(0).get("id").asText();
                    cache.put(channelName, id);
                    future.complete(new ChannelData(statusCode, id));
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    if (content.length() > 0) {
                        Error error = objectMapper.readValue(content, Error.class);
                        future.complete(new ChannelData(statusCode, error));
                    } else {
                        future.complete(new ChannelData(statusCode, new Error()));
                    }
                } catch (IOException e) {
                    future.complete(new ChannelData(statusCode, e));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }
}
