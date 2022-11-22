package com.fadhil.submissionstoryapp_akhir.CustomView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.fadhil.submissionstoryapp_akhir.R
import com.google.android.material.textfield.TextInputEditText

class CustomInput: TextInputEditText {

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super (context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputan = s.toString()
                when {

                    inputan.length < 6 -> error = context.getString(R.string.error_length_6)
                }
            }

            override fun afterTextChanged(p0: Editable?) {


            }
        })
    }


}