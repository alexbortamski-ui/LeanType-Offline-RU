// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.cjk

/**
 * Represents a single candidate option returned by a CJK engine.
 */
data class CjkCandidate(
    val index: Int,
    val text: String,
    val comment: String? = null
) {
    constructor(index: Int, text: String) : this(index, text, null)
}

/**
 * State snapshot of the composition engine holding pre-edit text and candidate options.
 */
data class CjkContextData(
    val preedit: String = "",
    val candidates: List<CjkCandidate> = emptyList(),
    val selectedIndex: Int = 0,
    val hasPrevPage: Boolean = false,
    val hasNextPage: Boolean = false
)
