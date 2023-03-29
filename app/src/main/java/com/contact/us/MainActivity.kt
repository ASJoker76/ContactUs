package com.contact.us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.contact.us.fragment.AkunFragment
import com.contact.us.fragment.BerandaFragment
import com.contact.us.utils.Extensions.tag
import com.contact.us.utils.Extensions.toast
import com.contact.us.utils.FirebaseUtils.firebaseAuth
import com.contact.us.utils.FirebaseUtils.firebaseUser
import com.contant.us.R
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*

private var mFirebaseAnalytics: FirebaseAnalytics? = null
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        navigation.menu.getItem(0).isCheckable = true
        setFragment(BerandaFragment())

        navigation.setOnNavigationItemSelectedListener {menu ->

            when(menu.itemId){

                R.id.beranda -> {
                    setFragment(BerandaFragment())
                    true
                }

                R.id.akun -> {
                    setFragment(AkunFragment())
                    true
                }

                else -> false
            }
        }

    }

    fun setFragment(fr : Fragment){
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.fl_view,fr)
        frag.commit()
    }

}