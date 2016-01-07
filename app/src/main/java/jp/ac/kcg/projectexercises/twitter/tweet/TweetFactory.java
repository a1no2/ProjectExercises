package jp.ac.kcg.projectexercises.twitter.tweet;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.kcg.projectexercises.twitter.client.ClientUser;
import twitter4j.Status;

/**
 */
public final class TweetFactory {
    private Map<Long, Map<Long, TweetImpl>> tweetMap = new HashMap<>();
    private List<OnStateChangeListener> onStateChangeListeners = new ArrayList<>();
    public static TweetFactory instance = new TweetFactory();

    private TweetFactory() {
    }

    public static TweetFactory getInstance() {
        return instance;
    }

    public Tweet createOrGetTweet(ClientUser clientUser, Status status) {
        Map<Long, TweetImpl> map = createOrGetMap(clientUser);
        TweetImpl tweet = map.get(status.getId());
        if (tweet != null)
            return tweet;
        tweet = new TweetImpl(clientUser, status);
        map.put(status.getId(), tweet);
        return tweet;
    }

    public void addOnStateChangeListener(OnStateChangeListener listener) {
        onStateChangeListeners.add(listener);
    }

    public void removeOnStateChangeListener(OnStateChangeListener listener) {
        onStateChangeListeners.remove(listener);
    }

    @NonNull
    private Map<Long, TweetImpl> createOrGetMap(ClientUser clientUser) {
        Map<Long, TweetImpl> map = tweetMap.get(clientUser.getUserId());
        if (map == null) {
            map = new HashMap<>();
            tweetMap.put(clientUser.getUserId(), map);
            startObserveTweetState(clientUser, map);
        }
        return map;
    }

    private void startObserveTweetState(ClientUser clientUser, Map<Long, TweetImpl> observedTweetMap) {
        clientUser.getStream()
                .addOnFavoriteListener((source, target, favoritedTweet) -> {
                    if (clientUser.getUserId() == source.getId()) {
                        TweetImpl tweet = observedTweetMap.get(favoritedTweet.getId());
                        if (tweet != null) {
                            tweet.setFavorited(true);
                            onStateChange();
                        } else {
                            for (TweetImpl tweet1 : observedTweetMap.values()) {
                                TweetImpl retweet = (TweetImpl) tweet1.getRetweetedStatus();
                                if (tweet1.getRetweetedStatus().getId() == favoritedTweet.getId()) {
                                    retweet.setFavorited(true);
                                }
                            }
                        }
                    }
                })
                .addOnUnFavoriteListener((source, target, unfavoritedTweet) -> {
                    if (clientUser.getUserId() == source.getId()) {
                        TweetImpl tweet = observedTweetMap.get(unfavoritedTweet.getId());
                        if (tweet != null) {
                            tweet.setFavorited(false);
                            onStateChange();
                        } else {
                            for (TweetImpl tweet1 : observedTweetMap.values()) {
                                TweetImpl retweet = (TweetImpl) tweet1.getRetweetedStatus();
                                if (tweet1.getRetweetedStatus().getId() == unfavoritedTweet.getId()) {
                                    retweet.setFavorited(false);
                                }
                            }
                        }
                    }
                });
    }

    private void onStateChange() {
        for (OnStateChangeListener onStateChangeListener : onStateChangeListeners) {
            onStateChangeListener.onStateChange();
        }
    }

    public interface OnStateChangeListener {
        void onStateChange();
    }

}
