/*
 * SPDX-License-Identifier: GPL-3.0-only
 */
package helium314.keyboard.latin

import helium314.keyboard.latin.dictionary.Dictionary
import java.util.Locale

internal object ProfanityLexicon {
    private val blockedWords = setOf(
        "бля", "блядь", "блять", "бляди", "блядина", "блядский", "блядство", "блядью", "блядей",
        "хуй", "хуя", "хую", "хуем", "хуе", "хуё", "хуёвый", "хуёвая", "хуёвое", "хуйня", "хуйни", "хуйнёй",
        "нахуй", "нахуя", "нихуя", "охуеть", "охуенный", "охуенная", "охуенно",
        "пизда", "пизды", "пизде", "пизду", "пиздой", "пиздец", "пиздеца", "пиздатый", "пиздатая",
        "ебать", "ебу", "ебёт", "ебет", "ебал", "ебала", "ебаный", "ебаная", "ебанутый", "ебанутая", "ебануто",
        "заебать", "заебал", "наебать", "наебал", "отъебаться", "отъебись",
        "сука", "суки", "суку", "сукой", "сучара", "сучары",
        "мудак", "мудака", "мудила", "мудозвон",
        "говно", "говна", "говне", "говном", "дерьмо", "дерьмище",
        "хер", "хера", "херу", "хером", "херня", "херни"
    )

    fun shouldBlock(
        word: String,
        sourceType: String,
        blockPotentiallyOffensive: Boolean,
        isPossiblyOffensive: Boolean
    ): Boolean {
        if (!blockPotentiallyOffensive || sourceType == Dictionary.TYPE_USER) return false
        return isPossiblyOffensive || blockedWords.contains(normalize(word))
    }

    private fun normalize(word: String): String =
        word.lowercase(Locale.ROOT).replace('ё', 'е')
}
