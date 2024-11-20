package com.example.mycabbooking.ui.driver.checkout

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer

class DriverCheckoutFragment : DialogFragment() {
    private var moneyText: TextView? = null
    private var moneyExtraText: TextView? = null
    private var exitBtn: Button? = null
    private var processBtn: Button? = null

    /**
     * Link view elements
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        moneyText = rootView.findViewById<TextView>(R.id.text_money)
        moneyExtraText = rootView.findViewById<TextView>(R.id.text_moneyExtra)
        exitBtn = rootView.findViewById<Button>(R.id.btn_exit)
        processBtn = rootView.findViewById<Button>(R.id.btn_process)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.Theme_Black_NoTitleBar_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_driver_checkout, container, false)
        linkViewElements(root)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get view model
        val driverProcessBookingViewModel: DriverProcessBookingViewModel =
            ViewModelProvider(requireActivity()).get<T>(
                DriverProcessBookingViewModel::class.java
            )

        // action handler for exit btn
        exitBtn!!.setOnClickListener {
            dismiss()
            driverProcessBookingViewModel.setCheckoutDone(true)
        }

        //action handler for process btn
        processBtn!!.setOnClickListener {
            dismiss()
            driverProcessBookingViewModel.setCheckoutDone(true)
        }
    }

    private fun setCheckoutDetails(priceInVNDString: String) {
        moneyText!!.text = priceInVNDString
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val driverCheckoutViewModel: DriverCheckoutViewModel =
            ViewModelProvider(requireActivity()).get<DriverCheckoutViewModel>(
                DriverCheckoutViewModel::class.java
            )
        driverCheckoutViewModel.priceInVNDString.observe(
            viewLifecycleOwner,
            object : Observer<String?> {
                override fun onChanged(s: String) {
                    setCheckoutDetails(s)
                }
            })
    }

    companion object {
        fun newInstance(): DriverCheckoutFragment {
            return DriverCheckoutFragment()
        }
    }
}