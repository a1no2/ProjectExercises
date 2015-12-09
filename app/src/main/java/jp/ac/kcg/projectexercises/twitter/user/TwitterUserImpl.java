package jp.ac.kcg.projectexercises.twitter.user;

import twitter4j.User;

/**
 * TwitterUserImpl
 */
final class TwitterUserImpl implements TwitterUser {

    private long id;
    private String name;
    private String screenName;
    private String description;
    private String locationStr;
    private String profileImageUrl;
    private String biggerProfileImageUrl;
    private String originalProfileImageUrl;
    private boolean isProtected;

    TwitterUserImpl(User user) {
        setField(this, user);
    }

    void change(User user) {
        if (user.getId() == id) {
            setField(this, user);
        }
    }

    private static void setField(TwitterUserImpl twitterUser, User user) {
        twitterUser.id = user.getId();
        twitterUser.name = user.getName();
        twitterUser.screenName = user.getScreenName();
        twitterUser.description = user.getDescription();
        twitterUser.locationStr = user.getLocation();
        twitterUser.profileImageUrl = user.getProfileImageURL();
        twitterUser.biggerProfileImageUrl = user.getBiggerProfileImageURL();
        twitterUser.originalProfileImageUrl = user.getOriginalProfileImageURL();
        twitterUser.isProtected = user.isProtected();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getScreenName() {
        return screenName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getLocationStr() {
        return locationStr;
    }

    @Override
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    @Override
    public String getBiggerProfileImageUrl() {
        return biggerProfileImageUrl;
    }

    @Override
    public String getOriginalProfileImageUrl() {
        return originalProfileImageUrl;
    }

    @Override
    public boolean isProtected() {
        return isProtected;
    }
}
