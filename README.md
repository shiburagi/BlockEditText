# BlockEditText

[ ![Download](https://api.bintray.com/packages/infideap2/Block-EditText/com.infideap.blockedittext/images/download.svg) ](https://bintray.com/infideap2/Block-EditText/com.infideap.blockedittext/_latestVersion)

Block EditText is a library provide an input view present in multiple block style that common use in **TAC** or **credit card field**.

#### Kotlin Example : [BlockEditText-Kotlin](https://github.com/shiburagi/BlockEditText-Kotlin)


![Alt Text](https://raw.githubusercontent.com/shiburagi/BlockEditText/preview/preview2.gif)

Android 9.0+ support
---

<a href='https://ko-fi.com/A0A0FB3V' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi4.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>
[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=D9JKYQL8452AL)

## Including In Your Project
If you are a Maven user you can easily include the library by specifying it as
a dependency:

#### Maven
``` xml
<dependency>
  <groupId>com.infideap.blockedittext</groupId>
  <artifactId>block-edittext</artifactId>
  <version>0.0.6</version>
  <type>pom</type>
</dependency>
```
#### Gradle
```groovy
dependencies {
   implementation 'com.infideap.blockedittext:block-edittext:0.0.6'
}
```

if **the gradle unable to sync**, you may include this line in project level gradle,
```groovy
repositories {
 maven{
   url "https://dl.bintray.com/infideap2/Block-EditText"
 }
}
```

**or**,
you can include it by **download this project** and **import /blockedittext** as **module**.

## How to use
**Creating the layout**
### TAC
```xml
<com.infideap.blockedittext.BlockEditText
    android:id="@+id/blockEditText_tac"
    app:bet_defaultLength="1"
    app:bet_numberOfBlock="4"
    app:bet_inputType="Integer"
    app:bet_hintTextAppearance="@style/TextAppearance.AppCompat.Medium"
    app:bet_hint="TAC"
    app:bet_textSize="24sp"
    android:layout_width="300dp"
    app:bet_editTextBackground="@drawable/selector_edittext_round_border"
    android:layout_height="wrap_content" />

```

### Credit Card
```xml
<com.infideap.blockedittext.BlockEditText
    android:id="@+id/blockEditText_visa"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:bet_defaultLength="4"
    app:bet_hint="Visa/Mastercard"
    app:bet_hintTextAppearance="@style/TextAppearance.AppCompat.Medium"
    app:bet_inputType="Integer"
    app:bet_numberOfBlock="4"
    app:bet_text="131321323-13213-21321312" />
```


**Customize**
```java
amexEditText.setNumberOfBlock(3);
amexEditText.setDefaultLength(4);
amexEditText.setLengthAt(1,6);

amexEditText.setHint("Amex");
amexEditText.setText("1234567890");
amexEditText.getText();

amexEditText.setTextSize(16);
amexEditText.setHintTextSize(16);
amexEditText.setSeparatorTextSize(16);

amexEditText.setSeparatorCharacter('-');
amexEditText.setSeparatorPadding(8);

amexEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

amexEditText.setTextAppearance(android.support.v7.appcompat.R.style.Base_TextAppearance_AppCompat_Medium);
amexEditText.setHintTextAppearance(android.support.v7.appcompat.R.style.Base_TextAppearance_AppCompat_Medium);
amexEditText.setSeparatorTextAppearance(android.support.v7.appcompat.R.style.Base_TextAppearance_AppCompat_Medium);

amexEditText.setEdiTextBackground(ContextCompat.getDrawable(this, R.drawable.selector_edittext_round_border_line));

amexEditText.setSelection(0);


amexEditText.addCardPrefix(CardPrefix.amex(this))
amexEditText.addCardPrefix(CardPrefix.amex(amexDrawable))
amexEditText.addCardPrefix(new CardPrefix(this, R.drawable.ic_amex, "34"))
amexEditText.addCardPrefix(new CardPrefix(amexDrawable, "34"))

```

## Contact
For any enquiries, please send an email to tr32010@gmail.com. 

## License

    Copyright 2018 Shiburagi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
