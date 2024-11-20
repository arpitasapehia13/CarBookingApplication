package com.example.mycabbooking.ui.driver.process_booking

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
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.cabbooking.model.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DriverProcessBookingFragment : Fragment(), OnMapReadyCallback {
    private var mViewModel: DriverProcessBookingViewModel? = null


    enum class BookingState {
        PICKUP,
        DROPOFF,
    }

    //Firestore instances
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var currentUser: FirebaseUser? = null


    private var pickUpText: TextView? = null
    private var dropOffText: TextView? = null
    private var addressText: TextView? = null
    private var serviceText: TextView? = null
    private var paymentMethodsText: TextView? = null

    private var directionBtn: Button? = null
    private var callBtn: Button? = null
    private var messageBtn: Button? = null
    private var pickUpBtn: Button? = null

    private var supportMapFragment: SupportMapFragment? = null //maps view
    private var mMap: GoogleMap? = null
    private var locationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var placesClient: PlacesClient? = null
    private var currentUserLocationMarker: Marker? = null
    private val currentOriginLocationMarker: Marker? = null
    private var currentDestinationLocationMarker: Marker? = null
    private var currentUserLocation: Location? = null
    private val currentRoute: ArrayList<Polyline> = ArrayList<Polyline>()

    //
    var state: BookingState? = null
    private var currentUserObject: User? = null
    private var currentBooking: Booking? = null
    private var currentBookingDocRef: DocumentReference? = null


    private fun linkViewElements(rootView: View) {
        pickUpText = rootView.findViewById<TextView>(R.id.text_pickup)
        dropOffText = rootView.findViewById<TextView>(R.id.text_dropOff)
        addressText = rootView.findViewById<TextView>(R.id.text_address)
        serviceText = rootView.findViewById<TextView>(R.id.text_service)
        paymentMethodsText = rootView.findViewById<TextView>(R.id.text_payment_method)

        directionBtn = rootView.findViewById<Button>(R.id.btn_direction)
        callBtn = rootView.findViewById<Button>(R.id.btn_call)
        messageBtn = rootView.findViewById<Button>(R.id.btn_message)
        pickUpBtn = rootView.findViewById<Button>(R.id.btn_pickUp)
    }

    // CONFIRM STATE
    @SuppressLint("SetTextI18n")
    private fun setViewInPickupState() {
        pickUpBtn.setText(R.string.btn_pickup_state) //Set 'i have arrived' for btn
        pickUpText!!.setBackgroundColor(resources.getColor(R.color.darker_gray))
        dropOffText!!.setBackgroundColor(resources.getColor(R.color.light_gray))


        //TODO

        //Draw currentDestinationLocationMarker as pickup place
        if (currentDestinationLocationMarker != null) {
            currentDestinationLocationMarker.remove()
        }
        currentDestinationLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        currentBooking.pickUpPlaceLatitude,
                        currentBooking.pickUpPlaceLongitude
                    )
                )
                .icon(
                    bitmapDescriptorFromVector(
                        activity,
                        R.drawable.ic_location_blue, Color.BLUE
                    )
                )
                .title("Pickup!")
        )


        //Set destination address as pickup address
        addressText.setText(currentBooking.pickupPlaceAddress)

        //Set service text
        serviceText.setText(currentBooking.transportationType + "-" + currentBooking.priceInVND)
    }

    //DROP OFF STATE
    @SuppressLint("SetTextI18n")
    private fun setViewInDropOffState() {
        pickUpBtn.setText(R.string.btn_dropoff_state)
        pickUpText!!.setBackgroundColor(resources.getColor(R.color.light_gray))
        dropOffText!!.setBackgroundColor(resources.getColor(R.color.darker_gray))

        //Draw currentDestinationLocationMarker as dropoff place
        if (currentDestinationLocationMarker != null) {
            currentDestinationLocationMarker.remove()
        }
        currentDestinationLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        currentBooking.dropOffPlaceLatitude,
                        currentBooking.dropOffPlaceLongitude
                    )
                )
                .icon(
                    bitmapDescriptorFromVector(
                        activity,
                        R.drawable.ic_location_red, Color.RED
                    )
                )
                .title("Drop off!")
        )


        //Set destination address as drop off address
        addressText.setText(currentBooking.dropOffPlaceAddress)
    }

    /**
     * Set view in check out state
     */
    private fun setViewInCheckoutState() {
        pickUpBtn.setText(R.string.btn_pickup_state)
    }


    /**
     * Update arrival status on DB
     */
    private fun updateArrivalStatusOnDB() {
        currentBookingDocRef.update(Constants.FSBooking.arrived, true)
            .addOnSuccessListener(object : OnSuccessListener<Void?>() {
                override fun onSuccess(aVoid: Void?) {
                    setViewInDropOffState()
                }
            })
    }

    /**
     * Update finish status on DB
     */
    private fun updateFinishStatusOnDB() {
        currentBookingDocRef.update(Constants.FSBooking.finished, true)
            .addOnSuccessListener(object : OnSuccessListener<Void?>() {
                override fun onSuccess(aVoid: Void?) {
                    sendDataToCheckoutFragment()
                    loadCheckoutFragment()
                }
            })
    }

    /**
     * load check out fragment
     */
    private fun loadCheckoutFragment() {
        //TODO
        val fm = childFragmentManager
        val driverCheckoutFragment: DriverCheckoutFragment = DriverCheckoutFragment.newInstance()
        driverCheckoutFragment.show(fm, "fragment_driver_checkout")
    }

    /**
     * send data to checkout fragment
     */
    private fun sendDataToCheckoutFragment() {
        //TODO
        val driverCheckoutViewModel: DriverCheckoutViewModel =
            ViewModelProvider(requireActivity()).get<T>(
                DriverCheckoutViewModel::class.java
            )
        driverCheckoutViewModel.setPriceInVNDString(currentBooking.priceInVND)
    }

    /**
     * event listener for pickupbtn
     */
    private fun addEventListenerForPickUpButton() {
        pickUpBtn!!.setOnClickListener {
            when (state) {
                BookingState.PICKUP -> {
                    updateArrivalStatusOnDB()
                    state = BookingState.DROPOFF
                }

                BookingState.DROPOFF -> updateFinishStatusOnDB()
                else -> {}
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =
            inflater.inflate(R.layout.fragment_driver_recieve_booking, container, false)
        linkViewElements(view)
        state = BookingState.PICKUP
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = mAuth.getCurrentUser()
        initMapsFragment()
        setActionHandlers()
        //        setViewInPickupState();
        return view
    }

    private fun setActionHandlers() {
        addEventListenerForPickUpButton()
    }

    /**
     * Init Google MapsFragment
     */
    private fun initMapsFragment() {
        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_maps) as SupportMapFragment?
        supportMapFragment.getMapAsync(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        addEventListenerForPickUpButton();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel!!.setCurrentBookingDocRef(null)
        mViewModel!!.setCurrentUserObject(null)
        mViewModel!!.setCheckoutDone(null)
    }

    private val currentBookingDetails: Unit
        get() {
            currentBookingDocRef.get()
                .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot?>() {
                    override fun onSuccess(documentSnapshot: DocumentSnapshot) {
                        currentBooking = documentSnapshot.toObject(Booking::class.java)
                        setViewInPickupState()
                    }
                })
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get<DriverProcessBookingViewModel>(
            DriverProcessBookingViewModel::class.java
        )

        //get current user object
        mViewModel!!.currentUserObject.observe(viewLifecycleOwner, object : Observer<User?> {
            override fun onChanged(user: User) {
                if (user == null) return
                currentUserObject = user
            }
        })

        //get current booking doc ref
        mViewModel!!.currentBookingDocRef.observe(
            viewLifecycleOwner,
            object : Observer<DocumentReference?> {
                override fun onChanged(documentReference: DocumentReference) {
                    if (documentReference == null) return
                    currentBookingDocRef = documentReference
                    this.currentBookingDetails
                    //                addEventListenerForPickUpButton();
                }
            })

        //Done checkout
        mViewModel!!.checkoutDone.observe(viewLifecycleOwner, object : Observer<Boolean?> {
            override fun onChanged(aBoolean: Boolean) {
                if (aBoolean == null) return
                Navigation.findNavController(activity, R.id.nav_host_fragment)
                    .navigate(R.id.nav_driver_home)
            }
        })
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
        locationRequest.setInterval(10 * 1000) //20s
        locationRequest.setFastestInterval(10 * 1000) //20s
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
                    drawRouteToDestination()
                    updateCurrentDriverLocationOnDB(latLng) //TODO Changeeee this shit
                }
            },
            null
        )
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

    /**
     * Update drive location on firebase
     * @param newLatLng
     */
    private fun updateCurrentDriverLocationOnDB(newLatLng: LatLng) {
//        currentUserObject.setCurrentPositionLatitude(newLatLng.latitude);
//        currentUserObject.setCurrentPositionLongitude(newLatLng.longitude);
//        db.collection(Constants.FSUser.userCollection)
//                .document(currentUserObject.getDocId())
//                .set(currentUserObject)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
////                        drawNewCurrentUserLocationMarker(newLatLng);
//                    }
//                });

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

    /**
     * Draw route on the map
     * @param result
     */
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
     * Draw route to destination
     */
    private fun drawRouteToDestination() {
        // Checks, whether start and end locations are captured
        // Getting URL to the Google Directions API

        if (currentDestinationLocationMarker == null) return

        val url = getRouteUrl(
            currentUserLocationMarker.getPosition(),
            currentDestinationLocationMarker.getPosition(),
            "driving"
        )
            ?: return

        val fetchRouteDataTask: FetchRouteDataTask = FetchRouteDataTask()

        // Start fetching json data from Google Directions API
        fetchRouteDataTask.execute(url)
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
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
    private fun getRouteUrl(origin: LatLng, destination: LatLng, directionMode: String): String? {
        val originParam: String = (Constants.GoogleMaps.DirectionApi.originParam +
                "=" + origin.latitude).toString() + "," + origin.longitude
        val destinationParam: String = (Constants.GoogleMaps.DirectionApi.destinationParam +
                "=" + destination.latitude).toString() + "," + destination.longitude
        val modeParam: String = Constants.GoogleMaps.DirectionApi.modeParam + "=" + directionMode
        val params = "$originParam&$destinationParam&$modeParam"
        val output: String = Constants.GoogleMaps.DirectionApi.outputParam
        if (!isDetached) {
            return (Constants.GoogleMaps.DirectionApi.baseUrl + output + "?" + params
                    + "&key=" + getString(R.string.google_maps_key))
        }
        return null
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
        const val TAG: String = "driverBookingFragment"

        //Google maps variables
        private const val MY_LOCATION_REQUEST = 99
        fun newInstance(): DriverProcessBookingFragment {
            return DriverProcessBookingFragment()
        }
    }
}
