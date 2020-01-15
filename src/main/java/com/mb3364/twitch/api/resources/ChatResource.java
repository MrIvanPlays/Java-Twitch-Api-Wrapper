package com.mb3364.twitch.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb3364.twitch.api.handlers.BadgesResponseHandler;
import com.mb3364.twitch.api.handlers.EmoticonsResponseHandler;
import com.mb3364.twitch.api.models.ChannelBadges;
import com.mb3364.twitch.api.models.Emoticons;
import com.mrivanplays.twitch.api.AsyncHttpClient;
import com.mrivanplays.twitch.api.ChannelNameToID;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The {@link ChatResource} provides the functionality to access the <code>/chat</code> endpoints of the Twitch API.
 *
 * @author Matthew Bell
 */
public class ChatResource extends AbstractResource {

    public ChatResource(AsyncHttpClient httpClient, ObjectMapper objectMapper, ChannelNameToID channelNameToID, String baseUrl, int apiVersion) {
        super(httpClient, objectMapper, channelNameToID, baseUrl, apiVersion);
    }

    /**
     * Returns a list of all emoticon objects.
     *
     * @param handler the Response Handler
     */
    public void getEmoticons(final EmoticonsResponseHandler handler) {
        String url = String.format("%s/chat/emoticons", getBaseUrl());

        http.get(url, new TwitchHttpResponseHandler(handler, objectMapper) {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    Emoticons value = objectMapper.readValue(content, Emoticons.class);
                    handler.onSuccess(value.getEmoticons());
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    /**
     * Returns a list of chat badges that can be used in the specified channel's chat.
     *
     * @param channel the name of the channel
     * @param handler the Response Handler
     */
    public void getBadges(final String channel, final BadgesResponseHandler handler) {
        getId(channel, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                String url = String.format("%s/chat/%s/badges", getBaseUrl(), content);

                http.get(url, new TwitchHttpResponseHandler(handler, objectMapper) {
                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                        try {
                            ChannelBadges value = objectMapper.readValue(content, ChannelBadges.class);
                            handler.onSuccess(value);
                        } catch (IOException e) {
                            handler.onFailure(e);
                        }
                    }
                });
            }
        });
    }
}
