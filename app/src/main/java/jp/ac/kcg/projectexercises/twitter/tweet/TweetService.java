package jp.ac.kcg.projectexercises.twitter.tweet;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import jp.ac.kcg.projectexercises.activites.ApplicationActivity;

/**
 * TweetService
 */
public class TweetService extends IntentService {
    private static final String NAME = "Tweet";
    private static TweetBuilder.Tweet tweet;

    /**
     * コンストラクタ
     */
    public TweetService() {
        super(NAME);
    }


    /**
     * ツイートを送信する。
     * このメソッドを使用すると自動的にツイートはキューティングされます。
     *
     * @param context アクティビティのContext
     * @param tweet   　Tweet
     */
    public static void sendTweet(Context context, TweetBuilder.Tweet tweet) {
        if (!(context instanceof ApplicationActivity)) return;
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(NOTIFICATION_SERVICE);

        tweet.setNotificationManager(notificationManager);
        TweetService.tweet = tweet;
        Intent intent = new Intent(context, TweetService.class);
        context.startService(intent);
    }


    protected void onHandleIntent(Intent intent) {
        Log.v(toString(), "onHandleIntent");
        tweet.tweet();
    }
}
