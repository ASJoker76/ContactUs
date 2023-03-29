package com.contact.us

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.contact.us.model.Task
import com.contact.us.utils.Extensions.sweetAlertIntent
import com.contant.us.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_task.*
import java.text.SimpleDateFormat
import java.util.*

private var mFirebaseAnalytics: FirebaseAnalytics? = null
class TaskActivity : AppCompatActivity() {

    private var pDialog: SweetAlertDialog? = null
    private var database: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        onChange()
    }

    private fun onChange() {
          val calendar = Calendar.getInstance()
            val date =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    calendar[Calendar.YEAR] = year
                    calendar[Calendar.MONTH] = monthOfYear
                    calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    //updateLabel();
                    val myFormat = "yyyy-MM-dd" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    etDeadline.setText(sdf.format(calendar.time))
                }

            etDeadline.setOnTouchListener(View.OnTouchListener { v, event ->
                val DRAWABLE_LEFT = 0
                val DRAWABLE_TOP = 1
                val DRAWABLE_RIGHT = 2
                val DRAWABLE_BOTTOM = 3
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= etDeadline.getRight() - etDeadline.getCompoundDrawables()
                            .get(DRAWABLE_RIGHT).getBounds().width()
                    ) {
                        DatePickerDialog(
                            this, date, calendar
                                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                        return@OnTouchListener true
                    }
                }
                false
            })
        btnSimpan.setOnClickListener {
            if (validate()) {
                createTask()

            }
        }
    }

    private fun createTask() {
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog!!.titleText = "Loading"
        pDialog!!.setCancelable(false)
        pDialog!!.show()

        database = FirebaseDatabase.getInstance().getReference("Task")

        val taskId = database!!.push().key.toString()
        val judul = etJudul.text.toString()
        val deskripsi = etDeskripsi.text.toString()
        val deadline = etDeadline.text.toString()
        val budget = etBudget.getDoubleValue().toString()


        val task = Task(taskId,judul, deskripsi, deadline, budget)
        database!!.child(taskId).setValue(task).addOnCompleteListener {
            pDialog!!.dismissWithAnimation()
            sweetAlertIntent("Berhasil mendaftar", MainActivity::class.java)
        }
    }

    private fun validate(): Boolean {
        if (!etJudul.isValid()) {
            return false
        }

        if (!etDeskripsi.isValid()) {
            return false
        }

        if (!etDeadline.isValid()) {
            return false
        }

        if (!etBudget.isValid()) {
            return false
        }


        return true
    }
}