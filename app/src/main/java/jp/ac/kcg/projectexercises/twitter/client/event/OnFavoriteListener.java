package jp.ac.kcg.projectexercises.twitter.client.event;

import jp.ac.kcg.projectexercises.twitter.tweet.Tweet;
import twitter4j.Status;
import twitter4j.User;

/**
 * OnFavoriteListener
 */
public interface OnFavoriteListener {
    /**
     * @param source          お気に入りを実行したユーザー
     * @param target          お気に入りを実行されたユーザー
     * @param favoritedTweet お気に入りされたツイート
     */
    void onFavorite(User source, User target, Tweet favoritedTweet);
}
