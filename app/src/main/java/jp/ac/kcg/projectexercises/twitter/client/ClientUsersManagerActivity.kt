package jp.ac.kcg.projectexercises.twitter.client

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle

import jp.ac.kcg.projectexercises.R
import jp.ac.kcg.projectexercises.activites.SubsidiaryActivity
import jp.ac.kcg.projectexercises.main.Global
import jp.ac.kcg.projectexercises.main.MainActivity
import jp.ac.kcg.projectexercises.twitter.tweet.fragment.manager.TweetsViewManager

import java.util.ArrayList

import kotlinx.android.synthetic.activity_oauth.*
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.auth.OAuthAuthorization
import twitter4j.auth.RequestToken
import twitter4j.conf.ConfigurationContext

/**
 * 認証用の画面
 */
final class ClientUsersManagerActivity : SubsidiaryActivity() {
    private var adapter: ClientUserListAdapter? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)
        adapter = ClientUserListAdapter(this, 0, ArrayList())
        client_user_list.adapter = adapter
        ClientUsers.instance.loadUsers { users, success ->
            if (success)
                adapter!!.addAll(users)
            else
                sendToast("失敗しました")
        }

        ClientUsers.instance.onDeleteListener = {
            adapter!!.remove(it)
        }

        twitter_login_button.setOnClickListener { oauthStart() }
    }

    private var oauth: OAuthAuthorization? = null
    private var resultToken: RequestToken? = null
    private fun oauthStart() {
        object : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void?): String? {
                try {
                    val configuration = ConfigurationContext.getInstance()
                    oauth = OAuthAuthorization(configuration)
                    oauth!!.setOAuthConsumer(getString(R.string.api_key), getString(R.string.api_secret))
                    resultToken = oauth!!.getOAuthRequestToken(callbackUrl)
                    return resultToken!!.authorizationURL
                } catch(e: TwitterException) {
                    e.printStackTrace()
                }
                return null
            }

            override fun onPostExecute(uri: String?) {
                if (uri != null) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    startActivity(intent)
                } else {
                    sendToast("失敗しました")
                }
            }
        }.execute()
    }

    override fun onNewIntent(intent: Intent) {
        if (intent.data.getQueryParameter("oauth_verifier") == null || intent.data == null || !intent.data.toString().startsWith(callbackUrl)) {
            sendToast("認証がキャンセルされました");
            return;
        }
        val oauthVerifier = intent.data.getQueryParameter("oauth_verifier")
        object : AsyncTask<String, Void, AccessToken?>() {
            override fun doInBackground(vararg params: String?): AccessToken? {
                try {
                    val token = oauth!!.getOAuthAccessToken(resultToken, params[0])
                    return token
                } catch(e: TwitterException) {
                    e.printStackTrace()
                }
                return null
            }

            override fun onPostExecute(token: AccessToken?) {
                if (resultToken != null) {
                    ClientUsers.instance.addClientUser(token!!) { user, success ->
                        if (success) {
                            sendToast(user!!.name + " 認証")
                            adapter!!.add(user)
                        } else {
                            sendToast("失敗")
                        }
                    }
                }
            }
        }.execute(oauthVerifier)
    }

    override fun finish() {
        if (TweetsViewManager.instance.isChanged && Global.instance.isActiveMainActivity) {
            Global.instance.destroyMainActivity()
            startActivity(MainActivity::class.java, false)
        }
        super.finish()
    }

    companion object {
        private val callbackUrl = "projectex://twitter"
    }
}
