package com.example.mycabbooking.ui.customer.booking.driver_info_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mycabbooking.Constants
import com.example.mycabbooking.R
import com.example.mycabbooking.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DriverInfoBarFragment : Fragment() {

    private var mViewModel: DriverInfoBarViewModel? = null

    private var driverUsernameTextView: TextView? = null
    private var plateNumberTextView: TextView? = null
    private var transportationTypeTextView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var profileImage: ImageView? = null

    // Firestore instances
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var currentUser: FirebaseUser? = null
    private var mStorageRef: StorageReference? = null

    private var driver: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_driver_info_bar, container, false)
        linkViewElement(view)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth!!.currentUser
        mStorageRef = FirebaseStorage.getInstance().reference
        return view
    }

    /**
     * Link view elements from xml file
     * @param rootView
     */
    private fun linkViewElement(rootView: View) {
        driverUsernameTextView = rootView.findViewById(R.id.driverUsernameTextView)
        plateNumberTextView = rootView.findViewById(R.id.plateNumberTextView)
        transportationTypeTextView = rootView.findViewById(R.id.transportationTypeTextView)
        ratingBar = rootView.findViewById(R.id.score_rating_bar)
        profileImage = rootView.findViewById(R.id.profile_avatar)
    }

    /**
     * Render driver information
     * @param driver
     */
    private fun setDriverInfo(driver: User) {
        driverUsernameTextView!!.text = driver.username
        plateNumberTextView!!.text = driver.vehiclePlateNumber
        transportationTypeTextView!!.text = driver.transportationType
        ratingBar?.rating = getRatingAverage(driver)
        setProfileImage(driver)
    }

    /**
     * Get driver profile image
     */
    private fun setProfileImage(driver: User) {
        // Retrieve driver information
        db?.collection(Constants.FSUser.userCollection)
            ?.whereEqualTo(Constants.FSUser.emailField, driver.email)
            ?.get()
            ?.addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                for (doc in queryDocumentSnapshots) {
                    val driverFromDb: User? = doc.toObject(User::class.java)

                    if (driverFromDb != null) {
                        // Get image URI
                        val fref: StorageReference =
                            mStorageRef?.child("profileImages")?.child(driverFromDb.docId + ".jpeg")!!

                        fref.getDownloadUrl()
                            .addOnSuccessListener { uri ->
                                // Load image into ImageView
                                profileImage?.setImageURI(uri)
                            }
                            .addOnFailureListener { exception ->
                                // Handle failure here
                            }
                    }
                }
            }
            ?.addOnFailureListener { exception ->
                // Handle failure here
            }
    }

    /**
     * Get driver avg rating
     * @param driver
     * @return avgRating
     */
    fun getRatingAverage(driver: User): Float {
        var total = 0.0
        val ratingList = driver.rating
        if (ratingList != null && ratingList.isNotEmpty()) {
            for (_rating in ratingList) {
                total += _rating.toDouble()
            }
            return (total / ratingList.size).toFloat()
        }
        return 0f
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get(DriverInfoBarViewModel::class.java)
        mViewModel!!.driver.observe(viewLifecycleOwner, Observer({

            this.driver = it
            setDriverInfo(it)
        }))
    }

    companion object {
        fun newInstance(): DriverInfoBarFragment {
            return DriverInfoBarFragment()
        }
    }
}
