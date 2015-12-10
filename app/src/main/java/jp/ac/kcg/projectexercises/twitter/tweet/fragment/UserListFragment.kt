package jp.ac.kcg.projectexercises.twitter.tweet.fragment

import android.os.Bundle

import jp.ac.kcg.projectexercises.activites.ThreadPoolHolder
import jp.ac.kcg.projectexercises.twitter.client.ClientUser
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager.TweetsViewManager
import jp.ac.kcg.projectexercises.twitter.tweet.listview.TweetListAdapter
import twitter4j.TwitterException

/**
 */
class UserListFragment : TweetsFragment() {

    private var listId: Long = -1

    override fun initialize(clientUser: ClientUser, tweetListAdapter: TweetListAdapter, bundle: Bundle?) {
        val idString = arguments.getString(EXTRA_LIST_ID)
        val listName = arguments.getString(EXTRA_LIST_NAME)
        if (idString == null)
            throw NullPointerException("userListIdがセットされていない")
        if (listName == null)
            throw NullPointerException("userListIdがセットされていない")

        listId = java.lang.Long.valueOf(idString)!!
        getTweets(TweetsFragment.DEFAULT_COUNT, null, null)
        swipeRefreshLayout!!.isEnabled = true
    }


    override fun getTweets(count: Int, sinceStatusId: Long?, maxStatusId: Long?) {
        val paging = createPaging(count, sinceStatusId, maxStatusId)
        val threadPoolHolder = context as ThreadPoolHolder
        val callback = defaultCallback(sinceStatusId, maxStatusId)
        threadPoolHolder.execute(Runnable {
            try {
                val tweets = clientUser!!.twitter.list().getUserListStatuses(listId, paging)
                callback.success(tweets)
            } catch(e: TwitterException) {
                callback.failure(e)
            }
        })
    }

    override val name: String
        get() = TweetsViewManager.TweetsViewType.LIST_USER.displayName + " : " + arguments.getString(EXTRA_LIST_NAME)

    companion object {
        val EXTRA_LIST_ID = "EXTRA_LIST_ID"
        val EXTRA_LIST_NAME = "EXTRA_LIST_NAME"
    }


}
