package jp.ac.kcg.projectexercises.twitter.user;

import java.util.HashMap;
import java.util.Map;

import twitter4j.User;

/**
 */
public final class TwitterUserFactory {
    private Map<Long, TwitterUserImpl> userMap = new HashMap<>();
    private static TwitterUserFactory instance = new TwitterUserFactory();

    private TwitterUserFactory() {

    }

    public static TwitterUserFactory getInstance() {
        return instance;
    }

    public TwitterUser createOrGetUser(User user) {
        long id = user.getId();
        TwitterUserImpl twitterUser = userMap.get(id);
        if (twitterUser != null) {
            twitterUser.change(user);
            return twitterUser;
        }
        twitterUser = new TwitterUserImpl(user);
        userMap.put(id, twitterUser);
        return twitterUser;
    }
}
