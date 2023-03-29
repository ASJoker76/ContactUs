package com.contact.us.adapter

import android.view.View
import com.contact.us.model.Task

interface RecyclerViewTaskClickListener {

    // method yang akan dipanggil di MainActivity
    fun onItemClicked(view: View,pos:Int, transaksi: Task)


}