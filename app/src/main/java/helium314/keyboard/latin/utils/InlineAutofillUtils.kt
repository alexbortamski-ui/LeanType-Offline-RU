/*
 * Copyright (C) 2019 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0
 */

package helium314.keyboard.latin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.os.Build
import android.util.AttributeSet
import android.util.Size
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InlineSuggestion
import android.view.inputmethod.InlineSuggestionInfo
import android.view.inputmethod.InlineSuggestionsRequest
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.inline.InlineContentView
import android.widget.inline.InlinePresentationSpec
import androidx.annotation.AttrRes
import androidx.annotation.RequiresApi
import androidx.autofill.HintConstants
import androidx.autofill.inline.UiVersions
import androidx.autofill.inline.common.ImageViewStyle
import androidx.autofill.inline.common.TextViewStyle
import androidx.autofill.inline.common.ViewStyle
import androidx.autofill.inline.v1.InlineSuggestionUi
import helium314.keyboard.latin.R
import helium314.keyboard.latin.common.ColorType
import helium314.keyboard.latin.settings.Settings

private val SMS_OTP_AUTOFILL_HINTS = buildSet {
    add(HintConstants.AUTOFILL_HINT_SMS_OTP)
    (1..8).forEach { position ->
        add(HintConstants.generateSmsOtpHintForCharacterPosition(position))
    }
}

@RequiresApi(Build.VERSION_CODES.R)
internal fun isSmsOtpInlineSuggestion(source: String, type: String, autofillHints: Array<String>?): Boolean {
    val isKnownSource = source == InlineSuggestionInfo.SOURCE_AUTOFILL || source == InlineSuggestionInfo.SOURCE_PLATFORM
    return isKnownSource && type == InlineSuggestionInfo.TYPE_SUGGESTION &&
        autofillHints?.any { it in SMS_OTP_AUTOFILL_HINTS } == true
}

@RequiresApi(Build.VERSION_CODES.R)
internal fun shouldSuppressSmsOtpInlineSuggestion(
    localOtpHasPriority: Boolean,
    source: String,
    type: String,
    autofillHints: Array<String>?,
): Boolean = localOtpHasPriority && isSmsOtpInlineSuggestion(source, type, autofillHints)

@SuppressLint("RestrictedApi")
@RequiresApi(api = Build.VERSION_CODES.R)
object InlineAutofillUtils {

    fun createInlineSuggestionRequest(context: Context): InlineSuggestionsRequest {
        val colors = Settings.getValues().mColors
        val chipBgDrawableId = androidx.autofill.R.drawable.autofill_inline_suggestion_chip_background
        val chipBgColor = colors.get(ColorType.AUTOFILL_BACKGROUND_CHIP)
        val chipTextColor = colors.get(ColorType.KEY_TEXT)
        val chipTextHintColor = colors.get(ColorType.KEY_HINT_TEXT)

        val stylesBuilder = UiVersions.newStylesBuilder()
        val style = InlineSuggestionUi.newStyleBuilder()
            .setSingleIconChipStyle(
                ViewStyle.Builder()
                    .setBackground(Icon.createWithResource(context, chipBgDrawableId).setTint(chipBgColor))
                    .setPadding(0, 0, 0, 0)
                    .build()
            )
            .setChipStyle(
                ViewStyle.Builder()
                    .setBackground(Icon.createWithResource(context, chipBgDrawableId).setTint(chipBgColor))
                    .build()
            )
            .setStartIconStyle(ImageViewStyle.Builder().setLayoutMargin(0, 0, 0, 0).build())
            .setTitleStyle(
                TextViewStyle.Builder()
                    .setTextColor(chipTextColor)
                    .setTextSize(12f)
                    .build()
            )
            .setSubtitleStyle(
                TextViewStyle.Builder()
                    .setTextColor(chipTextHintColor)
                    .setTextSize(10f)
                    .build()
            )
            .setEndIconStyle(ImageViewStyle.Builder().setLayoutMargin(0, 0, 0, 0).build())
            .build()
        stylesBuilder.addStyle(style)
        val stylesBundle = stylesBuilder.build()

        val height = context.resources.getDimensionPixelSize(R.dimen.config_suggestions_strip_height)
        val min = Size(100, height)
        val max = Size(740, height)

        // Three InlinePresentationSpec are required for some password managers
        val presentationSpecs = ArrayList<InlinePresentationSpec>()
        presentationSpecs.add(InlinePresentationSpec.Builder(min, max).setStyle(stylesBundle).build())
        presentationSpecs.add(InlinePresentationSpec.Builder(min, max).setStyle(stylesBundle).build())
        presentationSpecs.add(InlinePresentationSpec.Builder(min, max).setStyle(stylesBundle).build())

        return InlineSuggestionsRequest.Builder(presentationSpecs)
            .setMaxSuggestionCount(6)
            .build()
    }

    fun createView(
        inlineSuggestions: List<InlineSuggestion>,
        context: Context
    ): InlineContentClipView {
        val container = LinearLayout(context)
        val scrollableSuggestionsClip = InlineContentClipView(context)
        scrollableSuggestionsClip.setInlineSuggestionContainer(container, inlineSuggestions.map { it.info })
        for (inlineSuggestion in inlineSuggestions) {
            val info = inlineSuggestion.info
            inlineSuggestion.inflate(
                context,
                Size(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                context.mainExecutor
            ) { view ->
                if (view != null) {
                    scrollableSuggestionsClip.addInlineSuggestion(info, view)
                }
            }
        }

        val inlineSuggestionView = HorizontalScrollView(context)
        inlineSuggestionView.isHorizontalScrollBarEnabled = false
        inlineSuggestionView.overScrollMode = View.OVER_SCROLL_NEVER
        inlineSuggestionView.addView(container)

        scrollableSuggestionsClip.addView(inlineSuggestionView)
        return scrollableSuggestionsClip
    }

    /**
     * This class is a container for showing {@link InlineContentView}s for cases
     * where you want to ensure they appear only in a given area in your app. An
     * example is having a scrollable list of items. Note that without this container
     * the InlineContentViews' surfaces would cover parts of your app as these surfaces
     * are owned by another process and always appearing on top of your app.
     */
    class InlineContentClipView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0
    ) : FrameLayout(context, attrs, defStyleAttr) {
        private var inlineSuggestionContainer: LinearLayout? = null
        private var inlineSuggestionInfos: List<InlineSuggestionInfo> = emptyList()
        private val inlineSuggestions = mutableListOf<Pair<InlineSuggestionInfo, View>>()
        private var smsOtpSuppressionEnabled = false

        private val mOnDrawListener = ViewTreeObserver.OnDrawListener {
            clipDescendantInlineContentViews()
        }
        private val mParentBounds = Rect()
        private val mContentBounds = Rect()

        init {
            val backgroundView = SurfaceView(context)
            backgroundView.setZOrderOnTop(true)
            backgroundView.holder.setFormat(PixelFormat.TRANSPARENT)
            addView(backgroundView)
        }

        fun setInlineSuggestionContainer(container: LinearLayout, infos: List<InlineSuggestionInfo>) {
            inlineSuggestionContainer = container
            inlineSuggestionInfos = infos
        }

        fun addInlineSuggestion(info: InlineSuggestionInfo, view: View) {
            if (smsOtpSuppressionEnabled && isSmsOtpInlineSuggestion(info.source, info.type, info.autofillHints)) {
                return
            }
            inlineSuggestions.add(info to view)
            inlineSuggestionContainer?.addView(view)
        }

        /** Hide only SMS OTP entries; leave passwords, cards, addresses, and Autofill actions alone. */
        fun suppressSmsOtpInlineSuggestions(): Int {
            smsOtpSuppressionEnabled = true
            inlineSuggestions.forEach { (info, view) ->
                if (isSmsOtpInlineSuggestion(info.source, info.type, info.autofillHints)) {
                    (view.parent as? ViewGroup)?.removeView(view)
                }
            }
            inlineSuggestions.removeAll { (info, _) ->
                isSmsOtpInlineSuggestion(info.source, info.type, info.autofillHints)
            }
            return inlineSuggestionInfos.count { info ->
                !isSmsOtpInlineSuggestion(info.source, info.type, info.autofillHints)
            }
        }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            viewTreeObserver.addOnDrawListener(mOnDrawListener)
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            viewTreeObserver.removeOnDrawListener(mOnDrawListener)
        }

        private fun clipDescendantInlineContentViews() {
            mParentBounds.right = width
            mParentBounds.bottom = height
            clipDescendantInlineContentViews(this)
        }

        private fun clipDescendantInlineContentViews(root: View?) {
            if (root == null) return
            if (root is InlineContentView) {
                mContentBounds.set(mParentBounds)
                offsetRectIntoDescendantCoords(root, mContentBounds)
                root.clipBounds = mContentBounds
                return
            }
            if (root is ViewGroup) {
                val childCount = root.childCount
                for (i in 0 until childCount) {
                    val child = root.getChildAt(i)
                    clipDescendantInlineContentViews(child)
                }
            }
        }
    }
}
