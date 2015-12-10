package jp.ac.kcg.projectexercises.twitter.tweet.fragment

import android.os.Bundle

import jp.ac.kcg.projectexercises.activites.ThreadPoolHolder
import jp.ac.kcg.projectexercises.twitter.client.ClientUser
import jp.ac.kcg.projectexercises.twitter.tweet.Tweet
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager.TweetsViewManager
import jp.ac.kcg.projectexercises.twitter.tweet.listview.TweetListAdapter
import twitter4j.Paging
import twitter4j.TwitterException

/**
 */
class MentionsTimelineFragment : TweetsFragment() {

    private val onStatusListener = { status: Tweet ->
        if (status.isMention && !status.isRt) {
            tweetListView!!.insert(status)
        }
    }

    override fun initialize(clientUser: ClientUser, tweetListAdapter: TweetListAdapter, bundle: Bundle?) {
        getTweets(TweetsFragment.DEFAULT_COUNT, null, null)
        clientUser.stream.addOnStatusListener(onStatusListener)
    }

    override fun getTweets(count: Int, sinceStatusId: Long?, maxStatusId: Long?) {
        val paging = createPaging(count, sinceStatusId, maxStatusId)
        val threadPoolHolder = context as ThreadPoolHolder
        val callback = defaultCallback(sinceStatusId, maxStatusId)
        threadPoolHolder.execute(Runnable {
            try {
                val tweets = clientUser!!.twitter.timelines().getMentionsTimeline(paging)
                callback.success(tweets)
            } catch(e: TwitterException) {
                callback.failure(e)
            }
        })
    }

    override val name: String
        get() = TweetsViewManager.TweetsViewType.TIMELINE_MENTIONS.displayName + " : " + arguments.getString(TweetsFragment.EXTRA_CLIENT_USER)

    override fun onPostProcess() {
        clientUser?.stream?.removeOnStatusListener(onStatusListener)
        super.onPostProcess()
    }

}
