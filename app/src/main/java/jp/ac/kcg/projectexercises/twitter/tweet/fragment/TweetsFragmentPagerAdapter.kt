package jp.ac.kcg.projectexercises.twitter.tweet.fragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter

import java.util.ArrayList

/**
 */
class TweetsFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragments = ArrayList<TweetsFragment>()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemPosition(`object`: Any?): Int {
        if (`object` is TweetsFragment) {
            return fragments.indexOf(`object`)
        }
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragments[position].name
    }

    fun add(fragment: TweetsFragment) {
        fragments.add(fragment)
    }

    fun addAll(fragments: List<TweetsFragment>) {
        this.fragments.addAll(fragments)
    }

    fun clear() {
        for (fragment in fragments) {
            fragment.postProcess()
        }
        fragments.clear()
    }
}
