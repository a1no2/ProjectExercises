package jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import jp.ac.kcg.projectexercises.R


import kotlinx.android.synthetic.item_tweetsview_list.view.*

/**
 */
class TweetsViewListAdapter(context: Context, resource: Int, objects: List<TweetsViewManager.TweetsViewTable>) : ArrayAdapter<TweetsViewManager.TweetsViewTable>(context, resource, objects) {
    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val tweetsViewTable = getItem(position)
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_tweetsview_list, null)
        }
        if (tweetsViewTable.id != "") {
            if (tweetsViewTable.userListId.equals(0L)) {
                view!!.tweets_view_name_text.text = tweetsViewTable.typeName + " : " + tweetsViewTable.clientUserName
            } else {
                view!!.tweets_view_name_text.text = tweetsViewTable.typeName + " : " + tweetsViewTable.userListName
            }
            val home = TweetsViewManager.instance.homeTweetsViewIndex
            if (tweetsViewTable.order.equals(home)) {
                view.tweets_view_name_text.setTextColor(view.resources.getColor(R.color.accent_common))
            } else {
                view.tweets_view_name_text.setTextColor(view.resources.getColor(R.color.text_black))
            }
            view.delete_button.setImageDrawable(context.resources.getDrawable(R.drawable.ic_delete_black_24dp))
            view.delete_button.setOnClickListener { v -> TweetsViewManager.instance.deleteTweetsView(tweetsViewTable) }
            view.delete_button.visibility = View.VISIBLE
        } else {
            view!!.tweets_view_name_text.text = ""
            view.delete_button.visibility = View.GONE
        }
        view.delete_button.isFocusable = false

        return view
    }
}
