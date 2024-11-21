package com.example.mycabbooking.ui.customer.booking.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mycabbooking.Constants
import com.example.mycabbooking.R
import com.example.mycabbooking.model.User
import com.example.mycabbooking.ui.customer.booking.BookingViewModel

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
            val bookingViewModel: BookingViewModel = ViewModelProvider(requireActivity()).get(BookingViewModel::class.java)
            bookingViewModel.setBookBtnPressed(true)
        }
    }

    /**
     * Link view elements from xml file
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        carCardView = rootView.findViewById(R.id.carCardView)
        bikeCardView = rootView.findViewById(R.id.bikeCardView)
        distanceCarTextView = rootView.findViewById(R.id.distanceCarTextView)
        distanceBikeTextView = rootView.findViewById(R.id.distanceBikeTextView)
        priceCarTextView = rootView.findViewById(R.id.priceCarTextView)
        priceBikeTextView = rootView.findViewById(R.id.priceBikeTextView)
        bookBtn = rootView.findViewById(R.id.bookBtn)
    }

    /**
     * Display card based on vehicle types
     */
    private fun hideAccordingCardView() {
        if (transportationType == Constants.Transportation.Type.carType) {
            carCardView?.visibility = View.VISIBLE
            bikeCardView?.visibility = View.GONE
        } else {
            carCardView?.visibility = View.GONE
            bikeCardView?.visibility = View.VISIBLE
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
        mViewModel?.setDistanceInKmString(null.toString())
        mViewModel?.setPriceInVNDString(null.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get(CheckoutViewModel::class.java)

        // Get customer currently chosen transportation type
        mViewModel!!.transportationType.observe(viewLifecycleOwner, Observer { value ->
            transportationType = value
            hideAccordingCardView()
        })

        mViewModel!!.distanceInKmString.observe(viewLifecycleOwner, Observer { value ->
            distanceInKmString = value
        })

        mViewModel!!.priceInVNDString.observe(viewLifecycleOwner, Observer { value ->
            priceInVNDString = value
            setCheckoutInfo()
        })
    }

    companion object {
        fun newInstance(): CheckoutFragment {
            return CheckoutFragment()
        }
    }
}
