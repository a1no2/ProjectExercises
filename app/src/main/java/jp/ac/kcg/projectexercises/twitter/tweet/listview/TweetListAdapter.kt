package jp.ac.kcg.projectexercises.twitter.tweet.listview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import jp.ac.kcg.projectexercises.R

import jp.ac.kcg.projectexercises.activites.ApplicationActivity
import jp.ac.kcg.projectexercises.color.ColorRegister
import jp.ac.kcg.projectexercises.config.ConfigurationRegister
import jp.ac.kcg.projectexercises.imageview.SmartImageView
import jp.ac.kcg.projectexercises.twitter.client.ClientUser
import jp.ac.kcg.projectexercises.twitter.tweet.Tweet

import java.util.ArrayList

import kotlinx.android.synthetic.item_tweet.view.*

/**
 */
class TweetListAdapter(context: Context, resource: Int, private val tweets: MutableList<Tweet>) : ArrayAdapter<Tweet>(context, resource, tweets) {
    private val layoutInflater: LayoutInflater
    private var clientUser: ClientUser? = null

    init {
        if (context !is ApplicationActivity) {
            throw IllegalStateException("contextがNumeriActivityを継承していません")
        }
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var tmpTweet = getItem(position)
        val isRt = tmpTweet.isRt
        var tweet = if (!isRt) tmpTweet else tmpTweet.retweetedStatus
        val view: View = convertView ?: layoutInflater.inflate(R.layout.item_tweet, null)

        view.icon_image.setImage(true, SmartImageView.ProgressType.LOAD_ICON, tweet.user.biggerProfileImageUrl)

        view.screen_name_text.text = tweet.user.screenName
        view.name_text.text = tweet.user.name

        view.created_at_text.text = tweet.createdAt


        view.main_text.text = tweet.text

        val textSize = ConfigurationRegister.NumericalConfigurations.CHARACTER_SIZE.numericValue.toFloat() + 8
        view.main_text.textSize = textSize
        view.name_text.textSize = textSize * 0.85.toFloat()
        view.screen_name_text.textSize = textSize * 0.85.toFloat()
        view.created_at_text.textSize = textSize * 0.85.toFloat()


        if (isRt) {
            view.overlay_linear.setBackgroundColor(ColorRegister.Color.RT_ITEM.getTransColor("AA"))
            return view
        }
        if (tweet.isMention) {
            view.overlay_linear.setBackgroundColor(ColorRegister.Color.MENTION_ITEM.getTransColor("AA"))
            return view
        }

        view.overlay_linear.setBackgroundColor(ColorRegister.Color.NORMAL_ITEM.getTransColor("AA"))
        return view;
    }

    override fun remove(deletionModel: Tweet) {
        val tweets = ArrayList<Tweet>()
        tweets.addAll(this.tweets)
        for (i in tweets.indices) {
            if (tweets[i].id == deletionModel.id) {
                this.tweets.removeAt(i)
                notifyDataSetChanged()
                break
            }
        }
    }

    internal fun setClientUser(clientUser: ClientUser) {
        this.clientUser = clientUser
    }
}
