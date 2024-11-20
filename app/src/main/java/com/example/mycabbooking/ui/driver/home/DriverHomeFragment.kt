package com.example.mycabbooking.ui.driver.home

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
import androidx.navigation.Navigation
import com.example.cabbooking.model.User

class DriverHomeFragment : Fragment(), OnMapReadyCallback {
    private var driverHomeViewModel: DriverHomeViewModel? = null

    private var supportMapFragment: SupportMapFragment? = null //maps view
    private var mMap: GoogleMap? = null
    private var locationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var placesClient: PlacesClient? = null
    private var currentUserLocationMarker: Marker? = null
    private val currentOriginLocationMarker: Marker? = null
    private val currentDestinationLocationMarker: Marker? = null
    private var currentUserLocation: Location? = null
    private val currentRoute: ArrayList<Polyline> = ArrayList<Polyline>()


    private var getMyLocationBtn: FloatingActionButton? = null

    //Firestore instances
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var currentUser: FirebaseUser? = null

    private var currentUserObject: User? = null

    //Booking flow
    var isBusy: Boolean? = null

    //    Boolean isUpdateLocationOnDatabase;
    var currentBookingDocRef: DocumentReference? = null
    var currentBookingListener: ListenerRegistration? = null


    /**
     * Connect view elements for further use
     * @param rootView
     */
    private fun linkViewElements(rootView: View) {
        getMyLocationBtn =
            rootView.findViewById<FloatingActionButton>(R.id.fragmentMapsFindMyLocationBtn)
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

    //    @SuppressLint("SetTextI18n")
    //    private void setDriverInfo() {
    //        driverName.setText("Username: " + currentUserObject.getUsername());
    //        vehicleTypeTextView.setText("Vehicle type: " + currentUserObject.getTransportationType());
    //        vehiclePlateNumberTextView.setText("Plate number: " + currentUserObject.getVehiclePlateNumber());
    //        driverRating.setText(Double.toString(currentUserObject.getRating()));
    //    }
    /**
     * Set Action Handlers
     */
    private fun setActionHandlers() {
        setGetMyLocationBtnHandler() //Find My location Button listener
    }

    /**
     * //Find My location Button listener
     */
    private fun setGetMyLocationBtnHandler() {
        getMyLocationBtn.setOnClickListener(View.OnClickListener { onGetPositionClick() })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_driver_home, container, false)
        linkViewElements(root)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth.getCurrentUser()
        initMapsFragment()
        setActionHandlers()
        return root
    }

    private fun loadDriverInfoFragment() {
        //Load driver info fragment
        val driverInfoFragment: DriverInfoFragment = DriverInfoFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.driver_info, driverInfoFragment).commit()
    }

    private fun sendDriverInfoDataToViewModel() {
        val driverInfoViewModel: DriverInfoViewModel = ViewModelProvider(requireActivity()).get<T>(
            DriverInfoViewModel::class.java
        )
        driverInfoViewModel.setCurrentUserObject(currentUserObject)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetBookingFlow()
        //        loadDriverInfoFragment();
        setListenerForBooking()
    }

    /**
     * Init Google MapsFragment
     */
    private fun initMapsFragment() {
        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_maps) as SupportMapFragment?
        supportMapFragment.getMapAsync(this)
    }

    /**
     * //Start location update listener
     */
    @SuppressLint("MissingPermission", "RestrictedApi")
    private fun startLocationUpdate() {
        locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(10 * 1000) //5s
        locationRequest.setFastestInterval(10 * 1000) //5s
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
                    updateCurrentDriverLocationOnDB(latLng) //TODO change this shitttttt
                }
            },
            null
        )
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

    private fun updateCurrentDriverLocationOnDB(newLatLng: LatLng) {
//        currentUserObject.setCurrentPositionLatitude(newLatLng.latitude);
//        currentUserObject.setCurrentPositionLongitude(newLatLng.longitude);

        val driverLocation: DriverLocation = DriverLocation()
        driverLocation.currentPositionLatitude = newLatLng.latitude
        driverLocation.currentPositionLongitude = newLatLng.longitude

        db.collection(Constants.FSDriverLocation.driverLocationCollection)
            .document(currentUserObject!!.docId)
            .update(
                Constants.FSDriverLocation.currentPositionLatitude,
                driverLocation.currentPositionLatitude,
                Constants.FSDriverLocation.currentPositionLongitude,
                driverLocation.currentPositionLongitude
            )
            .addOnSuccessListener(object : OnSuccessListener<Void?>() {
                override fun onSuccess(aVoid: Void?) {
//                        drawNewCurrentUserLocationMarker(newLatLng);
                }
            })
    }

    /**
     * Update current user location marker
     * @param newLatLng
     */
    private fun updateCurrentUserLocationMarker(newLatLng: LatLng) {
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove()
        }
        drawNewCurrentUserLocationMarker(newLatLng)

        //        updateCurrentDriverLocationOnDB(newLatLng);
    }

    private fun drawNewCurrentUserLocationMarker(newLatLng: LatLng) {
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

    /**
     * Smoothly change camera position with zoom level
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

    /*************************************************** For booking synchronization  */
    private fun resetBookingFlow() {
        isBusy = false
        if (currentBookingDocRef != null) currentBookingDocRef = null
        if (currentBookingListener != null) {
            currentBookingListener.remove()
            currentBookingListener = null
        }

        loadDriverInfoFragment()
    }

    private fun setListenerForBooking() {
        db.collection(Constants.FSBooking.bookingCollection)
            .addSnapshotListener(object : EventListener<QuerySnapshot?>() {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        return
                    }

                    //Check if the driver is currently busy
                    if (isBusy!!) return

                    for (doc in value) {
                        val booking: Booking = doc.toObject(Booking::class.java)
                        //This booking is available and matches transportation type
                        if (booking.available && booking.transportationType == currentUserObject!!.transportationType) {
                            currentBookingDocRef = doc.getReference()
                            sendDataToAlertViewModel(booking)
                            loadDriverAlertFragment()
                            break
                        }
                    }
                }
            })
    }

    /**
     * Send data to alertViewModel
     * @param booking
     */
    private fun sendDataToAlertViewModel(booking: Booking) {
        val driverAlertViewModel: DriverAlertViewModel =
            ViewModelProvider(requireActivity()).get<T>(
                DriverAlertViewModel::class.java
            )
        driverAlertViewModel.setBooking(booking)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        driverHomeViewModel!!.setAcceptBookingBtnPressed(null)
    }

    /**
     * Show the driver alert dialog
     */
    private fun loadDriverAlertFragment() {
        val fm = childFragmentManager
        val driverAlertFragment: DriverAlertFragment = DriverAlertFragment.newInstance()
        driverAlertFragment.show(fm, "fragment_notify_booking")
    }

    private fun checkBookingStillAvailable() {
        currentBookingDocRef.get()
            .addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot?>() {
                override fun onComplete(task: Task<DocumentSnapshot?>) {
                    val booking: Booking = task.getResult().toObject(Booking::class.java)
                    if (booking.available) {
                        setDriverOfCurrentBooking()
                    } else {
                        resetBookingFlow()
                    }
                }
            })
    }

    private fun handleAcceptBooking() {
        isBusy = true
        checkBookingStillAvailable()
    }

    private fun sendDataToDriverProcessBookingViewModel() {
        val driverProcessBookingViewModel: DriverProcessBookingViewModel =
            ViewModelProvider(requireActivity()).get<T>(
                DriverProcessBookingViewModel::class.java
            )
        driverProcessBookingViewModel.setCurrentUserObject(currentUserObject)
        driverProcessBookingViewModel.setCurrentBookingDocRef(currentBookingDocRef)
    }

    private fun loadDriverProcessBookingFragment() {
        Navigation.findNavController(activity, R.id.nav_host_fragment)
            .navigate(R.id.nav_driver_booking)
    }

    private fun setDriverOfCurrentBooking() {
        currentBookingDocRef.update(
            Constants.FSBooking.driver, currentUserObject,
            Constants.FSBooking.available, false
        ).addOnSuccessListener(object : OnSuccessListener<Void?>() {
            override fun onSuccess(aVoid: Void?) {
                //TODO move to DriverBookingFragment
                sendDataToDriverProcessBookingViewModel()
                loadDriverProcessBookingFragment()
            }
        })
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        driverHomeViewModel = ViewModelProvider(requireActivity()).get<DriverHomeViewModel>(
            DriverHomeViewModel::class.java
        )
        driverHomeViewModel!!.currentUserObject.observe(
            viewLifecycleOwner,
            object : Observer<User?> {
                override fun onChanged(user: User) {
                    if (user == null) return
                    currentUserObject = user
                    sendDriverInfoDataToViewModel()
                }
            })

        //****************************** For booking synchronization ******************************//
        driverHomeViewModel!!.acceptBookingBtnPressed.observe(
            viewLifecycleOwner,
            object : Observer<Boolean?> {
                override fun onChanged(aBoolean: Boolean) {
                    if (aBoolean == null) return
                    if (aBoolean) {
                        handleAcceptBooking()
                    } else {
                        resetBookingFlow()
                    }
                    driverHomeViewModel!!.setAcceptBookingBtnPressed(null)
                }
            })
    }

    /*************************************************** For booking synchronization  */
    override fun onMapReady(googleMap: GoogleMap?) {
        val apiKey = getString(R.string.google_maps_key)
        if (!Places.isInitialized()) { //Init GooglePlaceAutocomplete if not existed
            Places.initialize(requireActivity().applicationContext, apiKey)
        }
        this.placesClient = Places.createClient(requireActivity().applicationContext)
        mMap = googleMap
        requestPermission() //Request user for location permission
        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mMap.getUiSettings().setZoomControlsEnabled(true)
        startLocationUpdate() //Start location update listener
        //        setUpCluster(); //Set up cluster on Google Map
        onGetPositionClick() // Position the map.
    }

    companion object {
        //Google maps variables
        private const val MY_LOCATION_REQUEST = 99
    }
}