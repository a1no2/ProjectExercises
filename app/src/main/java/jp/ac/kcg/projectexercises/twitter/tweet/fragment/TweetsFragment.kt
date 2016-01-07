package jp.ac.kcg.projectexercises.twitter.tweet.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.ac.kcg.projectexercises.R
import jp.ac.kcg.projectexercises.activites.ApplicationActivity
import jp.ac.kcg.projectexercises.twitter.client.ClientUser
import jp.ac.kcg.projectexercises.twitter.client.ClientUsers
import jp.ac.kcg.projectexercises.twitter.tweet.Tweet
import jp.ac.kcg.projectexercises.twitter.tweet.TweetFactory
import jp.ac.kcg.projectexercises.twitter.tweet.listview.TweetListAdapter

import jp.ac.kcg.projectexercises.twitter.tweet.listview.TweetListView

import java.util.ArrayList

import kotlinx.android.synthetic.fragment_tweets.view.*
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.TwitterException

/**
 */
abstract class TweetsFragment : Fragment() {

    protected var tweetListView: TweetListView? = null
        private set
    protected var clientUser: ClientUser? = null
    protected var adapter: TweetListAdapter? = null
        private set
    protected var swipeRefreshLayout: SwipeRefreshLayout? = null
        private set


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (context !is ApplicationActivity)
            throw IllegalStateException("親のアクティビティがApplicationActivityを継承していない")
        val screenName = arguments.getString(EXTRA_CLIENT_USER) ?: throw NullPointerException("clientUserがセットされていない")

        val rootView = inflater!!.inflate(R.layout.fragment_tweets, container, false)
        tweetListView = rootView.tweet_list
        swipeRefreshLayout = rootView.refresh_layout
        swipeRefreshLayout!!.isEnabled = false
        adapter = TweetListAdapter(context, 0, ArrayList<Tweet>())
        ClientUsers.instance.loadUsers { clientUsers, success ->
            clientUser = ClientUsers.instance.getClientUser(screenName)
            tweetListView!!.setAdapter(adapter!!)
            tweetListView!!.setClientUser(clientUser!!)
            tweetListView!!.setOnAttachedBottomCallback { item ->
                if (item is Tweet) {
                    tweetListView!!.attachedBottomCallbackEnabled(false)
                    getTweets(TweetsFragment.DEFAULT_COUNT, null, item.id)
                }
            }
            swipeRefreshLayout!!.setOnRefreshListener {
                swipeRefreshLayout!!.isRefreshing = true
                if (adapter!!.count > 0)
                    getTweets(TweetsFragment.DEFAULT_COUNT, adapter!!.getItem(0).id, null)
                else
                    getTweets(TweetsFragment.DEFAULT_COUNT, null, null)
            }
            initialize(clientUser!!, adapter!!, null)
        }
        return rootView
    }


    protected abstract fun initialize(clientUser: ClientUser, tweetListAdapter: TweetListAdapter, bundle: Bundle?)

    protected abstract fun getTweets(count: Int, sinceStatusId: Long?, maxStatusId: Long?)

    protected fun sendToast(text: String) {
        (context as ApplicationActivity).sendToast(text)
    }

    abstract val name: String


    internal final fun postProcess() {
        onPostProcess()
    }


    protected open fun onPostProcess() {

    }

    protected final fun defaultCallback(sinceStatusId: Long?, maxStatusId: Long?): Callback {

        val defaultCallback = object : Callback {
            override fun success(tweets: ResponseList<Status>) {
                if (activity != null && !activity.isFinishing) {
                    if (!tweets.isEmpty()) {
                        if (maxStatusId != null) tweets.removeAt(0)
                        if (sinceStatusId != null) {
                            for (i in tweets.indices) {
                                activity.runOnUiThread {
                                    adapter!!.insert(TweetFactory.instance.createOrGetTweet(clientUser!!, tweets[i]), i)
                                }
                            }
                            tweetListView!!.setSelection(tweets.size)
                        } else {
                            for (tweet in tweets) {
                                activity.runOnUiThread {
                                    adapter!!.add(TweetFactory.instance.createOrGetTweet(clientUser!!, tweet))
                                }
                            }
                        }
                    } else {
                        sendToast(getString(R.string.info_no_tweets_to_read_was))
                    }
                    tweetListView!!.attachedBottomCallbackEnabled(true)
                    activity.runOnUiThread {
                        swipeRefreshLayout!!.isRefreshing = false
                    }
                }
            }

            override fun failure(e: TwitterException) {
                if (activity != null && !activity.isFinishing) {
                    sendToast(e.message!!)
                    tweetListView!!.attachedBottomCallbackEnabled(true)
                    swipeRefreshLayout!!.isRefreshing = false
                }
            }
        }
        return defaultCallback
    }

    protected final fun createPaging(count: Int, sinceStatusId: Long?, maxStatusId: Long?): Paging {
        var n = count
        val paging = Paging()
        if (sinceStatusId != null) {
            n++
            paging.sinceId = sinceStatusId
        }
        if (maxStatusId != null) {
            n++
            paging.maxId = maxStatusId
        }
        paging.count = n
        return paging
    }

    interface Callback {
        fun success(tweets: ResponseList<Status>);

        fun failure(e: TwitterException);
    }

    companion object {
        var DEFAULT_COUNT = 30
        var EXTRA_CLIENT_USER = "CLIENT_USER"
    }
}
