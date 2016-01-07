package jp.ac.kcg.projectexercises.twitter.client;


import android.content.Context;

import jp.ac.kcg.projectexercises.R;
import jp.ac.kcg.projectexercises.twitter.tweet.TweetBuilder;
import lombok.Getter;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * クライアントのユーザーを表すクラス
 */
public final class ClientUser {
    private Twitter twitter;
    private String screenName;
    private Stream stream;
    private long userId;
    private AccessToken accessToken;
    private String name;
    private String profileImageUrl;
    private String biggerProfileImageUrl;
    private String originalProfileImageUrl;

    ClientUser(Context context, AccessToken accessToken) throws TwitterException {
        ConfigurationBuilder builder = new ConfigurationBuilder()
                .setOAuthConsumerKey(context.getString(R.string.api_key))
                .setOAuthConsumerSecret(context.getString(R.string.api_secret))
                .setOAuthAccessToken(accessToken.getToken())
                .setOAuthAccessTokenSecret(accessToken.getTokenSecret());
        twitter = new TwitterFactory(builder.build()).getInstance();
        userId = accessToken.getUserId();
        screenName = twitter.getScreenName();
        User user = twitter.showUser(userId);
        name = user.getName();
        profileImageUrl = user.getProfileImageURL();
        biggerProfileImageUrl = user.getBiggerProfileImageURL();
        originalProfileImageUrl = user.getOriginalProfileImageURL();

        this.accessToken = accessToken;
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.setOAuthConsumer(context.getString(R.string.api_key), context.getString(R.string.api_secret));
        twitterStream.setOAuthAccessToken(accessToken);
        StreamImpl streamImpl = new StreamImpl(this);
        twitterStream.addListener(streamImpl);
        twitterStream.user();
        stream = streamImpl;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUserId() {
        return userId;
    }

    public Stream getStream() {
        return stream;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getBiggerProfileImageUrl() {
        return biggerProfileImageUrl;
    }

    public String getOriginalProfileImageUrl() {
        return originalProfileImageUrl;
    }

    public TweetBuilder getTweetBuilder() {
        return new TweetBuilder(this);
    }

}
