package jp.ac.kcg.projectexercises.config

/**
 * どちらかを選択するような2値的な設定のインターフェース
 */
interface EitherConfiguration {
    /**
     * 設定のidを取得する

     * @return id
     */
    val id: String

    /**
     * 有効か無効かを取得する

     * @return true : 有効false : 無効
     */
    /**
     * 有効化無効化をセットする

     * @param enabled true : 有効false : 無効
     */
    var isEnabled: Boolean

}
