package com.example.mycabbooking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener

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

    //Get View variables from xml id
    private fun linkViewElements() {
        loginBtn = findViewById<Button>(R.id.loginLoginBtn)
        emailEditText = findViewById<EditText>(R.id.loginEmailEditText)
        passwordEditText = findViewById<EditText>(R.id.loginPasswordEditText)
        moveToRegister = findViewById<TextView>(R.id.moveToRegisterTextView)
    }

    //Login process when clicking 'login' button
    private fun setLoginBtnAction() {
        loginBtn!!.setOnClickListener(View.OnClickListener {
            val email: String = emailEditText.getText().toString()
            val password: String = passwordEditText.getText().toString()

            //Check if the input email or password is empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this@LoginActivity,
                    Constants.ToastMessage.emptyInputError,
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }

            //Call FirebaseAuth for authentication process
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    this@LoginActivity,
                    OnCompleteListener<Any?> { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@LoginActivity,
                                Constants.ToastMessage.signInSuccess,
                                Toast.LENGTH_SHORT
                            ).show()
                            moveToHomePage() //Move to HomeActivity
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

    //Move to user's homepage if successfully logged in
    private fun moveToHomePage() {
        val i: Intent = Intent(
            this@LoginActivity,
            MainActivity::class.java
        )
        i.putExtra("email", emailEditText.getText().toString())
        startActivity(i)
        finish()
    }

    private fun setRegisterTextViewAction() {
        moveToRegister!!.setOnClickListener {
            val i: Intent = Intent(
                this@LoginActivity,
                RegisterActivity::class.java
            )
            startActivity(i)
            finish()
        }
    }
}