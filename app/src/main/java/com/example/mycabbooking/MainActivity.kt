package com.example.cabbooking

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.example.cabbooking.R
import com.example.cabbooking.model.User
import com.example.cabbooking.ui.customer.booking.BookingViewModel
import com.example.cabbooking.ui.customer.booking.checkout.CheckoutViewModel
import com.example.cabbooking.ui.customer.booking.dropoff.DropoffViewModel
import com.example.cabbooking.ui.customer.booking.pickup.PickupViewModel
import com.example.cabbooking.ui.customer.home.CustomerHomeViewModel
import com.example.cabbooking.ui.driver.home.DriverHomeViewModel
import com.example.cabbooking.ui.user_profile.UserProfileViewModel
import com.example.mycabbooking.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {
    private var mAppBarConfiguration: AppBarConfiguration? = null

    private var navHeaderEmailTextView: TextView? = null
    private var navHeaderUsernameTextView: TextView? = null

    //Firebase, FireStore
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var currentUser: FirebaseUser? = null

    //Current user info
    var currentUserObject: User? = null

    //View models
    var customerHomeViewModel: CustomerHomeViewModel? = null
    var driverHomeViewModel: DriverHomeViewModel? = null
    var dropoffViewModel: DropoffViewModel? = null
    var pickupViewModel: PickupViewModel? = null
    var bookingViewModel: BookingViewModel? = null
    var checkoutViewModel: CheckoutViewModel? = null
    var userProfileViewModel: UserProfileViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) //setup navigation drawer
        linkViewElements() //Get view elements
        initAllChildFragmentsViewModel() //Init all child fragments viewModels
        initFirebaseCurrentUserInfo() //Get all fireStore instances
    }

    /**
     * Connect view elements of layout to this class variable
     */
    private fun linkViewElements() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val navHeaderView = navigationView.getHeaderView(0) as LinearLayout
        navHeaderUsernameTextView = navHeaderView.getChildAt(1) as TextView
        navHeaderEmailTextView = navHeaderView.getChildAt(2) as TextView
    }

    /**
     * Set up navigation drawer activity
     */
    private fun navigationDrawerSetup() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        mAppBarConfiguration = Builder(
            R.id.nav_customer_home,
            R.id.nav_driver_home,
            R.id.nav_profile
        )
            .setOpenableLayout(drawer)
            .build()

        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration)
        NavigationUI.setupWithNavController(navigationView, navController)

        navigateAndHideAccordingMenuBasedOnRole(navController)
    }

    /**
     * Logout menu item listener (sits in 3-dots collapsing menu)
     */
    private fun onLogoutOptionClick() {
        mAuth.signOut()
        val i = Intent(
            this@MainActivity,
            StartActivity::class.java
        )
        startActivity(i)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_logout -> onLogoutOptionClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    /**
     * Get instances of Firebase FireStore Auth, db, current user
     */
    private fun initFirebaseCurrentUserInfo() {
        //Get instances of Firebase FireStore
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth.getCurrentUser()
        getCurrentUserObject() //Get current user object info
    }

    /**
     * Get current user object from FireStore
     */
    private fun getCurrentUserObject() {
        db!!.collection(Constants.FSUser.userCollection)
            .whereEqualTo(Constants.FSUser.emailField, currentUser.getEmail())
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot?> {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    for (doc in queryDocumentSnapshots) {
                        currentUserObject = doc.toObject(User::class.java)
                        setNavHeaderEmailAndUsername() //Set nav header username and email
                        setAllChildFragmentsViewModelData()
                        navigationDrawerSetup()
                    }
                }
            })
    }

    /**
     * navigate to the right home and remove according home menu based on user role
     * @param navController navController object of this layout
     */
    private fun navigateAndHideAccordingMenuBasedOnRole(navController: NavController) {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu

        //Hide according menu and Navigate to the right fragment based on
        if (currentUserObject!!.role == "Customer") {
            val driverHomeMenuItem = menu.getItem(1)
            driverHomeMenuItem.setVisible(false)
            navController.navigate(R.id.nav_customer_home)
        } else {
            val customerHomeMenuItem = menu.getItem(0)
            customerHomeMenuItem.setVisible(false)
            navController.navigate(R.id.nav_driver_home)
        }
    }

    /**
     * Init all child fragments' view models
     */
    private fun initAllChildFragmentsViewModel() {
        customerHomeViewModel = ViewModelProvider(this).get<T>(CustomerHomeViewModel::class.java)
        driverHomeViewModel = ViewModelProvider(this).get<T>(DriverHomeViewModel::class.java)
        dropoffViewModel = ViewModelProvider(this).get<T>(DropoffViewModel::class.java)
        pickupViewModel = ViewModelProvider(this).get<T>(PickupViewModel::class.java)
        bookingViewModel = ViewModelProvider(this).get<T>(BookingViewModel::class.java)
        checkoutViewModel = ViewModelProvider(this).get<T>(CheckoutViewModel::class.java)
        userProfileViewModel = ViewModelProvider(this).get<T>(UserProfileViewModel::class.java)
    }

    /**
     * Set nav header username and email
     */
    private fun setNavHeaderEmailAndUsername() {
        navHeaderEmailTextView.setText(currentUser.getEmail())
        navHeaderUsernameTextView!!.text = currentUserObject!!.username
    }

    /**
     * Send current user data through child fragments' view models
     */
    private fun setAllChildFragmentsViewModelData() {
        if (currentUserObject!!.role == "Customer") {
            customerHomeViewModel.setCurrentUserObject(currentUserObject)
        } else {
            driverHomeViewModel.setCurrentUserObject(currentUserObject)
        }
        dropoffViewModel.setCurrentUserObject(currentUserObject)
        pickupViewModel.setCurrentUserObject(currentUserObject)
        bookingViewModel.setCurrentUserObject(currentUserObject)
        userProfileViewModel.setCurrentUserObject(currentUserObject)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp()
    }
}