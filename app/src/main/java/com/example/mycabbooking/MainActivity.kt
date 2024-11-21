package com.example.mycabbooking

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mycabbooking.model.User as AppUserModel
import com.example.mycabbooking.ui.customer.home.CustomerHomeViewModel
import com.example.mycabbooking.ui.driver.home.DriverHomeViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var navHeaderUsernameTextView: TextView
    private lateinit var navHeaderEmailTextView: TextView
    private lateinit var navHeaderImageView: ImageView

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null
    private var currentUserObject: AppUserModel? = null

    private lateinit var customerHomeViewModel: CustomerHomeViewModel
    private lateinit var driverHomeViewModel: DriverHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linkViewElements()
        initFirebaseCurrentUserInfo()
        initViewModels()
    }

    private fun linkViewElements() {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val header = navView.getHeaderView(0) as LinearLayout
        navHeaderUsernameTextView = header.findViewById(R.id.nav_header_username)
        navHeaderEmailTextView = header.findViewById(R.id.nav_header_email)
        navHeaderImageView = header.findViewById(R.id.nav_header_image)
    }

    private fun initFirebaseCurrentUserInfo() {
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth.currentUser

        currentUser?.let {
            db.collection("Users")
                .whereEqualTo("email", it.email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.firstOrNull()?.let { doc ->
                        currentUserObject = doc.toObject(AppUserModel::class.java)
                        updateUI()
                    }
                }
        }
    }

    private fun updateUI() {
        currentUserObject?.let { user ->
            navHeaderUsernameTextView.text = user.username
            navHeaderEmailTextView.text = user.email
            navHeaderImageView.setImageResource(R.drawable.car)
        }
    }

    private fun initViewModels() {
        customerHomeViewModel = ViewModelProvider(this).get(CustomerHomeViewModel::class.java)
        driverHomeViewModel = ViewModelProvider(this).get(DriverHomeViewModel::class.java)
    }
}
