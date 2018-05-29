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

        BlockEditText editText = findViewById(R.id.blockEditText);
        editText.setNumberOfBlock(3);
        editText.setDefaultLength(4);
        editText.setLengthAt(1,6);
        editText.setLengthAt(2,5);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setEdiTextBackground(ContextCompat.getDrawable(this, R.drawable.selector_edittext_round_border_line));
    }
}
