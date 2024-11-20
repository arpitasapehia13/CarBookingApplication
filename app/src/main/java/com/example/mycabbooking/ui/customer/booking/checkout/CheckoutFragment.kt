package com.example.mycabbooking.ui.customer.booking.checkout

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.cabbooking.Constants
import com.example.cabbooking.model.User

class CheckoutFragment : Fragment() {
    private var mViewModel: CheckoutViewModel? = null
    private var transportationType: String? = null
    private var distanceInKmString: String? = null
    private var priceInVNDString: String? = null


    //View variables
    private var carCardView: CardView? = null
    private var bikeCardView: CardView? = null
    private var distanceCarTextView: TextView? = null
    private var distanceBikeTextView: TextView? = null
    private var priceCarTextView: TextView? = null
    private var priceBikeTextView: TextView? = null
    private var bookBtn: Button? = null

    private val currentUserObject: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_customer_checkout, container, false)
        linkViewElements(view)
        setActionHandlers()
        return view
    }

    private fun setActionHandlers() {
        bookBtn!!.setOnClickListener {
            val bookingViewModel: BookingViewModel = ViewModelProvider(requireActivity()).get<T>(
                BookingViewModel::class.java
            )
            bookingViewModel.setBookBtnPressed(true)
        }
    }

    /**
     * Link view elements from xml file
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        carCardView = rootView.findViewById<CardView>(R.id.carCardView)
        bikeCardView = rootView.findViewById<CardView>(R.id.bikeCardView)
        distanceCarTextView = rootView.findViewById<TextView>(R.id.distanceCarTextView)
        distanceBikeTextView = rootView.findViewById<TextView>(R.id.distanceBikeTextView)
        priceCarTextView = rootView.findViewById<TextView>(R.id.priceCarTextView)
        priceBikeTextView = rootView.findViewById<TextView>(R.id.priceBikeTextView)
        bookBtn = rootView.findViewById<Button>(R.id.bookBtn)
    }

    /**
     * Display card based on vehicle types
     */
    private fun hideAccordingCardView() {
        if (transportationType == Constants.Transportation.Type.carType) {
            carCardView.setVisibility(View.VISIBLE)
            bikeCardView.setVisibility(View.GONE)
        } else {
            carCardView.setVisibility(View.GONE)
            bikeCardView.setVisibility(View.VISIBLE)
        }
    }

    /**
     * Display booking information
     */
    private fun setCheckoutInfo() {
        if (transportationType == Constants.Transportation.Type.carType) {
            distanceCarTextView!!.text = distanceInKmString
            priceCarTextView!!.text = priceInVNDString
        } else {
            distanceBikeTextView!!.text = distanceInKmString
            priceBikeTextView!!.text = priceInVNDString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel!!.setDistanceInKmString(null)
        mViewModel!!.setPriceInVNDString(null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel =
            ViewModelProvider(requireActivity()).get<CheckoutViewModel>(CheckoutViewModel::class.java)

        //Get customer currently chosen transportation type
        mViewModel!!.transportationType.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String) {
                if (s == null) return
                transportationType = s
                hideAccordingCardView()
            }
        })

        mViewModel!!.distanceInKmString.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String) {
                if (s == null) return
                distanceInKmString = s
            }
        })

        mViewModel!!.priceInVNDString.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String) {
                if (s == null) return
                priceInVNDString = s
                setCheckoutInfo()
            }
        })
    }

    companion object {
        fun newInstance(): CheckoutFragment {
            return CheckoutFragment()
        }
    }
}