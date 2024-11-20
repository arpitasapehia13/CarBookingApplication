package com.example.mycabbooking.ui.driver.alert

import android.R
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.util.Objects

class DriverAlertFragment : DialogFragment() {
    private var priceText: TextView? = null
    private var distanceText: TextView? = null
    private var pickUpLocationText: TextView? = null
    private var dropOffLocationText: TextView? = null

    private var declineBtn: Button? = null
    private var acceptBtn: Button? = null

    /**
     * Link view elements
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        priceText = rootView.findViewById<TextView>(R.id.priceTextView)
        distanceText = rootView.findViewById<TextView>(R.id.text_distance)
        pickUpLocationText = rootView.findViewById<TextView>(R.id.text_pickUpLocation)
        dropOffLocationText = rootView.findViewById<TextView>(R.id.text_dropLocation)
        declineBtn = rootView.findViewById<Button>(R.id.btn_decline)
        acceptBtn = rootView.findViewById<Button>(R.id.btn_accept)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.Theme_DeviceDefault_Dialog
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_driver_alert, container, false)
        linkViewElements(root)
        Objects.requireNonNull<Dialog?>(dialog).window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val driverHomeViewModel: DriverHomeViewModel = ViewModelProvider(requireActivity()).get<T>(
            DriverHomeViewModel::class.java
        )


        declineBtn!!.setOnClickListener {
            driverHomeViewModel.setAcceptBookingBtnPressed(false)
            dismiss()
        }

        acceptBtn!!.setOnClickListener {
            driverHomeViewModel.setAcceptBookingBtnPressed(true)
            dismiss()
        }
    }

    /**
     * Set booking detail on layout file
     * @param booking
     */
    private fun setBookingDetails(booking: Booking) {
        priceText.setText(booking.priceInVND)
        distanceText.setText(booking.distanceInKm)
        pickUpLocationText.setText(booking.pickupPlaceAddress)
        dropOffLocationText.setText(booking.dropOffPlaceAddress)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val driverAlertViewModel: DriverAlertViewModel =
            ViewModelProvider(requireActivity()).get<DriverAlertViewModel>(
                DriverAlertViewModel::class.java
            )
        driverAlertViewModel.booking.observe(
            viewLifecycleOwner
        ) { booking -> setBookingDetails(booking) }
    }

    companion object {
        var TAG: String = "DriverAlertDialog"

        fun newInstance(): DriverAlertFragment {
            return DriverAlertFragment()
        }
    }
}
