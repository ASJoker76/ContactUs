package com.contact.us

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.contact.us.model.CustomerOld
import com.contact.us.utils.Extensions.sweetAlert
import com.contact.us.utils.FirebaseUtils
import com.contact.us.utils.Host.REQUEST_CODE_PICK_IMAGE
import com.contant.us.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_data_customer.*
import java.text.SimpleDateFormat
import java.util.*


private var mFirebaseAnalytics: FirebaseAnalytics? = null
class DataCustomerActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var pDialog: SweetAlertDialog? = null
    private var jenisKelamin: String? = ""

    private var database: DatabaseReference? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_customer)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        if (FirebaseUtils.firebaseUser!!.displayName != null) {
            etName.setText(FirebaseUtils.firebaseUser!!.displayName.toString())
        }
        if (FirebaseUtils.firebaseUser!!.email != null) {
            etEmail.setText(FirebaseUtils.firebaseUser!!.email.toString())
        }

        storageReference = FirebaseStorage.getInstance().reference

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
                etDate.setText(sdf.format(calendar.time))
            }

        etDate.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= etDate.getRight() - etDate.getCompoundDrawables()
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

        ivAddImage.setOnClickListener {
            //check permission at runtime
            val checkSelfPermission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
                //Requests permissions to be granted to this application at runtime
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PICK_IMAGE)
            }
            else {
                openImageChooser()
            }
        }

        btnDaftar.setOnClickListener {
            if (validate()) {
                if(selectedImageUri != null){
                    registerCustomer()
                }
                else{
                    sweetAlert("Foto tidak boleh kosong")
                }
            }
        }
    }

    private fun registerCustomer() {
        pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog!!.titleText = "Loading"
        pDialog!!.setCancelable(false)
        pDialog!!.show()

        database = FirebaseDatabase.getInstance().getReference("Customer")

        val nama = etName.text.toString()
        val username = etUsername.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val tanggal_lahir = etDate!!.text.toString()
        val jenis_kelamin = spGender.selectedItem.toString()
        val customerId = database!!.push().key.toString()

        val foto_profile = UUID.randomUUID().toString()
        val ref = storageReference?.child("profile/" + foto_profile)
        if(selectedImageUri != null){
            val uploadTask = ref?.putFile(selectedImageUri!!)
        }
//        else if(firebaseUser!!.photoUrl   != null){
//            val uploadTask = ref?.putFile(firebaseUser!!.photoUrl!!)
//        }

        val customer = CustomerOld(customerId,nama,username,email, password, tanggal_lahir,jenis_kelamin,foto_profile)
        database!!.child(customerId).setValue(customer).addOnCompleteListener {
            pDialog!!.dismissWithAnimation()
            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Berhasil Submit")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismissWithAnimation()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }

    private fun validate(): Boolean {
        if (!etName.isValid()) {
            return false
        }

        if (!etUsername.isValid()) {
            return false
        }

        if (!etEmail.isValid()) {
            return false
        }

        if (!etEmail.isValidEmail()) {
            return false
        }

        if (!etPassword.isValid()) {
            return false
        }

        if (!etDate.isValid()) {
            return false
        }

        return true
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data
                    ivFoto.setImageURI(selectedImageUri)
                }
            }
        }
    }
}