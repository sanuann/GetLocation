package com.sanuann.getlocation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.Calendar;

public class GPSLocation extends Activity
        implements View.OnClickListener {

    private LocationManager locationManager = null;
    private Location location1 = null;
    private Location location2 = null;
    private int id;
    private View view;

    private Button btnGetLocation1 = null;
    private Button btnGetLocation2 = null;
    private Button btnDistance = null;
    private Button btnReset = null;
    private EditText editLocation1 = null;
    private EditText editLocation2 = null;
    private EditText editDistance = null;
    private ProgressBar pb1 = null;
    private ProgressBar pb2 = null;

    private static final String TAG = "Debug";
    private Boolean flag = false;

    private static final String[] PermissionsLocation =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

            };

    private static final int RequestLocationId = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation);

        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        editLocation1 = (EditText) findViewById(R.id.editGetLocation1);
        editLocation2 = (EditText) findViewById(R.id.editGetLocation2);
        editDistance = (EditText) findViewById(R.id.editGetDistance);

        pb1 = (ProgressBar) findViewById(R.id.progressBar1);
        pb1.setVisibility(View.INVISIBLE);

        pb2 = (ProgressBar) findViewById(R.id.progressBar2);
        pb2.setVisibility(View.INVISIBLE);

        btnGetLocation1 = (Button) findViewById(R.id.btnLocation1);
        btnGetLocation1.setOnClickListener(this);

        btnGetLocation2 = (Button) findViewById(R.id.btnLocation2);
        btnGetLocation2.setOnClickListener(this);

        btnDistance = (Button) findViewById(R.id.btnGetDistance);
        btnDistance.setOnClickListener(this);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        btnReset = (Button) findViewById(R.id.reset);
        btnReset.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        view = v;

        switch (v.getId()) {

            case R.id.btnLocation1: {

                id = R.id.btnLocation1;
                flag = displayGpsStatus();
                if (flag) {

                    Log.v(TAG, "onClick Loc1");
                    pb1.setVisibility(View.VISIBLE);
                    //final String permission = Manifest.permission.ACCESS_FINE_LOCATION;
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (int) PackageManager.PERMISSION_GRANTED) {
                        getLocationData();
                        return;
                    }

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Explain to the user why we need location access
                        Log.v(TAG, "req location message");
                        Snackbar.make(v, "Location access is required to locate coordinates.", Snackbar.LENGTH_INDEFINITE)
                                .show();
                        ActivityCompat.requestPermissions(this, PermissionsLocation, RequestLocationId);
                        return;
                    }
                } else {
                    alertbox("Gps Status!!", "Your GPS is: OFF");
                }
                break;
            }

            case R.id.btnLocation2: {

                id = R.id.btnLocation2;
                flag = displayGpsStatus();
                if (flag) {

                    Log.v(TAG, "onClick Loc2");
                    pb2.setVisibility(View.VISIBLE);

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (int) PackageManager.PERMISSION_GRANTED) {
                        getLocationData();
                        return;
                    }

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Explain to the user why we need location access
                        Log.v(TAG, "req location message");
                        Snackbar.make(view, "Location access is required to locate coordinates.", Snackbar.LENGTH_INDEFINITE)
                                .show();
                        ActivityCompat.requestPermissions(this, PermissionsLocation, RequestLocationId);
                        return;


                    }

                    //ActivityCompat.requestPermissions(this, PermissionsLocation, RequestLocationId);

                        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            Log.v(TAG, "Check if location2 permissions is enabled");
                            final Handler handler = new Handler();

                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(5000);
                                    } catch (Exception e) {
                                    } // Just catch the InterruptedException
                                    // Now we use the Handler to post back to the main thread
                                    handler.post(new Runnable() {
                                        public void run() {
                                            editLocation2.setText("Please Check your GPS Permission");
                                            editLocation2.setTextColor(Color.parseColor("#FF0000"));
                                            // Set the View's visibility back on the main UI Thread
                                            pb2.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            }).start();
                            return;
                        }
*/

                } else {
                    alertbox("Gps Status!!", "Your GPS is: OFF");
                }


                break;
            }
            case R.id.btnGetDistance: {
                Log.v(TAG, "distance onClick");
                double distance;
                try {
                    if (location1 != null && location2 != null) {
                        distance = location2.distanceTo(location1) * 3.2808;
                        editDistance.setText("Distance: " +
                                String.format("%.2f", distance) + " ft.");
                    } else {
                        editDistance.setText("Distance not found");
                        Log.v(TAG, "location values not found");
                    }
                } catch (Exception ex) {
                    Log.v(TAG, ex.getMessage());
                    ex.printStackTrace();
                    editDistance.setText("Unable to find distance: ");
                }

                break;
            }

            case R.id.reset: {
                location1 = null;
                editLocation1.setText("");
                pb1.setVisibility(View.INVISIBLE);
                location2 = null;
                editLocation2.setText("");
                pb2.setVisibility(View.INVISIBLE);
                editDistance.setText("");
                break;
            }

        }//end switch

    } // end onClick


    private void getLocationData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (int) PackageManager.PERMISSION_GRANTED) {
            LocationListener locationListener = null;
            switch (id) {
                case R.id.btnLocation1: {
                    //editLocation1.setText("Getting Location..");
                    Log.v(TAG, location1 + "<>" + locationManager);
                    try {
                        location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        //checks if location was received more than 1 minute ago
                        if (location1 != null && location1.getTime() > Calendar.getInstance().getTimeInMillis() - 1 * 60 * 1000) {
                            Log.v(TAG, location1 + "<");
                            pb1.setVisibility(View.INVISIBLE);
                            editLocation1.setText("Longitude: " +
                                    location1.getLongitude() + " Latitude: " + location1.getLatitude());
                        } else {
                            locationListener = new MyLocationListener();
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            locationManager.removeUpdates(locationListener);
                            //editLocation1.setText("Unable to find the location");
                        }

                    } catch (Exception ex) {
                        Log.v(TAG, ex.getMessage());
                        ex.printStackTrace();
                        editLocation1.setText("Unable to get location: ");
                    }
                    break;
                }

                case R.id.btnLocation2: {
                    //editLocation2.setText("Getting Location..");
                    Log.v(TAG, location2 + "<>" + locationManager);
                    try {
                        location2 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        //checks if location was received more than 1 minute ago
                        if (location2 != null && location2.getTime() > Calendar.getInstance().getTimeInMillis() - 1 * 60 * 1000) {
                            Log.v(TAG, location2 + "<");
                            pb2.setVisibility(View.INVISIBLE);
                            editLocation2.setText("Longitude: " +
                                    location2.getLongitude() + " Latitude: " + location2.getLatitude());
                        } else {
                            locationListener = new MyLocationListener();
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            locationManager.removeUpdates(locationListener);
                            //editLocation2.setText("Unable to find the location");
                        }

                    } catch (Exception ex) {
                        Log.v(TAG, ex.getMessage());
                        ex.printStackTrace();
                        editLocation2.setText("Unable to get location: ");
                    }
                    break;
                }
            } //end switch
        } //end if
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestLocationId:
                Log.v(TAG, "grantResults.length>" + grantResults.length);
                Log.v(TAG, "grantResults[0]>" + grantResults[0]);
                Log.v(TAG, "grantResults[1]>" + grantResults[1]);

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Snackbar.make(view, "Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();

                    getLocationData();

                } else {

                    Snackbar.make(view, "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

    //*----Method to Check GPS is enabled or disabled ----- *//*
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    //*----------Method to create an AlertBox ------------- *//*
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mymessage)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //*----------Listener class to get coordinates ------------- *//*
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            if (loc != null) {
                Log.v(TAG, "location change");
                switch (id) {
                    case R.id.btnLocation1: {

                        pb1.setVisibility(View.INVISIBLE);
                        location1 = loc;
                        break;
                    }
                    case R.id.btnLocation2: {

                        pb2.setVisibility(View.INVISIBLE);
                        location2 = loc;
                        break;
                    }
                }//end switch

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    }
}