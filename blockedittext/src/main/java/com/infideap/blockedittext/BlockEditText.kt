package com.infideap.blockedittext

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.*
import android.util.AttributeSet
import android.util.SparseArray
import android.util.SparseIntArray
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.View.OnKeyListener
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.app.infideap.stylishwidget.util.Utils
import com.app.infideap.stylishwidget.view.AEditText
import com.app.infideap.stylishwidget.view.ATextView
import java.util.*

class BlockEditText : FrameLayout {
    private var noOfBlock = 1
    private var linearLayout: LinearLayout? = null
    private var blockLinearLayout: LinearLayout? = null
    private val lengths = SparseIntArray()
    private var lengthUsed: SparseIntArray? = null
    private var defaultLength = 1
    private var hintTextView: ATextView? = null
    private var inputType = InputType.TYPE_CLASS_TEXT
    private var watcher: TextWatcher? = null
    private var callback: ActionMode.Callback? = null
    private val editTexts = SparseArray<AEditText?>()
    private var separator: Char? = null
    private var separatorTextAppearance = androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Medium
    private var separatorPadding = 16
    private var separatorTextSize = 0f
    private var textSize = 0f
    private var hintTextSize = 0f
    private var textAppearance = androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Medium
    private var hintTextAppearance = androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Medium
    private var editTextBackground: Drawable? = null
    private var hint: String? = null
    private var hintColorDefault: ColorStateList? = null
    private var hintColorFocus: ColorStateList? = null
    private var shiftPosition = true
    private val cardPrefixes: MutableList<CardPrefix> = ArrayList()
    private var iconImageView: ImageView? = null
    private var cardPrefixListener: OnCardPrefixListener? = null
    private var cardIconSize = Utils.convertDpToPixel(48f).toInt()
    private var isEnabled = true
    private var isShowCardIcon = false
    private var editTextStyle = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        lengthUsed = lengths
        linearLayout = LinearLayout(getContext())
        linearLayout!!.orientation = LinearLayout.VERTICAL
        linearLayout!!.layoutParams = createWidthMatchParentLayoutParams()
        blockLinearLayout = LinearLayout(getContext())
        blockLinearLayout!!.orientation = LinearLayout.HORIZONTAL
        blockLinearLayout!!.layoutParams = createWidthMatchParentLayoutParams()
        linearLayout!!.addView(blockLinearLayout)
        addView(linearLayout)
        val a = context.obtainStyledAttributes(attrs, R.styleable.BlockEditText)
        editTextBackground = a.getDrawable(R.styleable.BlockEditText_editTextBackground)
        hint = a.getString(R.styleable.BlockEditText_hint)
        setHint(hint)
        var tempStr = a.getString(R.styleable.BlockEditText_separatorCharacter)
        if (!TextUtils.isEmpty(tempStr)) {
            separator = tempStr!![0]
        }
        noOfBlock = a.getInt(
                R.styleable.BlockEditText_numberOfBlock,
                noOfBlock
        )
        defaultLength = a.getInt(
                R.styleable.BlockEditText_defaultLength,
                defaultLength
        )
        textAppearance = a.getResourceId(
                R.styleable.BlockEditText_hintTextAppearance,
                textAppearance
        )
        hintTextAppearance = a.getResourceId(
                R.styleable.BlockEditText_hintTextAppearance,
                hintTextAppearance
        )
        separatorTextAppearance = a.getResourceId(
                R.styleable.BlockEditText_separatorTextAppearance,
                separatorTextAppearance
        )
        textSize = a.getDimension(
                R.styleable.BlockEditText_textSize,
                textSize
        )
        hintTextSize = a.getDimension(
                R.styleable.BlockEditText_hintTextSize,
                hintTextSize
        )
        separatorTextSize = a.getDimension(
                R.styleable.BlockEditText_separatorTextSize,
                separatorTextSize
        )
        cardIconSize = a.getDimensionPixelOffset(
                R.styleable.BlockEditText_cardIconSize,
                cardIconSize
        )
        separatorPadding = a.getDimensionPixelOffset(
                R.styleable.BlockEditText_hintTextSize,
                separatorPadding
        )
        inputType = a.getInt(
                R.styleable.BlockEditText_inputType,
                inputType
        )
        shiftPosition = a.getBoolean(
                R.styleable.BlockEditText_showCardIcon,
                true
        )
        isShowCardIcon = a.getBoolean(
                R.styleable.BlockEditText_showCardIcon,
                true
        )
        editTextStyle = a.getResourceId(
                R.styleable.BlockEditText_style, -1
        )
        val cardPrefix = a.getInt(
                R.styleable.BlockEditText_cardPrefix,
                0
        )
        if (containsFlag(cardPrefix, AMEX)) {
            cardPrefixes.add(CardPrefix.amex(getContext()))
        }
        if (containsFlag(cardPrefix, MASTERCARD)) {
            cardPrefixes.add(CardPrefix.mastercard(getContext()))
        }
        if (containsFlag(cardPrefix, VISA)) {
            cardPrefixes.add(CardPrefix.visa(getContext()))
        }
        setHintTextAppearance(hintTextAppearance)
        shiftPosition = a.getBoolean(
                R.styleable.BlockEditText_shiftPosition,
                true
        )
        initLayout()
        tempStr = a.getString(R.styleable.BlockEditText_text)
        if (tempStr != null) text = tempStr
        a.recycle()
    }

    private fun containsFlag(flagSet: Int, flag: Int): Boolean {
        return flagSet or flag == flagSet
    }

    private fun createWidthMatchParentLayoutParams(): ViewGroup.LayoutParams {
        return LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun initLayout() {
        blockLinearLayout!!.removeAllViews()
        var i = 0
        var text: String = text.toString()
        while (i < noOfBlock) {
            val length = getLength(i)
            val editText: AEditText?
            if (editTexts[i] == null) {
                editText = if (editTextStyle == -1) {
                    AEditText(context)
                } else {
                    AEditText(context, null, editTextStyle)
                }
                editText.addTextChangedListener(createTextChangeListener(editText, i))
                editTexts.put(i, editText)
                editText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                    if (hintTextView != null)
                        hintTextView!!.setHintTextColor(
                                if (hasFocus) hintColorFocus else hintColorDefault
                        ) }
                editText.setSupportTextAppearance(textAppearance)
                setTextSize(editText, textSize)
                editText.setOnKeyListener(createKeyListener(editText, i))
            } else {
                editText = editTexts[i]
            }
            editText!!.inputType = inputType
            val filters = arrayOfNulls<InputFilter>(1)
            filters[0] = LengthFilter(editText, i)
            editText.filters = filters
            val params = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, length.toFloat()
            )
            editText.layoutParams = params
            editText.gravity = Gravity.CENTER
            blockLinearLayout!!.addView(editText)
            if (editTextBackground != null) setEdiTextBackground(editText, editTextBackground!!)
            if (i + 1 < noOfBlock && separator != null) {
                val textView = ATextView(context)
                textView.text = separator.toString()
                val textViewParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                textViewParams.gravity = Gravity.CENTER
                textView.layoutParams = textViewParams
                textView.setPadding(separatorPadding, 0, separatorPadding, 0)
                textView.setSupportTextAppearance(separatorTextAppearance)
                if (separatorTextSize > 0) textView.textSize = separatorTextSize
                blockLinearLayout!!.addView(textView)
            }
            editText.text = null
            setEditTextEnable(editText, i)
            i++
        }
        while (i < editTexts.size()) {
            editTexts.remove(i)
        }
        this.text = text
        hideOrShowCardIcon()
    }

    private fun setEditTextEnable(editText: AEditText?, i: Int) {
        if (shiftPosition && i > 0) editText!!.isEnabled = false else editText!!.isEnabled = isEnabled
    }

    fun getText(editText: EditText): Editable {
        return editText.text
    }

    private fun createKeyListener(editText: AEditText, i: Int): OnKeyListener {
        return OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (editText.selectionStart == 0 && editText.selectionEnd == 0) {
                    val prevEditText = editTexts[i - 1]
                    if (prevEditText != null) {
                        prevEditText.requestFocus()
                        if (editText.length() > 0) {
                            prevEditText.editableText.delete(getText(prevEditText).length - 1, prevEditText.text!!.length)
                            prevEditText.setSelection(prevEditText.text!!.length - 1)
                        }
                        return@OnKeyListener true
                    }
                }
            }
            false
        }
    }

    private fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (watcher != null) watcher!!.beforeTextChanged(s, start, count, after)
    }

    private fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (watcher != null) watcher!!.onTextChanged(s, start, before, count)
    }

    private fun afterTextChanged(s: Editable) {
        if (watcher != null) watcher!!.afterTextChanged(s)
    }

    fun setTextChangedListener(watcher: TextWatcher?) {
        this.watcher = watcher
    }

    fun setCustomInsertionActionModeCallback(callback: ActionMode.Callback?) {
        this.callback = callback
        for (i in 0 until editTexts.size()) {
            val editText = editTexts[i]
            setCustomInsertionActionModeCallback(editText, callback)
        }
    }

    private fun setCustomInsertionActionModeCallback(editText: AEditText?, callback: ActionMode.Callback?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editText!!.customInsertionActionModeCallback = callback
        } else editText!!.customSelectionActionModeCallback = callback
    }

    fun setSeparatorCharacter(separator: Char?) {
        this.separator = separator
        initLayout()
    }

    var text: CharSequence?
        get() {
            val builder = StringBuilder()
            for (i in 0 until editTexts.size()) {
                val editText = editTexts[i]
                builder.append(editText!!.text)
            }
            return builder.toString()
        }
        set(sequence) {
            var i = 0
            while (i < editTexts.size()) {
                val editText = editTexts[i]
                editText?.text= null
                i++
            }
            if (sequence != null) {
                val text = sequence.toString()
                val editText = editTexts[0]
                editText!!.editableText.insert(0, text)
            }
        }
    val maxLength: Int
        get() {
            var length = 0
            for (i in 0 until editTexts.size()) {
                length += getLength(i)
            }
            return length
        }

    private fun getLength(i: Int): Int {
        return lengthUsed!![i, defaultLength]
    }

    fun setTextSize(textSize: Float) {
        this.textSize = textSize
        for (i in 0 until editTexts.size()) {
            val editText = editTexts[i]
            setTextSize(editText, textSize)
        }
    }

    private fun setTextSize(editText: AEditText?, textSize: Float) {
        if (textSize > 0) editText!!.textSize = textSize
    }

    fun setTextAppearance(textAppearance: Int) {
        this.textAppearance = textAppearance
        for (i in 0 until editTexts.size()) {
            val editText = editTexts[i]
            editText!!.setSupportTextAppearance(textAppearance)
        }
    }

    fun setInputType(type: Int) {
        inputType = type
        for (i in 0 until editTexts.size()) {
            val editText = editTexts[i]
            editText!!.inputType = type
        }
    }

    fun setNumberOfBlock(block: Int) {
        noOfBlock = block
        initLayout()
    }

    fun setDefaultLength(defaultLength: Int) {
        this.defaultLength = defaultLength
        initLayout()
    }

    fun setEdiTextBackground(drawable: Drawable?) {
        for (i in 0 until editTexts.size()) {
            val editText = editTexts[i]
            setEdiTextBackground(editText, drawable)
        }
    }

    private fun setEdiTextBackground(editText: AEditText?, drawable: Drawable?) {
        ViewCompat.setBackground(editText!!, drawable?.constantState?.newDrawable())
    }

    fun setLengthAt(index: Int, length: Int) {
        lengths.put(index, length)
        initLayout()
    }

    fun setHint(hint: String?) {
        if (hintTextView == null) {
            hintTextView = ATextView(context)
            hintTextView!!.setPadding(16, 0, 16, 0)
            setHintTextAppearance(hintTextAppearance)
            setHintTextSize(hintTextAppearance.toFloat())
            linearLayout!!.addView(hintTextView, 0)
            hintColorDefault = hintTextView!!.hintTextColors
            hintColorFocus = ContextCompat.getColorStateList(
                    context,
                    R.color.colorAccent
            )
        }
        hintTextView!!.visibility = if (hint == null) GONE else VISIBLE
        hintTextView!!.hint = hint
    }

    fun setHintTextSize(textSize: Float) {
        hintTextSize = textSize
        if (hintTextView != null && textSize > 0) {
            hintTextView!!.textSize = textSize
        }
    }

    fun setHintTextAppearance(textAppearance: Int) {
        hintTextAppearance = textAppearance
        if (hintTextView != null) {
            hintTextView!!.setSupportTextAppearance(textAppearance)
        }
    }

    fun setSeparatorTextAppearance(textAppearance: Int) {
        separatorTextAppearance = textAppearance
        initLayout()
    }

    fun setSeparatorPadding(padding: Int) {
        separatorPadding = padding
        initLayout()
    }

    fun setSeparatorTextSize(textSize: Float) {
        separatorTextSize = textSize
        initLayout()
    }

    fun setSelection(selection: Int) {
        var selection = selection
        for (i in 0 until editTexts.size()) {
            val length = getLength(i)
            if (selection < length) {
                val editText = editTexts[i]
                editText!!.requestFocus()
                editText.setSelection(selection)
                break
            }
            selection -= length
        }
    }

    fun addCardPrefix(cardPrefix: CardPrefix) {
        cardPrefixes.add(cardPrefix)
        hideOrShowCardIcon()
    }

    fun addCardPrefix(index: Int, cardPrefix: CardPrefix) {
        cardPrefixes.add(index, cardPrefix)
        hideOrShowCardIcon()
    }

    fun removeCardPrefix(cardPrefix: CardPrefix): Boolean {
        val b = cardPrefixes.remove(cardPrefix)
        hideOrShowCardIcon()
        return b
    }

    fun removeCardPrefix(index: Int): CardPrefix {
        val cardPrefix = cardPrefixes.removeAt(index)
        hideOrShowCardIcon()
        return cardPrefix
    }

    fun setCardIconSize(size: Int) {
        cardIconSize = size
        if (iconImageView != null) {
            iconImageView!!.layoutParams.width = size
            iconImageView!!.requestLayout()
        }
    }

    override fun setEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
        for (i in 0 until editTexts.size()) {
            val editText = editTexts[i]
            editText!!.isEnabled = isEnabled
        }
    }

    fun setShowCardIcon(isShowCardIcon: Boolean) {
        this.isShowCardIcon = isShowCardIcon
        hideOrShowCardIcon()
    }

    fun setShiftPosition(b: Boolean) {
        shiftPosition = b
        initLayout()
    }

    override fun isEnabled(): Boolean {
        return isEnabled
    }

    fun isShiftPosition(): Boolean {
        return shiftPosition
    }

    private fun hideOrShowCardIcon() {
        if (isShowCardIcon && cardPrefixes.size > 0) {
            if (iconImageView == null) {
                iconImageView = ImageView(context)
                iconImageView!!.scaleType = ImageView.ScaleType.FIT_CENTER
                iconImageView!!.adjustViewBounds = true
                val params: ViewGroup.LayoutParams = LayoutParams(
                        cardIconSize,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
                iconImageView!!.layoutParams = params
                blockLinearLayout!!.addView(iconImageView)
                updateCardIcon()
            } else if (iconImageView!!.parent == null) {
                blockLinearLayout!!.addView(iconImageView)
            }
            iconImageView!!.visibility = VISIBLE
        } else if (iconImageView != null) {
            iconImageView!!.visibility = GONE
        }
    }

    private fun updateCardIcon() {
        if (cardPrefixes.size > 0) {
            val text: String = text.toString()
            for (cardPrefix in cardPrefixes) {
                for (prefix in cardPrefix.prefixes) if (text.startsWith(prefix)) {
                    iconImageView!!.setImageDrawable(cardPrefix.icon)
                    if (cardPrefix.lengths.size() > 0) {
                        lengthUsed = cardPrefix.lengths
                        updateEditTextLength()
                    }
                    if (cardPrefixListener != null) cardPrefixListener!!.onCardUpdate(cardPrefix)
                    return
                }
            }
            lengthUsed = lengths
            if (iconImageView != null) {
                iconImageView!!.setImageDrawable(null)
                updateEditTextLength()
            }
            if (cardPrefixListener != null) cardPrefixListener!!.onCardUpdate(null)
        }
    }

    private fun updateEditTextLength() {
        for (i in 0 until editTexts.size()) {
            val editText = editTexts[i]
            val params = editText!!.layoutParams as LinearLayout.LayoutParams
            params.weight = getLength(i).toFloat()
            editText.requestLayout()
        }
    }

    fun setOnCardPrefixListener(listener: OnCardPrefixListener?) {
        cardPrefixListener = listener
    }

    private fun createActionModeCallback(editText: AEditText): ActionMode.Callback {
        return object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {}
        }
    }

    private fun createTextChangeListener(editText: AEditText, index: Int): TextWatcher {
        return object : TextWatcher {
            var sequence: CharSequence = ""
            var beforeSequence: CharSequence = ""
            var prevLength = 0
            var selection = 0
            var start = 0
            var before = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                var start = start
                prevLength = s.length
                selection = editText.selectionStart
                beforeSequence = sequence
                for (i in 0 until index) {
                    start += getLength(i)
                }
                this.start = start
                before = beforeSequence.length
                this@BlockEditText.beforeTextChanged(beforeSequence, this.start, before, before + (after - count))
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val nextView: EditText? = editTexts[index + 1]
                val prevView: EditText? = editTexts[index - 1]
                if (s.length > prevLength && editText.isFocused && editText.selectionStart == getLength(index))
                    if (s.length == getLength(index) && nextView != null && nextView.text.isEmpty()) nextView.requestFocus()
                    else if (s.isEmpty() && prevView != null) prevView.requestFocus()
                if (shiftPosition && s.length < getLength(index)) {
                    if (editText.selectionStart == 0 && editText.isFocused && prevView != null) {
                        prevView.requestFocus()
                        prevView.setSelection(prevView.text.length)
                    }
                    if (nextView != null && !nextView.text.toString().isEmpty()) {
                        var length = getLength(index) - s.length
                        length = if (length > nextView.text.length) nextView.text.length else length
                        var editable = nextView.text
                        val temp = editable.toString().substring(0, length)
                        editable = editable.delete(0, length)
                        editText.append(temp)
                        editText.setSelection(selection)
                        nextView.text = editable
                    }
                }
                sequence = text!!
                this@BlockEditText.onTextChanged(sequence, this.start, this.before, sequence.length)
            }

            override fun afterTextChanged(s: Editable) {
                val nextView: EditText? = editTexts[index + 1]
                if (shiftPosition && nextView != null) nextView.isEnabled = isEnabled && s.length >= getLength(index)
                updateCardIcon()
                this@BlockEditText.afterTextChanged(s)
            }
        }
    }

    inner class LengthFilter(private val editText: AEditText, private val index: Int) : InputFilter {
        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned,
                            dstart: Int, dend: Int): CharSequence? {
            var source = source
            source = if (editText.inputType == InputType.TYPE_CLASS_NUMBER) {
                source.toString().replace("[\\D]".toRegex(), "")
            } else {
                source.toString().replace("[\\W]".toRegex(), "")
            }
            val mMax = getLength(index)
            var keep = mMax - (dest.length - (dend - dstart))
            val nextView: EditText? = editTexts[index + 1]
            if (source.isEmpty()) return null
            return if (keep <= 0) {
                if (nextView != null && text!!.length < maxLength) {
                    val s = source.toString()
                    var temp = editText.text.toString()
                    val selection = editText.selectionStart
                    temp = temp.substring(0, selection) + source + temp.substring(selection)
                    editText.setText(temp.substring(0, mMax))
                    temp = temp.substring(mMax)
                    if (selection + source.length <= mMax) editText.setSelection(selection + source.length) else {
                        nextView.requestFocus()
                        nextView.setSelection(0)
                    }
                    if (temp.isNotEmpty()) {
                        nextView.editableText.insert(0, temp)
                        val nextLength = getLength(index + 1)
                        nextView.setSelection(if (temp.length < nextLength) temp.length else nextLength)
                    } else nextView.setSelection(0)
                }
                ""
            } else if (keep >= end - start) {
                null // keep original
            } else {
                if (source.length > keep) if (nextView != null && text!!.length < maxLength) {
                    val s = source.toString()
                    var temp = editText.text.toString()
                    val selection = editText.selectionStart
                    temp = temp.substring(0, selection) + source + temp.substring(selection)
                    editText.setText(temp.substring(0, mMax))
                    temp = temp.substring(mMax)
                    if (selection + source.length <= mMax) editText.setSelection(selection + source.length) else {
                        nextView.requestFocus()
                        nextView.setSelection(0)
                    }
                    if (temp.isNotEmpty()) {
                        nextView.editableText.insert(0, temp)
                        val nextLength = getLength(index + 1)
                        nextView.setSelection(if (temp.length < nextLength) temp.length else nextLength)
                    } else nextView.setSelection(0)
                    return ""
                }
                keep += start
                if (Character.isHighSurrogate(source[keep - 1])) {
                    --keep
                    if (keep == start) {
                        return ""
                    }
                }
                source.subSequence(start, keep)
            }
        }
    }

    interface OnCardPrefixListener {
        fun onCardUpdate(cardPrefix: CardPrefix?)
    }

    companion object {
        private const val AMEX = 1
        private const val MASTERCARD = 2
        private const val VISA = 3
    }
}