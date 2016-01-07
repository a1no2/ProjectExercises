package jp.ac.kcg.projectexercises.utill

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import jp.ac.kcg.projectexercises.utill.helper.DataBaseHelper

/**
 * DB操作用のユーティリティクラス
 */

abstract class Register() {
    /**
     *@param context Context
     * @param tablaClass テーブルを表すクラスクラスのオブジェクト
     * @param handle tablaClass で指定したテーブルのDAOのハンドラ
     *
     */
    protected fun <T, ID> handleDao(context: Context, tablaClass: Class<T>, handle: (dao: Dao<T, ID>) -> Unit) {
        var connectionSource: ConnectionSource? = null
        try {
            val helper = DataBaseHelper(context)
            connectionSource = helper.connectionSource
            TableUtils.createTableIfNotExists(connectionSource, tablaClass)
            val dao = helper.getDao<Dao<T, ID>, T>(tablaClass)
            handle(dao)
        } catch (e: SQLException) {
            e.printStackTrace()
            throw RuntimeException(e)
        } finally {
            if (connectionSource != null) {
                try {
                    connectionSource.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
            }
        }
    }
}