package com.mb3364.twitch.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb3364.twitch.api.handlers.TopGamesResponseHandler;
import com.mb3364.twitch.api.models.Games;
import com.mrivanplays.twitch.api.AsyncHttpClient;
import com.mrivanplays.twitch.api.RequestParams;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The {@link GamesResource} provides the functionality
 * to access the <code>/games</code> endpoints of the Twitch API.
 *
 * @author Matthew Bell
 */
public class GamesResource extends AbstractResource {

    public GamesResource(AsyncHttpClient httpClient, ObjectMapper objectMapper, String baseUrl, int apiVersion) {
        super(httpClient, objectMapper, baseUrl, apiVersion);
    }

    /**
     * Returns a list of games objects sorted by number of current viewers on Twitch, most popular first.
     *
     * @param params  the optional request parameters:
     *                <ul>
     *                <li><code>limit</code>:  the maximum number of objects in array. Maximum is 100.</li>
     *                <li><code>offset</code>: the object offset for pagination. Default is 0.</li>
     *                </ul>
     * @param handler the response handler
     */
    public void getTop(final RequestParams params, final TopGamesResponseHandler handler) {
        String url = String.format("%s/games/top", getBaseUrl());

        http.get(url, params, new TwitchHttpResponseHandler(handler, objectMapper) {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    Games value = objectMapper.readValue(content, Games.class);
                    handler.onSuccess(value.getTotal(), value.getTop());
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    /**
     * Returns a list of games objects sorted by number of current viewers on Twitch, most popular first.
     *
     * @param handler the response handler
     */
    public void getTop(TopGamesResponseHandler handler) {
        getTop(new RequestParams(), handler);
    }
}
