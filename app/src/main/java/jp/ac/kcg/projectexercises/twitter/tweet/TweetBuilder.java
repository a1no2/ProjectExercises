package jp.ac.kcg.projectexercises.twitter.tweet;

import android.app.Notification;
import android.app.NotificationManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kcg.projectexercises.R;
import jp.ac.kcg.projectexercises.main.Global;
import jp.ac.kcg.projectexercises.twitter.client.ClientUser;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

/**
 */
public class TweetBuilder {

    private Tweet tweet = new Tweet();

    public TweetBuilder(ClientUser clientUser) {
        if (clientUser == null) throw new NullPointerException("コンストラクターにnullを渡されました");
        tweet.setClientUser(clientUser);
    }

    public TweetBuilder setText(String text) {
        tweet.setText(text);
        return this;
    }

    public TweetBuilder addImages(List<File> images) {
        if (images.size() > 4)
            throw new IllegalArgumentException("セットできるリストの長さは4までです");
        tweet.setImages(images);
        return this;
    }

    public TweetBuilder setReplyDestinationId(long statusId) {
        tweet.setStatusId(statusId);
        return this;
    }

    public Tweet create() {
        return tweet;
    }


    public static final class Tweet {
        private ClientUser clientUser = null;
        private List<File> images = new ArrayList<>();
        private long statusId = -1;
        private String text = "";
        private NotificationManager notificationManager = null;
        private Notification.Builder builder = null;
        private static int ID = 0;
        private int id;

        Tweet() {

        }


        public void setClientUser(ClientUser clientUser) {
            this.clientUser = clientUser;
        }


        public void setImages(List<File> images) {
            this.images.addAll(images);
        }


        public void setStatusId(long statusId) {
            this.statusId = statusId;
        }


        public void setText(String text) {
            this.text = text;
        }

        void tweet() {
            if (clientUser == null) {
                throw new NullPointerException();
            }
            StatusUpdate statusUpdate = new StatusUpdate(text);
            Twitter twitter = clientUser.getTwitter();
            if (statusId != -1) {
                statusUpdate.setInReplyToStatusId(statusId);
            }
            onProgress(5, null, "ツイートの送信...");

            if (!images.isEmpty()) {
                int progress = 80 / images.size();
                String ticker = "画像送信中...";
                onProgress(10, ticker, ticker);
                long[] medias = new long[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    try {
                        UploadedMedia uploadedMedia = clientUser.getTwitter().uploadMedia(images.get(i));
                        medias[i] = uploadedMedia.getMediaId();
                        onProgress(progress + 10, null, ticker);
                        progress += progress;
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
                statusUpdate.setMediaIds(medias);
            }
            try {
                twitter.updateStatus(statusUpdate);
                onProgress(100, "ツイート成功", "ツイート成功");

            } catch (TwitterException e) {
                onProgress(100, "ツイート失敗", "ツイート失敗");
                e.printStackTrace();
            }
        }

        private static int MAX = 100;

        void setNotificationManager(NotificationManager manager) {
            notificationManager = manager;
            builder = new Notification.Builder(Global.Companion.getInstance().getMainActivityContext());
            String ticker = "ツイートキュー";
            builder.setTicker(ticker);
            builder.setContentText(clientUser.getScreenName() + " : " + text);
            builder.setProgress(MAX, 0, false);
            builder.setContentTitle(ticker);
            builder.setSmallIcon(R.drawable.ic_send);

            builder.setWhen(System.currentTimeMillis());
            if (ID > 999) ID = 0;
            id = ++ID;
            onProgress(0, "ツイートの送信...", ticker);
        }

        private void onProgress(int progress, String ticker, String title) {
            if (builder != null) {
                if (progress < 100) {
                    if (ticker != null)
                        builder.setTicker(ticker);
                    builder.setContentTitle(title);
                    builder.setProgress(MAX, progress, false);
                    notificationManager.notify(id, builder.build());
                } else {
                    builder.setTicker(ticker);
                    builder.setContentTitle(title);
                    builder.setProgress(0, 0, false);
                    notificationManager.notify(id, builder.build());
                    notificationManager.cancel(id);
                }
            }
        }

    }
}
