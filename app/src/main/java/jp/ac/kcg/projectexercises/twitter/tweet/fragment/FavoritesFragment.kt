package jp.ac.kcg.projectexercises.twitter.tweet.fragment

import android.os.Bundle
import jp.ac.kcg.projectexercises.activites.ThreadPoolHolder

import jp.ac.kcg.projectexercises.twitter.client.ClientUser
import jp.ac.kcg.projectexercises.twitter.tweet.listview.TweetListAdapter
import twitter4j.TwitterException


/**
 */
class FavoritesFragment : TweetsFragment() {

    private var screenName: String? = null

    override fun initialize(clientUser: ClientUser, tweetListAdapter: TweetListAdapter, bundle: Bundle?) {
        val screenName = arguments.getString(EXTRA_SCREEN_NAME) ?: throw NullPointerException("screenNameがセットされていない")

        this.screenName = screenName
        getTweets(TweetsFragment.DEFAULT_COUNT, null, null)
        swipeRefreshLayout!!.isEnabled = true
    }

    override fun getTweets(count: Int, sinceStatusId: Long?, maxStatusId: Long?) {
        val paging = createPaging(count, sinceStatusId, maxStatusId)
        val threadPoolHolder = context as ThreadPoolHolder
        val callback = defaultCallback(sinceStatusId, maxStatusId)
        threadPoolHolder.execute(Runnable {
            try {
                val tweets = clientUser!!.twitter.getFavorites(screenName, paging)
                callback.success(tweets)
            } catch(e: TwitterException) {
                callback.failure(e)
            }
        })
    }


    override val name: String
        get() = ""

    companion object {

        val EXTRA_SCREEN_NAME = "EXTRA_SCREEN_NAME"
    }
}
