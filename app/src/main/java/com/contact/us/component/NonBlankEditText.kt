package com.contact.us.component

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Patterns
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.contact.us.utils.validate
import com.contant.us.R


open class NonBlankEditText(context: Context?, attrs: AttributeSet?) : AppCompatAutoCompleteTextView(
    context!!, attrs) {

    var textValidator: (String?) -> Boolean = { s: String? -> !s.isNullOrBlank() }
    var emailValidator: (String?) -> Boolean = { s: String? -> Patterns.EMAIL_ADDRESS.matcher(s).matches() }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

        if (focused) {
            validate(textValidator, context!!.resources.getString(R.string.errorEmpty))
        }
    }

    fun isValid(): Boolean {
        this.error = if (textValidator(this.text!!.toString())) null else context!!.resources.getString(R.string.errorEmpty)
        return if (error == null) {
            true
        } else {
            val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
            startAnimation(shake)
            requestFocus()
            false
        }
    }

    fun isValidEmail(): Boolean {
        var retVal = false
        this.error = if (textValidator(this.text!!.toString())) null else context!!.resources.getString(R.string.errorEmpty)
        if (this.error == null) {
            this.error = if (emailValidator(this.text!!.toString())) null else context!!.resources.getString(R.string.errorEmail)
            if (this.error == null) {
                retVal = true
            }
        } else {
            val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
            this.startAnimation(shake)
            this.requestFocus()
        }
        return retVal
    }

    fun shakeAndSetErrorMessage(message: String) {
        this.error = message
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        this.startAnimation(shake)
        this.requestFocus()
    }
}