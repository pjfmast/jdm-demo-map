package avd.jdm.demomaps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import avd.jdm.demomaps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val blindwalls: List<BlindWall> by lazy {
        BlindWallsReader(this).read()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        // stap 5.1. andere start locatie: Avans Hogeschoollaan: 51.5840579881456, 4.797372671775713
/*
        val avansHA = LatLng(51.5840579881456, 4.797372671775713)

        val avansHAMarkerOptions = MarkerOptions()
            .position(avansHA)
            .title("Avans HA")
            .snippet("Learn creating great Android Apps here!")
        val avansHAMarker = mMap.addMarker(avansHAMarkerOptions)
        avansHAMarker?.tag = 0 // we gebruiken dit verderop

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(avansHA, 12f))
*/

        // todo Stap 5.2. Controls op de Map toevoegen
        // Hint 1: simuleer pinch (en roteren map - view) met <Alt Gr>
        // Hint 2: simuleer tilt met <Alt Gr> en rechtermuisknop
/*
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isTiltGesturesEnabled = true
            isRotateGesturesEnabled = true
            isMyLocationButtonEnabled = true
        }

        mMap.apply {
            setMinZoomPreference(8f)
            setMaxZoomPreference(20f)
        }
*/

        // todo Stap 5.3: andere camera opties (MapType en CameraPosition.builder)
/*
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

        val cameraPosition = CameraPosition.builder()
            .target(avansHA)
            .zoom(17f)
            .bearing(90f)
            .tilt(45f)
            .build()

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

*/

        // todo stap 5.4 demonstrate setOnMarkerClick
/*
        mMap.setOnMarkerClickListener { marker ->
            val clickCount = ((marker.tag ?: 0) as Int) + 1
            marker.tag = clickCount
            Toast.makeText(
                this,
                "${marker.title} is $clickCount aangeklikt", Toast.LENGTH_LONG
            ).show()
            true //the click-event is handled
        }
*/

        // todo stap 6 Tekenen op de Map:
        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
/*
        val rondjeLD = googleMap.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(51.58589756724664, 4.794358257204294),
                    LatLng(51.58621754669707, 4.795420411974192),
                    LatLng(51.58612421959015, 4.796343091875315),
                    LatLng(51.58590423350819, 4.796246532350779),
                    LatLng(51.58562424968158, 4.7959246672689915),
                    LatLng(51.58537092949463, 4.7952487505972385),
                    LatLng(51.585444259167666, 4.794594291597605),
                    LatLng(51.58563091598323, 4.794315341860056),
                    LatLng(51.58589756724664, 4.794358257204294)
                )
        )

        rondjeLD.tag = "rondje LD"

        mMap.setOnPolylineClickListener { polyline ->
            if (polyline.pattern == null) {
                polyline.pattern = listOf<PatternItem>(Gap(20f), Dot())
            } else {
                polyline.pattern = null
            }
        }
*/

        // todo step 7. demo with BlindWall dataset
        // todo: 7a show without clustered markers first
        // todo: show effect on using: newLatLngBounds(bounds.build()
//        addMarkers()
        addClusterdMarkers()

        // camera position depending on Clustered markers:
        // setOnMapLoadedCallback: Sets a callback that's invoked when this map has finished rendering.
        mMap.setOnMapLoadedCallback {
            val bounds = LatLngBounds.builder()
            blindwalls.forEach { blindWall ->
                bounds.include(blindWall.latLng)
            }
            // todo show effect using this
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50))
        }

        mMap.setInfoWindowAdapter(BlindWallMarkerAdapter(this))


    }

    // wordt na het maken van clustered items niet meer benut.
    private fun addMarkers() {
        blindwalls.forEach { blindWall ->
            val marker = mMap.addMarker(
                MarkerOptions()
                    .title(blindWall.name)
                    .position(blindWall.latLng)
            )
            marker?.tag = blindWall
        }
    }

    private fun addClusterdMarkers() {
        val clusterManager = ClusterManager<BlindWall>(this, mMap)
        clusterManager.addItems(blindwalls)
        clusterManager.cluster()

        mMap.setOnCameraIdleListener {
            clusterManager.onCameraIdle()
        }
    }


}
