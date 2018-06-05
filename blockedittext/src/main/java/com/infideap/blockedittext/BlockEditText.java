package com.infideap.blockedittext;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.app.infideap.stylishwidget.view.AEditText;
import com.app.infideap.stylishwidget.view.ATextView;

public class BlockEditText extends FrameLayout {
    private int noOfBlock = 1;
    private LinearLayout linearLayout;
    private LinearLayout blockLinearLayout;
    private SparseArray<Integer> lengths = new SparseArray<>();
    private int defaultLength = 1;
    private ATextView hintTextView;
    private int inputType = InputType.TYPE_CLASS_TEXT;
    private TextWatcher watcher;
    private ActionMode.Callback callback;
    private SparseArray<AEditText> editTexts = new SparseArray<>();
    private Character separator;
    private int separatorTextAppearance = android.support.v7.appcompat.R.style.Base_TextAppearance_AppCompat_Medium;
    private int separatorPadding = 16;
    private float separatorTextSize;
    private float textSize;
    private float hintTextSize;
    private int textAppearance = android.support.v7.appcompat.R.style.Base_TextAppearance_AppCompat_Medium;
    private int hintTextAppearance = android.support.v7.appcompat.R.style.Base_TextAppearance_AppCompat_Medium;
    private Drawable editTextBackground;
    private String hint;
    private ColorStateList hintColorDefault;
    private ColorStateList hintColorFocus;

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

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlockEditText);
        editTextBackground = a.getDrawable(R.styleable.BlockEditText_bet_editTextBackground);
        hint = a.getString(R.styleable.BlockEditText_bet_hint);
        setHint(hint);

        String tempStr = a.getString(R.styleable.BlockEditText_bet_separatorCharacter);
        if (!TextUtils.isEmpty(tempStr)) {
            separator = tempStr.charAt(0);
        }

        noOfBlock = a.getInt(
                R.styleable.BlockEditText_bet_numberOfBlock,
                noOfBlock
        );

        defaultLength = a.getInt(
                R.styleable.BlockEditText_bet_defaultLength,
                defaultLength
        );

        textAppearance = a.getResourceId(
                R.styleable.BlockEditText_bet_hintTextAppearance,
                textAppearance
        );

        hintTextAppearance = a.getResourceId(
                R.styleable.BlockEditText_bet_hintTextAppearance,
                hintTextAppearance
        );

        separatorTextAppearance = a.getResourceId(
                R.styleable.BlockEditText_bet_separatorTextAppearance,
                separatorTextAppearance
        );

        textSize = a.getDimension(
                R.styleable.BlockEditText_bet_textSize,
                textSize
        );


        hintTextSize = a.getDimension(
                R.styleable.BlockEditText_bet_hintTextSize,
                hintTextSize
        );

        separatorTextSize = a.getDimension(
                R.styleable.BlockEditText_bet_separatorTextSize,
                separatorTextSize
        );


        separatorPadding = a.getDimensionPixelOffset(
                R.styleable.BlockEditText_bet_hintTextSize,
                separatorPadding
        );

        inputType = a.getInt(
                R.styleable.BlockEditText_bet_inputType,
                inputType
        );


        setHintTextAppearance(hintTextAppearance);


        initLayout();
        tempStr = a.getString(R.styleable.BlockEditText_bet_text);
        if (tempStr != null)
            setText(tempStr);
        a.recycle();

    }

    private ViewGroup.LayoutParams createWidthMatchParentLayoutParams() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private void initLayout() {
        blockLinearLayout.removeAllViews();
        int i = 0;
        String text = getText();
        for (; i < noOfBlock; i++) {

            final int length = getLength(i);
            final AEditText editText;
            if (editTexts.get(i) == null) {
                editText = new AEditText(getContext());
                editText.addTextChangedListener(createTextChangeListener(editText, i));
                editTexts.put(i, editText);
                editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hintTextView != null)
                            hintTextView.setHintTextColor(hasFocus ? hintColorFocus : hintColorDefault);
                    }
                });

                editText.setSupportTextAppearance(textAppearance);
                setTextSize(editText, textSize);
                editText.setOnKeyListener(createKeyListener(editText, i));

            } else {
                editText = editTexts.get(i);
            }

            editText.setInputType(inputType);
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new LengthFilter(editText, length, i);
            editText.setFilters(filters);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, length
            );
            editText.setLayoutParams(params);
            editText.setGravity(Gravity.CENTER);
            blockLinearLayout.addView(editText);
            if (editTextBackground != null)
                setEdiTextBackground(editText, editTextBackground);

            if (i + 1 < noOfBlock && separator != null) {
                ATextView textView = new ATextView(getContext());
                textView.setText(String.valueOf(separator));
                LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                );
                textViewParams.gravity = Gravity.CENTER;
                textView.setLayoutParams(textViewParams);
                textView.setPadding(separatorPadding, 0, separatorPadding, 0);
                textView.setSupportTextAppearance(separatorTextAppearance);
                if (separatorTextSize > 0)
                    textView.setTextSize(separatorTextSize);
                blockLinearLayout.addView(textView);
            }

            editText.setText(null);

            if (i > 0)
                editText.setEnabled(false);


        }
        for (; i < editTexts.size(); ) {
            editTexts.remove(i);
        }

        setText(text);

    }

    private OnKeyListener createKeyListener(final AEditText editText, final int i) {
        return new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (editText.getSelectionStart() == 0 && editText.getSelectionEnd() == 0) {
                        AEditText prevEditText = editTexts.get(i - 1);
                        if (prevEditText != null) {
                            prevEditText.requestFocus();
                            prevEditText.getEditableText().delete(prevEditText.getText().length() - 1, prevEditText.getText().length());
                            prevEditText.setSelection(prevEditText.getText().length() - 1);
                            return true;

                        }
                    }
                }
                return false;
            }
        };
    }


    private ActionMode.Callback createActionModeCallback(AEditText editText) {
        return new ActionMode.Callback() {
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

                return false;
            }


            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        };
    }

    private TextWatcher createTextChangeListener(final AEditText editText, final int index) {
        return new TextWatcher() {
            int prevLength = 0;

            int selection = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevLength = s.length();
                selection = editText.getSelectionStart();
                BlockEditText.this.beforeTextChanged(s, start, count, after);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText nextView = editTexts.get(index + 1);
                EditText prevView = editTexts.get(index - 1);
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
                BlockEditText.this.onTextChanged(s, start, before, count);


            }

            @Override
            public void afterTextChanged(Editable s) {
                EditText nextView = editTexts.get(index + 1);

                if (nextView != null)
                    nextView.setEnabled(s.length() >= getLength(index));

                BlockEditText.this.afterTextChanged(s);
            }
        };
    }

    private void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (watcher != null) watcher.beforeTextChanged(s, start, count, after);
    }

    private void onTextChanged(CharSequence s, int start, int before, int count) {
        if (watcher != null) watcher.onTextChanged(s, start, before, count);

    }

    private void afterTextChanged(Editable s) {
        if (watcher != null) watcher.afterTextChanged(s);

    }

    public void setTextChangedListener(TextWatcher watcher) {
        this.watcher = watcher;
    }


    public void setCustomInsertionActionModeCallback(ActionMode.Callback callback) {
        this.callback = callback;
        for (int i = 0; i < editTexts.size(); i++) {
            AEditText editText = editTexts.get(i);
            setCustomInsertionActionModeCallback(editText, callback);
        }
    }

    private void setCustomInsertionActionModeCallback(AEditText editText, ActionMode.Callback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editText.setCustomInsertionActionModeCallback(callback);
        } else
            editText.setCustomSelectionActionModeCallback(callback);
    }

    public void setSeparatorCharacter(Character separator) {
        this.separator = separator;
        initLayout();
    }

    public void setText(CharSequence sequence) {
        int i = 0;
        for (; i < editTexts.size(); i++) {
            AEditText editText = editTexts.get(i);
            editText.setText(null);
        }
        if (sequence != null) {
            String text = String.valueOf(sequence);
            AEditText editText = editTexts.get(0);
            editText.getEditableText().insert(0, text);

        }


    }

    public String getText() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < editTexts.size(); i++) {

            AEditText editText = editTexts.get(i);
            builder.append(editText.getText());

        }

        return builder.toString();
    }

    private int getLength(int i) {
        return lengths.get(i, defaultLength);
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        for (int i = 0; i < editTexts.size(); i++) {
            AEditText editText = editTexts.get(i);
            setTextSize(editText, textSize);
        }
    }

    private void setTextSize(AEditText editText, float textSize) {
        if (textSize > 0)
            editText.setTextSize(textSize);
    }

    public void setTextAppearance(int textAppearance) {
        this.textAppearance = textAppearance;
        for (int i = 0; i < editTexts.size(); i++) {
            AEditText editText = editTexts.get(i);
            editText.setSupportTextAppearance(textAppearance);
        }
    }

    public void setInputType(int type) {
        inputType = type;
        for (int i = 0; i < editTexts.size(); i++) {
            AEditText editText = editTexts.get(i);
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
        for (int i = 0; i < editTexts.size(); i++) {
            AEditText editText = editTexts.get(i);

            setEdiTextBackground(editText, drawable);
        }
    }

    private void setEdiTextBackground(AEditText editText, Drawable drawable) {
        ViewCompat.setBackground(editText, drawable.getConstantState().newDrawable());
    }

    public void setLengthAt(int index, int length) {
        lengths.put(index, length);
        initLayout();
    }

    public void setHint(String hint) {
        if (hintTextView == null) {
            hintTextView = new ATextView(getContext());
            hintTextView.setPadding(16, 0, 16, 0);
            setHintTextAppearance(hintTextAppearance);
            setHintTextSize(hintTextAppearance);
            linearLayout.addView(hintTextView, 0);
            hintColorDefault = hintTextView.getHintTextColors();
            hintColorFocus = ContextCompat.getColorStateList(
                    getContext(),
                    R.color.colorAccent
            );


        }
        hintTextView.setVisibility(hint == null ? GONE : VISIBLE);
        hintTextView.setHint(hint);


    }

    public void setHintTextSize(float textSize) {
        this.hintTextSize = textSize;
        if (hintTextView != null && textSize > 0) {
            hintTextView.setTextSize(textSize);
        }
    }

    public void setHintTextAppearance(int textAppearance) {
        this.hintTextAppearance = textAppearance;
        if (hintTextView != null) {
            hintTextView.setSupportTextAppearance(textAppearance);
        }
    }


    public void setSeparatorTextAppearance(int textAppearance) {
        this.separatorTextAppearance = textAppearance;
        initLayout();
    }

    public void setSeparatorPadding(int padding) {
        this.separatorPadding = padding;
        initLayout();
    }

    public void setSeparatorTextSize(float textSize) {
        this.separatorTextSize = textSize;
        initLayout();
    }

    public void setSelection(int selection) {
        for (int i = 0; i < editTexts.size(); i++) {
            int length = getLength(i);
            if (selection < length) {
                AEditText editText = editTexts.get(i);
                editText.requestFocus();
                editText.setSelection(selection);

                break;
            }
            selection -= length;
        }
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
            if (editText.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                source = String.valueOf(source).replaceAll("[\\D]", "");
            } else {
                source = String.valueOf(source).replaceAll("[\\W]", "");
            }

            int keep = mMax - (dest.length() - (dend - dstart));

            EditText nextView = editTexts.get(index + 1);
            if (source.length() == 0)
                return null;
            if (keep <= 0) {
                if (nextView != null) {
                    String s = source.toString();
                    String temp = editText.getText().toString();

                    int selection = editText.getSelectionStart();
                    temp = temp.substring(0, selection) + source + temp.substring(selection);
                    editText.setText(temp.substring(0, mMax));

                    temp = temp.substring(mMax);
                    if (selection + source.length() <= mMax)
                        editText.setSelection(selection + source.length());
                    else {
                        nextView.requestFocus();
                        nextView.setSelection(0);

                    }
                    if (temp.length() > 0) {
                        nextView.getEditableText().insert(0, temp);
                        int nextLength = getLength(index + 1);
                        nextView.setSelection(temp.length() < nextLength ? temp.length() : nextLength);
                    } else
                        nextView.setSelection(0);

                }
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                if (source.length() > keep)
                    if (nextView != null) {
                        String s = source.toString();
                        String temp = editText.getText().toString();
                        int selection = editText.getSelectionStart();
                        temp = temp.substring(0, selection) + source + temp.substring(selection);
                        editText.setText(temp.substring(0, mMax));

                        temp = temp.substring(mMax);
                        if (selection + source.length() <= mMax)
                            editText.setSelection(selection + source.length());
                        else {
                            nextView.requestFocus();
                            nextView.setSelection(0);

                        }
                        if (temp.length() > 0) {
                            nextView.getEditableText().insert(0, temp);
                            int nextLength = getLength(index + 1);
                            nextView.setSelection(temp.length() < nextLength ? temp.length() : nextLength);
                        } else
                            nextView.setSelection(0);
                        return "";
                    }

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
