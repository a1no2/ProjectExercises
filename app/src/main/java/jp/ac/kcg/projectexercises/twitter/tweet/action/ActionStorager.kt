package jp.ac.kcg.projectexercises.twitter.tweet.action

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import jp.ac.kcg.projectexercises.main.Global
import jp.ac.kcg.projectexercises.utill.Register


/**
 * ツイートに対するアクションについての設定を保存したり読み込むためのクラス
 */
class ActionStorager private constructor() : Register() {

    /**
     * 現在のアクションに設定を保存する
     */
    fun saveActions() {
        handleDao<ActionTable, String>(Global.instance.applicationContext!!, ActionTable::class.java) { dao ->
            RespectTapPositionAction.values().forEach {
                dao.createOrUpdate(ActionTable(it.id, it.twitterAction!!.id!!))
            }
        }
    }

    /**
     * アクションについての設定を読み込み初期化する
     */
    fun initializeActions() {
        handleDao<ActionTable, String>(Global.instance.applicationContext!!, ActionTable::class.java) { dao ->
            RespectTapPositionAction.values().forEach {
                val table = dao.queryForId(it.id)
                if (table != null) {
                    if (!it.setTwitterActionForId(table.twitterActionId)) {
                        it.twitterAction = TwitterActions.Action.ACTION_NONE
                    }
                }
            }
        }
    }


    /**
     * アクションについての設定の情報を保存するテーブルクラス
     */
    @DatabaseTable(tableName = "actionTable")
    class ActionTable {
        @DatabaseField(canBeNull = false, id = true)
        var actionId: String = ""
            private set
        @DatabaseField(canBeNull = false)
        var twitterActionId: String = ""
            private set

        constructor() {

        }

        constructor(actionId: String, twitterActionId: String) {
            this.actionId = actionId
            this.twitterActionId = twitterActionId
        }
    }


    /**
     * タップした位置に対するアクションを保存する列挙型
     */
    enum class RespectTapPositionAction internal
    constructor(val id: String,
                defaultAction: TwitterActions.Action) {
        RIGHT("RIGHT", TwitterActions.Action.REPLY),
        CENTER("CENTER", TwitterActions.Action.CONFIRM_RT),
        LEFT("LEFT", TwitterActions.Action.CONFIRM_FAVORITE),
        LONG_RIGHT("LONG_RIGHT", TwitterActions.Action.MENU),
        LONG_CENTER("LONG_CENTER", TwitterActions.Action.OPEN_USER_PROFILE),
        LONG_LEFT("LONG_LEFT", TwitterActions.Action.QT);

        public var twitterAction: TwitterActions.Action? = null

        init {
            twitterAction = defaultAction
        }

        /**
         * twitterActionをセット

         * @param twitterActionId TwitterActionID
         * *
         * @return true:成功 false 失敗
         */
        fun setTwitterActionForId(twitterActionId: String): Boolean {
            var success = false
            for (action in TwitterActions.Action.values) {
                if (twitterActionId == action.id) {
                    this.twitterAction = action
                    success = true
                    break
                }
            }
            return success
        }
    }

    companion object {
        val instance: ActionStorager = ActionStorager()
    }
}
