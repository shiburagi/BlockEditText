package com.infideap.blockedittext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.app.infideap.stylishwidget.view.AEditText;

public class BlockEditText extends FrameLayout {
    private int noOfBlock = 1;
    private LinearLayout linearLayout;
    private LinearLayout blockLinearLayout;
    private SparseArray<Integer> lengths = new SparseArray<>();
    private int defaultLength = 1;

    public BlockEditText(@NonNull Context context) {
        super(context);
        init(context, null);

    }

    public BlockEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BlockEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BlockEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(createWidthMatchParentLayoutParams());

        blockLinearLayout = new LinearLayout(getContext());
        blockLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        blockLinearLayout.setLayoutParams(createWidthMatchParentLayoutParams());

        linearLayout.addView(blockLinearLayout);

        addView(linearLayout);
        initLayout();
    }

    private ViewGroup.LayoutParams createWidthMatchParentLayoutParams() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private void initLayout() {
        blockLinearLayout.removeAllViews();

        for (int i = 0; i < noOfBlock; i++) {

            final int length = getLength(i);
            final AEditText editText = new AEditText(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, length
            );
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(length);
            editText.setFilters(filters);
            editText.setLayoutParams(params);
            editText.setGravity(Gravity.CENTER);

            final int finalI = i;
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (start != before)
                        if (s.length() == getLength(finalI) && blockLinearLayout.getChildAt(finalI + 1) != null)
                            blockLinearLayout.getChildAt(finalI + 1).requestFocus();
                        else if (s.length() == 0 && blockLinearLayout.getChildAt(finalI - 1) != null)
                            blockLinearLayout.getChildAt(finalI - 1).requestFocus();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            blockLinearLayout.addView(editText);

        }


    }

    private int getLength(int i) {
        return lengths.get(i, defaultLength);
    }

    public void setInputType(int type) {
        for (int i = 0; i < blockLinearLayout.getChildCount(); i++) {
            AEditText editText = (AEditText) blockLinearLayout.getChildAt(i);
            editText.setInputType(type);
        }
    }

    public void setNumberOfBlock(int block) {
        this.noOfBlock = block;
        initLayout();
    }

    public void setDefaultLength(int defaultLength) {
        this.defaultLength = defaultLength;
        initLayout();
    }

    public void setEdiTextBackground(Drawable drawable) {
        for (int i = 0; i < blockLinearLayout.getChildCount(); i++) {
            AEditText editText = (AEditText) blockLinearLayout.getChildAt(i);
            ViewCompat.setBackground(editText, drawable.getConstantState().newDrawable());
        }
    }

    public void setLengthAt(int index, int length) {
        lengths.put(index, length);
        initLayout();
    }
}
