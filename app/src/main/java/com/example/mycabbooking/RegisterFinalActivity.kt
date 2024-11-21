package com.example.mycabbooking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.ParseException
import java.text.SimpleDateFormat

class RegisterFinalActivity : AppCompatActivity() {
    private lateinit var backBtn: Button
    private lateinit var registerBtn: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private var username: String? = null
    private var phone: String? = null
    private var birthDate: String? = null
    private var gender: String? = null
    private var role: String? = null
    private var transportationType: String? = null
    private var vehiclePlateNumber: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_final)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        linkViewElements()
        getPreviousRegisterFormInfo()
        setBackBtnAction()
        setRegisterBtnAction()
    }

    private fun linkViewElements() {
        backBtn = findViewById(R.id.registerFinalBackBtn)
        registerBtn = findViewById(R.id.registerFinalRegisterBtn)
        emailEditText = findViewById(R.id.registerFinalEmailEditText)
        passwordEditText = findViewById(R.id.registerFinalPasswordEditText)
    }

    private fun setBackBtnAction() {
        backBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun getPreviousRegisterFormInfo() {
        intent.extras?.let { bundle ->
            username = bundle.getString(Constants.FSUser.usernameField)
            phone = bundle.getString(Constants.FSUser.phoneField)
            birthDate = bundle.getString(Constants.FSUser.birthDateField)
            gender = bundle.getString(Constants.FSUser.genderField)
            role = bundle.getString(Constants.FSUser.roleField)
            transportationType = bundle.getString(Constants.FSUser.transportationType)
            vehiclePlateNumber = bundle.getString(Constants.FSUser.vehiclePlateNumber)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun saveUserInfo() {
        try {
            val df = SimpleDateFormat("MM/dd/yyyy")
            val birthDateFormatted = df.parse(birthDate ?: "") ?: throw ParseException("Invalid date", 0)

            val data = hashMapOf(
                Constants.FSUser.usernameField to username,
                Constants.FSUser.phoneField to phone,
                Constants.FSUser.birthDateField to birthDateFormatted,
                Constants.FSUser.genderField to gender,
                Constants.FSUser.emailField to emailEditText.text.toString(),
                Constants.FSUser.roleField to role,
                Constants.FSUser.transportationType to transportationType,
                Constants.FSUser.vehiclePlateNumber to vehiclePlateNumber,
                Constants.FSUser.currentPositionLatitude to 0.0,
                Constants.FSUser.currentPositionLongitude to 0.0,
                Constants.FSUser.rating to listOf(5)
            )

            db.collection(Constants.FSUser.userCollection)
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "User info saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save user info: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: ParseException) {
            e.printStackTrace()
            Toast.makeText(this, "Error parsing birth date", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setRegisterBtnAction() {
        registerBtn.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, Constants.ToastMessage.emptyInputError, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, Constants.ToastMessage.registerSuccess, Toast.LENGTH_SHORT).show()
                        saveUserInfo()
                        moveToLoginActivity()
                    } else {
                        Toast.makeText(this, Constants.ToastMessage.registerFailure, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
