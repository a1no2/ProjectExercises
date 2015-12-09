package jp.ac.kcg.projectexercises.listview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AbsListView
import android.widget.ListView


/**
 * 一番下まで見るとそれをコールバックしてくれる抽象クラスなListView
 */
open class AttachedBottomCallBackListView : ListView {
    private var onAttachedBottom = false
    private var onAttachedBottomCallbackEnabled = true
    private var onScroll: ((AbsListView, Int, Int, Int) -> Unit)? = null
    private var onScrollStateChanged: ((AbsListView, Int) -> Unit)? = null
    private var onAttachedBottomCallback: ((Any) -> Unit) = {}

    constructor(context: Context) : super(context) {
        setScrollListener()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setScrollListener()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setScrollListener()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var height = 0
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode != View.MeasureSpec.EXACTLY) {
            val listAdapter = adapter
            if (listAdapter != null && !listAdapter.isEmpty) {
                for (position in 0..listAdapter.count - 1) {
                    val view = listAdapter.getView(position, null, this)
                    view.measure(widthMeasureSpec, heightMeasureSpec)
                    height += view.measuredHeight + dividerHeight
                }
            }
            if ((heightMode == View.MeasureSpec.AT_MOST) && (height > heightSize)) {
                height = heightSize
            }
        } else {
            height = measuredHeight
        }
        setMeasuredDimension(measuredWidth, height)
    }

    private fun setScrollListener() {
        this.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                if (this@AttachedBottomCallBackListView.onScrollStateChanged != null)
                    this@AttachedBottomCallBackListView.onScrollStateChanged!!(view, scrollState)
            }

            override fun onScroll(view: AbsListView, firstVisibleItemPosition: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (this@AttachedBottomCallBackListView.onScroll != null)
                    this@AttachedBottomCallBackListView.onScroll!!(view, firstVisibleItemPosition, visibleItemCount, totalItemCount)

                if (onAttachedBottomCallbackEnabled && !onAttachedBottom && firstVisibleItemPosition + visibleItemCount == totalItemCount && visibleItemCount < totalItemCount) {
                    val bottomItemY = getChildAt(childCount - 1).height
                    val bottomItemPositionY = getChildAt(childCount - 1).top
                    val itemPositionTargetLine = height - bottomItemY
                    if (bottomItemPositionY <= itemPositionTargetLine) {
                        onAttachedBottom(adapter.getItem(adapter.count - 1))
                        onAttachedBottom = true
                    }
                } else if (firstVisibleItemPosition + visibleItemCount <= totalItemCount - 1) {
                    onAttachedBottom = false
                }
            }
        })
    }

    public fun setOnScrollCallback(onScroll: (AbsListView, firstVisiblePosition: Int, visibleCount: Int, total: Int) -> Unit) {
        this.onScroll = onScroll
    }

    public fun setOnScrollStateChangeCallback(onScrollStateChange: (AbsListView, scrollState: Int) -> Unit) {
        this.onScrollStateChanged = onScrollStateChange;
    }

    public fun setOnAttachedBottomCallback(onAttachedBottom: (bottomItem: Any) -> Unit) {
        this.onAttachedBottomCallback = onAttachedBottom
    }

    /**
     * onAttachedBottom
     * セットされたonAttachedBottomListenerを実行する

     * @param item 一番下のアイテム
     */
    protected open fun onAttachedBottom(item: Any) {
        onAttachedBottomCallback(item)
    }

    /**
     * コールバックするか否かを切り替える

     * @param onAttachedBottomCallbackEnabled true : コールバックする false : コールバックしない
     */
    fun attachedBottomCallbackEnabled(onAttachedBottomCallbackEnabled: Boolean) {
        this.onAttachedBottomCallbackEnabled = onAttachedBottomCallbackEnabled
    }

}