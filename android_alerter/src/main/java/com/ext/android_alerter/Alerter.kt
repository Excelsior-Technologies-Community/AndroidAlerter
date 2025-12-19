package com.ext.android_alerter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.*

class Alerter private constructor(private val activity: Activity) {

    private var alertView: View? = null
    private var rootLayout: FrameLayout? = null

    private var title = "Alert Title"
    private var text = "Alert text..."

    private var icon: Drawable? =
        ContextCompat.getDrawable(activity, R.drawable.alerter_ic_notifications)

    private var backgroundColor =
        ContextCompat.getColor(activity, R.color.alerter_default_background)

    private var backgroundDrawable: Drawable? = null

    private var titleTextColor =
        ContextCompat.getColor(activity, android.R.color.white)

    private var textColor =
        ContextCompat.getColor(activity, android.R.color.white)

    private var titleTextSize = 18f
    private var textSize = 14f
    private var iconSize = 48

    private var duration = 3000L
    private var enableInfiniteDuration = false
    private var enableSwipeToDismiss = true
    private var enableProgress = false

    private var progressColor = 0xCCFFFFFF.toInt()
    private var iconTintColor: Int? = null
    private var showIcon = true

    private var onClickListener: (() -> Unit)? = null
    private var onShowListener: (() -> Unit)? = null
    private var onHideListener: (() -> Unit)? = null

    companion object {
        private var currentAlerter: Alerter? = null
        fun create(activity: Activity) = Alerter(activity)
        fun hide() = currentAlerter?.hide()
    }

    // ---------------- CONFIG ----------------

    fun setTitle(title: String) = apply { this.title = title }
    fun setText(text: String) = apply { this.text = text }

    fun setIcon(@DrawableRes iconRes: Int) = apply {
        icon = ContextCompat.getDrawable(activity, iconRes)
        showIcon = true
    }

    fun hideIcon() = apply { showIcon = false }

    fun setIconTint(@ColorInt color: Int) = apply { iconTintColor = color }
    fun setIconSize(dp: Int) = apply { iconSize = dp }

    fun setBackgroundColor(@ColorInt color: Int) = apply { backgroundColor = color }
    fun setBackgroundDrawable(drawable: Drawable?) = apply { backgroundDrawable = drawable }

    fun setTitleTextColor(@ColorInt color: Int) = apply { titleTextColor = color }
    fun setTextColor(@ColorInt color: Int) = apply { textColor = color }

    fun setTitleTextSize(size: Float) = apply { titleTextSize = size }
    fun setTextSize(size: Float) = apply { textSize = size }

    fun setDuration(duration: Long) = apply { this.duration = duration }
    fun enableInfiniteDuration(enable: Boolean = true) = apply { enableInfiniteDuration = enable }
    fun enableProgress(enable: Boolean = true) = apply { enableProgress = enable }
    fun setProgressColor(@ColorInt color: Int) = apply { progressColor = color }

    fun setOnClickListener(listener: () -> Unit) = apply { onClickListener = listener }
    fun setOnShowListener(listener: () -> Unit) = apply { onShowListener = listener }
    fun setOnHideListener(listener: () -> Unit) = apply { onHideListener = listener }

    // ---------------- SHOW ----------------

    fun show() {
        hide()
        currentAlerter = this

        activity.window.setDecorFitsSystemWindows(false)

        val decorView = activity.window.decorView as ViewGroup

        alertView = LayoutInflater.from(activity)
            .inflate(R.layout.alerter_layout, decorView, false)

        rootLayout = FrameLayout(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP
            )
            addView(alertView)
        }

        decorView.addView(rootLayout)

        // ✅ Apply inset ONLY here
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout!!) { view, insets ->
            val topInset = insets.getInsets(
                WindowInsetsCompat.Type.statusBars() or
                        WindowInsetsCompat.Type.displayCutout()
            ).top

            view.updatePadding(top = topInset)
            insets
        }

        setupViews()
        animateShow()

        onShowListener?.invoke()

        if (!enableInfiniteDuration && duration > 0) {
            rootLayout?.postDelayed({ hide() }, duration)
        }

        if (enableProgress && duration > 0) startProgress()
    }

    // ---------------- HIDE ----------------

    fun hide() {
        rootLayout?.doOnLayout { view ->
            view.animate()
                .translationY(-view.height * 0.6f)
                .alpha(0f)
                .scaleY(0.96f)
                .setDuration(220)
                .setInterpolator(android.view.animation.AccelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        (view.parent as? ViewGroup)?.removeView(view)
                        onHideListener?.invoke()
                        if (currentAlerter == this@Alerter) currentAlerter = null
                    }
                })
                .start()
        }
    }

    // ---------------- ANIMATION ----------------

    private fun animateShow() {
        rootLayout?.doOnLayout { view ->
            view.translationY = -view.height.toFloat()
            view.alpha = 0f

            view.animate()
                .translationY(12f)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .withEndAction {
                    view.animate()
                        .translationY(0f)
                        .setDuration(120)
                        .setInterpolator(android.view.animation.OvershootInterpolator(1.2f))
                        .start()
                }
                .start()
        }
    }

    // ---------------- VIEW SETUP ----------------

    private fun setupViews() {
        alertView?.apply {
            val titleTv = findViewById<TextView>(R.id.tvAlertTitle)
            val textTv = findViewById<TextView>(R.id.tvAlertText)
            val iconIv = findViewById<ImageView>(R.id.ivAlertIcon)
            val container = findViewById<ViewGroup>(R.id.alertContainer)
            val progress = findViewById<View>(R.id.progressBar)

            titleTv.text = title
            textTv.text = text

            titleTv.setTextColor(titleTextColor)
            textTv.setTextColor(textColor)

            titleTv.textSize = titleTextSize
            textTv.textSize = textSize

            if (showIcon && icon != null) {
                iconIv.visibility = View.VISIBLE
                iconIv.setImageDrawable(icon)
                iconTintColor?.let { iconIv.setColorFilter(it) }

                val sizePx = (iconSize * resources.displayMetrics.density).toInt()
                iconIv.layoutParams.width = sizePx
                iconIv.layoutParams.height = sizePx
            } else {
                iconIv.visibility = View.GONE
            }

            backgroundDrawable?.let { container.background = it }
                ?: container.setBackgroundColor(backgroundColor)

            progress.visibility = if (enableProgress) View.VISIBLE else View.GONE
            progress.setBackgroundColor(progressColor)

            setOnClickListener { onClickListener?.invoke() }
            if (enableSwipeToDismiss) setupSwipe()
        }
    }

    private fun startProgress() {
        alertView?.findViewById<View>(R.id.progressBar)?.let { bar ->
            bar.pivotX = 0f
            ValueAnimator.ofFloat(1f, 0f).apply {
                duration = this@Alerter.duration
                addUpdateListener { bar.scaleX = it.animatedValue as Float }
                start()
            }
        }
    }

    private fun setupSwipe() {
        var startY = 0f
        var isSwiping = false

        alertView?.setOnTouchListener { v, e ->
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startY = e.rawY
                    isSwiping = false
                    false // IMPORTANT
                }

                MotionEvent.ACTION_MOVE -> {
                    val dy = e.rawY - startY
                    if (dy < -20) { // swipe threshold
                        isSwiping = true
                        v.translationY = dy
                        true
                    } else {
                        false
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (isSwiping) {
                        if (v.translationY < -v.height / 3) {
                            hide()
                        } else {
                            v.animate().translationY(0f).setDuration(200).start()
                        }
                        true
                    } else {
                        v.performClick() // ✅ click works now
                        false
                    }
                }

                else -> false
            }
        }
    }

}
