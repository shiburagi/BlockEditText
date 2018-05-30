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
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
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
            filters[0] = new LengthFilter(editText, length, i);
            editText.setFilters(filters);
            editText.setLayoutParams(params);
            editText.setGravity(Gravity.CENTER);

            editText.addTextChangedListener(createTextChangeListener(editText, i));

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
            } else
                editText.setCustomSelectionActionModeCallback(callback);

            blockLinearLayout.addView(editText);

        }


    }

    private TextWatcher createTextChangeListener(final AEditText editText, final int index) {
        return new TextWatcher() {
            int prevLength = 0;

            int selection = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevLength = s.length();
                selection = editText.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText nextView = (EditText) blockLinearLayout.getChildAt(index + 1);
                EditText prevView = (EditText) blockLinearLayout.getChildAt(index - 1);
                if (s.length() > prevLength && editText.isFocused() && editText.getSelectionStart() == getLength(index))
                    if (s.length() == getLength(index) && nextView != null && nextView.getText().length() == 0)
                        nextView.requestFocus();
                    else if (s.length() == 0 && prevView != null)
                        prevView.requestFocus();

                if (s.length() < getLength(index)) {
                    if (editText.getSelectionStart() == 0 && editText.isFocused() && prevView != null) {
                        prevView.requestFocus();
                        prevView.setSelection(prevView.getText().length());
                    }
                    if (nextView != null && !nextView.getText().toString().isEmpty()) {
                        int length = getLength(index) - s.length();
                        length = length > nextView.getText().length() ? nextView.getText().length() : length;
                        Editable editable = nextView.getText();
                        String temp = editable.toString().substring(0, length);
                        editable = editable.delete(0, length);
                        editText.append(temp);
                        editText.setSelection(selection);
                        nextView.setText(editable);

                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }


    public void setText(CharSequence sequence) {
        int i = 0;
        if (sequence != null) {
            String text = String.valueOf(sequence).replaceAll("[\\W]", "");
//            if (inputType == InputType.TYPE_CLASS_NUMBER){
//                text = text.replaceAll("[\\D]", "");
//            }else{
//                text = text.replaceAll("[\\W]", "");
//            }
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

    public class LengthFilter implements InputFilter {
        private final int mMax;
        private final int index;
        private final AEditText editText;

        public LengthFilter(AEditText editText, int max, int index) {
            mMax = max;
            this.index = index;
            this.editText = editText;
        }

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {
            int keep = mMax - (dest.length() - (dend - dstart));
            EditText nextView = (EditText) blockLinearLayout.getChildAt(index + 1);
            if (keep <= 0) {
                if (nextView != null) {
                    String s = source.toString();
                    String temp = editText.getText().toString();
                    int selection = editText.getSelectionStart();
                    temp = temp.substring(0, selection) + source + temp.substring(selection);
                    editText.setText(temp.substring(0, mMax));
                    nextView.getEditableText().insert(0, temp.substring(mMax));

                    if (selection + source.length() < mMax)
                        editText.setSelection(selection + source.length());
                    else {
                        nextView.setSelection(0);
                        nextView.requestFocus();
                    }
                }
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                keep += start;
                if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                    --keep;
                    if (keep == start) {
                        return "";
                    }
                }
                return source.subSequence(start, keep);
            }
        }

        /**
         * @return the maximum length enforced by this input filter
         */
        public int getMax() {
            return mMax;
        }
    }
}
