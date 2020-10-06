package com.infideap.blockedittext

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.SparseIntArray
import androidx.core.content.ContextCompat

class CardPrefix(icon: Drawable?, vararg prefix: String) {
    val prefixes: Array<out String> = prefix
    val icon: Drawable? = icon
    val lengths = SparseIntArray()

    constructor(context: Context?, iconRes: Int, vararg prefix: String) : this(ContextCompat.getDrawable(context!!, iconRes), *prefix) {}

    fun lengthAt(index: Int, length: Int) {
        lengths.put(index, length)
    }

    companion object {
        @JvmStatic
        fun visa(context: Context?): CardPrefix {
            val cardPrefix = visa(ContextCompat.getDrawable(context!!, R.drawable.ic_visa))
            cardPrefix.lengthAt(0, 5)
            cardPrefix.lengthAt(1, 6)
            cardPrefix.lengthAt(2, 5)
            return cardPrefix
        }
        @JvmStatic
        fun visa(drawable: Drawable?): CardPrefix {
            return CardPrefix(drawable, "4")
        }
        @JvmStatic
        fun amex(context: Context?): CardPrefix {
            val cardPrefix = amex(ContextCompat.getDrawable(context!!, R.drawable.ic_amex))
            cardPrefix.lengthAt(0, 4)
            cardPrefix.lengthAt(1, 6)
            cardPrefix.lengthAt(2, 5)
            return cardPrefix
        }
        @JvmStatic
        fun amex(drawable: Drawable?): CardPrefix {
            return CardPrefix(drawable, "34", "37")
        }
        @JvmStatic
        fun mastercard(context: Context?): CardPrefix {
            val cardPrefix = mastercard(ContextCompat.getDrawable(context!!, R.drawable.ic_mastercard))
            cardPrefix.lengthAt(0, 5)
            cardPrefix.lengthAt(1, 6)
            cardPrefix.lengthAt(2, 5)
            return cardPrefix
        }
        @JvmStatic
        fun mastercard(drawable: Drawable?): CardPrefix {
            return CardPrefix(drawable, "50", "51", "52", "53", "54", "55")
        }
    }

}