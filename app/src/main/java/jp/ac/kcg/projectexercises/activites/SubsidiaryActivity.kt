package jp.ac.kcg.projectexercises.activites

import android.content.Intent
import android.view.KeyEvent
import android.view.MenuItem
import jp.ac.kcg.projectexercises.main.Global

import jp.ac.kcg.projectexercises.main.MainActivity

/**
 * Main画面以外が継承すべきActivity
 */
open class SubsidiaryActivity : ApplicationActivity() {

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!Global.instance.isActiveMainActivity) {
                startActivity(MainActivity::class.java, true)
            } else {
                super.supportFinishAfterTransition()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                val intent = Intent(this@SubsidiaryActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
