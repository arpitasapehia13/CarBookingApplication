package com.example.mycabbooking.ui.customer.booking.popup_driver_arrived

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mycabbooking.R
import com.example.mycabbooking.model.User

class PopupDriverArrivalFragment : DialogFragment() {
    private var mViewModel: PopupDriverArrivalViewModel? = null

    private var driverUsernameTextView: TextView? = null
    private var vehicleInfo: TextView? = null

    private var closeBtn: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_pop_up_driver_arrival, container, false)
        linkViewElements(view)

        closeBtn!!.setOnClickListener { dismiss() }

        return view
    }

    /**
     * Link view elements from xml file
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        driverUsernameTextView = rootView.findViewById<TextView>(R.id.driverUsernameTextView)
        vehicleInfo = rootView.findViewById<TextView>(R.id.vehicleInfo)
        closeBtn = rootView.findViewById<Button>(R.id.closeBtn)
    }

    /**
     * Render driver information to view
     * @param driver
     */
    @SuppressLint("SetTextI18n")
    private fun setDriverInfo(driver: User) {
        driverUsernameTextView!!.text = driver.username
        vehicleInfo!!.text = driver.vehiclePlateNumber + " ● " + driver.transportationType
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get<PopupDriverArrivalViewModel>(PopupDriverArrivalViewModel::class.java)
        mViewModel!!.driver.observe(viewLifecycleOwner, Observer<User?> { user ->
            // Check if the user is not null before passing it to setDriverInfo
            user?.let {
                setDriverInfo(it)
            }
        })
    }

    companion object {
        fun newInstance(): PopupDriverArrivalFragment {
            return PopupDriverArrivalFragment()
        }
    }
}
