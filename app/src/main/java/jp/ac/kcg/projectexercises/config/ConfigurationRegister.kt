package jp.ac.kcg.projectexercises.config

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import jp.ac.kcg.projectexercises.main.Global
import jp.ac.kcg.projectexercises.utill.Register


/**
 * 設定を保存したり読み出したりするためのクラス
 */
class ConfigurationRegister private constructor() : Register() {

    /**
     * 数値的な情報をもつ設定を保存する

     * @param numericalConfiguration numericalConfiguration
     */
    fun saveNumericalConfigTable(numericalConfiguration: NumericalConfiguration) {
        handleDao<NumericalConfigTable, String>(Global.instance.applicationContext!!, NumericalConfigTable::class.java) {
            val table = NumericalConfigTable(numericalConfiguration)
            it.createOrUpdate(table)
        }
    }

    /**
     * どちらかを選ぶ2値的な情報を持つ設定を保存する

     * @param eitherConfiguration eitherConfiguration
     */
    fun saveEitherConfigTable(eitherConfiguration: EitherConfiguration) {

        handleDao<EitherConfigTable, String>(Global.instance.applicationContext!!, EitherConfigTable::class.java) {
            val table = EitherConfigTable(eitherConfiguration)
            it.createOrUpdate(table)
        }
    }

    /**
     * アプリケーションの設定をすべて読み込む
     */
    fun loadConfigurations() {
        handleDao<EitherConfigTable, String>(Global.instance.applicationContext!!, EitherConfigTable::class.java) { dao ->
            EitherConfigurations.values().forEach {
                val table = dao.queryForId(it.id)
                if (table != null) {
                    it.isEnabled = table.enabled
                }
            }
        }

        handleDao<NumericalConfigTable, String>(Global.instance.applicationContext!!, NumericalConfigTable::class.java) { dao ->
            NumericalConfigurations.values().forEach {
                val table = dao.queryForId(it.id)
                if (table != null) {
                    it.numericValue = table.numericValue
                }
            }
        }

    }

    /**
     * どちらかを選ぶ2値的な情報を持つ設定を保存するテーブル
     */
    @DatabaseTable(tableName = "eitherConfig")
    class EitherConfigTable {
        internal constructor() {

        }

        @DatabaseField(canBeNull = false, id = true)
        var id: String = ""
            private set
        @DatabaseField(canBeNull = false)
        var enabled: Boolean = false
            private set

        internal constructor(eitherConfiguration: EitherConfiguration) {
            this.id = eitherConfiguration.id
            this.enabled = eitherConfiguration.isEnabled
        }

    }

    /**
     * 数値的な情報をもつ設定を保存するテーブル
     */
    @DatabaseTable(tableName = "numericalConfig")
    class NumericalConfigTable {
        internal constructor() {

        }

        @DatabaseField(canBeNull = false, id = true)
        var id: String = ""
            private set
        @DatabaseField(canBeNull = false)
        var numericValue: Int = 0
            private set

        internal constructor(numericalConfiguration: NumericalConfiguration) {
            this.id = numericalConfiguration.id
            this.numericValue = numericalConfiguration.numericValue
        }

    }


    /**
     * どちらかを選択するような設定の情報を保持する列挙型
     */
    enum class EitherConfigurations internal constructor(override val id: String, defaultValue: Boolean) : EitherConfiguration {
        DARK_THEME("DARK_THEME", false),
        SLEEPLESS("SLEEPLESS", false),
        CONFIRMATION_LESS_GET_TWEET("CONFIRMATION_LESS_GET_TWEET", false),
        USE_HIGH_RESOLUTION_ICON("USE_HIGH_RESOLUTION_ICON", true),
        USE_FAST_SCROLL("USE_FAST_SCROLL", false);
        override var isEnabled = false

        init {
            isEnabled = defaultValue
        }
    }

    /**
     * 数値的な値を持つ設定を保持する列挙型
     */
    enum class NumericalConfigurations internal constructor(override val id: String, override var numericValue: Int) : NumericalConfiguration {
        CHARACTER_SIZE("CHARACTER_SIZE", 4)
    }

    companion object {
        val instance: ConfigurationRegister = ConfigurationRegister()
    }
}
