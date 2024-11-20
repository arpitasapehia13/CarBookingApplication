package com.example.mycabbooking.ui.customer.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.cabbooking.model.User

class CustomerHomeFragment : Fragment() {
    private var customerHomeViewModel: CustomerHomeViewModel? = null

    private var bikeBtn: ImageButton? = null
    private var carBtn: ImageButton? = null

    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var currentUser: FirebaseUser? = null

    private var currentUserObject: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_customer_home, container, false)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth.getCurrentUser()
        linkViewElements(view) //Link view elements to class properties
        setActionHandlers()


        return view
    }

    /**
     * link view elements
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        bikeBtn = rootView.findViewById<ImageButton>(R.id.bike_image_button)
        carBtn = rootView.findViewById<ImageButton>(R.id.car_image_button)
    }

    private fun setActionHandlers() {
        setBikeBtnActionHandler()
        setCarBtnActionHandler()
    }

    /**
     * Event listener for Bike btn
     */
    private fun setBikeBtnActionHandler() {
        bikeBtn.setOnClickListener(View.OnClickListener { //Pass data of chosen transportation type
            val checkoutViewModel: CheckoutViewModel = ViewModelProvider(requireActivity()).get<T>(
                CheckoutViewModel::class.java
            )
            checkoutViewModel.setTransportationType(Constants.Transportation.Type.bikeType)

            val bookingViewModel: BookingViewModel = ViewModelProvider(requireActivity()).get<T>(
                BookingViewModel::class.java
            )
            bookingViewModel.setTransportationType(Constants.Transportation.Type.bikeType)

            //Move to Drop off page
            Navigation.findNavController(activity, R.id.nav_host_fragment)
                .navigate(R.id.nav_customer_booking)
        })
    }

    /**
     * Event listener for Car btn
     */
    private fun setCarBtnActionHandler() {
        carBtn.setOnClickListener(View.OnClickListener { //Pass data of chosen transportation type
            val checkoutViewModel: CheckoutViewModel = ViewModelProvider(requireActivity()).get<T>(
                CheckoutViewModel::class.java
            )
            checkoutViewModel.setTransportationType(Constants.Transportation.Type.carType)

            val bookingViewModel: BookingViewModel = ViewModelProvider(requireActivity()).get<T>(
                BookingViewModel::class.java
            )
            bookingViewModel.setTransportationType(Constants.Transportation.Type.carType)

            //Move to Drop off page
            Navigation.findNavController(activity, R.id.nav_host_fragment)
                .navigate(R.id.nav_customer_booking)
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        customerHomeViewModel = ViewModelProvider(this).get<CustomerHomeViewModel>(
            CustomerHomeViewModel::class.java
        )
        customerHomeViewModel!!.currentUserObject.observe(
            viewLifecycleOwner,
            object : Observer<User?> {
                override fun onChanged(user: User) {
                    currentUserObject = user
                }
            })
    }
}