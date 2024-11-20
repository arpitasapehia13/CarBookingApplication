package com.example.mycabbooking.ui.customer.booking.processing_booking

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.cabbooking.R

class ProcessingBookingFragment : Fragment() {
    private var mViewModel: ProcessingBookingViewModel? = null

    private var originTextView: TextView? = null
    private var destinationTextView: TextView? = null
    private var priceTextView: TextView? = null
    private var cancelBookingBtn: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_processing_booking, container, false)
        linkViewElements(view)
        setActionHandlers()
        return view
    }

    private fun setActionHandlers() {
        cancelBookingBtn!!.setOnClickListener {
            val bookingViewModel: BookingViewModel = ViewModelProvider(requireActivity()).get<T>(
                BookingViewModel::class.java
            )
            bookingViewModel.setCancelBookingBtnPressed(true)
        }
    }

    /**
     * Link view elements from xml file
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        originTextView = rootView.findViewById<TextView>(R.id.originTextView)
        destinationTextView = rootView.findViewById<TextView>(R.id.destinationTextView)
        priceTextView = rootView.findViewById<TextView>(R.id.priceTextView)
        cancelBookingBtn = rootView.findViewById<Button>(R.id.cancelBookingBtn)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get<ProcessingBookingViewModel>(
            ProcessingBookingViewModel::class.java
        )
        mViewModel!!.dropOffPlaceString.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String) {
                destinationTextView!!.text = s
            }
        })

        mViewModel!!.pickupPlaceString.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String) {
                originTextView!!.text = s
            }
        })

        mViewModel!!.priceInVNDString.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String) {
                priceTextView!!.text = s
            }
        })
    }

    companion object {
        fun newInstance(): ProcessingBookingFragment {
            return ProcessingBookingFragment()
        }
    }
}