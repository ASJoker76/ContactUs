package com.contact.us.component

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import com.contant.us.R

class NonBlankSpinner(context: Context?, attrs: AttributeSet?): AppCompatSpinner(context!!, attrs) {

//    var selectedValidator: (Int?) -> Boolean = { int:Int? -> int != null && int != 0 }
    var selectedValidator: (Int?) -> Boolean = { int:Int? -> int != null }

    override fun setSelection(position: Int) {
        super.setSelection(position)
    }

    override fun setSelection(position: Int, animate: Boolean) {
        super.setSelection(position, animate)
    }

    fun validate(position: Int){
        val errorText = (this.selectedView as TextView)
        errorText.error =  if (selectedValidator(position)) null else context!!.resources.getString(
            R.string.errorEmpty)
        if (errorText.error != null) {
            this.requestFocus()
        }
    }

    fun isValid(): Boolean {
        if( this.selectedView == null ){
            this.requestFocusFromTouch()
            val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
            this.startAnimation(shake)
            return false
        }
        val errorText = (this.selectedView as TextView)
        errorText.error =  if (selectedValidator(selectedItemPosition)) null else context!!.resources.getString(R.string.errorEmpty)
        return if (errorText.error == null) {
            true
        } else {
            this.requestFocusFromTouch()
            val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
            this.startAnimation(shake)
            false
        }
    }
}