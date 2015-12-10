package jp.ac.kcg.projectexercises.listview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView


/**
 */
class DragAndDroppableListView : ListView, AdapterView.OnItemLongClickListener {
    private var onStartDragListener: ((Int) -> Unit)? = null
    private var onDragListener: ((Int) -> Unit)? = null
    private var onDropListener: ((Int, Int) -> Unit)? = null
    private var onDragCancelListener: ((Int) -> Unit)? = null

    private var onItemLongClickListener: ((AdapterView<*>, View, Int, Long) -> Unit)? = null


    private var dragging = false
    private var draggingItemPosition = -1
    private var draggingImageLayoutParams: WindowManager.LayoutParams? = null
    private var draggingImageView: ImageView? = null
    private var draggingBitmap: Bitmap? = null
    private val fastScrollMovement = 35
    private val slowScrollMovement = 20

    var isDraggable = true
        set(draggable) {
            field = draggable
        }

    private var motionEvent: MotionEvent? = null

    constructor(context: Context) : super(context) {
        super.setOnItemLongClickListener(this)
        initDraggingImageLayoutParamsParams()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        super.setOnItemLongClickListener(this)
        initDraggingImageLayoutParamsParams()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        super.setOnItemLongClickListener(this)
        initDraggingImageLayoutParamsParams()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isDraggable) {
            return super.onTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> motionEvent = MotionEvent.obtain(ev)
            MotionEvent.ACTION_MOVE -> doingDrag(ev)
            MotionEvent.ACTION_UP -> drop(ev)
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> drop(ev)
        }
        return super.onTouchEvent(ev)
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        if (!isDraggable && onItemLongClickListener != null) {
            onItemLongClickListener!!(parent, view, position, id)
        } else {
            startDrag()
        }
        return true
    }

    public fun setOnItemLongClickListener(onLongClick: (parent: AdapterView<*>, view: View, position: Int, id: Long) -> Unit) {
        this.onItemLongClickListener = onLongClick
    }

    public fun setOnStartDragListener(startDrag: (dragItemPosition: Int) -> Unit) {
        onStartDragListener = startDrag
    }


    public fun setOnDropListener(onDrop: (dragItemPosition: Int, droppedItemPosition: Int) -> Unit) {
        onDropListener = onDrop
    }

    public fun setOnDragListener(onDrag: (dragPointItemPosition: Int) -> Unit) {
        onDragListener = onDrag
    }

    public fun setOnDragCancelListener(onCancel: (draggingItemPosition: Int) -> Unit) {
        onDragCancelListener = onCancel
    }

    private fun startDrag() {
        draggingItemPosition = pointToPosition(motionEvent!!.x.toInt(), motionEvent!!.y.toInt())
        if (draggingItemPosition < 0)
            return

        val canvas = Canvas()
        val doDragView = getChildAt(draggingItemPosition - firstVisiblePosition)

        if (draggingImageView != null) {
            windowManager.removeView(draggingImageView)
        }

        draggingBitmap = Bitmap.createBitmap(doDragView.width, doDragView.height, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(draggingBitmap)
        doDragView.draw(canvas)
        draggingImageView = ImageView(context)
        draggingImageView!!.setBackgroundColor(Color.parseColor("#88888888"))
        draggingImageView!!.setImageBitmap(draggingBitmap)
        windowManager.addView(draggingImageView, draggingImageLayoutParams)
        draggingImageLayoutParams!!.y = motionEvent!!.y.toInt()
        if (onStartDragListener != null)
            onStartDragListener!!(draggingItemPosition)

        doingDrag(motionEvent!!)
    }

    private fun initDraggingImageLayoutParamsParams() {
        draggingImageLayoutParams = WindowManager.LayoutParams()
        draggingImageLayoutParams!!.x = left
        draggingImageLayoutParams!!.y = top
        draggingImageLayoutParams!!.windowAnimations = 0
        draggingImageLayoutParams!!.gravity = Gravity.TOP or Gravity.START
        draggingImageLayoutParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        draggingImageLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        draggingImageLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        draggingImageLayoutParams!!.format = PixelFormat.TRANSLUCENT
    }

    private fun doingDrag(event: MotionEvent) {
        if (!isDraggable || draggingImageView == null)
            return
        dragging = true
        val y = event.y.toInt()
        val height = height
        val verticalCenter = height / 2
        var speed = 0
        val fastBorder = height / 8
        val slowBorder = height / 3

        if (y < slowBorder) {
            speed = if (y < fastBorder) -fastScrollMovement else -slowScrollMovement
        } else if (y > height - slowBorder) {
            speed = if (y > height - fastBorder) fastScrollMovement else slowScrollMovement
        }

        if (speed != 0) {
            var verticalCenterPosition = pointToPosition(0, verticalCenter)
            if (verticalCenterPosition == AdapterView.INVALID_POSITION) {
                verticalCenterPosition = pointToPosition(0, verticalCenter + dividerHeight)
            }
            val verticalCenterView = getChildAt(verticalCenterPosition - firstVisiblePosition)
            if (verticalCenterView != null) {
                setSelectionFromTop(verticalCenterPosition, verticalCenterView.top - speed)
            }
        }

        if (draggingImageView!!.height < 0) {
            draggingImageView!!.visibility = View.GONE
        } else {
            draggingImageView!!.visibility = View.VISIBLE
        }

        draggingImageLayoutParams!!.y = event.y.toInt()

        windowManager.updateViewLayout(draggingImageView, draggingImageLayoutParams)
        val dragPointItemPosition = pointToPosition(event.x.toInt(), event.y.toInt())
        if (onDragListener != null && dragPointItemPosition >= 0 && dragPointItemPosition < adapter.count) {
            onDragListener!!(dragPointItemPosition)
        }
    }

    private fun drop(event: MotionEvent) {
        if (!dragging) {
            return
        }
        dragging = false
        if (draggingImageView != null) {
            windowManager.removeView(draggingImageView)

            draggingImageView!!.setImageDrawable(null)
            draggingBitmap!!.recycle()
            draggingBitmap = null
            draggingImageView = null

            motionEvent!!.recycle()
            motionEvent = null
        }
        val droppedItemPosition = pointToPosition(event.x.toInt(), event.y.toInt())
        if (onDropListener != null && droppedItemPosition >= 0 && droppedItemPosition < adapter.count) {
            onDropListener!!(draggingItemPosition, droppedItemPosition)
        } else if ( onDragCancelListener != null) {
            onDragCancelListener!!(draggingItemPosition)
        }
        draggingItemPosition = -1
    }

    private val windowManager: WindowManager
        get() = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

}
