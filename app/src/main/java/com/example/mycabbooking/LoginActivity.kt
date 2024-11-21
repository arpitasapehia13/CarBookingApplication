package com.example.mycabbooking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycabbooking.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    var backBtn: Button? = null
    var loginBtn: Button? = null
    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    var moveToRegister: TextView? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        linkViewElements()
        setRegisterTextViewAction()
        setLoginBtnAction()
    }

    // Get View variables from XML id
    private fun linkViewElements() {
        loginBtn = findViewById(R.id.loginLoginBtn)
        emailEditText = findViewById(R.id.loginEmailEditText)
        passwordEditText = findViewById(R.id.loginPasswordEditText)
        moveToRegister = findViewById(R.id.moveToRegisterTextView)
    }

    // Login process when clicking 'login' button
    private fun setLoginBtnAction() {
        loginBtn!!.setOnClickListener(View.OnClickListener {
            val email: String = emailEditText?.text.toString()
            val password: String = passwordEditText?.text.toString()

            // Check if the input email or password is empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this@LoginActivity,
                    Constants.ToastMessage.emptyInputError,
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }

            // Call FirebaseAuth for authentication process
            mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(
                    this@LoginActivity,
                    OnCompleteListener<AuthResult> { task ->  // Corrected the OnCompleteListener type
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@LoginActivity,
                                Constants.ToastMessage.signInSuccess,
                                Toast.LENGTH_SHORT
                            ).show()
                            moveToHomePage() // Move to HomeActivity
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                Constants.ToastMessage.signInFailure,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        })
    }

    // Move to user's homepage if successfully logged in
    private fun moveToHomePage() {
        val i = Intent(this@LoginActivity, MainActivity::class.java)
        i.putExtra("email", emailEditText?.text.toString())
        startActivity(i)
        finish()
    }

    // Set action to move to RegisterActivity when clicking the register text
    private fun setRegisterTextViewAction() {
        moveToRegister!!.setOnClickListener {
            val i = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}
