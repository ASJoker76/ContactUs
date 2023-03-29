package com.contact.us

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.pedant.SweetAlert.SweetAlertDialog
import com.contact.us.model.Customer
import com.contant.us.databinding.ActivityRegisterCustomerBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterCustomerActivity : AppCompatActivity() {

    private var binding: ActivityRegisterCustomerBinding? = null
    private var database: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterCustomerBinding.inflate(getLayoutInflater())
        setContentView(binding!!.getRoot())

        database = FirebaseDatabase.getInstance().getReference("Customer")

        binding!!.tvLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding!!.btnRegister.setOnClickListener {
            if (validate()) {
                val nama = binding!!.etNamaLengkap.text.toString()
                val email = binding!!.etEmail.text.toString()
                val ktp = binding!!.etNoKtp.text.toString()
                val alamat = binding!!.etAlamat.text.toString()
                val notlp = binding!!.etNoTelp.text.toString()
                val password = binding!!.etPassword.text.toString()
                val role = "Customer"

                val customer = Customer(nama,email,ktp,alamat,notlp,password,role)
                val customerId = database!!.push().key.toString()

                database!!.child(customerId).setValue(customer).addOnCompleteListener {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Succeed!")
                        .setConfirmClickListener { sDialog ->
                            sDialog.dismissWithAnimation()
                            binding!!.etNamaLengkap.setText("")
                            binding!!.etEmail.setText("")
                            binding!!.etNoKtp.setText("")
                            binding!!.etAlamat.setText("")
                            binding!!.etNoTelp.setText("")
                            binding!!.etPassword.setText("")
                            binding!!.etPasswordKonfirm.setText("")

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .show()
                }
            }
        }
    }

    private fun validate(): Boolean {
        if (!binding!!.etNamaLengkap.isValid()) {
            return false
        }

        if (!binding!!.etEmail.isValid()) {
            return false
        }

        if (!binding!!.etEmail.isValidEmail()) {
            return false
        }

        if (!binding!!.etNoKtp.isValid()) {
            return false
        }

        if (!binding!!.etAlamat.isValid()) {
            return false
        }

        if (!binding!!.etNoTelp.isValid()) {
            return false
        }

        if (!binding!!.etPassword.isValid()) {
            return false
        }
        if (!binding!!.etPasswordKonfirm.isValid()) {
            return false
        }

        if (!binding!!.etPassword.text.toString().trim().equals(binding!!.etPasswordKonfirm.text.toString().trim())) {
            binding!!.etPasswordKonfirm.shakeAndSetErrorMessage("This field is different with your password")
            return false
        }

        return true
    }
}