/*
 * Copyright (C) 2013 The Android Open Source Project
 * modified
 * SPDX-License-Identifier: Apache-2.0 AND GPL-3.0-only
 */
package helium314.keyboard.keyboard.emoji

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import helium314.keyboard.keyboard.internal.KeyboardBottomRowGeometry
import helium314.keyboard.latin.settings.Settings

internal class EmojiLayoutParams(context: Context) {
    var emojiKeyboardHeight: Int = 0
        private set
    var bottomRowKeyboardHeight: Int = 0
        private set
    var bottomRowKeyHeight: Int = 0
        private set

    init {
        updateDimensions(context)
    }

    fun updateDimensions(context: Context) {
        val dims = KeyboardBottomRowGeometry.getDimensions(context, Settings.getValues())
        emojiKeyboardHeight = dims.bottomRowY
        bottomRowKeyboardHeight = dims.bottomRowAreaHeight
        bottomRowKeyHeight = dims.bottomRowKeyHeight
    }

    fun setEmojiListProperties(vp: ViewPager2) {
        val lp = vp.layoutParams as? LinearLayout.LayoutParams ?: return
        lp.height = emojiKeyboardHeight
        lp.bottomMargin = 0
        vp.layoutParams = lp
    }

    fun setCategoryPageIdViewProperties(v: View) {
        val lp = v.layoutParams as? LinearLayout.LayoutParams ?: return
        lp.height = 0
        v.layoutParams = lp
    }
}
