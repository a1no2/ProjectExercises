package jp.ac.kcg.projectexercises.color

/**
 * 色を表すインターフェース
 */
interface Color {

    /**
     * 色の情報を取得

     * @return 16進数で表された色例)FFFFFF
     */
    open var colorValue: String

    /**
     * 色のIdを取得する

     * @return 色のId
     */
    val colorId: String

    /**
     * @return intに変換されたColor値を返す
     */
    val color: Int

    val description: String
    /**
     * @param tranceHexValue 16進数で表された透明度
     * *
     * @return intに変換された透明度をもったColor値を返す
     */
    fun getTransColor(transHexValue: String): Int

}
