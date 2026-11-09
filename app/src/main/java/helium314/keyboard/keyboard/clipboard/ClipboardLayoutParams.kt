// SPDX-License-Identifier: GPL-3.0-only

package helium314.keyboard.keyboard.clipboard

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import helium314.keyboard.keyboard.internal.KeyboardBottomRowGeometry
import helium314.keyboard.latin.R
import helium314.keyboard.latin.settings.Settings
import helium314.keyboard.latin.utils.ResourceUtils

class ClipboardLayoutParams(ctx: Context) {

    private val keyVerticalGap: Int
    private val keyHorizontalGap: Int
    var listHeight: Int = 0
        private set
    var bottomRowKeyboardHeight: Int = 0
        private set
    var bottomRowKeyHeight: Int = 0
        private set

    init {
        val res = ctx.resources
        val sv = Settings.getValues()
        val defaultKeyboardHeight = ResourceUtils.getSecondaryKeyboardHeight(res, sv)
        val defaultKeyboardWidth = ResourceUtils.getKeyboardWidth(ctx, sv)

        val verticalGapStandard = res.getFraction(R.fraction.config_key_vertical_gap_holo,
            defaultKeyboardHeight, defaultKeyboardHeight)
        val horizontalGapStandard = res.getFraction(R.fraction.config_key_horizontal_gap_holo,
            defaultKeyboardWidth, defaultKeyboardWidth)

        if (sv.mNarrowKeyGaps) {
            val verticalGapNarrow = res.getFraction(R.fraction.config_key_vertical_gap_holo_narrow,
                defaultKeyboardHeight, defaultKeyboardHeight)
            val horizontalGapNarrow = res.getFraction(R.fraction.config_key_horizontal_gap_holo_narrow,
                defaultKeyboardWidth, defaultKeyboardWidth)

            val level = sv.mNarrowKeyGapsLevel
            val hasBorders = sv.mThemeKeyBorders

            if (level <= 0) {
                keyVerticalGap = verticalGapStandard.toInt()
                keyHorizontalGap = horizontalGapStandard.toInt()
            } else if (level == 1) {
                keyVerticalGap = verticalGapNarrow.toInt()
                keyHorizontalGap = horizontalGapNarrow.toInt()
            } else {
                var factor = ((10 - level).coerceIn(0, 9)) / 9f
                if (!hasBorders && factor < 0.1f) {
                    factor = 0.1f
                }
                keyVerticalGap = (verticalGapNarrow * factor).toInt()
                keyHorizontalGap = (horizontalGapNarrow * factor).toInt()
            }
        } else {
            keyVerticalGap = verticalGapStandard.toInt()
            keyHorizontalGap = horizontalGapStandard.toInt()
        }

        updateDimensions(ctx)
    }

    fun updateDimensions(ctx: Context) {
        val dims = KeyboardBottomRowGeometry.getDimensions(ctx, Settings.getValues())
        listHeight = dims.bottomRowY
        bottomRowKeyboardHeight = dims.bottomRowAreaHeight
        bottomRowKeyHeight = dims.bottomRowKeyHeight
    }

    fun setListProperties(recycler: RecyclerView) {
        (recycler.layoutParams as? FrameLayout.LayoutParams)?.apply {
            height = FrameLayout.LayoutParams.MATCH_PARENT
            recycler.layoutParams = this
        }
    }

    fun setItemProperties(view: View) {
        (view.layoutParams as? RecyclerView.LayoutParams)?.apply {
            topMargin = keyHorizontalGap / 2
            bottomMargin = keyVerticalGap / 2
            marginStart = keyHorizontalGap / 2
            marginEnd = keyHorizontalGap / 2
            view.layoutParams = this
        }
    }
}
