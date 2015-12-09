package jp.ac.kcg.projectexercises.twitter.tweet;


import java.util.List;

import jp.ac.kcg.projectexercises.twitter.user.TwitterUser;
import twitter4j.Status;

/**
 * Tweetを表すクラス
 */
public interface Tweet {

    long getId();

    long getInReplyToStatusId();

    String getText();

    String getCreatedAt();

    String getSourceClientName();

    boolean isClientUserTweet();

    int retweetCount();

    int favoriteCount();

    boolean isRt();

    boolean isFavorited();

    boolean isRetweeted();

    boolean isMention();

    List<String> getImageUris();

    List<String> getHashTags();

    List<String> getInvolvedUserNames();

    List<String> getUris();

    List<String> getMentionEntityUserNames();

    Tweet getRetweetedStatus();

    TwitterUser getUser();
}
