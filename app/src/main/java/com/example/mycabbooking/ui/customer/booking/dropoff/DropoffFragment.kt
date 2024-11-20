package com.example.mycabbooking.ui.customer.booking.dropoff

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cabbooking.Constants
import java.util.Arrays

class DropoffFragment : Fragment() {
    private var mViewModel: DropoffViewModel? = null

    //Places autocomplete
    private var placesClient: PlacesClient? = null
    private var autocompleteFragment: AutocompleteSupportFragment? = null


    /**
     * Init GooglePlacesAutocomplete search bar
     */
    private fun initGooglePlacesAutocomplete() {
        //Init the SDK
        val apiKey = getString(R.string.google_maps_key)

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity().applicationContext, apiKey)
        }

        this.placesClient = Places.createClient(requireActivity().applicationContext)

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.maps_place_autocomplete_fragment) as AutocompleteSupportFragment?

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            Arrays.asList<T>(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.PLUS_CODE
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_dropoff, container, false)
        //linkViewElements()
        initGooglePlacesAutocomplete()
        setActionHandlers()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bookingViewModel: BookingViewModel = ViewModelProvider(requireActivity()).get<T>(
            BookingViewModel::class.java
        )
        autocompleteFragment.setOnPlaceSelectedListener(null)
    }

    fun setActionHandlers() {
        setPlaceSelectedActionHandler()
    }

    /**
     * Set up a PlaceSelectionListener to handle the response
     */
    private fun setPlaceSelectedActionHandler() {
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener() {
            override fun onPlaceSelected(place: Place) {
                if (place == null) return
                //                smoothlyMoveCameraToPosition(place.getLatLng(), Constants.GoogleMaps.CameraZoomLevel.betweenCityAndStreets);
                //Send customer selected drop off place to booking fragment
                val bookingViewModel: BookingViewModel =
                    ViewModelProvider(requireActivity()).get<T>(
                        BookingViewModel::class.java
                    )
                bookingViewModel.setCustomerSelectedDropOffPlace(place)
            }

            override fun onError(status: Status) {
                Toast.makeText(
                    activity!!.applicationContext,
                    Constants.ToastMessage.placeAutocompleteError + status, Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel =
            ViewModelProvider(requireActivity()).get<DropoffViewModel>(DropoffViewModel::class.java)
    }

    companion object {
        fun newInstance(): DropoffFragment {
            return DropoffFragment()
        }
    }
}