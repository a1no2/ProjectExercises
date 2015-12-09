package jp.ac.kcg.projectexercises.config

/**
 * 数値的な情報をもつ設定
 */
interface NumericalConfiguration {
    /**
     * 設定のidを取得する

     * @return id
     */
    val id: String

    /**
     * 数値を取得する

     * @return 設定された数値
     */
    /**
     * 数値をセットする

     * @param numericValue 設定する数値
     */

    var numericValue: Int
}
