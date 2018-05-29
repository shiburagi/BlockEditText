package com.infideap.blockedittext;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.app.infideap.stylishwidget.view.AEditText;
import com.app.infideap.stylishwidget.view.ATextView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class BlockEditText extends FrameLayout {
    private int noOfBlock = 1;
    private LinearLayout linearLayout;
    private LinearLayout blockLinearLayout;
    private SparseArray<Integer> lengths = new SparseArray<>();
    private int defaultLength = 1;
    private ATextView hintTextView;
    private int inputType;

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

            ActionMode.Callback callback = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {

                        case android.R.id.paste:
                            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                            if (clipboard != null) {
                                CharSequence sequence = clipboard.getPrimaryClip().getItemAt(0).getText();
                                setText(sequence);
                            }
                            return true;

                        default:
                            break;
                    }

                    return false;
                }


                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editText.setCustomInsertionActionModeCallback(callback);
            }else
                editText.setCustomSelectionActionModeCallback(callback);

            blockLinearLayout.addView(editText);

        }


    }


    public void setText(CharSequence sequence) {
        int i = 0;
        if (sequence != null) {
            String text = String.valueOf(sequence);
            if (inputType == InputType.TYPE_CLASS_NUMBER){
                text = text.replaceAll("[\\D]", "");
            }
            for (; i < blockLinearLayout.getChildCount() && !TextUtils.isEmpty(text); i++) {

                AEditText editText = (AEditText) blockLinearLayout.getChildAt(i);
                int length = getLength(i) > text.length() ? text.length() : getLength(i);
                editText.setText(text.substring(0, length));
                editText.setSelection(length);
                text = text.substring(length);
                editText.requestFocus();
            }
        }

        for (; i < blockLinearLayout.getChildCount(); i++) {
            AEditText editText = (AEditText) blockLinearLayout.getChildAt(i);
            editText.setText(null);
        }
    }

    private int getLength(int i) {
        return lengths.get(i, defaultLength);
    }

    public void setInputType(int type) {
        inputType = type;
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

    public void setHint(String hint) {
        if (hintTextView == null) {
            hintTextView = new ATextView(getContext());
            linearLayout.addView(hintTextView, 0);
        }
        hintTextView.setHint(hint);

    }
}
