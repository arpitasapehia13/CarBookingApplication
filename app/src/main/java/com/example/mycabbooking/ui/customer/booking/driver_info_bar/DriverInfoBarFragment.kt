package com.example.mycabbooking.ui.customer.booking.driver_info_bar

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.cabbooking.Constants
import com.example.cabbooking.model.User

class DriverInfoBarFragment : Fragment() {
    private var mViewModel: DriverInfoBarViewModel? = null

    private var driverUsernameTextView: TextView? = null
    private var plateNumberTextView: TextView? = null
    private var transportationTypeTextView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var profileImage: ImageView? = null

    //Firestore instances
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
        currentUser = mAuth.getCurrentUser()
        mStorageRef = FirebaseStorage.getInstance().getReference()
        return view
    }

    /**
     * Link view elements from xml file
     * @param rootView
     */
    private fun linkViewElement(rootView: View) {
        driverUsernameTextView = rootView.findViewById<TextView>(R.id.driverUsernameTextView)
        plateNumberTextView = rootView.findViewById<TextView>(R.id.plateNumberTextView)
        transportationTypeTextView =
            rootView.findViewById<TextView>(R.id.transportationTypeTextView)
        ratingBar = rootView.findViewById<RatingBar>(R.id.score_rating_bar)
        profileImage = rootView.findViewById<ImageView>(R.id.profile_avatar)
    }

    /**
     * Render driver information
     * @param driver
     */
    private fun setDriverInfo(driver: User) {
        driverUsernameTextView!!.text = driver.username
        plateNumberTextView!!.text = driver.vehiclePlateNumber
        transportationTypeTextView!!.text = driver.transportationType
        ratingBar.setRating(getRatingAverage(driver))
        setProfileImage()
    }

    /**
     * Get driver profile image
     */
    private fun setProfileImage() {
        // Retrieve driver information
        db.collection(Constants.FSUser.userCollection)
            .whereEqualTo(Constants.FSUser.emailField, driver!!.email)
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot?>() {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    for (doc in queryDocumentSnapshots) {
                        val driver: User = doc.toObject(User::class.java)

                        //                            assert driver != null;
                        // Get image URI
                        val fref: StorageReference =
                            mStorageRef.child("profileImages").child(driver.docId + ".jpeg")

                        fref.getDownloadUrl()
                            .addOnSuccessListener(object : OnSuccessListener<Uri?>() {
                                override fun onSuccess(uri: Uri?) {
                                    Picasso.get().load(uri).into(profileImage)
                                }
                            }).addOnFailureListener(object : OnFailureListener() {
                                override fun onFailure(exception: Exception) {
                                }
                            })
                    }
                }
            })
    }

    /**
     * Get driver avg rating
     * @param driver
     * @return avgRating
     */
    fun getRatingAverage(driver: User): Float {
        var total = 0.0
        for (_rating in driver.rating!!) {
            total += _rating.toDouble()
        }
        return (total / driver.rating!!.size).toFloat()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get<DriverInfoBarViewModel>(
            DriverInfoBarViewModel::class.java
        )
        mViewModel!!.driver.observe(viewLifecycleOwner, object : Observer<User?> {
            override fun onChanged(user: User) {
                driver = user
                setDriverInfo(user)
            }
        })
    }

    companion object {
        fun newInstance(): DriverInfoBarFragment {
            return DriverInfoBarFragment()
        }
    }
}