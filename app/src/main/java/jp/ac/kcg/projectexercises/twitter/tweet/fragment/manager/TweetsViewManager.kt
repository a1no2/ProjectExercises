package jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager


import android.util.Log
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import jp.ac.kcg.projectexercises.main.Global
import jp.ac.kcg.projectexercises.twitter.client.ClientUser
import jp.ac.kcg.projectexercises.twitter.client.ClientUsers
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.HomeTimelineFragment
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.MentionsTimelineFragment
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.TweetsFragment
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.UserListFragment
import jp.ac.kcg.projectexercises.utill.BundleBuilder
import jp.ac.kcg.projectexercises.utill.Register
import java.util.ArrayList


/**
 */
class TweetsViewManager private constructor() : Register() {
    private val tweetsFragments = ArrayList<TweetsFragment>()
    var onDeleteListener: ((table: TweetsViewTable) -> Unit) = {}
    var isChanged = false
        private set
    var homeTweetsViewIndex: Int = 0

    init {
        setHomeTweetsViewIndex()
    }

    /**
     * TweetsViewの情報を保存する

     * @param tweetsViewTables tweetsViewTablesのリスト
     * *
     * @return 保存できたか否か
     */
    fun saveTweetsViews(tweetsViewTables: List<TweetsViewTable>): Boolean {
        if (tweetsViewTables.isEmpty()) return false
        handleDao<TweetsViewTable, String>(Global.instance.applicationContext!!, TweetsViewTable::class.java) {
            if (tweetsViewTables.size == 1) {
                setHomeTweetsViewTable(tweetsViewTables[0])
            }
            tweetsViewTables.forEachIndexed { i, table ->
                table.order = i
                it.createOrUpdate(table)
            }
            setHomeTweetsViewIndex()
            isChanged = true
        }
        return true
    }

    private fun setHomeTweetsViewIndex() {
        handleDao<HomeTweetsViewTable, String>(Global.instance.applicationContext!!, HomeTweetsViewTable::class.java) {
            val table = it.queryForId(HomeTweetsViewTable.id)
            if (table != null) {
                homeTweetsViewIndex = tweetsViewTables.find {
                    table.tweetsViewId.equals(it.id)
                }?.order ?: 0
            } else {
                homeTweetsViewIndex = 0
            }
        }
    }

    fun setHomeTweetsViewTable(table: TweetsViewTable): Int {
        var order: Int = 0
        handleDao<HomeTweetsViewTable, String>(Global.instance.applicationContext!!, HomeTweetsViewTable::class.java) {
            val homeViewTable: HomeTweetsViewTable = HomeTweetsViewTable(table.id)
            it.createOrUpdate(homeViewTable)
            setHomeTweetsViewIndex()
            order = table.order
        }
        return order
    }

    private fun getHomeTweetsViewTable(): HomeTweetsViewTable {
        var table: HomeTweetsViewTable? = null
        handleDao<HomeTweetsViewTable, String>(Global.instance.applicationContext!!, HomeTweetsViewTable::class.java) {
            table = it.queryForId(HomeTweetsViewTable.id)
        }
        return table ?: HomeTweetsViewTable("")
    }

    fun deleteTweetsView(tweetsViewTable: TweetsViewTable) {
        handleDao<TweetsViewTable, String>(Global.instance.applicationContext!!, TweetsViewTable::class.java) {
            it.delete(tweetsViewTable)
            isChanged = true
            if (tweetsViewTables.isNotEmpty()) {
                setHomeTweetsViewTable(tweetsViewTables[0])
            }
            setHomeTweetsViewIndex()
            onDeleteListener(tweetsViewTable)
        }
    }

    private fun setHomgeFirst() {
        handleDao<TweetsViewTable, String>(Global.instance.applicationContext!!, TweetsViewTable::class.java) {

        }
    }

    fun deleteTweetsViewByClientUser(clientUser: ClientUser) {
        handleDao<TweetsViewTable, String>(Global.instance.applicationContext!!, TweetsViewTable::class.java) { dao ->
            dao.queryForAll().forEach {
                if (it.userId == clientUser.userId) {
                    val home = getHomeTweetsViewTable()
                    dao.delete(it)
                    isChanged = true
                    if (tweetsViewTables.isNotEmpty()) {
                        setHomeTweetsViewTable(tweetsViewTables[0])
                    }
                    onDeleteListener(it)
                }
            }
            setHomeTweetsViewIndex()
        }
    }

    fun loadTweetsViews() {
        isChanged = false
        tweetsFragments.clear()
        tweetsViewTables.forEach { tweetsViewTable ->
            val typeName = tweetsViewTable.typeName

            for (clientUser in ClientUsers.instance.allUser()) {
                val builder = BundleBuilder()
                if (tweetsViewTable.userId == clientUser.userId) {
                    var fragment: TweetsFragment? = null
                    if (TweetsViewType.TIMELINE_HOME.typeName.equals(typeName)) {
                        fragment = HomeTimelineFragment()
                    } else if (TweetsViewType.TIMELINE_MENTIONS.typeName.equals(typeName)) {
                        fragment = MentionsTimelineFragment()
                    } else if (TweetsViewType.LIST_USER.typeName.equals(typeName)) {
                        fragment = UserListFragment()
                        builder.put(UserListFragment.EXTRA_LIST_ID, tweetsViewTable.userListId.toString())
                        builder.put(UserListFragment.EXTRA_LIST_NAME, tweetsViewTable.userListName!!)
                    }
                    if (fragment != null) {
                        builder.put(TweetsFragment.EXTRA_CLIENT_USER, clientUser.screenName)
                        fragment.arguments = builder.build()
                        tweetsFragments.add(fragment)
                    }
                    break
                }
            }
        }
    }

    val tweetsViewTables: ArrayList<TweetsViewTable>
        get() {
            val list = ArrayList<TweetsViewTable>()
            handleDao<TweetsViewTable, String>(Global.instance.applicationContext!!, TweetsViewTable::class.java) {
                list.addAll(it.queryForAll().sortedBy { it.order })
            }
            return list
        }

    fun getTweetsFragments(): List<TweetsFragment> {
        val tweetsFragments = ArrayList<TweetsFragment>()
        tweetsFragments.addAll(this.tweetsFragments)
        return tweetsFragments
    }


    @DatabaseTable(tableName = "TweetsViewTable")
    class TweetsViewTable {
        @DatabaseField(id = true, canBeNull = false)
        var id: String = ""
            private set

        @DatabaseField(canBeNull = false)
        var typeName: String? = ""
            private set

        @DatabaseField(canBeNull = false)
        var userId: Long = 0
            private set

        @DatabaseField(canBeNull = false)
        var clientUserName: String? = ""
            private set

        @DatabaseField(canBeNull = false)
        var order: Int = 0

        @DatabaseField(canBeNull = false)
        var userListId: Long = 0
            private set

        @DatabaseField(canBeNull = true)
        var userListName: String? = ""
            private set

        constructor() {

        }

        constructor(tweetsView: TweetsViewType, clientUser: ClientUser) : this(tweetsView, clientUser, 0, null) {
        }

        constructor(tweetsViewType: TweetsViewType, clientUser: ClientUser, userListId: Long, userListName: String?) {
            typeName = tweetsViewType.typeName
            userId = clientUser.userId
            this.userListId = userListId
            id = createKey(tweetsViewType, clientUser, userListId)
            clientUserName = clientUser.screenName
            Log.v(toString(), "" + clientUserName)
            this.userListName = userListName
        }

        companion object {

            fun createKey(tweetsViewType: TweetsViewType, clientUser: ClientUser, userListId: Long): String {
                return tweetsViewType.id + clientUser.userId.toString() + userListId.toString()
            }

            fun createEmptyTable(): TweetsViewTable {
                val table = TweetsViewTable()
                table.userListName = ""
                table.clientUserName = ""
                table.typeName = ""
                table.userId = 0
                table.userListId = 0
                table.order = 0
                table.id = ""
                return table
            }
        }
    }

    @DatabaseTable(tableName = "homeTweetsViewTable")
    class HomeTweetsViewTable {
        constructor() {

        }

        constructor(tweetsViewId: String) {
            this.tweetsViewId = tweetsViewId
        }

        @DatabaseField(id = true, canBeNull = false)
        val id: String = HomeTweetsViewTable.id
        @DatabaseField(canBeNull = false)
        var tweetsViewId: String = ""
            private set

        companion object {
            val id = "home"
        }
    }

    enum class TweetsViewType internal constructor(
            val displayName: String,
            val typeName: String,
            val id: String) {
        TIMELINE_HOME("Home", "HomeTimeLine", "TIMELINE_HOME"),
        TIMELINE_MENTIONS("Mentions", "Mentions", "TIMELINE_MENTIONS"),
        LIST_USER("List", "List", "LIST_USER")
    }


    companion object {
        val instance: TweetsViewManager = TweetsViewManager()
    }
}
