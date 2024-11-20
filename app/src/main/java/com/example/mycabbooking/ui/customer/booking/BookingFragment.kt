package com.example.mycabbooking.ui.customer.booking

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.cabbooking.Constants
import com.example.cabbooking.model.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Objects

class BookingFragment : Fragment(), OnMapReadyCallback {
    private var mViewModel: BookingViewModel? = null

    //View elements
    private var getMyLocationBtn: FloatingActionButton? = null
    private var restartBookingBtn: FloatingActionButton? = null

    //Maps marker clustering
    private val clusterManager: ClusterManager<MyClusterItem>? = null

    private var supportMapFragment: SupportMapFragment? = null //maps view
    private var mMap: GoogleMap? = null
    private var locationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var currentPickupLocationMarker: Marker? = null
    private var currentDropOffLocationMarker: Marker? = null
    private var currentUserLocationMarker: Marker? = null
    private var currentDriverLocationMarker: Marker? = null
    private var currentUserLocation: Location? = null
    private val prevUserLocation: LatLng? = null
    private val currentTargetLocationClusterItem: MyClusterItem? = null
    private val prevTargetLocation: LatLng? = null
    private val currentRoute: ArrayList<Polyline> = ArrayList<Polyline>()
    private var placesClient: PlacesClient? = null

    //Firebase, FireStore
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var currentUser: FirebaseUser? = null
    var currentUserObject: User? = null

    //Booking info
    var customerDropOffPlace: Place? = null
    var customerPickupPlace: Place? = null
    var transportationType: String? = null
    var distanceInKm: Double? = null
    var distanceInKmString: String? = null
    var priceInVNDString: String? = null

    //Booking flow
    var bookBtnPressed: Boolean? = null
    var cancelBookingBtnPressed: Boolean? = null
    var currentBookingDocRef: DocumentReference? = null
    var currentDriver: User? = null
    var currentBookingListener: ListenerRegistration? = null
    var currentDriverListener: ListenerRegistration? = null

    /**
     * Init Google MapsFragment
     */
    private fun initMapsFragment() {
        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_maps) as SupportMapFragment?
        supportMapFragment.getMapAsync(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_customer_booking, container, false)
        linkViewElements(view) //Link view elements to class properties
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth.getCurrentUser()
        initMapsFragment()
        setActionHandlers()


        return view
    }

    /**
     * Connect view elements of layout to this class variable
     *
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        getMyLocationBtn =
            rootView.findViewById<FloatingActionButton>(R.id.fragmentMapsFindMyLocationBtn)
        restartBookingBtn = rootView.findViewById<FloatingActionButton>(R.id.fragmentMapsBackBtn)
    }

    /**
     * Set Action Handlers
     */
    private fun setActionHandlers() {
        setGetMyLocationBtnHandler() //Find My location Button listener
        setRestartBtnHandler()
    }

    /**
     * Set event listener for restart btn
     */
    private fun setRestartBtnHandler() {
        restartBookingBtn.setOnClickListener(View.OnClickListener { resetBookingFlow() })
    }

    /**
     * Reset booking
     */
    private fun resetBookingFlow() {
        //Remove listener to driver driver marker
        removeListenerForDrawingDriverMarker()
        //Remove listener to current booking
        removeListenerForCurrentBooking()
        //Remove all markers if existed
        removeAllMarkers()
        //Remove current route
        removeCurrentRoute()
        //Go back to the picking drop-off place step
        loadDropOffPlacePickerFragment()
        //Hide back btn
        restartBookingBtn.setVisibility(View.GONE)
    }

    /**
     * Remove all the marker existing in the map fragment
     */
    private fun removeAllMarkers() {
        //Clear pickup/drop-off markers if exists
        if (currentPickupLocationMarker != null) {
            currentPickupLocationMarker.remove()
            currentPickupLocationMarker = null
        }

        //Clear drop-off markers if exists
        if (currentDropOffLocationMarker != null) {
            currentDropOffLocationMarker.remove()
            currentDropOffLocationMarker = null
        }

        if (currentDriverLocationMarker != null) {
            currentDriverLocationMarker.remove()
            currentDriverLocationMarker = null
        }
    }

    /**
     * Load drop-off picker fragment
     */
    private fun loadDropOffPlacePickerFragment() {
        //Load drop-off picker fragment
        val dropoffFragment: DropoffFragment = DropoffFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.booking_info, dropoffFragment).commit()
    }

    /**
     * Load pick up picker fragment
     */
    private fun loadPickupPlacePickerFragment() {
        val pickupFragment: PickupFragment = PickupFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.booking_info, pickupFragment).commit()
    }

    /**
     * Load checkout fragment
     */
    private fun loadCheckoutFragment() {
        val checkoutFragment: CheckoutFragment = CheckoutFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.booking_info, checkoutFragment).commit()
    }

    /**
     * Draw marker on dropoff and pickup map fragment
     */
    private fun drawDropOffAndPickupMarkers() {
        currentPickupLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(Objects.requireNonNull<T>(customerPickupPlace.getLatLng()))
                .icon(
                    bitmapDescriptorFromVector(
                        activity,
                        R.drawable.ic_location_blue, Color.BLUE
                    )
                )
                .title(customerPickupPlace.getAddress())
        )

        currentDropOffLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(customerDropOffPlace.getLatLng())
                .icon(
                    bitmapDescriptorFromVector(
                        activity,
                        R.drawable.ic_location_red, Color.RED
                    )
                )
                .title(customerDropOffPlace.getAddress())
        )

        currentPickupLocationMarker.showInfoWindow()
        currentDropOffLocationMarker.showInfoWindow()

        //Smoothly move camera to include 2 points in the map
        val latLngBounds: LatLngBounds.Builder = Builder()
        latLngBounds.include(customerDropOffPlace.getLatLng())
        latLngBounds.include(customerPickupPlace.getLatLng())
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 200))
    }

    /**
     * Draw route from pickup location to drop off location on the map fragment
     */
    private fun drawRouteFromPickupToDropOff() {
        // Checks, whether start and end locations are captured
        // Getting URL to the Google Directions API
        val url = getRouteUrl(
            customerPickupPlace.getLatLng(),
            customerDropOffPlace.getLatLng(),
            "driving"
        )

        val fetchRouteDataTask: FetchRouteDataTask = FetchRouteDataTask()

        // Start fetching json data from Google Directions API
        fetchRouteDataTask.execute(url)
    }


    /**
     * //Find My location Button listener
     */
    private fun setGetMyLocationBtnHandler() {
        getMyLocationBtn.setOnClickListener(View.OnClickListener { onGetPositionClick() })
    }

    /**
     * Smoothly change camera position with zoom level
     *
     * @param latLng
     * @param zoomLevel
     */
    private fun smoothlyMoveCameraToPosition(latLng: LatLng, zoomLevel: Float) {
        val cameraPosition: CameraPosition = Builder()
            .target(latLng)
            .build()
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        mMap.animateCamera(cameraUpdate)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
    }


    /**
     * Find my position action handler
     */
    @SuppressLint("MissingPermission")
    fun onGetPositionClick() {
        locationClient.getLastLocation().addOnSuccessListener
        (object : OnSuccessListener<Location?>() {
            override fun onSuccess(location: Location?) {
                if (location == null) {
                    Toast.makeText(
                        activity,
                        Constants.ToastMessage.currentLocationNotUpdatedYet,
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                val latLng: LatLng = LatLng(location.latitude, location.longitude)
                currentUserLocation = location
                if (currentUserLocationMarker == null) {
                    updateCurrentUserLocationMarker(latLng)
                }
                smoothlyMoveCameraToPosition(latLng, Constants.GoogleMaps.CameraZoomLevel.streets)
            }
        })
    }


    /**
     * Update current user location marker
     *
     * @param newLatLng
     */
    private fun updateCurrentUserLocationMarker(newLatLng: LatLng) {
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove()
        }
        currentUserLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(newLatLng)
                .icon(
                    bitmapDescriptorFromVector(
                        activity,
                        R.drawable.ic_current_location_marker, Color.BLUE
                    )
                )
                .title("You are here!")
        )
    }

    /**
     * Get BitmapDescriptor from drawable vector asset, for custom cluster marker
     *
     * @param context
     * @param vectorResId
     * @param color
     * @return
     */
    private fun bitmapDescriptorFromVector(
        context: Context?,
        vectorResId: Int,
        color: Int
    ): BitmapDescriptor? {
        if (context == null) {
            return null
        }
        val vectorDrawable: Drawable = ContextCompat.getDrawable(context, vectorResId)
        DrawableCompat.setTint(vectorDrawable, color)
        DrawableCompat.setTintMode(vectorDrawable, PorterDuff.Mode.SRC_IN)
        vectorDrawable.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        loadDropOffPlacePickerFragment();
        resetBookingFlow()
    }

    /**
     * Request user for location permission
     */
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
            MY_LOCATION_REQUEST
        )
    }

    /**
     * //Start location update listener
     */
    @SuppressLint("MissingPermission", "RestrictedApi")
    private fun startLocationUpdate() {
        locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5 * 1000) //5s
        locationRequest.setFastestInterval(5 * 1000) //5s
        locationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val location: Location = locationResult.getLastLocation()
                    val latLng: LatLng = LatLng(
                        location.latitude,
                        location.longitude
                    )
                    updateCurrentUserLocationMarker(latLng)

                    //                        updateCurrentRoute();
                }
            },
            null
        )
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()) { //Init GooglePlaceAutocomplete if not existed
            Places.initialize(requireActivity().applicationContext, apiKey)
        }
        this.placesClient = Places.createClient(requireActivity().applicationContext)
        mMap = googleMap
        requestPermission() //Request user for location permission
        locationClient = LocationServices.getFusedLocationProviderClient(activity)
        mMap.getUiSettings().setZoomControlsEnabled(true)
        startLocationUpdate() //Start location update listener
        //        setUpCluster(); //Set up cluster on Google Map
        onGetPositionClick() // Position the map.
    }

    /**
     * Clear the route in the map
     */
    private fun removeCurrentRoute() {
        //Clear current route
        if (currentRoute.isEmpty()) return
        for (polyline in currentRoute) {
            polyline.remove()
        }
        currentRoute.clear()
    }

    /**
     * Sent the required data to checkout fragment
     */
    @SuppressLint("DefaultLocale")
    private fun sendCheckoutInfoToCheckoutFragment() {
        val checkoutViewModel: CheckoutViewModel = ViewModelProvider(requireActivity()).get<T>(
            CheckoutViewModel::class.java
        )
        distanceInKmString = String.format("%.1fkm", distanceInKm)
        checkoutViewModel.setDistanceInKmString(distanceInKmString)
        val price = if (transportationType == Constants.Transportation.Type.bikeType) {
            (distanceInKm * Constants.Transportation.UnitPrice.bikeType) as Int
        } else {
            (distanceInKm * Constants.Transportation.UnitPrice.carType) as Int
        }
        priceInVNDString = "Rs.$price"
        checkoutViewModel.setPriceInVNDString(priceInVNDString)
    }

    /**
     * Reset all BookingViewModel data to null to prevent caching
     */
    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel!!.setCustomerSelectedDropOffPlace(null)
        mViewModel!!.setCustomerSelectedPickupPlace(null)
        mViewModel!!.setTransportationType(null)
        mViewModel!!.setBookBtnPressed(null)
        mViewModel!!.setCancelBookingBtnPressed(null)
        mViewModel!!.setFeedBackRating(null)
    }

    /**
     * Send data to ProcessBookingViewModel
     */
    private fun sendDataToProcessBookingViewModel() {
        val processingBookingViewModel: ProcessingBookingViewModel =
            ViewModelProvider(requireActivity()).get<T>(
                ProcessingBookingViewModel::class.java
            )
        processingBookingViewModel.setDropOffPlaceString(customerDropOffPlace.getName())
        processingBookingViewModel.setPickupPlaceString(customerPickupPlace.getName())
        processingBookingViewModel.setPriceInVNDString(priceInVNDString)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel =
            ViewModelProvider(requireActivity()).get<BookingViewModel>(BookingViewModel::class.java)

        mViewModel!!.currentUserObject.observe(requireActivity(), object : Observer<User?> {
            override fun onChanged(user: User) {
                currentUserObject = user
            }
        })

        //Action handler when customer's chosen drop off place is selected
        mViewModel!!.customerSelectedDropOffPlace.observe(viewLifecycleOwner,
            Observer<Any?> { place ->
                if (place == null) return@Observer
                customerDropOffPlace = place
                restartBookingBtn.setVisibility(View.VISIBLE) //Show back button

                //TODO Move to customerPickUpPlace fragment
                loadPickupPlacePickerFragment()
                smoothlyMoveCameraToPosition(
                    LatLng(currentUserLocation!!.latitude, currentUserLocation!!.longitude),
                    Constants.GoogleMaps.CameraZoomLevel.betweenStreetsAndBuildings
                )
            })

        //Action handler when customer's chosen pickup place is selected
        mViewModel!!.customerSelectedPickupPlace.observe(viewLifecycleOwner,
            Observer<Any?> { place ->
                if (place == null) return@Observer
                customerPickupPlace = place

                //TODO load checkout fragment
                loadCheckoutFragment()

                //TODO Draw 2 pickup/drop-off markers
                drawDropOffAndPickupMarkers()

                //TODO Draw route from pickup place to drop-off place
                drawRouteFromPickupToDropOff()
            })

        mViewModel!!.transportationType.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String) {
                if (s == null) return
                transportationType = s
            }
        })

        //*********************** For booking synchronization between user and driver flow *********************** //

        //Book btn pressed
        mViewModel!!.bookBtnPressed.observe(viewLifecycleOwner, object : Observer<Boolean?> {
            override fun onChanged(aBoolean: Boolean) {
                if (aBoolean == null) return
                restartBookingBtn.setVisibility(View.GONE)
                removeCurrentRoute() //Remove drawn route
                createNewBookingInDB() //Create new booking in DB, set listener to update for driver accepting this booking
                sendDataToProcessBookingViewModel()
                loadProcessingBookingFragment() //Load processing booking fragment
            }
        })

        //Cancel booking btn pressed
        mViewModel!!.cancelBookingBtnPressed.observe(
            viewLifecycleOwner,
            object : Observer<Boolean?> {
                override fun onChanged(aBoolean: Boolean) {
                    if (aBoolean == null) return
                    resetBookingFlow()
                    cancelBooking()
                }
            })

        mViewModel!!.feedBackRating.observe(viewLifecycleOwner, object : Observer<Int?> {
            override fun onChanged(integer: Int) {
                if (integer == null) return
                val ratingList = currentDriver!!.rating as ArrayList<Int>?
                ratingList!!.add(integer)
                db.collection(Constants.FSUser.userCollection)
                    .whereEqualTo(Constants.FSUser.emailField, currentDriver!!.email)
                    .get()
                    .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot?>() {
                        override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                            for (doc in queryDocumentSnapshots) {
                                val driver: User = doc.toObject(User::class.java)
                                db.collection(Constants.FSUser.userCollection)
                                    .document(driver.docId)
                                    .update(Constants.FSUser.rating, ratingList)
                                    .addOnSuccessListener(object : OnSuccessListener<Void?>() {
                                        override fun onSuccess(aVoid: Void?) {
                                            println("Update Rating success")
                                        }
                                    })
                                resetBookingFlow()
                            }
                        }
                    })
            }
        })
    }

    /*************************************************** For booking synchronization  */
    /**
     * Load process booking fragment
     */
    private fun loadProcessingBookingFragment() {
        //Load drop-off picker fragment
        val processingBookingFragment: ProcessingBookingFragment = ProcessingBookingFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.booking_info, processingBookingFragment).commit()
    }

    /**
     * Load DriverInfoBarFragment
     */
    private fun loadDriverInfoBarFragment() {
        //Load driver info bar fragment
        val driverInfoBarFragment: DriverInfoBarFragment = DriverInfoBarFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.booking_info, driverInfoBarFragment).commit()
    }

    /**
     * load PopupFounderDriverInfo
     */
    private fun loadPopupFoundedDriverInfo() {
        val fm = childFragmentManager
        val popupDriverInfoFragment: PopupDriverInfoFragment = PopupDriverInfoFragment.newInstance()
        popupDriverInfoFragment.show(fm, "fragment_notify_founded_driver")
    }

    /**
     * Load PopupDriverArrivalFragment
     */
    private fun loadPopupDriverArrivalFragment() {
        val fm = childFragmentManager
        val popUpDriverArrivalFragment: PopupDriverArrivalFragment =
            PopupDriverArrivalFragment.newInstance()
        popUpDriverArrivalFragment.show(fm, "fragment_notify_driver_arrived")
    }

    /**
     * load RatingFragment
     */
    private fun loadCustomerRatingFragment() {
        //Load customer rating fragment
//        RatingFragment ratingFragment = new RatingFragment();
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.booking_info, ratingFragment).commit();
        val fm = childFragmentManager
        val ratingFragment: RatingFragment = RatingFragment.newInstance()
        ratingFragment.show(fm, "fragment_feedback_rating")
    }

    /**
     * Create booking in db
     */
    private fun createNewBookingInDB() {
        val data: MutableMap<String, Any?> = HashMap()
        data[Constants.FSBooking.pickupPlaceAddress] = customerPickupPlace.getAddress()
        data[Constants.FSBooking.pickUpPlaceLatitude] = customerPickupPlace.getLatLng().latitude
        data[Constants.FSBooking.pickUpPlaceLongitude] = customerPickupPlace.getLatLng().longitude
        data[Constants.FSBooking.dropOffPlaceAddress] = customerDropOffPlace.getAddress()
        data[Constants.FSBooking.dropOffPlaceLatitude] = customerDropOffPlace.getLatLng().latitude
        data[Constants.FSBooking.dropOffPlaceLongitude] =
            customerDropOffPlace.getLatLng().longitude

        data[Constants.FSBooking.distanceInKm] = distanceInKmString
        data[Constants.FSBooking.priceInVND] = priceInVNDString
        data[Constants.FSBooking.transportationType] = transportationType
        data[Constants.FSBooking.available] = true
        data[Constants.FSBooking.finished] = false
        data[Constants.FSBooking.arrived] = false
        data[Constants.FSBooking.driver] = null

        db.collection(Constants.FSBooking.bookingCollection)
            .add(data)
            .addOnSuccessListener(object : OnSuccessListener<DocumentReference?>() {
                override fun onSuccess(documentReference: DocumentReference?) {
                    currentBookingDocRef = documentReference
                    setDetectAcceptedDriver()
                }
            })
            .addOnFailureListener(object : OnFailureListener() {
                override fun onFailure(e: Exception) {
                    Toast.makeText(
                        requireActivity(),
                        Constants.ToastMessage.addNewBookingToDbFail,
                        Toast.LENGTH_SHORT
                    ).show()
                    resetBookingFlow()
                }
            })
    }

    /**
     * send data to driverInfoBarViewModel
     */
    private fun sendDataToInfoBarViewModel() {
        val driverInfoBarViewModel: DriverInfoBarViewModel =
            ViewModelProvider(requireActivity()).get<T>(
                DriverInfoBarViewModel::class.java
            )
        driverInfoBarViewModel.setDriver(currentDriver)
    }

    /**
     * Send data to popupDriverArrivalViewModel
     */
    private fun sendDataToPopupDriverArrivalViewModel() {
        val popupDriverArrivalViewModel: PopupDriverArrivalViewModel =
            ViewModelProvider(requireActivity()).get<T>(
                PopupDriverArrivalViewModel::class.java
            )
        popupDriverArrivalViewModel.setDriver(currentDriver)
    }

    /**
     * Send data to ratingViewModel
     */
    private fun sendDataToRatingViewModel() {
        val ratingViewModel: RatingViewModel = ViewModelProvider(requireActivity()).get<T>(
            RatingViewModel::class.java
        )
        ratingViewModel.setDriver(currentDriver)
    }

    /**
     * Set driver for a booking
     */
    private fun setDetectAcceptedDriver() {
        currentBookingListener =
            currentBookingDocRef.addSnapshotListener(object : EventListener<DocumentSnapshot?>() {
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        return
                    }
                    if (value != null && value.exists()) {
                        val booking: Booking = value.toObject(Booking::class.java)
                        val driver: User = booking.getDriver()
                        if (driver != null) {
                            currentDriver = driver
                            sendDriverObjectToPopupDriverViewModel()
                            loadPopupFoundedDriverInfo()
                            setListenerForDrawingDriverMarker()
                            setListenerForDriverArrival()
                            sendDataToInfoBarViewModel()
                            loadDriverInfoBarFragment()
                        }
                    }
                }
            })
    }

    /**
     * Event listener for driver arrival
     */
    private fun setListenerForDriverArrival() {
        currentBookingListener.remove()
        currentBookingListener =
            currentBookingDocRef.addSnapshotListener(object : EventListener<DocumentSnapshot?>() {
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        return
                    }
                    if (value != null && value.exists()) {
                        val booking: Booking = value.toObject(Booking::class.java)
                        //If driver has arrived
                        if (booking.arrived) {
                            sendDataToPopupDriverArrivalViewModel()
                            loadPopupDriverArrivalFragment()
                            setListenerForBookingFinished()
                        }
                    }
                }
            })
    }

    /**
     * Event listener for finishing booking
     */
    private fun setListenerForBookingFinished() {
        currentBookingListener.remove()
        currentBookingListener =
            currentBookingDocRef.addSnapshotListener(object : EventListener<DocumentSnapshot?>() {
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        return
                    }
                    if (value != null && value.exists()) {
                        val booking: Booking = value.toObject(Booking::class.java)
                        //If driver has finished this booking
                        if (booking.finished) {
                            //TODO aaaaaaahhhhhhhhhhhhhhhhhhhhhhhhhh
                            println("Finisheddddddddd this trip")
                            sendDataToRatingViewModel()
                            loadCustomerRatingFragment()
                        }
                    }
                }
            })
    }

    /**
     * Event listener for driver marker
     */
    private fun setListenerForDrawingDriverMarker() {
        val resourceType: Int = if (transportationType == Constants.Transportation.Type.carType) {
            R.drawable.ic_checkout_car
        } else {
            R.drawable.ic_checkout_bike
        }
        db.collection(Constants.FSUser.userCollection)
            .whereEqualTo(Constants.FSUser.emailField, currentDriver!!.email)
            .get()
            .addOnSuccessListener(object : OnSuccessListener<QuerySnapshot?>() {
                override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                    for (doc in queryDocumentSnapshots) {
                        val driver: User = doc.toObject(User::class.java)
                        currentDriverListener =
                            db.collection(Constants.FSDriverLocation.driverLocationCollection)
                                .document(driver.docId)
                                .addSnapshotListener(object : EventListener<DocumentSnapshot?>() {
                                    override fun onEvent(
                                        value: DocumentSnapshot?,
                                        error: FirebaseFirestoreException?
                                    ) {
                                        if (error != null) {
                                            return
                                        }

                                        if (value != null && value.exists()) {
                                            val driverLocation: DriverLocation =
                                                value.toObject(DriverLocation::class.java)
                                            if (currentDriverLocationMarker != null) {
                                                currentDriverLocationMarker.remove()
                                                currentDriverLocationMarker = null
                                            }

                                            currentDriverLocationMarker = mMap.addMarker(
                                                MarkerOptions()
                                                    .position(
                                                        LatLng(
                                                            driverLocation.currentPositionLatitude,
                                                            driverLocation.currentPositionLongitude
                                                        )
                                                    )
                                                    .icon(
                                                        bitmapDescriptorFromVector(
                                                            activity,
                                                            resourceType, Color.RED
                                                        )
                                                    )
                                                    .title("Driver is here!")
                                            )
                                        }
                                    }
                                })
                    }
                }
            })
    }

    /**
     * Remove listener for driver marker
     */
    private fun removeListenerForDrawingDriverMarker() {
        if (currentDriverListener == null) return
        currentDriverListener.remove()
        currentDriverListener = null
    }

    /**
     * Send data to PopupDriverInfoViewModel
     */
    private fun sendDriverObjectToPopupDriverViewModel() {
        val popupDriverInfoViewModel: PopupDriverInfoViewModel =
            ViewModelProvider(requireActivity()).get<T>(
                PopupDriverInfoViewModel::class.java
            )
        popupDriverInfoViewModel.setDriver(currentDriver)
    }

    /**
     * Remove listener for current booking
     */
    private fun removeListenerForCurrentBooking() {
        if (currentBookingListener == null) return
        currentBookingListener.remove()
        currentBookingListener = null
    }

    /**
     * Cancel booking
     */
    private fun cancelBooking() {
        currentBookingDocRef.update(
            Constants.FSBooking.available,
            false
        ) //Set available field to false
        if (currentBookingListener != null) currentBookingListener.remove() //Remove listener
    }

    /*************************************************** For booking synchronization  */
    private fun drawRoute(result: List<List<HashMap<String, String>>>) {
        //Clear current route
        for (polyline in currentRoute) {
            polyline.remove()
        }
        currentRoute.clear()

        var points: ArrayList<LatLng?>? = null
        var lineOptions: PolylineOptions? = null

        for (i in result.indices) {
            points = ArrayList<Any?>()
            lineOptions = PolylineOptions()

            val route = result[i]

            for (j in route.indices) {
                val point = route[j]

                val lat = point["lat"]!!.toDouble()
                val lng = point["lng"]!!.toDouble()
                val position: LatLng = LatLng(lat, lng)

                points.add(position)
            }

            lineOptions.addAll(points)
            lineOptions.width(12)
            lineOptions.color(Color.RED)
            lineOptions.geodesic(true)
        }

        // Drawing polyline in the Google Map for the i-th route
        currentRoute.add(mMap.addPolyline(lineOptions))
    }

    /**
     * A Class to call Google Directions API with callback
     */
    private inner class FetchRouteDataTask : AsyncTask<String?, Void?, String?>() {
        override fun doInBackground(vararg url: String): String {
            var data = ""
            try {
                data = fetchDataFromURL(url[0])
            } catch (ignored: Exception) {
            }
            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val routeParserTask: RouteParserTask = RouteParserTask()
            routeParserTask.execute(result)
        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private inner class RouteParserTask :
        AsyncTask<String?, Int?, List<List<HashMap<String?, String?>?>?>?>() {
        // Parsing the data in non-ui thread
        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(jsonData[0])
                val parser: DirectionsJSONParser = DirectionsJSONParser(jObject)

                routes = parser.getRoutes()

                distanceInKm = parser.getTotalDistanceInKm()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            sendCheckoutInfoToCheckoutFragment() //Send calculated checkout info to checkout fragment
            drawRoute(result) //Draw new route
        }
    }

    /**
     * Method to get URL for fetching data from Google Directions API (finding direction from origin to destination)
     *
     * @param origin
     * @param destination
     * @param directionMode
     * @return
     */
    private fun getRouteUrl(origin: LatLng, destination: LatLng, directionMode: String): String {
        val originParam: String = (Constants.GoogleMaps.DirectionApi.originParam +
                "=" + origin.latitude).toString() + "," + origin.longitude
        val destinationParam: String = (Constants.GoogleMaps.DirectionApi.destinationParam +
                "=" + destination.latitude).toString() + "," + destination.longitude
        val modeParam: String = Constants.GoogleMaps.DirectionApi.modeParam + "=" + directionMode
        val params = "$originParam&$destinationParam&$modeParam"
        val output: String = Constants.GoogleMaps.DirectionApi.outputParam
        return (Constants.GoogleMaps.DirectionApi.baseUrl + output + "?" + params
                + "&key=" + getString(R.string.google_maps_key))
    }

    /**
     * A method to fetch json data from url
     */
    @Throws(IOException::class)
    private fun fetchDataFromURL(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection!!.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while ((br.readLine().also { line = it }) != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    companion object {
        //Google maps variables
        private const val MY_LOCATION_REQUEST = 99
        fun newInstance(): BookingFragment {
            return BookingFragment()
        }
    }
}