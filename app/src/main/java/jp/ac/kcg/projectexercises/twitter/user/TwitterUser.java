package jp.ac.kcg.projectexercises.twitter.user;

/**
 * Twitterのユーザーを表すクラス
 */
public interface TwitterUser {

    long getId();

    String getName();

    String getScreenName();

    String getDescription();

    String getLocationStr();

    String getProfileImageUrl();

    String getBiggerProfileImageUrl();

    String getOriginalProfileImageUrl();

    boolean isProtected();
}
