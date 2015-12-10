package jp.ac.kcg.projectexercises.config

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import jp.ac.kcg.projectexercises.R
import jp.ac.kcg.projectexercises.activites.SubsidiaryActivity
import jp.ac.kcg.projectexercises.twitter.tweet.action.ActionStorager
import jp.ac.kcg.projectexercises.twitter.tweet.action.TwitterActions

import kotlinx.android.synthetic.activity_config.*
import java.util.*

/**
 * アプリケーションの設定についてのActivity
 */
class ConfigActivity : SubsidiaryActivity() {
    private var textColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        textColor = resources.getColor(R.color.text_black)
        initActionConfComponent()
        initLayoutConfComponent()
    }

    private fun initActionConfComponent() {
        open_action_menu_button.setTextColor(textColor)
        tweet_additional_acquisition_flag_text.setTextColor(textColor)
        sleepless_text.setTextColor(textColor)
        use_high_resolutionIcon_text.setTextColor(textColor)
        fast_scroll_text.setTextColor(textColor)
        for (i in 0..action_menu.childCount - 1) {
            (action_menu.getChildAt(i) as Button).setTextColor(textColor)
        }


        open_action_menu_button.setOnClickListener { v ->
            if (action_menu.visibility == View.GONE) {
                action_menu.visibility = View.VISIBLE
            } else if (action_menu.visibility == View.VISIBLE) {
                action_menu.visibility = View.GONE
            }
        }


        right_tap_conf_button.setOnClickListener { v -> chooseAction(ActionStorager.RespectTapPositionAction.RIGHT, (v as Button).text) }
        center_tap_conf_button.setOnClickListener { v -> chooseAction(ActionStorager.RespectTapPositionAction.CENTER, (v as Button).text) }
        left_tap_conf_button.setOnClickListener { v -> chooseAction(ActionStorager.RespectTapPositionAction.LEFT, (v as Button).text) }
        right_long_tap_conf_button.setOnClickListener { v -> chooseAction(ActionStorager.RespectTapPositionAction.LONG_RIGHT, (v as Button).text) }
        center_long_tap_conf_button.setOnClickListener { v -> chooseAction(ActionStorager.RespectTapPositionAction.LONG_CENTER, (v as Button).text) }
        left_long_tap_conf_button.setOnClickListener { v -> chooseAction(ActionStorager.RespectTapPositionAction.LONG_LEFT, (v as Button).text) }

        initChooseEitherAction(sleepless_text, sleepless_check_box, ConfigurationRegister.EitherConfigurations.SLEEPLESS)

        initChooseEitherAction(tweet_additional_acquisition_flag_text, confirmation_less_get_tweet_check_box,
                ConfigurationRegister.EitherConfigurations.CONFIRMATION_LESS_GET_TWEET)

        initChooseEitherAction(use_high_resolutionIcon_text, use_high_resolution_icon_check_box,
                ConfigurationRegister.EitherConfigurations.USE_HIGH_RESOLUTION_ICON)

        initChooseEitherAction(fast_scroll_text, fast_scroll_check_box,
                ConfigurationRegister.EitherConfigurations.USE_FAST_SCROLL)

    }

    private fun initChooseEitherAction(textView: TextView, checkBox: CheckBox, eitherConfiguration: ConfigurationRegister.EitherConfigurations) {
        checkBox.isChecked = eitherConfiguration.isEnabled
        checkBox.setOnClickListener { v -> chooseEither(eitherConfiguration, v as CheckBox) }
        textView.setOnClickListener { v -> chooseEither(eitherConfiguration, checkBox) }
    }

    private fun initLayoutConfComponent() {
        choose_char_size_button.setTextColor(textColor)
        //onCLick
        choose_char_size_button.setOnClickListener { v -> chooseTextSize() }
    }

    private fun chooseTextSize() {
        val textSizes = arrayOfNulls<CharSequence>(15)
        for (i in textSizes.indices) {
            textSizes[i] = "" + (i + 8)
        }
        val checkedItem = ConfigurationRegister.NumericalConfigurations.CHARACTER_SIZE.numericValue
        val alertDialog = AlertDialog.Builder(this).setSingleChoiceItems(textSizes, checkedItem) { dialog, witch ->
            ConfigurationRegister.NumericalConfigurations.CHARACTER_SIZE.numericValue = witch
            ConfigurationRegister.instance.saveNumericalConfigTable(ConfigurationRegister.NumericalConfigurations.CHARACTER_SIZE)
            (dialog as AlertDialog).hide()
            dialog.dismiss()
        }.create()
        showDialog(alertDialog)
    }

    private fun chooseAction(respectTapPositionAction: ActionStorager.RespectTapPositionAction, title: CharSequence) {
        val actionTexts = ArrayList<CharSequence>()
        val actions = ArrayList<TwitterActions.Action>()
        for (action in TwitterActions.Action.values()) {
            if (action.isTouchAction) {
                actionTexts.add(action.actionName)
                actions.add(action)
            }
        }
        var position = 0
        for (action in actions) {
            if (action === respectTapPositionAction.twitterAction) {
                break
            }
            position++
        }
        val alertDialog = AlertDialog.Builder(this).setTitle(title).setSingleChoiceItems(actionTexts.toArray<CharSequence>(arrayOfNulls<CharSequence>(actionTexts.size)),
                position) { dialog, which ->
            respectTapPositionAction.twitterAction = actions[which]
            AsyncTask.execute { ActionStorager.instance.saveActions() }
            (dialog as AlertDialog).hide()
            dialog.dismiss()
        }.create()
        showDialog(alertDialog)
    }

    private fun chooseEither(eitherConfigurations: ConfigurationRegister.EitherConfigurations, checkBox: CheckBox) {
        eitherConfigurations.isEnabled = !eitherConfigurations.isEnabled
        checkBox.isChecked = eitherConfigurations.isEnabled
        ConfigurationRegister.instance.saveEitherConfigTable(eitherConfigurations)
    }

}
