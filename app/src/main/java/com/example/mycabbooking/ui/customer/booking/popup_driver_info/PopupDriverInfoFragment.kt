package com.example.mycabbooking.ui.customer.booking.popup_driver_info

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mycabbooking.Constants
import com.example.mycabbooking.R
import com.example.mycabbooking.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class PopupDriverInfoFragment : DialogFragment() {
    private var mViewModel: PopupDriverInfoViewModel? = null

    private var driverUsernameTextView: TextView? = null
    private var plateNumberAndBike: TextView? = null
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
        val view: View = inflater.inflate(R.layout.fragment_popup_driver_info, container, false)
        linkViewElements(view)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth?.currentUser
        mStorageRef = FirebaseStorage.getInstance().reference

        return view
    }

    /**
     * Link view elements from xml file
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        driverUsernameTextView = rootView.findViewById(R.id.driverUsernameTextView)
        plateNumberAndBike = rootView.findViewById(R.id.plateNumberAndBike)
        ratingBar = rootView.findViewById(R.id.ratingBar)
        profileImage = rootView.findViewById(R.id.profile_avatar)
    }

    /**
     * Render driver information
     */
    @SuppressLint("SetTextI18n")
    private fun setDriverInfo() {
        driverUsernameTextView?.text = driver?.username
        plateNumberAndBike?.text = "${driver?.vehiclePlateNumber} ● ${driver?.transportationType}"
        ratingBar?.rating = getRatingAverage(driver!!)
        setProfileImage()
    }

    private fun setProfileImage() {
        db?.collection(Constants.FSUser.userCollection)
            ?.whereEqualTo(Constants.FSUser.emailField, driver?.email)
            ?.get()
            ?.addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    for (doc in queryDocumentSnapshots) {
                        val driver: User = doc.toObject(User::class.java)

                        val fref: StorageReference? =
                            mStorageRef?.child("profileImages")?.child("${driver.docId}.jpeg")

                        fref?.getDownloadUrl()
                            ?.addOnSuccessListener(object : OnSuccessListener<Uri?> {
                                override fun onSuccess(uri: Uri?) {
                                    Picasso.get().load(uri).into(profileImage)
                                }
                            })?.addOnFailureListener(object : OnFailureListener {
                                override fun onFailure(exception: Exception) {
                                    // Handle failure here if needed
                                }
                            })
                    }
                }
            })?.addOnFailureListener { exception ->
                // Handle failure of the Firestore query
            }
    }

    fun getRatingAverage(driver: User): Float {
        var total = 0.0
        for (_rating in driver.rating!!) {
            total += _rating.toDouble()
        }
        return (total / driver.rating!!.size).toFloat()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get(PopupDriverInfoViewModel::class.java)

        // Observe the driver LiveData
        mViewModel?.driver?.observe(viewLifecycleOwner, Observer {
            // Ensure user is not null before accessing it
            driver = it
            setDriverInfo() // Set the driver info when data changes
        })
    }

    companion object {
        fun newInstance(): PopupDriverInfoFragment {
            return PopupDriverInfoFragment()
        }
    }
}
