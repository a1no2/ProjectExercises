package jp.ac.kcg.projectexercises.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import jp.ac.kcg.projectexercises.R
import jp.ac.kcg.projectexercises.activites.ApplicationActivity
import jp.ac.kcg.projectexercises.config.ConfigActivity
import jp.ac.kcg.projectexercises.twitter.client.ClientUsers
import jp.ac.kcg.projectexercises.twitter.client.ClientUsersManagerActivity
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.TweetsFragmentPagerAdapter
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager.TweetsViewManager
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager.TweetsViewManagerActivity

import kotlinx.android.synthetic.activity_main.*

final class MainActivity : ApplicationActivity() {
    private var adapter: TweetsFragmentPagerAdapter? = null
    private var loaded = false
    private var home = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Global.instance.mainActivityContext = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ClientUsers.instance.size() == 0) {
            startActivity(ClientUsersManagerActivity::class.java, true)
            return
        }
        ClientUsers.instance.loadUsers { clientUsers, success ->
            var s = ""
            if ((!success)) {
                sendToast("ユーザー情報の取得に失敗しました\n",
                        Toast.LENGTH_LONG)
                return@loadUsers
            }
            if (!clientUsers.isEmpty()) {
                for (clientUser in clientUsers) {
                    s += clientUser.name + "\n"
                }
                sendToast(s + "認証")
                initComponent()
                loaded = true
            }
        }


    }

    private fun initComponent() {
        start_tweet_button.isEnabled = true
        adapter = TweetsFragmentPagerAdapter(supportFragmentManager)
        pager.offscreenPageLimit = 30
        pager_tab_strip.drawFullUnderline = true
        TweetsViewManager.instance.loadTweetsViews()
        val tweetsFragments = TweetsViewManager.instance.getTweetsFragments()

        adapter!!.addAll(tweetsFragments)
        pager.adapter = adapter
        if (tweetsFragments.size == 0) {
            info_text.text = getString(R.string.info_tweets_view_management_tutorial)
            info_text.visibility = View.VISIBLE
        } else {
            home = TweetsViewManager.instance.homeTweetsViewIndex
            pager.currentItem = if (tweetsFragments.size > home) home else 0
        }
    }

    override fun onResume() {
        super.onResume()
        ClientUsers.instance.loadUsers { clientUsers, success ->
            if (clientUsers.isEmpty()) {
                startActivity(ClientUsersManagerActivity::class.java, true)
            } else {
                home = TweetsViewManager.instance.homeTweetsViewIndex
                loaded = true
                start_tweet_button.isEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> pager.currentItem = home

            R.id.action_account -> startActivity(ClientUsersManagerActivity::class.java, false)

            R.id.action_setting -> startActivity(ConfigActivity::class.java, false)

            R.id.action_tweetsview_manage -> startActivity(TweetsViewManagerActivity::class.java, false)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> moveTaskToBack(true)
            else -> {
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun finish() {
        if (pager != null && adapter != null) {
            pager!!.adapter = null
            adapter!!.clear()
        }
        super.finish()
    }
}
