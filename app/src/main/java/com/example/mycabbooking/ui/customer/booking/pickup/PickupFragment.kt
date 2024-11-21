package com.example.mycabbooking.ui.customer.booking.pickup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mycabbooking.Constants
import com.example.mycabbooking.R
import com.example.mycabbooking.ui.customer.booking.BookingViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place  // Corrected import
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.Arrays

class PickupFragment : Fragment() {
    private var mViewModel: PickupViewModel? = null

    // Places autocomplete
    private var placesClient: PlacesClient? = null
    private var autocompleteFragment: AutocompleteSupportFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_pickup, container, false)
        initGooglePlacesAutocomplete()
        setActionHandlers()
        return view
    }

    fun setActionHandlers() {
        setPlaceSelectedActionHandler()
    }

    /**
     * Init GooglePlacesAutocomplete search bar
     */
    private fun initGooglePlacesAutocomplete() {
        // Init the SDK
        val apiKey = getString(R.string.google_maps_key)

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity().applicationContext, apiKey)
        }

        this.placesClient = Places.createClient(requireActivity().applicationContext)

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.maps_place_autocomplete_fragment) as AutocompleteSupportFragment?

        // Specify the types of place data to return.
        autocompleteFragment?.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.PLUS_CODE
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autocompleteFragment?.setOnPlaceSelectedListener(null)
    }

    /**
     * Set up a PlaceSelectionListener to handle the response
     */
    private fun setPlaceSelectedActionHandler() {
        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Send customer selected pickup place to booking fragment
                val bookingViewModel: BookingViewModel =
                    ViewModelProvider(requireActivity()).get(BookingViewModel::class.java)
                bookingViewModel.setCustomerSelectedPickupPlace(place)
            }

            override fun onError(status: Status) {
                Toast.makeText(
                    activity!!.applicationContext,
                    Constants.ToastMessage.placeAutocompleteError + status,
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(PickupViewModel::class.java)
    }

    companion object {
        fun newInstance(): PickupFragment {
            return PickupFragment()
        }
    }
}
