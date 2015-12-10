package jp.ac.kcg.projectexercises.twitter.tweet.listview

import android.app.Activity
import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.MotionEvent
import jp.ac.kcg.projectexercises.R
import jp.ac.kcg.projectexercises.activites.ApplicationActivity
import jp.ac.kcg.projectexercises.config.ConfigurationRegister

import jp.ac.kcg.projectexercises.listview.AttachedBottomCallBackListView
import jp.ac.kcg.projectexercises.main.getWindowSize
import jp.ac.kcg.projectexercises.twitter.client.ClientUser
import jp.ac.kcg.projectexercises.twitter.tweet.Tweet
import jp.ac.kcg.projectexercises.twitter.tweet.action.ActionStorager
import jp.ac.kcg.projectexercises.twitter.tweet.action.TwitterActions

import java.util.ArrayList

/**
 */
class TweetListView(context: Context, attrs: AttributeSet) : AttachedBottomCallBackListView(context, attrs) {
    private var touchedCoordinatesX: Float = 0.toFloat()
    private var insertItemEnabled = true

    private var firstVisibleItemPosition = 0
    private var visibleItemCount = 0

    private var twitterAction: TwitterActions? = null
    private var clientUser: ClientUser? = null
    private var enabledFastScroll: Boolean = false
    private val storedItems = ArrayList<Tweet>()


    private val onModelDeleteListener: (Tweet) -> Unit = { deletionTweetModel ->
        (getContext() as Activity).runOnUiThread { adapter!!.remove(deletionTweetModel) }
    }

    private var onChangeStateCallback: () -> Unit = {
        (context as Activity).runOnUiThread { adapter?.notifyDataSetChanged() }
    }

    init {
        if (context !is ApplicationActivity) {
            throw IllegalStateException("親のActivityがNumeriActivityでない")
        }
        // enabledFastScroll = ConfigurationStorager.EitherConfigurations.USE_FAST_SCROLL.isEnabled
        isFastScrollEnabled = enabledFastScroll

        setOnScrollCallback({ view, firstVisibleItemPosition, visibleItemCount, totalItemCount ->
            if (firstVisibleItemPosition == 0 && !storedItems.isEmpty() && insertItemEnabled) {
                insertItemEnabled = false
                val y = getChildAt(0).top
                val size = storedItems.size
                while (!storedItems.isEmpty()) {
                    adapter!!.insert(storedItems[0], 0)
                    storedItems.removeAt(0)
                    setSelectionFromTop(size - storedItems.size, y)
                }
                insertItemEnabled = true
            }
            if (enabledFastScroll != ConfigurationRegister.EitherConfigurations.USE_FAST_SCROLL.isEnabled) {
                enabledFastScroll = ConfigurationRegister.EitherConfigurations.USE_FAST_SCROLL.isEnabled
                isFastScrollEnabled = enabledFastScroll
            }
            this.firstVisibleItemPosition = firstVisibleItemPosition
            this.visibleItemCount = visibleItemCount
        })
        context.addRefreshLayoutCallbacks(onChangeStateCallback)
        //addOnStateChangeListener(onChangeStateCallback)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        touchedCoordinatesX = ev.x
        return super.onTouchEvent(ev)
    }


    private val touchedCoordinates: Int
        get() {
            val windowX = getWindowSize(context).x.toFloat()
            if (windowX / 3 > touchedCoordinatesX)
                return LEFT
            if (windowX / 3 * 2 > touchedCoordinatesX)
                return CENTER
            if (windowX > touchedCoordinatesX)
                return RIGHT

            throw InternalError("何らかの原因で返される値が正常ではありません")
        }

    /**
     * タッチによるアクションの実行を有効にする

     * @param clientUser アクションを実行するユーザー
     * *
     * @param activity   activity
     */
    private fun onTouchItemEnabled(clientUser: ClientUser?, activity: ApplicationActivity?) {
        if (clientUser == null) {
            throw NullPointerException("numeriUserがセットされていません")
        }
        if (activity == null) {
            throw NullPointerException("contextがセットされていません")
        }
        if (adapter == null) {
            throw NullPointerException("adapterがセットされていません")
        }

        twitterAction = TwitterActions(activity, clientUser)

        setOnItemClickListener { parent, view, position, id ->
            when (touchedCoordinates) {
                LEFT -> {
                    val action = ActionStorager.RespectTapPositionAction.LEFT.twitterAction
                    activity.sendToast(action!!.actionName)
                    twitterAction!!.action(action, adapter!!.getItem(position))
                }
                CENTER -> {
                    val action1 = ActionStorager.RespectTapPositionAction.CENTER.twitterAction
                    activity.sendToast(action1!!.actionName)
                    twitterAction!!.action(action1, adapter!!.getItem(position))
                }
                RIGHT -> {
                    val action2 = ActionStorager.RespectTapPositionAction.RIGHT.twitterAction
                    activity.sendToast(action2!!.actionName)
                    twitterAction!!.action(action2, adapter!!.getItem(position))
                }
                else -> {
                }
            }
        }
        setOnItemLongClickListener { parent, view, position, id ->
            when (touchedCoordinates) {
                LEFT -> {
                    val action = ActionStorager.RespectTapPositionAction.LONG_LEFT.twitterAction
                    activity.sendToast(action!!.actionName)
                    twitterAction!!.action(action, adapter!!.getItem(position))
                }
                CENTER -> {
                    val action1 = ActionStorager.RespectTapPositionAction.LONG_CENTER.twitterAction
                    activity.sendToast(action1!!.actionName)
                    twitterAction!!.action(action1, adapter!!.getItem(position))
                }
                RIGHT -> {
                    val action2 = ActionStorager.RespectTapPositionAction.LONG_RIGHT.twitterAction
                    activity.sendToast(action2!!.actionName)
                    twitterAction!!.action(action2, adapter!!.getItem(position))
                }
                else -> {
                }
            }
            true
        }
    }


    fun setClientUser(clientUser: ClientUser) {
        this.clientUser = clientUser
        adapter!!.setClientUser(clientUser)
        onTouchItemEnabled(clientUser, context as ApplicationActivity)
        //addOnModelDeleteListener(onModelDeleteListener)
    }

    /**
     * @param adapter TimeLineItemAdapter
     */
    fun setAdapter(adapter: TweetListAdapter) {
        super.setAdapter(adapter)
    }

    override fun getAdapter(): TweetListAdapter? {
        return super.getAdapter() as TweetListAdapter?
    }

    override fun onAttachedBottom(item: Any) {
        if (ConfigurationRegister.EitherConfigurations.CONFIRMATION_LESS_GET_TWEET.isEnabled) {
            super.onAttachedBottom(item)
        } else {
            val alertDialog = AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.dialog_message_confirmation_more_read_tweet))
                    .setPositiveButton(context.getString(R.string.yes)) { dialog, which ->
                        super.onAttachedBottom(item)
                    }.setNegativeButton(context.getString(R.string.cancel), null)
                    .create()
            (context as ApplicationActivity).showDialog(alertDialog)
        }
    }

    /**
     * 一番上にアイテムを追加する際に使用

     * @param item SimpleTweetModel
     */
    fun insert(item: Tweet) {
        if (firstVisiblePosition == 0 && insertItemEnabled) {
            (context as Activity).runOnUiThread { adapter!!.insert(item, 0) }
        } else {
            storedItems.add(item)
        }
    }

    override fun onDetachedFromWindow() {
        (context as ApplicationActivity).removeRefreshLayoutCallbacks(onChangeStateCallback)
        super.onDetachedFromWindow()
    }

    companion object {
        private val LEFT = 0
        private val CENTER = 1
        private val RIGHT = 2
    }

}
