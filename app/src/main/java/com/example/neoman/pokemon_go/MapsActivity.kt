package com.example.neoman.pokemon_go

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getPermission()
        loadPokemon()
    }

    var ACCESSLOCATION=123

    fun getPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission
            (this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }
        getUserLocation()
    }

    var location:Location?=null
    var oldlocation:Location?=null

    @SuppressLint("MissingPermission")
    fun getUserLocation(){
        Toast.makeText(this,"getting your location",Toast.LENGTH_LONG).show()

        var myLocation=MyLocationListener()

        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)

        var myThread=MyThread()
        myThread.start()
    }

    var pokemon=ArrayList<Pokemon>()
    var playerpower=0.0

    fun loadPokemon(){
        /*is there way to cycle through drawable's get file name(if name not player), pass that in as  pokemon name, and the drawable as img?
        or need to hardcode each pokemon image?*/
        pokemon.add(Pokemon("Torracat",R.drawable.torracat,"At its throat, it bears a bell of fire. The bell rings brightly whenever this Pokémon spits fire.",
                37.2,-122.036,100.0))
        pokemon.add(Pokemon("Toucannon",R.drawable.toucannon,"When it battles, its beak heats up. The temperature can easily exceed 212 degrees Fahrenheit, causing severe burns when it hits.",
                37.3,-122.03,90.0))
        pokemon.add(Pokemon("Snivy",R.drawable.snivy,"Being exposed to sunlight makes its movements swifter. It uses vines more adeptly than its hands.",
                37.4,-122.03,110.0))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        when(requestCode){
            ACCESSLOCATION->{//check if this app has been granted fine location access, if so call getUserLocation() else let them know we don't
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }
                else{
                Toast.makeText(this,"We do not have access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
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
    }


    //getting user location
    inner class MyLocationListener: LocationListener {
        constructor(){
            location= Location("Start")
            location!!.longitude=0.0
            location!!.latitude=0.0
        }

        override fun onLocationChanged(p0: Location?) {
            location=p0
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    //thread to manage location updating
    inner class MyThread: Thread{
        constructor():super(){
            oldlocation= Location("Start")
            oldlocation!!.longitude=0.0
            oldlocation!!.latitude=0.0

        }

        override fun run() {
            while(true){
                try{
                    if(oldlocation==location){//or set threshold and use oldlocation.distanceto(location)>=1f or something
                        continue
                    }
                    oldlocation=location

                    runOnUiThread {
                        mMap!!.clear()
                        // Update curLocation as app runs
                        val curLoc = LatLng(location!!.latitude, location!!.longitude)
                        mMap!!.addMarker(MarkerOptions().
                                position(curLoc).
                                title("You").
                                snippet("Power: "+playerpower).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.ob)))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(curLoc))

                        for(i in pokemon.indices){
                            if(!pokemon[i].caught){
                                var pokeLoc=LatLng(pokemon[i].loc!!.latitude, pokemon[i].loc!!.longitude)

                                mMap!!.addMarker(MarkerOptions().
                                    position(pokeLoc).
                                    title(pokemon[i].name).
                                    snippet("Power: "+pokemon[i].power+" hp").
                                    icon(BitmapDescriptorFactory.fromResource(pokemon[i].img!!)))
                                if(pokemon[i].loc!!.distanceTo(location)<=2f){
                                    pokemon[i].caught=true
                                    playerpower+=pokemon[i].power!!
                                    //the text doesn't show up? and pokemon still shows up for while until update location again?
                                    Toast.makeText(applicationContext,"You caught a "+pokemon[i].name!!+"\n Your new power is "+playerpower,Toast.LENGTH_LONG)
                                }
                            }
                        }
                    }
                    Thread.sleep(1000)

                }catch(ex:Exception){

                }
            }
        }
    }
}

