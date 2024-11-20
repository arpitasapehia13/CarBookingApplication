package com.example.mycabbooking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

class StartActivity : AppCompatActivity() {
    var loginActivityBtn: Button? = null
    var registerActivityBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        linkViewElements()
        setLoginActivityBtnAction()
        setRegisterActivityBtnAction()
    }

    //Get View variables from xml id
    private fun linkViewElements() {
        loginActivityBtn = findViewById<View>(R.id.loginActivityBtn) as Button?
        registerActivityBtn = findViewById<View>(R.id.registerActivityBtn) as Button?
    }

    //Set action when press on 'login' button, move to login activity
    private fun setLoginActivityBtnAction() {
        loginActivityBtn!!.setOnClickListener {
            val i: Intent = Intent(
                this@StartActivity,
                LoginActivity::class.java
            )
            startActivity(i)
            finish()
        }
    }

    //Set action when press on 'register' button, move to register activity
    private fun setRegisterActivityBtnAction() {
        registerActivityBtn!!.setOnClickListener {
            val i: Intent = Intent(
                this@StartActivity,
                RegisterActivity::class.java
            )
            startActivity(i)
            finish()
        }
    }
}