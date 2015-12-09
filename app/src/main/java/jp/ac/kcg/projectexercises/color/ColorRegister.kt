package jp.ac.kcg.projectexercises.color


import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import jp.ac.kcg.projectexercises.main.Global
import jp.ac.kcg.projectexercises.utill.Register
import java.util.*


/**
 * 色をローカルに保存したり読み込んだりするためのクラス
 */
class ColorRegister private constructor() : Register() {

    private val onColorChangeCallbacks: ArrayList<() -> Unit> = ArrayList()

    fun addOnColorChangeCallback(callback: () -> Unit) {
        onColorChangeCallbacks.add(callback)
    }

    fun removeOnColorChangeCallback(callback: () -> Unit) {
        onColorChangeCallbacks.remove(callback)
    }

    /**
     * 現在設定されている色情報をすべて保存する
     */
    fun saveColorData() {
        handleDao<ColorTable, String>(Global.instance.applicationContext!!, ColorTable::class.java) { dao ->
            Color.values().forEach {
                val data = ColorTable(it)
                dao.createOrUpdate(data)
            }
        }
        onColorChangeCallbacks.forEach { it() }
    }

    /**
     * 色設定をローカルから読み込む。
     * 任意のタイミングで呼び出し。
     */
    fun loadColor() {
        handleDao<ColorTable, String>(Global.instance.applicationContext!!, ColorTable::class.java) { dao ->
            Color.values().forEach {
                val colorData = dao.queryForId(it.colorId)
                if (colorData != null)
                    it.colorValue = colorData.color
            }
        }
    }

    /**
     * 色の情報を保存するテーブル
     */
    @DatabaseTable(tableName = "ColorData")
    class ColorTable {
        constructor() {

        }

        constructor(color: Color) {
            this.colorId = color.colorId
            this.color = color.colorValue
            val matcher = "([0-9]|[a-f]|[A-F]){6}"
            if (!this.color.matches(matcher.toRegex()))
                throw  IllegalArgumentException("")
        }

        @DatabaseField(canBeNull = false, id = true)
        private var colorId: String = ""
        @DatabaseField(canBeNull = false)
        var color: String = "FFFFFF"
            private set
    }

    /**
     * 色を保存する列挙型
     */
    enum class Color internal constructor(override val colorId: String, override val description: String, override var colorValue: String) : jp.ac.kcg.projectexercises.color.Color {
        NORMAL_ITEM("NORMAL\nITEM", "普通のツイート", "FFFFFF"),
        RT_ITEM("RT\nITEM", "RTされたツイート", "3769CA"),
        MENTION_ITEM("MENTION\nITEM", "自分宛てのツイート", "FF2E00"),
        MYTWEET_MARK("MYTWEET\nMARK", "自分のツイートのマーク", "88EC2F"),
        CHARACTER("CHARACTER", "ツイートの文字の色", "000000"),
        LINK("LINK", "リンクの色", "5DFFFF");

        override val color: Int
            get() = android.graphics.Color.parseColor("#" + colorValue)

        override fun getTransColor(transHexValue: String): Int {
            val matcher = "([0-9]|[a-f]|[A-F]){2}"
            if (!transHexValue.matches(matcher.toRegex()))
                throw IllegalArgumentException("与えられた値が16進数でない")
            return android.graphics.Color.parseColor("#" + transHexValue + colorValue)
        }
    }

    companion object {
        val instance: ColorRegister = ColorRegister()
    }

}
