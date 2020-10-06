package com.infideap.blockedittextexample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import com.infideap.blockedittext.BlockEditText
import com.infideap.blockedittext.CardPrefix

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val amexEditText: BlockEditText = findViewById(R.id.blockEditText_amex)
        amexEditText.setNumberOfBlock(3)
        amexEditText.setDefaultLength(4)
        amexEditText.setLengthAt(1, 6)
        amexEditText.setLengthAt(2, 5)
        amexEditText.setHint("Amex")
        amexEditText.setSeparatorCharacter('-')
        amexEditText.setInputType(InputType.TYPE_CLASS_NUMBER)
        amexEditText.setEdiTextBackground(ContextCompat.getDrawable(this, com.infideap.blockedittext.R.drawable.selector_edittext_round_border_line))
        amexEditText.setTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= amexEditText.maxLength) {
                    amexEditText.setBackgroundColor(Color.GREEN)
                } else amexEditText.setBackgroundColor(Color.TRANSPARENT)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        val cardEditText: BlockEditText = findViewById(R.id.blockEditText_card)
        cardEditText.addCardPrefix(CardPrefix.amex(this))
        val icNumberEditText: BlockEditText = findViewById(R.id.blockEditText_ic_number)
        icNumberEditText.setNumberOfBlock(3)
        icNumberEditText.setDefaultLength(6)
        icNumberEditText.setLengthAt(1, 2)
        icNumberEditText.setLengthAt(2, 4)
        icNumberEditText.setSelection(0)
    }
}