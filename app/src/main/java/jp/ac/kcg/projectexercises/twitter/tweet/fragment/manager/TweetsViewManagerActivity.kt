package jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.widget.AdapterView
import jp.ac.kcg.projectexercises.R
import jp.ac.kcg.projectexercises.activites.SubsidiaryActivity
import jp.ac.kcg.projectexercises.main.Global
import jp.ac.kcg.projectexercises.main.MainActivity
import jp.ac.kcg.projectexercises.twitter.client.ClientUser
import jp.ac.kcg.projectexercises.twitter.client.ClientUsers

import kotlinx.android.synthetic.main.activity_tweetsview_manager.*
import twitter4j.UserList
import java.util.*

/**
 */
class TweetsViewManagerActivity : SubsidiaryActivity() {
    private var draggingItem: TweetsViewManager.TweetsViewTable? = null
    private var prevDragPointItemPosition = -1

    private var tables: MutableList<TweetsViewManager.TweetsViewTable>? = null
    private var adapter: TweetsViewListAdapter? = null
    private val userListMap = LinkedHashMap<ClientUser, List<UserList>>()
    private val clientUsers = ArrayList<ClientUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweetsview_manager)
        tables = ArrayList<TweetsViewManager.TweetsViewTable>()
        tables!!.addAll(TweetsViewManager.instance.tweetsViewTables)
        adapter = TweetsViewListAdapter(this, 0, tables!!)
        setDragnDropListeners()
        TweetsViewManager.instance.onDeleteListener = {
            adapter!!.remove(it)
            adapter!!.notifyDataSetChanged()
        }
        fragment_list.adapter = adapter
        clientUsers.addAll(ClientUsers.instance.allUser())
        add_list_button.isEnabled = false

        add_home_button.setTextColor(resources.getColorStateList(R.color.button_text_dark))
        add_mentions_button.setTextColor(resources.getColorStateList(R.color.button_text_dark))
        add_list_button!!.setTextColor(resources.getColorStateList(R.color.button_text_dark))

        add_home_button.setOnClickListener { v -> addHomeTimelineTweetsView() }
        add_mentions_button.setOnClickListener { v -> addMentionsTweetsView() }
        addUserLists()
        setOnClickListener()
    }

    private fun addHomeTimelineTweetsView() {
        showUsersDialog({ clientUser ->
            val table = TweetsViewManager.TweetsViewTable(
                    TweetsViewManager.TweetsViewType.TIMELINE_HOME,
                    clientUser)
            add(table)
        })
    }

    private fun addMentionsTweetsView() {
        showUsersDialog({ clientUser ->
            val table = TweetsViewManager.TweetsViewTable(
                    TweetsViewManager.TweetsViewType.TIMELINE_MENTIONS,
                    clientUser)
            add(table)
        })
    }

    private fun addUserListTweetsView() {
        val listNames = ArrayList<CharSequence>()
        userListMap.forEach { lists ->
            lists.value.forEach {
                listNames.add(it.fullName)
            }
        }

        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_choose_list)
                .setItems(listNames.toArray<CharSequence>(arrayOfNulls(listNames.size)))
                { dialog, which ->
                    userListMap.forEach listMapForEach@ { lists ->
                        lists.value.forEach {
                            if (it.fullName == listNames[which]) {
                                val table = TweetsViewManager.TweetsViewTable(
                                        TweetsViewManager.TweetsViewType.LIST_USER,
                                        lists.key,
                                        it.id,
                                        it.name
                                )
                                add(table)
                                return@listMapForEach
                            }
                        }
                    }
                }.create()
        showDialog(dialog)
    }

    private fun addUserLists() {
        clientUsers.forEach {
            val handler = Handler()
            execute(Runnable {
                val userLists = it.twitter.getUserLists(it.userId)
                userListMap.put(it, userLists)
                handler.post {
                    add_list_button!!.setOnClickListener { v -> addUserListTweetsView() }
                    add_list_button.isEnabled = true
                }
            })
        }
    }

    private fun showUsersDialog(listener: (clientUser: ClientUser) -> Unit) {
        val userNames = ArrayList<CharSequence>()
        val clientUsers = ArrayList<ClientUser>()
        for (clientUser in ClientUsers.instance.allUser()) {
            userNames.add(clientUser.screenName)
            clientUsers.add(clientUser)
        }
        val alertDialog = AlertDialog.Builder(this).setTitle(getString(R.string.dialog_title_choose_user))
                .setItems(userNames.toArray<CharSequence>(arrayOfNulls<CharSequence>(userNames.size)))
                { dialog, which ->
                    for (clientUser in clientUsers) {
                        if (clientUser.screenName == userNames[which]) {
                            listener(clientUser)
                            break
                        }
                    }
                }.create()
        showDialog(alertDialog)
    }

    private fun save(): Boolean {
        return TweetsViewManager.instance.saveTweetsViews(tables!!)
    }

    private fun add(table: TweetsViewManager.TweetsViewTable) {
        for (tweetsViewTable in TweetsViewManager.instance.tweetsViewTables) {
            if (tweetsViewTable.id == table.id) {
                sendToast("追加済みです。")
                return
            }
        }

        adapter!!.add(table)
        if (!save()) {
            sendToast("追加できませんでした。")
            adapter!!.remove(table)
        }
    }

    private fun setDragnDropListeners() {
        fragment_list.setOnStartDragListener { dragItemPosition ->
            draggingItem = adapter!!.getItem(dragItemPosition)
            tables!!.remove(draggingItem!!)
            adapter!!.insert(emptyTable, dragItemPosition)
        }

        fragment_list.setOnDragListener { dragPointItemPosition ->
            if (prevDragPointItemPosition == -1 || prevDragPointItemPosition != dragPointItemPosition) {
                tables!!.remove(emptyTable)
                adapter!!.insert(emptyTable, dragPointItemPosition.toInt())
                prevDragPointItemPosition = dragPointItemPosition.toInt()
            }
        }

        fragment_list.setOnDropListener { dragItemPosition, dropPointPosition ->
            tables!![dropPointPosition] = draggingItem!!
            prevDragPointItemPosition = -1
            save()
            adapter!!.notifyDataSetChanged()
        }

        fragment_list.setOnDragCancelListener { draggingItemPosition ->
            adapter!!.insert(draggingItem, draggingItemPosition)
            adapter!!.remove(emptyTable)
            prevDragPointItemPosition = -1
        }

    }

    private fun setOnClickListener() {
        fragment_list.onItemClickListener =
                AdapterView.OnItemClickListener { adapterView, view, position, id ->
                    val dialog = AlertDialog.Builder(this)
                            .setMessage(R.string.dialog_message_confirm_choose_item_home)
                            .setPositiveButton(R.string.yes, { dialogInterface, i ->
                                TweetsViewManager.instance.setHomeTweetsViewTable(tables!![position])
                                adapter!!.notifyDataSetChanged()
                            })
                            .setNegativeButton(R.string.cancel, null).create()
                    showDialog(dialog)
                }
    }

    override fun finish() {
        if (TweetsViewManager.instance.isChanged && Global.instance.isActiveMainActivity) {
            Global.instance.destroyMainActivity()
            startActivity(MainActivity::class.java, false)
        }
        super.finish()
    }


    companion object {
        private val emptyTable = TweetsViewManager.TweetsViewTable.createEmptyTable()
    }

}
