package com.xygg.library

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat

/**
 * titleBar
 *
 *
 * author:loper7
 *
 *
 * time:2018-1-8
 */
class TitleBar : RelativeLayout {
    private val TAG = "TitleBar"
    private var mContext: Context
    private var isCenterTitle = true
    private var isUseRipple = false
    private var titleTextBold = false
    private var isShowBorder = false
    private var titleEllipsize: TruncateAt? = null
    private var tvTitle: TextView? = null
    private var tvMenu: TextView? = null
    private var ivBack: ImageView? = null
    private var border: View? = null
    private var backParams: LayoutParams? = null
    private var titleParams: LayoutParams? = null
    private var menuParams: LayoutParams? = null
    private var borderParams: LayoutParams? = null
    private var backImageRes: Drawable? = null
    private var menuImageRes: Drawable? = null
    private var padding = 0
    private var titleTextSize = 0
    private var menuTextSize = 0
    private var borderWidth = 0
    private var titleTextColor = 0
    private var menuTextColor = 0
    private var backGroundColor = 0
    private var borderColor = 0
    private var titleText: String? = null
    private var menuText: String? = null
    private var onTitleListener: OnTitleListener? = null

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        mContext = context
        if (defaultPadding == 0) defaultPadding =
            dip2px(context, 16.0f)
        if (defaultTitleTextSize == 0) defaultTitleTextSize =
            sp2px(context, 18.0f)
        if (defaultMenuTextSize == 0) defaultMenuTextSize =
            sp2px(context, 14.0f)
        if (defaultBorderWidth == 0) defaultBorderWidth =
            dip2px(context, 0.6f)
        getAttr(attrs)
        initLayout()
    }

    /**
     * 加载自定义属性
     *
     * @param attrs 自定属性
     */
    private fun getAttr(attrs: AttributeSet?) {
        val typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.TitleBar)
        backImageRes = typedArray.getDrawable(R.styleable.TitleBar_tBackImage)
        isCenterTitle = typedArray.getBoolean(
            R.styleable.TitleBar_tCenterTitle,
            defaultCenterTitle
        )
        padding = typedArray.getDimensionPixelSize(
            R.styleable.TitleBar_tPadding,
            defaultPadding
        )
        titleTextSize = typedArray.getDimensionPixelSize(
            R.styleable.TitleBar_tTitleTextSize,
            defaultTitleTextSize
        )
        titleTextColor = typedArray.getColor(
            R.styleable.TitleBar_tTitleTextColor,
            defaultTitleTextColor
        )
        titleText = typedArray.getString(R.styleable.TitleBar_tTitleText)
        menuTextSize = typedArray.getDimensionPixelSize(
            R.styleable.TitleBar_tMenuTextSize,
            defaultMenuTextSize
        )
        menuTextColor = typedArray.getColor(
            R.styleable.TitleBar_tMenuTextColor,
            defaultMenuTextColor
        )
        menuText = typedArray.getString(R.styleable.TitleBar_tMenuText)
        menuImageRes = typedArray.getDrawable(R.styleable.TitleBar_tMenuImage)
        isUseRipple = typedArray.getBoolean(
            R.styleable.TitleBar_tUseRipple,
            defaultUseRipple
        )
        backGroundColor = typedArray.getColor(
            R.styleable.TitleBar_tBackgroundColor,
            defaultBackGroundColor
        )
        borderColor = typedArray.getColor(
            R.styleable.TitleBar_tBorderColor,
            defaultBorderColor
        )
        isShowBorder = typedArray.getBoolean(
            R.styleable.TitleBar_tShowBorder,
            defaultShowBorder
        )
        borderWidth = typedArray.getDimensionPixelSize(
            R.styleable.TitleBar_tBorderWidth,
            defaultBorderWidth
        )
        titleTextBold = typedArray.getBoolean(
            R.styleable.TitleBar_tTitleTextBold,
            defaultTitleTextBold
        )
        val ellipsize = typedArray.getInt(R.styleable.TitleBar_tTitleEllipsize, -1)
        titleEllipsize = when (ellipsize) {
            0 -> TruncateAt.START
            1 -> TruncateAt.END
            2 -> TruncateAt.MIDDLE
            3 -> TruncateAt.MARQUEE
            else -> defaultTitleEllipsize
        }
        typedArray.recycle()
    }

    private fun initLayout() {
        initBack(backImageRes)
        initMenu()
        initTitle()
        initBorder()
        setUseRipple(isUseRipple)
        setBackgroundColor(backGroundColor)
    }

    /**
     * 初始化返回按钮
     *
     * @param backImageRes 返回图片资源id
     */
    @SuppressLint("NewApi")
    private fun initBack(backImageRes: Drawable?) {
        backParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT
        )
        backParams!!.addRule(ALIGN_PARENT_LEFT)
        ivBack = ImageView(mContext)
        ivBack!!.id = View.generateViewId()
        ivBack!!.setPadding(padding, 0, padding, 0)
        ivBack!!.layoutParams = backParams
        if (backImageRes != null) {
            ivBack!!.setImageDrawable(backImageRes)
            ivBack!!.visibility = View.VISIBLE
        } else {
            ivBack!!.visibility = View.GONE
        }
        ivBack!!.setOnClickListener {
            if (mListener != null) {
                mListener!!.back.invoke()
            } else {
                try {
                    val activity = activity
                    closeKeyboard(activity)
                    activity.finish()
                    if (activityEnterAnim != 0 && activityExitAnim != 0) activity.overridePendingTransition(
                        activityEnterAnim,
                        activityExitAnim
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        this.addView(ivBack)
    }

    /**
     * 初始化菜单按钮
     */
    @SuppressLint("NewApi")
    private fun initMenu() {
        menuParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT
        )
        menuParams!!.addRule(ALIGN_PARENT_RIGHT)
        tvMenu = TextView(mContext)
        tvMenu!!.id = View.generateViewId()
        tvMenu!!.gravity = Gravity.CENTER_VERTICAL
        tvMenu!!.setPadding(padding, 0, padding, 0)
        tvMenu!!.layoutParams = menuParams
        tvMenu!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize.toFloat())
        tvMenu!!.setTextColor(menuTextColor)
        tvMenu!!.text = menuText
        if (menuImageRes != null) {
            tvMenu!!.text = ""
            setCompoundDrawable(tvMenu, menuImageRes!!)
        }
        tvMenu!!.visibility =
            if (TextUtils.isEmpty(menuText) && menuImageRes == null) View.GONE else View.VISIBLE
        tvMenu!!.setOnClickListener {
            mListener?.menu?.invoke()
        }
        this.addView(tvMenu)
    }


    /**
     * 初始化标题
     */
    @SuppressLint("NewApi")
    private fun initTitle() {
        titleParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT
        )
        tvTitle = TextView(mContext)
        tvTitle!!.id = View.generateViewId()
        tvTitle!!.gravity = Gravity.CENTER_VERTICAL
        tvTitle!!.layoutParams = titleParams
        tvTitle!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize.toFloat())
        tvTitle!!.text = titleText
        tvTitle!!.setTextColor(titleTextColor)
        tvTitle!!.isSingleLine = true
        val paint = tvTitle!!.paint
        paint.isFakeBoldText = titleTextBold
        setTitleEllipsize(titleEllipsize)
        setCenterTitle(isCenterTitle)
        tvTitle!!.setOnClickListener { if (onTitleListener != null) onTitleListener!!.onTitleClick() }
        this.addView(tvTitle)
    }

    /**
     * 初始化底部线
     */
    private fun initBorder() {
        borderParams =
            LayoutParams(LayoutParams.MATCH_PARENT, borderWidth)
        borderParams!!.addRule(ALIGN_PARENT_BOTTOM)
        border = View(mContext)
        border!!.setBackgroundColor(borderColor)
        border!!.layoutParams = borderParams
        if (isShowBorder) border!!.visibility = View.VISIBLE else border!!.visibility = View.GONE
        this.addView(border)
    }
    /**********************************************************公共方法 */
    /**
     * 设置返回按钮图片
     *
     * @param drawable drawable
     */
    fun setBackImageDrawable(drawable: Drawable?) {
        if (drawable == null) {
            ivBack!!.visibility = View.GONE
            return
        }
        ivBack!!.visibility = View.VISIBLE
        backImageRes = drawable
        ivBack!!.setImageDrawable(backImageRes)
        setCenterTitle(isCenterTitle)
    }

    /**
     * 设置返回按钮图片
     *
     * @param resId resId
     */
    fun setBackImageResource(@DrawableRes resId: Int) {
        val drawable = ContextCompat.getDrawable(mContext, resId)
        setBackImageDrawable(drawable)
    }

    /**
     * 设置标题文字
     *
     * @param title title
     */
    fun setTitleText(title: String?) {
        titleText = title
        tvTitle!!.text = title
        setCenterTitle(isCenterTitle)
    }

    /**
     * 设置标题文字
     *
     * @param resId resId
     */
    fun setTitleText(@StringRes resId: Int) {
        titleText = mContext.resources.getText(resId).toString()
        tvTitle!!.text = titleText
        setCenterTitle(isCenterTitle)
    }

    /**
     * 设置标题文字颜色
     *
     * @param color color
     */
    fun setTitleTextColor(@ColorInt color: Int) {
        titleTextColor = color
        tvTitle!!.setTextColor(titleTextColor)
    }

    /**
     * 设置标题文字大小
     *
     * @param sp sp
     */
    fun setTitleTextSize(@Dimension sp: Int) {
        titleTextSize = sp2px(mContext, sp.toFloat())
        tvTitle!!.textSize = titleTextSize.toFloat()
    }

    /**
     * 设置标题显示模式
     *
     * @param isCenterTitle 是否居中
     */
    @SuppressLint("NewApi")
    fun setCenterTitle(isCenterTitle: Boolean) {
        if (tvTitle == null || titleParams == null) return
        this.isCenterTitle = isCenterTitle
        if (isCenterTitle && isEnoughAvailableWidth) {
            titleParams!!.removeRule(RIGHT_OF)
            titleParams!!.removeRule(LEFT_OF)
            titleParams!!.removeRule(ALIGN_PARENT_LEFT)
            titleParams!!.addRule(CENTER_IN_PARENT)
            tvTitle!!.setPadding(0, 0, 0, 0)
        } else {
            titleParams!!.removeRule(CENTER_IN_PARENT)
            titleParams!!.removeRule(ALIGN_PARENT_LEFT)
            titleParams!!.addRule(RIGHT_OF, ivBack!!.id)
            titleParams!!.addRule(LEFT_OF, tvMenu!!.id)
            if (ivBack!!.visibility == View.GONE) titleParams!!.addRule(ALIGN_PARENT_LEFT)
            tvTitle!!.setPadding(
                if (ivBack!!.visibility == View.GONE) padding else 0,
                0,
                if (tvMenu!!.visibility == View.GONE) padding else 0,
                0
            )
        }
    }

    /**
     * 设置标题ellipsize属性
     *
     * @param ellipsize
     */
    fun setTitleEllipsize(ellipsize: TruncateAt?) {
        titleEllipsize = ellipsize
        tvTitle!!.ellipsize = titleEllipsize
        tvTitle!!.isSelected = true
        tvTitle!!.isFocusable = true
        tvTitle!!.isFocusableInTouchMode = true
    }

    /**
     * 设置菜单按钮图片
     *
     * @param drawable drawable
     */
    fun setMenuImageDrawable(drawable: Drawable?) {
        if (drawable == null) {
            tvMenu!!.visibility = View.GONE
            return
        }
        menuImageRes = drawable
        setCompoundDrawable(tvMenu, drawable)
        tvMenu!!.text = ""
        tvMenu!!.visibility = View.VISIBLE
        setCenterTitle(isCenterTitle)
    }

    /**
     * 设置菜单按钮图片
     *
     * @param resId resId
     */
    fun setMenuImageResource(@DrawableRes resId: Int) {
        val drawable = ContextCompat.getDrawable(mContext, resId)
        setMenuImageDrawable(drawable)
    }

    /**
     * 设置菜单文字
     *
     * @param menuText menuText
     */
    fun setMenuText(menuText: String?) {
        if (TextUtils.isEmpty(menuText)) {
            tvMenu!!.visibility = View.GONE
            return
        }
        this.menuText = menuText
        tvMenu!!.text = menuText
        tvMenu!!.visibility = View.VISIBLE
        setCenterTitle(isCenterTitle)
    }

    /**
     * 设置菜单文字
     *
     * @param resId resId
     */
    fun setMenuText(@StringRes resId: Int) {
        if (resId == 0) {
            tvMenu!!.visibility = View.GONE
            return
        }
        menuText = mContext.resources.getText(resId).toString()
        tvMenu!!.text = menuText
        tvMenu!!.visibility = View.VISIBLE
        setCenterTitle(isCenterTitle)
    }

    /**
     * 设置菜单文字颜色
     *
     * @param color color
     */
    fun setMenuTextColor(@ColorInt color: Int) {
        menuTextColor = color
        tvMenu!!.setTextColor(menuTextColor)
    }

    /**
     * 设置菜单文字大小
     *
     * @param sp sp
     */
    fun setMenuTextSize(@Dimension sp: Int) {
        menuTextSize = sp2px(mContext, sp.toFloat())
        tvMenu!!.textSize = menuTextSize.toFloat()
    }

    /**
     * 设置activity进入退出动画
     *
     * @param enter 进入动画
     * @param exit  退出动画
     */
    fun setActivityAnim(@AnimRes enter: Int, @AnimRes exit: Int) {
        activityEnterAnim = enter
        activityExitAnim = exit
    }

    /**
     * 清除activity动画
     */
    fun clearActivityAnim() {
        activityEnterAnim = 0
        activityExitAnim = 0
    }


    /**
     * 标题点击监听
     *
     * @param onTitleListener onTitleListener
     */
    fun setOnTitleListener(onTitleListener: OnTitleListener?) {
        this.onTitleListener = onTitleListener
    }

    /**
     * 设置水波纹
     *
     * @param isUseRipple isUseRipple
     */
    fun setUseRipple(isUseRipple: Boolean) {
        this.isUseRipple = isUseRipple
        if (isUseRipple) {
            if (tvMenu != null) tvMenu!!.setBackgroundResource(R.drawable.control_background_40dp_material)
            if (ivBack != null) ivBack!!.setBackgroundResource(R.drawable.control_background_40dp_material)
        } else {
            tvMenu!!.setBackgroundResource(0)
            ivBack!!.setBackgroundResource(0)
        }
    }

    /**
     * 设置底部分割线是否显示
     *
     * @param isShowBorder isShowBorder
     */
    fun setShowBorder(isShowBorder: Boolean) {
        this.isShowBorder = isShowBorder
        if (isShowBorder) border!!.visibility = View.VISIBLE else border!!.visibility = View.GONE
    }

    /**
     * 设置底部分割线颜色
     *
     * @param color color
     */
    fun setBorderColor(@ColorInt color: Int) {
        borderColor = color
        if (border != null) border!!.setBackgroundColor(borderColor)
    }

    /**
     * 设置底部分割线的宽度
     *
     * @param dp
     */
    fun setBorderWidth(@Dimension dp: Float) {
        borderWidth = dip2px(mContext, dp)
        borderParams =
            LayoutParams(LayoutParams.MATCH_PARENT, borderWidth)
        borderParams!!.addRule(ALIGN_PARENT_BOTTOM)
        border!!.layoutParams = borderParams
    }

    /**
     * 设置背景颜色
     *
     * @param color color
     */
    fun setBackGroundColor(@ColorInt color: Int) {
        backGroundColor = color
        setBackgroundColor(backGroundColor)
    }

    /**
     * 获取返回view
     *
     * @return backView
     */
    val backView: ImageView?
        get() {
            if (ivBack == null) NullPointerException("back imageView is null,may be titBar is no initialization")
            return ivBack
        }

    /**
     * 获取标题view
     *
     * @return titleView
     */
    val titleView: TextView?
        get() {
            if (tvTitle == null) NullPointerException("title textView is null,may be titBar is no initialization")
            return tvTitle
        }

    /**
     * 获取菜单view
     *
     * @return menuView
     */
    val menuView: TextView?
        get() {
            if (tvMenu == null) NullPointerException("menuView textView is null,may be titBar is no initialization")
            return tvMenu
        }

    /**
     * 获取底线view
     *
     * @return BorderView
     */
    val borderView: View?
        get() {
            if (border == null) NullPointerException("borderView View is null,may be titBar is no initialization")
            return border
        }
    /**********************************************************end */
    /**
     * 得到Title文字宽度
     *
     * @param text     文字
     * @param textSize px
     * @return
     */
    private fun getTitleTextWidth(text: String?, textSize: Int): Float {
        if (TextUtils.isEmpty(text)) return 0f
        val paint = tvTitle!!.paint
        paint.textSize = textSize.toFloat()
        return paint.measureText(text)
    }//titleBar留给Title的宽度

    /**
     * titleBar留给Title的宽度是否足够
     *
     * @return
     */
    private val isEnoughAvailableWidth: Boolean
        get() {
            val width = MeasureSpec.makeMeasureSpec(
                0,
                MeasureSpec.UNSPECIFIED
            )
            val height = MeasureSpec.makeMeasureSpec(
                0,
                MeasureSpec.UNSPECIFIED
            )
            ivBack!!.measure(width, height)
            tvMenu!!.measure(width, height)
            //titleBar留给Title的宽度
            val availableWidth =
                getWindowWidth(mContext) - (tvMenu!!.measuredWidth + ivBack!!.measuredWidth)
            val titleTextWidth = getTitleTextWidth(titleText, titleTextSize)
            return availableWidth > titleTextWidth
        }

    /**
     * 得到屏幕宽度
     *
     * @param context
     * @return
     */
    private fun getWindowWidth(context: Context): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.width.toInt()
    }

    /**
     * 获取activity
     *
     * @return
     * @throws Exception Unable to get Activity.
     */
    @get:Throws(Exception::class)
    private val activity: Activity
        private get() {
            var context = context
            while (context !is Activity && context is ContextWrapper) {
                context = context.baseContext
            }
            if (context is Activity) {
                return context
            }
            throw Exception("Unable to get Activity.")
        }

    /**
     * 关闭软键盘
     *
     * @param activity
     */
    private fun closeKeyboard(activity: Activity?) {
        if (activity == null) return
        val view = activity.window.peekDecorView()
        if (view != null) {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * textView上设置图片
     *
     * @param tv       textView上设置图片
     * @param drawable 图片
     */
    private fun setCompoundDrawable(tv: TextView?, drawable: Drawable) {
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight) //设置边界
        tv!!.setCompoundDrawables(drawable, null, null, null) //画在左边
    }

    interface OnTitleListener {
        fun onTitleClick()
    }

    inner class ListenerBuilder {
        internal var menu: () -> Unit = {}
        internal var back: () -> Unit = {}
        fun onMenuListener(action: () -> Unit){
            menu = action
        }
        fun onBackListener(action: () -> Unit){
            back = action
        }

    }

    var mListener:ListenerBuilder ?= null
    //提供方法供外部实现接口的回调监听
    fun registerListener(listenerBuilder: ListenerBuilder.() -> Unit){
        mListener = ListenerBuilder().also(listenerBuilder)
    }


    /**
     * 全局配置的Class，对所有使用到的地方有效
     */
    class Config {
        fun setTitleTextSize(context: Context, @Dimension sp: Int): Config {
            defaultTitleTextSize =
                sp2px(context, sp.toFloat())
            return config
        }

        fun setTitleTextColor(@ColorInt color: Int): Config {
            defaultTitleTextColor = color
            return config
        }

        fun setMenuTextSize(context: Context, @Dimension sp: Int): Config {
            defaultMenuTextSize = sp2px(context, sp.toFloat())
            return config
        }

        fun setMenuTextColor(@ColorInt color: Int): Config {
            defaultMenuTextColor = color
            return config
        }

        fun setPadding(context: Context, @Dimension dp: Int): Config {
            defaultPadding = dip2px(context, dp.toFloat())
            return config
        }

        fun setCenterTitle(isCenter: Boolean): Config {
            defaultCenterTitle = isCenter
            return config
        }

        fun setUseRipple(isUseRipple: Boolean): Config {
            defaultUseRipple = isUseRipple
            return config
        }

        fun setTitleEllipsize(titleEllipsize: TruncateAt): Config {
            defaultTitleEllipsize = titleEllipsize
            return config
        }

        fun setBackgroundColor(@ColorInt color: Int): Config {
            defaultBackGroundColor = color
            return config
        }

        fun setBorderColor(@ColorInt color: Int): Config {
            defaultBorderColor = color
            return config
        }

        fun setShowBorder(isShow: Boolean): Config {
            defaultShowBorder = isShow
            return config
        }

        fun setBorderWidth(
            context: Context,
            dp: Float
        ): Config {
            defaultBorderWidth = dip2px(context, dp)
            return config
        }

        fun setTitleTextBold(isBold: Boolean): Config {
            defaultTitleTextBold = isBold
            return config
        }

        fun setActivityBackAnim(@AnimRes enter: Int, @AnimRes exit: Int): Config {
            activityEnterAnim = enter
            activityExitAnim = exit
            return config
        }
    }

    companion object {
        /**
         * 获取全局配置的class
         *
         * @return
         */
        val config = Config()
        private var defaultMenuTextSize = 0
        private var defaultTitleTextSize = 0
        private var defaultBorderWidth = 0
        private var defaultPadding = 0
        private var defaultCenterTitle = false
        private var defaultUseRipple = false
        private var defaultShowBorder = false
        private var defaultTitleTextBold = false
        private var defaultTitleEllipsize = TruncateAt.MARQUEE
        private var defaultTitleTextColor = Color.parseColor("#333333")
        private var defaultMenuTextColor = Color.parseColor("#666666")
        private var defaultBorderColor = Color.parseColor("#F2F2F2")
        private var defaultBackGroundColor = 0
        private var activityEnterAnim = 0
        private var activityExitAnim = 0

        /**
         * 根据手机的分辨率dp 转成px(像素)
         */
        private fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * 根据手机的分辨率sp 转成px(像素)
         */
        private fun sp2px(context: Context, spValue: Float): Int {
            val scale = context.resources.displayMetrics.scaledDensity
            return (spValue * scale + 0.5f).toInt()
        }
    }
}