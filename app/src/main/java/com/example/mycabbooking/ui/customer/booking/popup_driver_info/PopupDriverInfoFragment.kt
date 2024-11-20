package com.example.mycabbooking.ui.customer.booking.popup_driver_info

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.example.cabbooking.Constants
import com.example.cabbooking.model.User

class PopupDriverInfoFragment : DialogFragment() {
    private var mViewModel: PopupDriverInfoViewModel? = null

    private var driverUsernameTextView: TextView? = null
    private var plateNumberAndBike: TextView? = null
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
        val view: View = inflater.inflate(R.layout.fragment_popup_driver_info, container, false)
        linkViewElements(view)
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
    private fun linkViewElements(rootView: View) {
        driverUsernameTextView = rootView.findViewById<TextView>(R.id.driverUsernameTextView)
        plateNumberAndBike = rootView.findViewById<TextView>(R.id.plateNumberAndBike)
        ratingBar = rootView.findViewById<RatingBar>(R.id.ratingBar)
        profileImage = rootView.findViewById<ImageView>(R.id.profile_avatar)
    }

    /**
     * Render driver information
     */
    @SuppressLint("SetTextI18n")
    private fun setDriverInfo() {
        driverUsernameTextView!!.text = driver!!.username
        plateNumberAndBike!!.text =
            driver!!.vehiclePlateNumber + " ● " + driver!!.transportationType
        ratingBar.setRating(getRatingAverage(driver!!))
        setProfileImage()
    }

    private fun setProfileImage() {
        db.collection(Constants.FSUser.userCollection)
            .whereEqualTo(Constants.FSUser.emailField, driver!!.email)
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot?>() {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    for (doc in queryDocumentSnapshots) {
                        val driver: User = doc.toObject(User::class.java)

                        //                            assert driver != null;
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

    fun getRatingAverage(driver: User): Float {
        var total = 0.0
        for (_rating in driver.rating!!) {
            total += _rating.toDouble()
        }
        return (total / driver.rating!!.size).toFloat()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get<PopupDriverInfoViewModel>(
            PopupDriverInfoViewModel::class.java
        )
        mViewModel!!.driver.observe(viewLifecycleOwner, object : Observer<User?> {
            override fun onChanged(user: User) {
                driver = user
                setDriverInfo()
            }
        })
    }

    companion object {
        fun newInstance(): PopupDriverInfoFragment {
            return PopupDriverInfoFragment()
        }
    }
}