package com.infideap.blockedittext;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BlockEditText editText1 = findViewById(R.id.blockEditText1);
        editText1.setNumberOfBlock(4);
        editText1.setDefaultLength(4);
        editText1.setHint("Visa/Mastercard");
        editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText1.setEdiTextBackground(ContextCompat.getDrawable(this, R.drawable.selector_edittext_round_border_line));

        BlockEditText editText2 = findViewById(R.id.blockEditText2);
        editText2.setNumberOfBlock(3);
        editText2.setDefaultLength(4);
        editText2.setLengthAt(1,6);
        editText2.setLengthAt(2,5);
        editText2.setHint("Amex");
        editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText2.setEdiTextBackground(ContextCompat.getDrawable(this, R.drawable.selector_edittext_round_border_line));



    }
}
