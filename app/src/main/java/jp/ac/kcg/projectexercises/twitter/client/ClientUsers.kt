package jp.ac.kcg.projectexercises.twitter.client

import android.os.Handler
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import jp.ac.kcg.projectexercises.main.Global
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager.TweetsViewManager
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager.TweetsViewManagerActivity
import jp.ac.kcg.projectexercises.utill.Register
import twitter4j.TwitterException
import twitter4j.auth.AccessToken
import java.util.*
import java.util.concurrent.Executors

/**
 * ClientUsers
 */
final class ClientUsers private constructor() : Register() {
    private val threadPool = Executors.newCachedThreadPool()
    var onDeleteListener: (ClientUser) -> Unit = {}

    private var clientUsers = ArrayList<ClientUser>()
    private var loadCalled = false
    private var loaded = false

    fun loadUsers(loadCompleted: (List<ClientUser>, success: Boolean) -> Unit) {
        if (!loadCalled) {
            val context = Global.instance.applicationContext!!.applicationContext
            loadCalled = true
            handleDao<ClientUserTable, String>(context, ClientUserTable::class.java) {
                val tables = it.queryForAll()
                if (tables == null || tables.isEmpty()) {
                    loadCompleted(ArrayList<ClientUser>(), true)
                    loaded = true
                    return@handleDao
                }

                val handler = Handler()
                threadPool.execute {
                    try {
                        tables.forEach { table ->
                            val accessToken = AccessToken(table.authToken, table.authTokenSecret)
                            clientUsers.add(ClientUser(context, accessToken))
                        }
                        handler.post { confirmationLoadComplete(tables, loadCompleted, true) }
                    } catch(e: TwitterException) {
                        handler.post { confirmationLoadComplete(tables, loadCompleted, false) }
                    }
                }
            }

        } else if (!loaded) {
            loadCompletedListeners.add(loadCompleted)
        } else {
            loadCompleted(ArrayList(clientUsers), true);
        }
    }

    fun allUser(): List<ClientUser> {
        if (!loaded)
            throw IllegalStateException("loadUsers()が呼ばれていない")
        val clientUsers = ArrayList<ClientUser>()
        clientUsers.addAll(this.clientUsers)
        return clientUsers
    }

    internal fun addClientUser(accessToken: AccessToken, added: (clientUser: ClientUser?, success: Boolean) -> Unit) {
        val context = Global.instance.applicationContext!!.applicationContext
        handleDao<ClientUserTable, String>(context, ClientUserTable::class.java) {
            val table = it.queryForId(accessToken.userId.toString())
            if (table == null) {
                val table1 = ClientUserTable(accessToken)
                it.createOrUpdate(table1)
                val handler = Handler()
                threadPool.execute {
                    try {
                        val user = ClientUser(context, accessToken)
                        clientUsers.add(user)
                        handler.post { added(user, true) }

                    } catch(e: TwitterException) {
                        e.printStackTrace()
                        handler.post { added(null, false) }
                    }
                }
            } else {
                added(null, false)
            }
        }
    }

    fun deleteClientUser(clientUser: ClientUser) {
        val context = Global.instance.applicationContext!!.applicationContext
        handleDao<ClientUserTable, String>(context, ClientUserTable::class.java) {
            it.delete(ClientUserTable(clientUser))
            onDeleteListener(clientUser)
            TweetsViewManager.instance.deleteTweetsViewByClientUser(clientUser)
            clientUsers.remove(clientUser)
        }
    }

    fun getClientUser(screenName: String): ClientUser? {
        if (!loaded)
            throw IllegalStateException("loadUsers()が呼ばれていない")
        for (clientUser in clientUsers) {
            if (clientUser.screenName == screenName)
                return clientUser
        }
        return null
    }

    fun size(): Int {
        var size: Int = 0
        val context = Global.instance.applicationContext!!.applicationContext
        handleDao<ClientUserTable, String>(context, ClientUserTable::class.java) {
            val tables = it.queryForAll()
            size = tables.size
        }
        return size
    }


    private fun confirmationLoadComplete(tables: List<ClientUserTable>, loadCompleted: (List<ClientUser>, Boolean) -> Unit, success: Boolean) {
        if (clientUsers.size == tables.size) {
            loaded = true
            val clientUsers = ArrayList<ClientUser>()
            clientUsers.addAll(this.clientUsers)
            loadCompleted(clientUsers, success)
            loadCompletedListeners.forEach { it(clientUsers, success) }
            loadCompletedListeners.clear()
        }
    }

    companion object {
        private val loadCompletedListeners = ArrayList<(List<ClientUser>, Boolean) -> Unit>()
        val instance = ClientUsers()
    }

    @DatabaseTable(tableName = "ClientUserTable")
    class ClientUserTable {
        @DatabaseField(canBeNull = false, id = true)
        var userId: Long = 0
            private set
        @DatabaseField(canBeNull = false)
        var authToken: String? = null
            private set
        @DatabaseField(canBeNull = false)
        var authTokenSecret: String? = null
            private set

        constructor() {
        }

        constructor(accessToken: AccessToken) {
            userId = accessToken.userId
            authToken = accessToken.token
            authTokenSecret = accessToken.tokenSecret
        }

        constructor(clientUser: ClientUser) {
            userId = clientUser.userId
            authToken = clientUser.accessToken.token
            authTokenSecret = clientUser.accessToken.tokenSecret
        }
    }

}
