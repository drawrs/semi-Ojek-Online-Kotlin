package com.khilman.mapkotlinretrofit

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Path
import android.location.Location
import android.location.LocationProvider
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocomplete

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.khilman.mapkotlinretrofit.Init.InitRetrofit
import com.khilman.mapkotlinretrofit.Init.ResponseJSON
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Response
import java.util.jar.Manifest
import javax.security.auth.callback.Callback

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    var markerUtama : Marker? = null
    var markerAwal : Marker? = null
    var markerAkhir : Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        var permission = (android.Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(permission), 2)
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
        val sydney = LatLng(-6.1954047,106.7774069)
        // simpan ke marker utama

        markerUtama = mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Jakarta"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        // auto zoom
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17.toFloat()))
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.uiSettings.isCompassEnabled = true
        // tipe peta
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
    //    mMap!!.uiSettings.isMyLocationButtonEnabled = true
        //mMap!!.isMyLocationEnabled = true
      //  mMap!!.isTrafficEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.checkSelfPermission(this@MapsActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this@MapsActivity,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        // ngambil lokasi
        mMap = googleMap

        edtdarimana.setOnClickListener {
           completeAuto(1)
        }
        edtkemana.setOnClickListener {
            completeAuto(2)
        }
        btnCheckIn.setOnClickListener {
            Toast.makeText(applicationContext, "Coming Soon..", Toast.LENGTH_SHORT).show()
        }

    }

    private fun completeAuto(i: Int) {
        val typeFilter = AutocompleteFilter.Builder()
                //.setTypeFilter(Place.TYPE_LAUNDRY) Filter berdasarkan tipe tempat
                .setCountry("ID") // nah ini filter daerah indonesia aja
                .build()
        var intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter)
                .build(this@MapsActivity)
        //var intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this)
        //intent.setFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)

        startActivityForResult(intent, i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            // Ambil koordinat dari mana
            // check key
            if (requestCode == 1 && resultCode != null){
                // get data pengambilan
                var place = PlaceAutocomplete.getPlace(this, data)
                var lat = place.latLng.latitude
                var lon = place.latLng.longitude

                // include to latlang
                var awal = LatLng(lat, lon) // nama
                // menghapus marker
               // mMap!!.clear()
                markerUtama?.isVisible = false

                if (edtdarimana.text.toString().length > 0){
                    markerAwal?.isVisible = false
                }
                // tampilkan ke settext
                edtdarimana.setText(place.address.toString())
                // add marker di maps
                markerAwal = mMap!!.addMarker(MarkerOptions().position(awal)
                        .title(place.address.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                // set camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(awal, 20.toFloat()))
                // get data pengembalian

            }
            // ambil koordinat kemana
            else if (requestCode == 2 && resultCode != null){
                // get data pengambilan
                var place = PlaceAutocomplete.getPlace(this, data)
                var lat = place.latLng.latitude
                var lon = place.latLng.longitude

                // include to latlang
                var akhir = LatLng(lat, lon) // nama lokasi
                // tampilkan ke settext
                edtkemana.setText(place.address.toString())
                // add marker di maps
                if (edtkemana.text.toString().length > 0){
                    markerAkhir?.isVisible = false
                }
                // aksi route
                actionRoute()
                markerAkhir = mMap!!.addMarker(MarkerOptions().position(akhir)
                        .title(place.address.toString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                // set camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(akhir, 20.toFloat()))
                // get data pengembalian

            } else {
                Toast.makeText(applicationContext, "Tidak ditemukan !", Toast.LENGTH_SHORT).show()
            }
        } catch (e : Exception){

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 2){
            mMap!!.isMyLocationEnabled = true
            //mMap!!.setOnMyLocationButtonClickListener(GoogleMap.OnMyLocationButtonClickListener { MyLocationClicked() })
        }
    }

    private fun MyLocationClicked(): Boolean {
        // ketika my location di klik
        Toast.makeText(applicationContext, "Telusuri..", Toast.LENGTH_SHORT).show()
        return false
    }

    private fun actionRoute() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        var api = InitRetrofit().getInitInstance()
        // get request to server
        var call = api?.request_route(edtdarimana.text.toString(), edtkemana.text.toString())


        call?.enqueue(object : retrofit2.Callback<ResponseJSON> {
            override fun onResponse(call: Call<ResponseJSON>?, response: Response<ResponseJSON>?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d("response : ", response?.message())
                if(response != null){
                    if (response.isSuccessful){
                        // get json array route
                        var route = response.body()?.routes
                        // get object overview polyline
                        var overview = route?.get(0)?.overviewPolyline
                        // get string json point
                        var point = overview?.points

                        var direction = DirectionMapsV2(this@MapsActivity)
                        direction.gambarRoute(mMap!!, point!!)
                        var jarak = route?.get(0)?.legs?.get(0)?.distance?.text
                        var jarak_meter = route?.get(0)?.legs?.get(0)?.distance?.value
                        var waktu = route?.get(0)?.legs?.get(0)?.duration?.text
                        var harga = (jarak_meter!!/1000) * 75000 // 75000 itu tarif permeternya

                        txtjarak.setText("Jarak : " + jarak)
                        txtharga.setText("Harga : " + harga)
                        txttime.setText("Waktu : " + waktu)
                    }
                }

            }

            override fun onFailure(call: Call<ResponseJSON>?, t: Throwable?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.d("response ", t?.toString())
            }

        })

    }

}
