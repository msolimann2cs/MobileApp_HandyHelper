package com.mariam.registeration;

import static android.content.Context.LOCATION_SERVICE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Locale;

public class location  implements LocationListener {
    LocationManager locationManager;
    List<Address> addresses;
    Context ctx;
    private final static int REQUEST_CODE = 100;

    double lat;
    double lon;


    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void getLastLocation() {
        try {
            locationManager = (LocationManager) ctx.getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.




                return;
            }
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            lat  = myLocation.getLatitude();
            lon = myLocation.getLongitude();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        try{
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            addresses.add(geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0));
            lat = addresses.get(0).getLatitude();
            lon=addresses.get(0).getLongitude();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}
