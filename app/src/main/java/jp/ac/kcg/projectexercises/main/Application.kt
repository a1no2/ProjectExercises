package jp.ac.kcg.projectexercises.main

import jp.ac.kcg.projectexercises.color.ColorRegister
import jp.ac.kcg.projectexercises.config.ConfigurationRegister
import jp.ac.kcg.projectexercises.twitter.tweet.action.ActionStorager

/**
 * Application
 */
final class Application() : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        Global.instance.applicationContext = this
        ColorRegister.instance.loadColor()
        ConfigurationRegister.instance.loadConfigurations()
        ActionStorager.instance.initializeActions()
    }
}
