package jp.ac.kcg.projectexercises.twitter.tweet;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import jp.ac.kcg.projectexercises.twitter.client.ClientUser;
import twitter4j.Status;

/**
 */
public final class TweetFactory {
    private Map<Long, Map<Long, TweetImpl>> tweetMap = new HashMap<>();
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

    @NonNull
    private Map<Long, TweetImpl> createOrGetMap(ClientUser clientUser) {
        Map<Long, TweetImpl> map = tweetMap.get(clientUser.getUserId());
        if (map == null) {
            map = new HashMap<>();
            tweetMap.put(clientUser.getUserId(), map);
        }
        return map;
    }

    private void startObserveTweetState(ClientUser clientUser, Map<Long, TweetImpl> observedTweetMap) {
        clientUser.getStream()
                .addOnFavoriteListener((source, target, favoritedTweet) -> {

                });

    }

}
