package com.helenafranczak.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationListener locationListener;
    LocationManager locationManager;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

            startListening();

        }

    }


    public void startListening(){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

            locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        }


    }

    public void updateLocationInfo(Location location){

        TextView latTextView= (TextView) findViewById(R.id.latTextView);
        TextView accTextView= (TextView) findViewById(R.id.accTextView);
        TextView altTextView= (TextView) findViewById(R.id.altTextView);
        TextView longTextView= (TextView) findViewById(R.id.longTextView);

        // the below is to set text view to those messages below

        latTextView.setText("Latitude: " + location.getLatitude());
        longTextView.setText("Longitude: " + location.getLongitude());
        altTextView.setText("Altitude: " + location.getAltitude());
        accTextView.setText("Accuracy: " + location.getAccuracy());

        // to get address. We creat GeoCoder object. We create Geocoder, we get the application context
        // and the default Locale

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            //once I have address in logs. i want to display it nicely to the user
            //so I create a string of addrees in case there is no address

            String address= "no address found";

            //create list of addresses. we take our geocoder and we get info from the location.WE are interested in one result
            // this can fail so its sourranded by try and catch.


            List<Address> listAddress= geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if (listAddress!=null && listAddress.size()>0){

                Log.i("place info", listAddress.get(0).toString());

                //once we know we are successfull we need to set address to en empty string:

                address="Address:\n";

                // we need to do checking of each item in list adresses. listAddress.get(0) - to get our address
                // if getSubThoroughfare() is not null we will append (+=) to address that value

                if (listAddress.get(0).getSubThoroughfare() != null ){

                    address += listAddress.get(0).getSubThoroughfare()+ "" ;
                }

                if (listAddress.get(0).getThoroughfare() != null ) {

                    address += listAddress.get(0).getThoroughfare() + "\n ";
                }

                if (listAddress.get(0).getLocality() != null ) {

                    address += listAddress.get(0).getLocality() + "\n";
                }

                if (listAddress.get(0).getPostalCode() != null ) {

                    address += listAddress.get(0).getPostalCode() + "\n";

                }

                if (listAddress.get(0).getCountryName() != null ) {

                    address += listAddress.get(0).getCountryName() + "";

                }


            }

            // address is ready to go. we need to update the TextView to it


            TextView addTextView = (TextView)findViewById(R.id.addTextView);

            addTextView.setText(address);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocationInfo(location);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT<23) {

            startListening(); //replaced the below:

            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {


                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {

                    updateLocationInfo(location);



                }
            }
        }



    }
}
