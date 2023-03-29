package com.contact.us.component

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText
import com.contant.us.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class CurrencyEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {
    private val prefix = context.getString(R.string.currency_prefix) + " "
    private val currencyTextWatcher = CurrencyTextWatcher(this, prefix)

    var textValidator: (String?) -> Boolean = { s: String? ->
        val cleanString = s?.replace(prefix, "")?.replace("[,]".toRegex(), "")
        !cleanString.isNullOrBlank() && cleanString.toDouble() > 0.0
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            this.addTextChangedListener(currencyTextWatcher)
        } else {
            this.removeTextChangedListener(currencyTextWatcher)
        }
        handleCaseCurrencyEmpty(focused)
    }

    private fun handleCaseCurrencyEmpty(focused: Boolean) {
        if (focused) {
            if (text.toString().isEmpty()) {
                setText(prefix)
            }
        } else {
            if (text.toString() == prefix) {
                setText("")
            }
        }
    }

    fun isValid(): Boolean {
        this.error = if (textValidator(this.text?.toString())) null else context!!.resources.getString(R.string.errorEmpty)
        return if (this.error == null) {
            true
        } else {
            val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
            this.startAnimation(shake)
            this.requestFocus()
            false
        }
    }

    fun setAddedChangeListener(onChangeListener: (string: String) -> Unit) {
        this.currencyTextWatcher.setAddedChangeListener(onChangeListener)
    }

    fun getDoubleValue(): Double {
        val cleanString = this.text.toString().replace(prefix, "").replace("[,]".toRegex(), "")
        val doubleValue = cleanString.toDoubleOrNull()
        return doubleValue ?: 0.0
    }

    fun setDoubleValue(value: Double?) {
        if (value == null) {
            this.setText("")
            return
        }
        this.addTextChangedListener(currencyTextWatcher)
        this.setText(value.toString())
        this.removeTextChangedListener(currencyTextWatcher)
    }

    private class CurrencyTextWatcher internal constructor(private val editText: EditText, private val prefix: String) : TextWatcher {
        private var addedChangeListener: (text: String) -> Unit = {}

        private var previousCleanString: String? = null
        val MAX_LENGTH = 20
        val MAX_DECIMAL = 3

        fun setAddedChangeListener(onChangeListener: (string: String) -> Unit) {
            this.addedChangeListener = onChangeListener
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable) {
            val str = editable.toString()
            if (str.length < prefix.length) {
                editText.setText(prefix)
                editText.setSelection(prefix.length)
                return
            }
            if (str == prefix) {
                return
            }
            val cleanString = str.replace(prefix, "").replace("[,]".toRegex(), "")
            if (cleanString == previousCleanString || cleanString.isEmpty()) {
                return
            }
            previousCleanString = cleanString

            try {
                val formattedString: String
                formattedString = formatInteger(cleanString)

                addedChangeListener(cleanString)
                editText.removeTextChangedListener(this)
                editText.setText(formattedString)
                handleSelection()
                editText.addTextChangedListener(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun formatInteger(str: String): String {
            val parsed = BigDecimal(str)
            val formatter = DecimalFormat("$prefix#,###", DecimalFormatSymbols(Locale.US))
            return formatter.format(parsed)
        }

        private fun formatDecimal(str: String): String {
            if (str == ".") {
                return "$prefix."
            }
            val parsed = BigDecimal(str)
            val formatter = DecimalFormat(prefix + "#,###." + getDecimalPattern(str),
                DecimalFormatSymbols(Locale.US))
            formatter.setRoundingMode(RoundingMode.DOWN)
            return formatter.format(parsed)
        }

        private fun getDecimalPattern(str: String): String {
            val decimalCount = str.length - str.indexOf(".") - 1
            val decimalPattern = StringBuilder()
            var i = 0
            while (i < decimalCount && i < MAX_DECIMAL) {
                decimalPattern.append("0")
                i++
            }
            return decimalPattern.toString()
        }

        private fun handleSelection() {
            if (editText.text.length <= MAX_LENGTH) {
                editText.setSelection(editText.text.length)
            } else {
                editText.setSelection(MAX_LENGTH)
            }
        }
    }
}