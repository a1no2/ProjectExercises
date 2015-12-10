package jp.ac.kcg.projectexercises.twitter.client.event;

import jp.ac.kcg.projectexercises.twitter.tweet.Tweet;

/**
 * OnStatusListener
 */
public interface OnStatusListener {
    /**
     * @param tweet ツイート
     */
    void onStatus(Tweet tweet);
}
