package com.mb3364.twitch.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb3364.twitch.api.auth.Scopes;
import com.mb3364.twitch.api.handlers.BlockResponseHandler;
import com.mb3364.twitch.api.handlers.BlocksResponseHandler;
import com.mb3364.twitch.api.handlers.UnblockResponseHandler;
import com.mb3364.twitch.api.handlers.UserFollowResponseHandler;
import com.mb3364.twitch.api.handlers.UserFollowsResponseHandler;
import com.mb3364.twitch.api.handlers.UserResponseHandler;
import com.mb3364.twitch.api.handlers.UserSubscriptionResponseHandler;
import com.mb3364.twitch.api.handlers.UserUnfollowResponseHandler;
import com.mb3364.twitch.api.models.Block;
import com.mb3364.twitch.api.models.Blocks;
import com.mb3364.twitch.api.models.User;
import com.mb3364.twitch.api.models.UserFollow;
import com.mb3364.twitch.api.models.UserFollows;
import com.mb3364.twitch.api.models.UserSubscription;
import com.mrivanplays.twitch.api.AsyncHttpClient;
import com.mrivanplays.twitch.api.ChannelNameToID;
import com.mrivanplays.twitch.api.RequestParams;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The {@link UsersResource} provides the functionality to access the <code>/users</code> endpoints of the Twitch API.
 *
 * @author Matthew Bell
 */
public class UsersResource extends AbstractResource {

    public UsersResource(AsyncHttpClient httpClient, ObjectMapper objectMapper, ChannelNameToID channelNameToID, String baseUrl, int apiVersion) {
        super(httpClient, objectMapper, channelNameToID, baseUrl, apiVersion);
    }

    /**
     * Returns a {@link User} object.
     *
     * @param user    the user to request
     * @param handler the response handler
     */
    public void get(final String user, final UserResponseHandler handler) {
        getId(user, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                String url = String.format("%s/users/%s", getBaseUrl(), content);

                http.get(url, new TwitchHttpResponseHandler(handler, objectMapper) {
                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                        try {
                            User value = objectMapper.readValue(content, User.class);
                            handler.onSuccess(value);
                        } catch (IOException e) {
                            handler.onFailure(e);
                        }
                    }
                });
            }
        });
    }

    /**
     * Returns the authenticated {@link User} object. Authenticated, required scope: {@link Scopes#USER_READ}
     *
     * @param handler the response handler
     */
    public void get(final UserResponseHandler handler) {
        String url = String.format("%s/user", getBaseUrl());

        http.get(url, new TwitchHttpResponseHandler(handler, objectMapper) {
            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                try {
                    User value = objectMapper.readValue(content, User.class);
                    handler.onSuccess(value);
                } catch (IOException e) {
                    handler.onFailure(e);
                }
            }
        });
    }

    /**
     * Returns the channel subscription that the user subscribes to. Authenticated, required scope: {@link
     * Scopes#USER_SUBSCRIPTIONS}
     *
     * @param user    the authenticated user's name
     * @param channel the channel name of the subscription
     * @param handler the response handler
     */
    public void getSubscription(final String user, final String channel, final UserSubscriptionResponseHandler handler) {
        getId(user, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String userId) {
                getId(channel, new TwitchHttpResponseHandler(handler, objectMapper) {

                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String channelId) {
                        String url = String.format("%s/users/%s/subscriptions/%s", getBaseUrl(), userId, channelId);

                        http.get(url, new TwitchHttpResponseHandler(handler, objectMapper) {
                            @Override
                            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                                try {
                                    UserSubscription value = objectMapper.readValue(content, UserSubscription.class);
                                    handler.onSuccess(value);
                                } catch (IOException e) {
                                    handler.onFailure(e);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Returns a {@link UserFollows} object that contains a list of {@link UserFollow} objects representing channels the
     * user is following.
     *
     * @param user    the user's name
     * @param params  the optional request parameters:
     *                <ul>
     *                <li><code>limit</code>:  Maximum number of objects in array. Default is 25. Maximum is 100.</li>
     *                <li><code>offset</code>: Object offset for pagination. Default is 0.</li>
     *                <li><code>direction</code>: Sorting direction. Default is <code>desc</code>.
     *                Valid values are <code>asc</code> and <code>desc</code>.</li>
     *                <li><code>sortby</code>: Sort key. Default is <code>created_at</code>.
     *                Valid values are <code>created_at</code>, <code>last_broadcast</code>, and <code>login</code>.</li>
     *                </ul>
     * @param handler the response handler
     */
    public void getFollows(final String user, final RequestParams params, final UserFollowsResponseHandler handler) {
        getId(user, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                String url = String.format("%s/users/%s/follows/channels", getBaseUrl(), content);

                http.get(url, params, new TwitchHttpResponseHandler(handler, objectMapper) {
                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                        try {
                            UserFollows value = objectMapper.readValue(content, UserFollows.class);
                            handler.onSuccess(value.getTotal(), value.getFollows());
                        } catch (IOException e) {
                            handler.onFailure(e);
                        }
                    }
                });
            }
        });
    }

    /**
     * Returns a {@link UserFollows} object that contains a list of {@link UserFollow} objects representing channels the
     * user is following.
     *
     * @param user    the user's name
     * @param handler the response handler
     */
    public void getFollows(final String user, final UserFollowsResponseHandler handler) {
        getFollows(user, new RequestParams(), handler);
    }

    /**
     * Returns a {@link UserFollow} object representing a channel follow.
     *
     * @param user    the user
     * @param channel the channel
     * @param handler the response handler
     */
    public void getFollow(final String user, final String channel, final UserFollowResponseHandler handler) {
        getId(user, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String userId) {
                getId(channel, new TwitchHttpResponseHandler(handler, objectMapper) {

                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String channelId) {
                        String url = String.format("%s/users/%s/follows/channels/%s", getBaseUrl(), userId, channelId);

                        http.get(url, new TwitchHttpResponseHandler(handler, objectMapper) {
                            @Override
                            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                                try {
                                    UserFollow value = objectMapper.readValue(content, UserFollow.class);
                                    handler.onSuccess(value);
                                } catch (IOException e) {
                                    handler.onFailure(e);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Follow a channel. Must be authenticated as the <code>user</code>. Authenticated, required scope: {@link
     * Scopes#USER_FOLLOWS_EDIT}
     *
     * @param user                the authenticated user
     * @param channel             the channel to follow
     * @param enableNotifications receive email/push notifications when channel goes live. Default is
     *                            <code>false</code>.
     * @param handler             the response handler
     */
    public void follow(final String user, final String channel, final boolean enableNotifications, final UserFollowResponseHandler handler) {
        getId(user, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String userId) {
                getId(channel, new TwitchHttpResponseHandler(handler, objectMapper) {

                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String channelId) {
                        String url = String.format("%s/users/%s/follows/channels/%s", getBaseUrl(), userId, channelId);

                        RequestParams params = new RequestParams();
                        params.put("notifications", Boolean.toString(enableNotifications));

                        http.put(url, params, new TwitchHttpResponseHandler(handler, objectMapper) {
                            @Override
                            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                                try {
                                    UserFollow value = objectMapper.readValue(content, UserFollow.class);
                                    handler.onSuccess(value);
                                } catch (IOException e) {
                                    handler.onFailure(e);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Follow a channel. Must be authenticated as the <code>user</code>. Authenticated, required scope: {@link
     * Scopes#USER_FOLLOWS_EDIT}
     *
     * @param user    the authenticated user
     * @param channel the channel to follow
     * @param handler the response handler
     */
    public void follow(final String user, final String channel, final UserFollowResponseHandler handler) {
        follow(user, channel, false, handler);
    }

    /**
     * Unfollow a channel. Must be authenticated as the <code>user</code>. Authenticated, required scope: {@link
     * Scopes#USER_FOLLOWS_EDIT}
     *
     * @param user    the authenticated user
     * @param channel the channel to unfollow
     * @param handler the response handler
     */
    public void unfollow(final String user, final String channel, final UserUnfollowResponseHandler handler) {
        getId(user, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String userId) {
                getId(channel, new TwitchHttpResponseHandler(handler, objectMapper) {

                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String channelId) {
                        String url = String.format("%s/users/%s/follows/channels/%s", getBaseUrl(), userId, channelId);

                        http.delete(url, new TwitchHttpResponseHandler(handler, objectMapper) {
                            @Override
                            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                                handler.onSuccess();
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Returns a list of {@link Block} objects on <code>User</code>'s block list. List sorted by recency, newest first.
     * Authenticated, required scope: {@link Scopes#USER_BLOCKS_READ}
     *
     * @param user    the authenticated user
     * @param params  the optional request parameters:
     *                <ul>
     *                <li><code>limit</code>:  Maximum number of objects in array. Default is 25. Maximum is 100.</li>
     *                <li><code>offset</code>: Object offset for pagination. Default is 0.</li>
     *                </ul>
     * @param handler the response handler
     */
    public void getBlocks(final String user, final RequestParams params, final BlocksResponseHandler handler) {
        getId(user, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                String url = String.format("%s/users/%s/blocks", getBaseUrl(), content);

                http.get(url, params, new TwitchHttpResponseHandler(handler, objectMapper) {
                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                        try {
                            Blocks value = objectMapper.readValue(content, Blocks.class);
                            handler.onSuccess(value.getBlocks());
                        } catch (IOException e) {
                            handler.onFailure(e);
                        }
                    }
                });
            }
        });
    }

    /**
     * Returns a list of {@link Block} objects on <code>User</code>'s block list. List sorted by recency, newest first.
     * Authenticated, required scope: {@link Scopes#USER_BLOCKS_READ}
     *
     * @param user    the authenticated user
     * @param handler the response handler
     */
    public void getBlocks(final String user, final BlocksResponseHandler handler) {
        getBlocks(user, new RequestParams(), handler);
    }

    /**
     * Blocks a <code>target</code> for the authenticated <code>user</code>. Authenticated, required scope: {@link
     * Scopes#USER_FOLLOWS_EDIT}
     *
     * @param user    the authenticated user
     * @param target  the user to block
     * @param handler the response handler
     */
    public void putBlock(final String user, final String target, final BlockResponseHandler handler) {
        getId(user, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String userId) {
                getId(target, new TwitchHttpResponseHandler(handler, objectMapper) {

                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String channelId) {
                        String url = String.format("%s/users/%s/blocks/%s", getBaseUrl(), userId, channelId);

                        http.put(url, new TwitchHttpResponseHandler(handler, objectMapper) {
                            @Override
                            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                                try {
                                    Block value = objectMapper.readValue(content, Block.class);
                                    handler.onSuccess(value);
                                } catch (IOException e) {
                                    handler.onFailure(e);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Removes the {@link Block} of <code>target</code> for the authenticated <code>user</code>. Authenticated, required
     * scope: {@link Scopes#USER_FOLLOWS_EDIT}
     *
     * @param user    the authenticated user
     * @param target  the user to unblock
     * @param handler the response handler
     */
    public void deleteBlock(final String user, final String target, final UnblockResponseHandler handler) {
        getId(user, new TwitchHttpResponseHandler(handler, objectMapper) {

            @Override
            public void onSuccess(int statusCode, Map<String, List<String>> headers, String userId) {
                getId(target, new TwitchHttpResponseHandler(handler, objectMapper) {

                    @Override
                    public void onSuccess(int statusCode, Map<String, List<String>> headers, String channelId) {
                        String url = String.format("%s/users/%s/blocks/%s", getBaseUrl(), userId, channelId);

                        http.delete(url, new TwitchHttpResponseHandler(handler, objectMapper) {
                            @Override
                            public void onSuccess(int statusCode, Map<String, List<String>> headers, String content) {
                                handler.onSuccess();
                            }
                        });
                    }
                });
            }
        });
    }
}
