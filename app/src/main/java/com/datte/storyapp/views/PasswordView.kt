package com.datte.storyapp.views


import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatEditText


class PasswordView(context: Context) : AppCompatEditText(context) {

//    constructor(context: Context) : super(context) {
//
//    }
//
//    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//
//    }
//
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//
//    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        if (text?.length ?: 0 < 8) {
            setText("8 character minimum!")
            setTextColor(Color.RED)
        } else {
            setTextColor(Color.BLACK)
        }
    }

}
