package com.infideap.blockedittext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;

public class CardPrefix {


    private String[] prefixes;
    private Drawable icon;
    private SparseIntArray lengths = new SparseIntArray();

    public CardPrefix(Drawable icon, String... prefix) {
        this.prefixes = prefix;
        this.icon = icon;
    }

    public CardPrefix(Context context, int iconRes, String... prefix) {
        this(ContextCompat.getDrawable(context, iconRes), prefix);
    }

    public void lengthAt(int index, int length) {
        lengths.put(index, length);
    }

    public SparseIntArray getLengths() {
        return lengths;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String[] getPrefixes() {
        return prefixes;
    }

    public static CardPrefix visa(Context context) {
        CardPrefix cardPrefix = visa(ContextCompat.getDrawable(context, R.drawable.ic_visa));
        cardPrefix.lengthAt(0, 5);
        cardPrefix.lengthAt(1, 6);
        cardPrefix.lengthAt(2, 5);
        return cardPrefix;
    }

    public static CardPrefix visa(Drawable drawable) {
        return new CardPrefix(drawable, "4");
    }

    public static CardPrefix amex(Context context) {
        CardPrefix cardPrefix = amex(ContextCompat.getDrawable(context, R.drawable.ic_amex));
        cardPrefix.lengthAt(0, 4);
        cardPrefix.lengthAt(1, 6);
        cardPrefix.lengthAt(2, 5);
        return cardPrefix;
    }

    public static CardPrefix amex(Drawable drawable) {
        return new CardPrefix(drawable, "34", "37");
    }

    public static CardPrefix mastercard(Context context) {
        CardPrefix cardPrefix =
                mastercard(ContextCompat.getDrawable(context, R.drawable.ic_mastercard));
        cardPrefix.lengthAt(0, 5);
        cardPrefix.lengthAt(1, 6);
        cardPrefix.lengthAt(2, 5);
        return cardPrefix;
    }

    public static CardPrefix mastercard(Drawable drawable) {
        return new CardPrefix(drawable, "50", "51", "52", "53", "54", "55");
    }


}
