package jp.ac.kcg.projectexercises.twitter.tweet;

import android.text.Html;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kcg.projectexercises.twitter.client.ClientUser;
import jp.ac.kcg.projectexercises.twitter.user.TwitterUser;
import jp.ac.kcg.projectexercises.twitter.user.TwitterUserFactory;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

/**
 */
final class TweetImpl implements Tweet {
    protected ClientUser clientUser;

    private final long id;
    private final long inReplyToStatusId;

    private final String text;
    private final String createdAt;
    private final String sourceClientName;

    private final boolean isClientUserTweet;
    private final int retweetCount;
    private final int favoriteCount;

    private boolean isRetweeted;
    private boolean isFavorited;
    private final boolean isRt;
    private boolean isMention = false;

    private List<String> imageUris = new ArrayList<>();
    private List<String> hashTags = new ArrayList<>();
    private List<String> involvedUserNames = new ArrayList<>();
    private List<String> uris = new ArrayList<>();
    private List<String> mentionEntityUserNames = new ArrayList<>();

    private Tweet retweetedStatus = null;

    private TwitterUser user;

    TweetImpl(ClientUser clientUser, Status status) {
        id = status.getId();
        inReplyToStatusId = status.getInReplyToStatusId();
        text = status.getText();
        createdAt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(status.getCreatedAt());
        sourceClientName = Html.fromHtml(status.getSource()).toString();
        isClientUserTweet = status.getUser().getId() == clientUser.getUserId();

        retweetCount = status.getRetweetCount();
        favoriteCount = status.getFavoriteCount();

        isRt = status.isRetweet();
        isRetweeted = status.isRetweeted();
        isFavorited = status.isFavorited();

        this.clientUser = clientUser;

        user = TwitterUserFactory.getInstance().createOrGetUser(status.getUser());

        retweetedStatus = status.isRetweet() && status.getRetweetedStatus() != null ?
                new TweetImpl(clientUser, status.getRetweetedStatus()) :
                null;

        setImageUris(this, status);
        setUris(this, status);
        setHashtags(this, status);
        setUserEntities(this, status);
    }

    private static void setImageUris(TweetImpl tweet, Status status) {
        for (MediaEntity mediaEntity : status.getMediaEntities()) {
            tweet.imageUris.add(mediaEntity.getMediaURL());
        }
    }

    private static void setHashtags(TweetImpl tweet, Status status) {
        for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
            tweet.hashTags.add(hashtagEntity.getText());
        }
    }

    private static void setUserEntities(TweetImpl tweet, Status status) {
        tweet.involvedUserNames.add(tweet.user.getName());

        for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
            if ((tweet.clientUser.getUserId() != userMentionEntity.getId())) {
                tweet.mentionEntityUserNames.add(userMentionEntity.getName());
                tweet.involvedUserNames.add(userMentionEntity.getName());
            } else {
                tweet.isMention = true;
            }
        }
    }

    private static void setUris(TweetImpl tweet, Status status) {
        for (URLEntity urlEntity : status.getURLEntities()) {
            tweet.uris.add(urlEntity.getExpandedURL());
        }
    }


    @Override
    public long getId() {
        return id;
    }

    @Override
    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public List<String> getImageUris() {
        return new ArrayList<>(imageUris);
    }

    @Override
    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getSourceClientName() {
        return sourceClientName;
    }

    @Override
    public boolean isClientUserTweet() {
        return isClientUserTweet;
    }

    @Override
    public int retweetCount() {
        return retweetCount;
    }

    @Override
    public int favoriteCount() {
        return favoriteCount;
    }

    @Override
    public boolean isRt() {
        return isRt;
    }

    @Override
    public boolean isFavorited() {
        return isFavorited;
    }

    @Override
    public boolean isRetweeted() {
        return isRetweeted;
    }

    @Override
    public boolean isMention() {
        return isMention;
    }

    @Override
    public List<String> getHashTags() {
        return new ArrayList<>(hashTags);
    }

    @Override
    public List<String> getInvolvedUserNames() {
        return new ArrayList<>(involvedUserNames);
    }

    @Override
    public List<String> getUris() {
        return new ArrayList<>(uris);
    }

    @Override
    public List<String> getMentionEntityUserNames() {
        return new ArrayList<>(mentionEntityUserNames);
    }

    @Override
    public Tweet getRetweetedStatus() {
        return retweetedStatus;
    }

    @Override
    public TwitterUser getUser() {
        return user;
    }
}
