package jp.ac.kcg.projectexercises.color

import jp.ac.kcg.projectexercises.activites.SubsidiaryActivity

/*
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import jp.ac.kcg.projectexercises.R

import jp.ac.kcg.projectexercises.activites.SubsidiaryActivity
import java.text.SimpleDateFormat
import java.util.*

import kotlinx.android.synthetic.activity_color_manager.*
import kotlinx.android.synthetic.item_tweet.view.*
*/
/**
 * ColorManagerActivity
 */
class ColorManagerActivity : SubsidiaryActivity() {
    /*    private val tempColor = createTempColor()
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_color_manager)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

            init()

        }

        private fun init() {
            red_seek.max = MAX
            green_seek.max = MAX
            blue_seek.max = MAX
            red_edit.addTextChangedListener(createColorEditTextWatcher(red_edit, red_seek))
            green_edit.addTextChangedListener(createColorEditTextWatcher(green_edit, green_seek))
            blue_edit.addTextChangedListener(createColorEditTextWatcher(blue_edit, blue_seek))

            setSeekState(tempColor[ColorStorager.Color.NORMAL_ITEM.colorId]!!)

            initColorSpinner()
            initSampleTweetsViews()

            save_button.setOnClickListener {
                ColorStorager.Color.values().forEach {
                    it.colorValue = tempColor[it.colorId]!!.colorValue
                }
                ColorStorager.instance.saveColorData()
                sendToast(getString(R.string.info_saved))
            }
        }

        private fun initColorSpinner() {
            val adapter = ColorSpinnerAdapter(this, R.layout.support_simple_spinner_dropdown_item)

            ColorStorager.Color.values().forEach {
                adapter.add(tempColor[it.colorId]!!)
            }
            color_spinner.adapter = adapter

            color_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val color = adapter.getItem(position)
                    setSeekState(color)
                }
            }
        }

        private fun initSampleTweetView(view: View, text: String, screenName: String, name: String, color: Int) {
            val textSize = (ConfigurationStorager.NumericalConfigurations.CHARACTER_SIZE.numericValue + 8)
                    .toFloat()
            view.icon_image.setImageDrawable(resources.getDrawable(R.drawable.ic_launcher))
            view.main_text.text = text
            view.source_text.text = "via numeri for Android"
            view.created_date_text.text = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date())
            view.screen_name_text.textSize = textSize * 0.9.toFloat()
            view.user_name_text.textSize = textSize * 0.8.toFloat()
            view.main_text.textSize = textSize
            view.source_text.textSize = textSize * 0.7.toFloat()
            view.created_date_text!!.textSize = textSize * 0.7.toFloat()
            view.retweet_info_text.textSize = textSize * 0.9.toFloat()
            view.logo.setImageDrawable(resources.getDrawable(ResourceIds.ic_twitter_logo))
            val linkColor = ColorStorager.Color.LINK.color
            view.user_name_text.text = SpannableStringBuilder(this, name)
                    .setClickablePattern(name, true) { text -> }.build()
            view.user_name_text.setLinkTextColor(linkColor)
            view.user_name_text.setOnTouchListener(SimpleTextViewTouchListener())
            view.screen_name_text.text = SpannableStringBuilder(this, screenName)
                    .setClickablePattern(screenName, true) { text -> }.build()
            view.screen_name_text.setOnTouchListener(SimpleTextViewTouchListener())
            view.screen_name_text.setLinkTextColor(linkColor)
            view.main_text.text = SpannableStringBuilder(this, text + " http://www.hoge.com/hogehoge")
                    .setClickablePattern("http://www.hoge.com/hogehoge", true) { text -> }
                    .setClickablePattern("@hogehoge") { text -> }.build()
            view.main_text.setLinkTextColor(linkColor)
            view.main_text.setOnTouchListener(SimpleTextViewTouchListener())
            view.main_text.setTextColor(ColorStorager.Color.CHARACTER.color)
            view.created_date_text.setTextColor(ColorStorager.Color.CHARACTER.color)
            view.source_text.setTextColor(ColorStorager.Color.CHARACTER.color)
            view.retweet_info_text.setTextColor(ColorStorager.Color.CHARACTER.color)
            view.overlay_linear.setBackgroundColor(color)
        }

        private fun initSampleTweetsViews() {
            initSampleTweetView(tweet1, "普通のツイート", "hogehoge", "ほげらりおん", ColorStorager.Color.NORMAL_ITEM.getTransColor("BB"))
            tweet1.is_my_tweet_state_view.setBackgroundColor(ColorStorager.Color.MYTWEET_MARK.color)

            initSampleTweetView(tweet2, "RTされたツイート", "hogehoge", "ほげらりおん", ColorStorager.Color.RT_ITEM.getTransColor("BB"))
            tweet2.retweet_info_text.text = SpannableStringBuilder("fuga" + getString(R.string.info_retweet))
                    .setClickablePattern("fuga") { text -> }.build()
            tweet2.retweet_info_text.setLinkTextColor(ColorStorager.Color.LINK.color)
            tweet2.retweet_info_text.visibility = View.VISIBLE

            initSampleTweetView(tweet3, "@hogehoge 自分宛てのツイート", "fuga", "ふがぐりおん", ColorStorager.Color.MENTION_ITEM.getTransColor("BB"))
        }

        private fun setSeekState(color: Color) {
            val rgbColor = purseToDecRgbValue(color.colorValue)

            red_seek.setOnSeekBarChangeListener(createOnSeekBarChangeListener { seekBar, progress, fromUser ->
                color.colorValue = purseToHexRgbValue(progress, green_seek.progress, blue_seek.progress)
                red_edit.setText(progress.toString())
                setColor(color)
            })

            green_seek.setOnSeekBarChangeListener(createOnSeekBarChangeListener { seekBar, progress, fromUser ->
                color.colorValue = purseToHexRgbValue(red_seek.progress, progress, blue_seek.progress)
                green_edit.setText(progress.toString())
                setColor(color)
            })

            blue_seek.setOnSeekBarChangeListener(createOnSeekBarChangeListener { seekBar, progress, fromUser ->
                color.colorValue = purseToHexRgbValue(red_seek.progress, green_seek.progress, progress)
                blue_edit.setText(progress.toString())
                setColor(color)
            })

            red_seek.progress = rgbColor[0]
            green_seek.progress = rgbColor[1]
            blue_seek.progress = rgbColor[2]

            red_edit.setText(rgbColor[0].toString())
            green_edit.setText(rgbColor[1].toString())
            blue_edit.setText(rgbColor[2].toString())
        }

        private fun setColor(color: Color) {
            when (color.colorId) {
                ColorStorager.Color.NORMAL_ITEM.colorId -> {
                    tweet1.overlay_linear.setBackgroundColor(color.getTransColor("BB"))
                }

                ColorStorager.Color.RT_ITEM.colorId -> {
                    tweet2.overlay_linear.setBackgroundColor(color.getTransColor("BB"))
                }

                ColorStorager.Color.MENTION_ITEM.colorId -> {
                    tweet3.overlay_linear.setBackgroundColor(color.getTransColor("BB"))
                }

                ColorStorager.Color.MYTWEET_MARK.colorId -> {
                    tweet1.is_my_tweet_state_view.setBackgroundColor(color.color)
                }

                ColorStorager.Color.CHARACTER.colorId -> {
                    tweet1.created_date_text.setTextColor(color.color)
                    tweet2.created_date_text.setTextColor(color.color)
                    tweet3.created_date_text.setTextColor(color.color)

                    tweet1.main_text.setTextColor(color.color)
                    tweet2.main_text.setTextColor(color.color)
                    tweet3.main_text.setTextColor(color.color)

                    tweet1.source_text.setTextColor(color.color)
                    tweet2.source_text.setTextColor(color.color)
                    tweet3.source_text.setTextColor(color.color)

                    tweet2.retweet_info_text.setTextColor(color.color)
                }

                ColorStorager.Color.LINK.colorId -> {
                    tweet1.screen_name_text.setLinkTextColor(color.color)
                    tweet2.screen_name_text.setLinkTextColor(color.color)
                    tweet3.screen_name_text.setLinkTextColor(color.color)

                    tweet1.user_name_text.setLinkTextColor(color.color)
                    tweet2.user_name_text.setLinkTextColor(color.color)
                    tweet3.user_name_text.setLinkTextColor(color.color)

                    tweet1.main_text.setLinkTextColor(color.color)
                    tweet2.main_text.setLinkTextColor(color.color)
                    tweet3.main_text.setLinkTextColor(color.color)

                    tweet2.retweet_info_text.setLinkTextColor(color.color)
                }
            }
        }


        private fun createColorEditTextWatcher(editText: EditText, seekBar: SeekBar): TextWatcher {
            return object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    try {
                        val num = s.toString().toInt()
                        if (num < 0) {
                            editText.setText(0.toString())
                            seekBar.progress = 0
                        } else if (num > 255) {
                            editText.setText(255.toString())
                            seekBar.progress = 255
                        } else {
                            seekBar.progress = num
                        }
                        editText.setSelection(editText.text.length)
                    } catch(e: NumberFormatException) {
                        editText.setText(0.toString())
                        seekBar.progress = 0
                        editText.setSelection(editText.text.length)
                    }
                }
            }
        }

        private fun createOnSeekBarChangeListener(onProgressChanged: (seekBar: SeekBar, progress: Int, fromUser: Boolean) -> Unit): SeekBar.OnSeekBarChangeListener {
            return object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    onProgressChanged(seekBar, progress, fromUser)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            }
        }

        companion object {
            private val MAX = 255;

            private fun purseToHexRgbValue(r: Int, g: Int, b: Int): String {
                val hex = ArrayList<String>()
                hex.add(Integer.toHexString(r))
                hex.add(Integer.toHexString(g))
                hex.add(Integer.toHexString(b))
                var hexRgbValue = ""


                for (s in hex) {
                    if (s.length.equals(1)) {
                        hexRgbValue += "0" + s
                    } else {
                        hexRgbValue += s
                    }
                }

                return hexRgbValue
            }

            private fun purseToDecRgbValue(hexRgbValue: String): List<Int> {
                val rgbValue = ArrayList<Int>()
                Log.v(toString(), hexRgbValue)
                rgbValue.add(Integer.parseInt(hexRgbValue.substring(0, 2), 16))
                rgbValue.add(Integer.parseInt(hexRgbValue.substring(2, 4), 16))
                rgbValue.add(Integer.parseInt(hexRgbValue.substring(4), 16))
                return rgbValue
            }

            private fun createTempColor(): HashMap<String, Color> {
                val colorMap = HashMap<String, Color>()
                ColorStorager.Color.values.forEach {
                    val color = object : Color {
                        override var colorValue: String = it.colorValue

                        override val colorId: String = it.colorId

                        override val color: Int
                            get() = android.graphics.Color.parseColor("#" + colorValue)

                        override val description: String = it.description

                        override fun getTransColor(transHexValue: String): Int {
                            return android.graphics.Color.parseColor("#" + transHexValue + colorValue)
                        }

                        override fun toString(): String {
                            return description
                        }

                    }
                    colorMap.put(color.colorId, color)
                }
                return colorMap
            }
        }

        private class ColorSpinnerAdapter(context: Context, resource: Int) : ArrayAdapter<Color>(context, resource) {

            private val inflater: LayoutInflater
            private val resource = R.layout.support_simple_spinner_dropdown_item

            init {
                inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var view = convertView
                if (view == null) {
                    view = inflater.inflate(resource, null)
                }
                (view as TextView).text = (getItem(position).description)
                return view
            }

        }*/
}
