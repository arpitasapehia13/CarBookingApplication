package com.example.mycabbooking.ui.driver.driver_info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.cabbooking.R
import com.example.cabbooking.model.User

class DriverInfoFragment : Fragment() {
    private var mViewModel: DriverInfoViewModel? = null

    private var driverName: TextView? = null
    private var vehicleTypeTextView: TextView? = null
    private var vehiclePlateNumberTextView: TextView? = null
    private var driverRating: TextView? = null

    var currentUserObject: User? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_driver_info, container, false)
        linkViewElements(view)

        return view
    }

    /**
     * Connect view elements for further use
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        driverName = rootView.findViewById<TextView>(R.id.usernameTextView)
        vehicleTypeTextView = rootView.findViewById<TextView>(R.id.vehicleTypeTextView)
        vehiclePlateNumberTextView =
            rootView.findViewById<TextView>(R.id.vehiclePlateNumberTextView)
        driverRating = rootView.findViewById<TextView>(R.id.text_rating)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setDriverInfo() {
        driverName!!.text = "Username: " + currentUserObject!!.username
        vehicleTypeTextView!!.text = "Vehicle type: " + currentUserObject!!.transportationType
        vehiclePlateNumberTextView!!.text =
            "Plate number: " + currentUserObject!!.vehiclePlateNumber
        driverRating!!.text = String.format("%.1f", getRatingAverage(currentUserObject!!))
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
        mViewModel = ViewModelProvider(requireActivity()).get<DriverInfoViewModel>(
            DriverInfoViewModel::class.java
        )
        mViewModel!!.currentUserObject.observe(viewLifecycleOwner, object : Observer<User?> {
            override fun onChanged(user: User) {
                currentUserObject = user
                setDriverInfo()
            }
        })
    }

    companion object {
        fun newInstance(): DriverInfoFragment {
            return DriverInfoFragment()
        }
    }
}