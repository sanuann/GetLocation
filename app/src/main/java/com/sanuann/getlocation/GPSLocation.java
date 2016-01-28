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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GPSLocation extends Activity
        implements View.OnClickListener {

    private LocationManager locationManager = null;
    private Location location1 = null;
    private Location location2 = null;
    private int id;
    private View view;

    private EditText editLocation1 = null;
    private EditText editLocation2 = null;
    private EditText editDistance = null;
    private ProgressBar pb1 = null;
    private ProgressBar pb2 = null;

    private static final String TAG = "Debug";

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

        Button btnGetLocation1 = (Button) findViewById(R.id.btnLocation1);
        btnGetLocation1.setOnClickListener(this);

        Button btnGetLocation2 = (Button) findViewById(R.id.btnLocation2);
        btnGetLocation2.setOnClickListener(this);

        Button btnDistance = (Button) findViewById(R.id.btnGetDistance);
        btnDistance.setOnClickListener(this);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        Button btnReset = (Button) findViewById(R.id.reset);
        btnReset.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        view = v;
        Boolean flag;
        switch (v.getId()) {

            case R.id.btnLocation1: {

                id = R.id.btnLocation1;
                flag = displayGpsStatus();
                if (flag) {

                    Log.v(TAG, "onClick Loc1");
                    //appendLog("onClick Loc1");
                    pb1.setVisibility(View.VISIBLE);
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (int) PackageManager.PERMISSION_GRANTED) {
                        getLocationData();
                        return;
                    }

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Explain to the user why we need location access
                        Log.v(TAG, "request location permission message");
                        //appendLog("request location permission message");
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
                    //appendLog("onClick Loc2");
                    pb2.setVisibility(View.VISIBLE);
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (int) PackageManager.PERMISSION_GRANTED) {
                        getLocationData();
                        return;
                    }


                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Explain to the user why we need location access
                        Log.v(TAG, "ShowRequestPermissionRationale");
                        //appendLog("ShowRequestPermissionRationale");
                        Snackbar.make(view, "Location access is required to locate coordinates.", Snackbar.LENGTH_INDEFINITE)
                                .show();
                        ActivityCompat.requestPermissions(this, PermissionsLocation, RequestLocationId);
                        return;

                    }

                } else {
                    alertbox("Gps Status!!", "Your GPS is: OFF");
                }
                break;
            }

            case R.id.btnGetDistance: {
                Log.v(TAG, "distance onClick");
                //appendLog("distance onClick");
                double distance;
                try {
                    if (location1 != null && location2 != null) {
                        distance = location2.distanceTo(location1) * 3.2808;
                        editDistance.setText("Distance: " +
                                String.format("%.2f", distance) + " ft.");
                    } else {
                        editDistance.setText("Distance not found");
                        Log.v(TAG, "location values not found");
                        //appendLog("location values not found");
                    }
                } catch (Exception ex) {
                    Log.v(TAG, ex.getMessage());
                    //appendLog(ex.getMessage());
                    ex.printStackTrace();
                    editDistance.setText("Unable to find distance: ");
                }

                break;
            }

            case R.id.reset: {
                Log.v(TAG, "reset onClick");
                //appendLog("distance onClick");
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
            switch (id) {
                case R.id.btnLocation1: {
                    editLocation1.setText("Getting Location..");
                    try {
                        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                            @Override
                            public void gotLocation(Location location) {
                                //Got the location!
                                location1 = location;
                                Log.v(TAG, location1 + "<>" + locationManager);
                                //appendLog(location1 + "<>" + locationManager);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pb1.setVisibility(View.INVISIBLE);
                                        editLocation1.setText("Longitude: " +
                                                location1.getLongitude() + " Latitude: " + location1.getLatitude());
                                    }
                                });

                            }
                        };

                        MyLocation myLocation = new MyLocation();
                        myLocation.getLocation(this, locationResult);

                    } catch (Exception ex) {
                        Log.v(TAG, ex.getMessage());
                        //appendLog(ex.getMessage());
                        ex.printStackTrace();
                        pb1.setVisibility(View.INVISIBLE);
                        editLocation1.setText("Unable to get location: ");
                    }
                    break;
                }

                case R.id.btnLocation2: {
                    editLocation2.setText("Getting Location..");
                    try {
                        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                            @Override
                            public void gotLocation(Location location) {
                                //Got the location!
                                location2 = location;
                                Log.v(TAG, location2 + "<>" + locationManager);
                                //appendLog(location2 + "<>" + locationManager);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pb2.setVisibility(View.INVISIBLE);
                                        editLocation2.setText("Longitude: " +
                                                location2.getLongitude() + " Latitude: " + location2.getLatitude());
                                    }
                                });
                            }
                        };

                        MyLocation myLocation = new MyLocation();
                        myLocation.getLocation(this, locationResult);

                    } catch (Exception ex) {
                        Log.v(TAG, ex.getMessage());
                        //appendLog(ex.getMessage());
                        ex.printStackTrace();
                        pb2.setVisibility(View.INVISIBLE);
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
                /*appendLog("grantResults.length>" + grantResults.length);
                appendLog("grantResults[0]>" + grantResults[0]);
                appendLog("grantResults[1]>" + grantResults[1]);*/

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

    /*public void appendLog(String text) {
        File logFile = new File("sdcard/log.file");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append("---NEW SESSION---");
            buf.append(text);
            buf.newLine();
            buf.flush();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/


}