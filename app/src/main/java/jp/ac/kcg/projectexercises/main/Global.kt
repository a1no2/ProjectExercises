package jp.ac.kcg.projectexercises.main

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

/**
 */
/**
 * Applicationのフィールド的な役割を持つクラス
 */
class Global private constructor() {

    public var applicationContext: Application? = null
        internal set
    public var mainActivityContext: MainActivity? = null
        internal set

    /**
     * 現在のMainActivityを殺す
     */
    fun destroyMainActivity() {
        if (mainActivityContext != null && isActiveMainActivity) {
            mainActivityContext!!.finish()
        }
    }

    /**
     * 今のMainActivityが生きているかどうか

     * @return true : 生きている false : 死んでいる
     */
    val isActiveMainActivity: Boolean
        get() = mainActivityContext == null || !(mainActivityContext!!.isFinishing)


    companion object {
        val instance = Global()
    }
}


fun getWindowSize(context: Context): Point {
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val size = Point()
    wm.defaultDisplay.getSize(size)
    return size
}