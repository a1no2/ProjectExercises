package jp.ac.kcg.projectexercises.twitter.tweet.action

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import jp.ac.kcg.projectexercises.R
import jp.ac.kcg.projectexercises.activites.ApplicationActivity
import jp.ac.kcg.projectexercises.twitter.TweetActivity
import jp.ac.kcg.projectexercises.twitter.client.ClientUser
import jp.ac.kcg.projectexercises.twitter.tweet.Tweet
import twitter4j.TwitterException


import java.util.ArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * ツイートに対するアクション
 */
class TwitterActions(private val applicationActivity: ApplicationActivity, private val clientUser: ClientUser) {
    private val threadPool: ExecutorService = Executors.newCachedThreadPool()

    fun action(action: Action, tweet: Tweet) {
        val targetTweet = if (tweet.isRt) tweet.retweetedStatus else tweet
        when (action) {
            TwitterActions.Action.REPLY -> reply(targetTweet)
            TwitterActions.Action.FAVORITE -> favorite(targetTweet)
            TwitterActions.Action.CONFIRM_FAVORITE -> confirmFavorite(targetTweet)
            TwitterActions.Action.RT -> retweet(targetTweet)
            TwitterActions.Action.CONFIRM_RT -> confirmRetweet(targetTweet)
            TwitterActions.Action.QT -> quoteRetweet(targetTweet)
            TwitterActions.Action.OPEN_TWEET_DETAIL -> openTweetDetail(targetTweet)
            TwitterActions.Action.SHOW_CONVERSATION -> showConversation(targetTweet)
            TwitterActions.Action.MENU -> showMenu(tweet)
            TwitterActions.Action.SHOW_MEDIA -> showMedia(targetTweet.imageUris)
            TwitterActions.Action.OPEN_USER_PROFILE -> openUserProfile(targetTweet)
            TwitterActions.Action.ALL_TAG_TWEET -> hashtagsTweet(targetTweet)
            TwitterActions.Action.OPEN_TWEET_ACTIVITY -> openTweetActivity()
            TwitterActions.Action.ACTION_NONE -> {
            }
            else -> {
            }
        }
    }

    private fun confirmRetweet(tweet: Tweet) {
        val message = if (tweet.isRetweeted)
            applicationActivity.getString(R.string.dialog_message_confirmation_tweet_unretweet)
        else
            applicationActivity.getString(R.string.dialog_message_confirmation_tweet_retweet)
        if (!applicationActivity.isFinishing) {
            val alertDialog = AlertDialog.Builder(applicationActivity)
                    //.setCustomTitle(tweet.convertView(applicationActivity))
                    .setMessage(message).setNegativeButton(applicationActivity.getString(R.string.cancel), null)
                    .setPositiveButton(applicationActivity.getString(R.string.yes)) {
                        dialog, id ->
                        retweet(tweet)
                    }.create()
            applicationActivity.showDialog(alertDialog)
        }
    }


    private fun retweet(tweet: Tweet) {
        if (tweet.isRetweeted)
            if (tweet.user.isProtected) {
                applicationActivity.sendToast(applicationActivity.getString(R.string.can_not_be_retweet_protected_user))
                return
            }
        threadPool.execute {
            try {
                if (!tweet.isRetweeted) {
                    clientUser.twitter.retweetStatus(tweet.id)
                } else {
                    clientUser.twitter.destroyStatus(tweet.id)
                }
            } catch(e: TwitterException) {
                e.printStackTrace()
            }
        }
    }


    private fun confirmFavorite(tweet: Tweet) {
        val message = if (!tweet.isFavorited)
            applicationActivity.getString(R.string.dialog_message_confirmation_tweet_favorite)
        else
            applicationActivity.getString(R.string.dialog_message_confirmation_tweet_unfavorite)
        if (!applicationActivity.isFinishing) {
            val alertDialog = AlertDialog.Builder(applicationActivity)
                    // .setCustomTitle(tweet.convertView(applicationActivity))
                    .setMessage(message).setNegativeButton(applicationActivity.getString(R.string.cancel), null)
                    .setPositiveButton(applicationActivity.getString(R.string.yes)
                    ) { dialog, id ->
                        favorite(tweet)
                    }.create()
            applicationActivity.showDialog(alertDialog)
        }

    }

    private fun favorite(tweet: Tweet) {
        val targetStatus = if (tweet.isRt) tweet.retweetedStatus else tweet
        if (targetStatus.isRetweeted)
            if (tweet.user.isProtected) {
                applicationActivity.sendToast(applicationActivity.getString(R.string.can_not_be_retweet_protected_user))
                return
            }
        threadPool.execute {
            try {
                if (!targetStatus.isFavorited) {
                    clientUser.twitter.createFavorite(targetStatus.id)
                } else {
                    clientUser.twitter.destroyFavorite(targetStatus.id)
                }
            } catch(e: TwitterException) {
                e.printStackTrace()
            }
        }
    }

    private fun reply(tweet: Tweet) {
        TweetActivity.replyTweet(applicationActivity, clientUser, tweet)
    }

    private fun openTweetDetail(tweet: Tweet) {
        //ToDo 未実装
    }

    private fun showConversation(tweet: Tweet) {
        //ToDo 未実装
    }


    private fun hashtagTweet(hashtag: String) {
        TweetActivity.hashtagTweet(applicationActivity, clientUser, hashtag)
    }


    private fun hashtagsTweet(tweet: Tweet) {
        TweetActivity.hashtagsTweet(applicationActivity, clientUser, tweet.hashTags)
    }


    private fun showMenu(tweet: Tweet) {

        val targetTweet = if (tweet.isRt) tweet.retweetedStatus else tweet
        val menuItems = ArrayList<MenuItem>()

        menuItems.add(MenuItem(Action.REPLY))

        if (!tweet.isFavorited)
            menuItems.add(MenuItem(Action.FAVORITE))
        else
            menuItems.add(MenuItem(Action.FAVORITE, "お気に入り解除"))

        if (!tweet.user.isProtected) menuItems.add(MenuItem(Action.RT))

        if (!tweet.user.isProtected) menuItems.add(MenuItem(Action.QT))

        menuItems.add(MenuItem(Action.OPEN_TWEET_DETAIL))

        for (s in targetTweet.involvedUserNames) {
            menuItems.add(MenuItem(Action.OPEN_INVOLVED_USER_PROFILE, "@" + s))
        }

        if (!targetTweet.inReplyToStatusId.equals(-1L))
            menuItems.add(MenuItem(Action.SHOW_CONVERSATION))

        if (!targetTweet.hashTags.isEmpty()) {
            for (s in tweet.hashTags) {
                menuItems.add(MenuItem(Action.TAG_TWEET, "#" + s))
            }
            if (targetTweet.hashTags.size > 1)
                menuItems.add(MenuItem(Action.ALL_TAG_TWEET))
        }

        if (!targetTweet.imageUris.isEmpty())
            menuItems.add(MenuItem(Action.SHOW_MEDIA))

        if (!targetTweet.imageUris.isEmpty()) {
            for (s in tweet.uris) {
                menuItems.add(MenuItem(Action.OPEN_URL, s))
            }
        }

        val menuItemTexts = ArrayList<CharSequence>()
        menuItems.forEach { menuItem ->
            if (menuItem.value == null) {
                menuItemTexts.add(menuItem.action!!.actionName)
            } else {
                menuItemTexts.add(menuItem.value!!)
            }
        }

        val alertDialog = AlertDialog.Builder(applicationActivity)
                //.setCustomTitle(tweet.convertView(applicationActivity))
                .setItems(menuItemTexts.toArray<CharSequence>(arrayOfNulls<CharSequence>(menuItemTexts.size))) { dialog, which ->
                    action(menuItems[which].action!!, tweet)
                    when (menuItems[which].action) {
                        TwitterActions.Action.TAG_TWEET -> hashtagTweet(menuItems[which].value!!)
                        TwitterActions.Action.OPEN_URL -> openUrl(menuItems[which].value!!)
                        TwitterActions.Action.OPEN_INVOLVED_USER_PROFILE -> openInvolvedUserProfile(menuItems[which].value!!)
                        else -> {
                        }
                    }
                }.create()
        applicationActivity.showDialog(alertDialog)
    }

    private fun openUrl(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        applicationActivity.startActivity(intent)
    }

    private fun showMedia(urls: List<String>) {
        //ToDo 未実装
    }

    private fun openUserProfile(tweet: Tweet) {
        //ToDo 未実装
    }

    private fun openInvolvedUserProfile(screenName: String) {
        //ToDo 未実装
    }

    private fun quoteRetweet(tweet: Tweet) {
        TweetActivity.quoteRetweet(applicationActivity, clientUser, tweet)
    }

    private fun openTweetActivity() {
        applicationActivity.startActivity(TweetActivity::class.java, false)
    }

    private class MenuItem {
        internal var action: Action? = null
        internal var value: String? = null
            private set

        internal constructor(action: Action) {
            this.action = action
        }

        internal constructor(action: Action, value: String) {
            this.action = action
            this.value = value
        }
    }


    /**
     * アクションのidと名前を保持する列挙型
     */
    enum class Action {
        REPLY("リプライ", "REPLY"),
        RT("リツイート", "RT"),
        CONFIRM_RT("リツイート(確認あり)", "CONFIRM_RT"),
        FAVORITE("お気に入り", "FAVORITE"),
        CONFIRM_FAVORITE("お気に入り(確認あり)", "CONFIRM_FAVORITE"),
        MENU("メニュー", "MENU"),
        QT("引用リツイート", "QT"),
        OPEN_USER_PROFILE("ユーザー情報", "OPEN_USER_PROFILE"),
        OPEN_INVOLVED_USER_PROFILE("", "OPEN_INVOLVED_USER_PROFILE", false),
        OPEN_TWEET_DETAIL("ツイートの詳細を開く", "OPEN_TWEET_DETAIL"),
        SHOW_CONVERSATION("会話を表示", "SHOW_CONVERSATION"),
        SHOW_MEDIA("画像を表示", "SHOW_MEDIA"),
        OPEN_URL("", "OPEN_URL", false),
        TAG_TWEET("", "TAG_TWEET", false),
        ALL_TAG_TWEET("すべてのハッシュタグをツイート", "ALL_TAG_TWEET"),
        OPEN_TWEET_ACTIVITY("ツイート画面を開く", "OPEN_TWEET_ACTIVITY"),
        ACTION_NONE("何もしない", "ACTION_NONE");

        var id: String? = null
            private set
        var actionName = ""
            private set
        var isTouchAction = true
            private set

        internal constructor(name: String, id: String) {
            this.actionName = name
            this.id = id
        }

        internal constructor(name: String, id: String, isTouchAction: Boolean) {
            this.actionName = name
            this.id = id
            this.isTouchAction = isTouchAction
        }
    }
}