package jp.ac.kcg.projectexercises.twitter.client.event;

import jp.ac.kcg.projectexercises.twitter.tweet.Tweet;
import twitter4j.Status;
import twitter4j.User;

/**
 * OnUnFavoriteListener
 */
public interface OnUnFavoriteListener {
    /**
     * @param source           お気に入り解除を実行したユーザー
     * @param target           お気に入り解除を実行されたユーザー
     * @param unfavoritedTweet お気に入り解除を実行されたツイート
     */
    void onUnFavorite(User source, User target, Tweet unfavoritedTweet);
}
