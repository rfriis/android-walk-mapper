package uk.ac.abertay.cmp309.WalkMapper;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_REQUEST = 1;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Marker myLocationMarker;
    private Marker startMarker;
    private Marker endMarker;
    private boolean isTrackingWalk = false;     // this is for checking if activity is tracking a walk
    private boolean alternateStart = false;     // this is for startup sequence for plotting a previous walk
    Button startButton;
    Button endButton;
    public List<LatLng> coordList;
    public double distanceWalked = 0.0;
    private SQLiteHelper sqLiteHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sqLiteHelper = SQLiteHelper.getInstance(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // initialise coordinates list
        coordList = new ArrayList<>();

        // get intent from Walks fragment
        // alternate start sequence
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            alternateStart = true;
            String message = intent.getStringExtra("EXTRA_COORDINATES");
            // parse coordinates into a list
            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(message);
            while(m.find()) {
                String tempCoords = m.group(1);
                String[] splitCoords = tempCoords.split(",");
                LatLng coords = new LatLng(Double.parseDouble(splitCoords[0]), Double.parseDouble(splitCoords[1]));
                coordList.add(coords);
            }
            // plot start marker


            // plot end marker

            // hide start and end buttons
        }

        // show Start button, hide End button
        startButton = findViewById(R.id.StartWalkButton);
        endButton = findViewById(R.id.EndWalkButton);
        startButton.setVisibility(View.VISIBLE);
        endButton.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if walk in progress, hide start button, show end button
        if (isTrackingWalk) {
            startButton.setVisibility(View.GONE);
            endButton.setVisibility(View.VISIBLE);
        } else {
            startButton.setVisibility(View.VISIBLE);
            endButton.setVisibility(View.GONE);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationRequest != null) {
                    Log.d("CurrentLocationDebug", locationResult.getLastLocation().toString());
                    if (map != null) {
                        double lat = locationResult.getLastLocation().getLatitude();
                        double lng = locationResult.getLastLocation().getLongitude();
                        LatLng myLocation = new LatLng(lat, lng);
                        if (!alternateStart) {
                            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                            myLocationMarker.setPosition(myLocation);
                        }


                        // drawing lines while walking
                        // only store coords when a walk is in progress
                        if (isTrackingWalk) {
                            coordList.add(myLocation);
                            Log.d("coordList", coordList.get(coordList.size() - 1).toString());
                            // only draw if there is more than 1 coordinate -> cannot draw with 1 or 0 coordinates
                            if (coordList.size() > 1) {
                                int secondLast = coordList.size() - 2;
                                int last = coordList.size() - 1;
                                drawLine(coordList.get(secondLast), coordList.get(last));
                                // add to distance walked
                                float[] results = new float[1];
                                Location.distanceBetween(coordList.get(secondLast).latitude, coordList.get(secondLast).longitude, coordList.get(last).latitude, coordList.get(last).longitude, results);
                                distanceWalked += results[0] / 1000;
                            }
                        }

                    }
                }
            }
        };

        // check permissions
        boolean checkLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        if (fusedLocationClient != null && checkLocationPermission) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.zoomTo(16));
        myLocationMarker = map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("My Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        // if plotting a saved route
        if (alternateStart) {
            // draw line
            for (int i = 0; i < coordList.size(); i++) {
                if (i > 0) {
                    drawLine(coordList.get(i-1), coordList.get(i));
                }
            }
            map.moveCamera(CameraUpdateFactory.newLatLng(coordList.get(0)));
            startMarker = map.addMarker(new MarkerOptions().position(coordList.get(0)).title("Start of Walk").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            endMarker = map.addMarker(new MarkerOptions().position(coordList.get(coordList.size()-1)).title("End of Walk").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            map.moveCamera(CameraUpdateFactory.zoomTo(15));
            startButton.setVisibility(View.GONE);
        }
    }

    // this function draws a line between two given points on the map
    private void drawLine(LatLng source, LatLng destination) {
        Polyline line = map.addPolyline(
                new PolylineOptions().add(
                        new LatLng(source.latitude, source.longitude),
                        new LatLng(destination.latitude, destination.longitude)
                )
        );
        stylePolyline(line);
    }

    // this function sets the style of a given line
    private void stylePolyline(Polyline polyline) {
        polyline.setWidth(16);
        polyline.setColor(0xff000000);
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setJointType(JointType.ROUND);
    }


    // start tracking the walk
    // this is triggered by pressing the "Start Walk" button in the map
    public void startWalk(View view) {
        isTrackingWalk = true;
        startMarker = map.addMarker(new MarkerOptions().position(myLocationMarker.getPosition()).title("Start of walk"));
        startButton.setVisibility(View.GONE);
        endButton.setVisibility(View.VISIBLE);
    }


    // triggered by pressing "End Walk" button
    // create a dialog to name and rate the walk
    // tracking does not stop if "Cancel" is pressed
    // includes option to stop tracking but not save walk
    public void endWalk(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.walk_dialog, null);
        builder.setView(dialogView);
        EditText walkName = dialogView.findViewById(R.id.walkNameEditText);
        RatingBar walkRating = dialogView.findViewById(R.id.walkRatingBar);
        // Save the walk
        // Stop tracking
        builder.setPositiveButton(R.string.walkDialogSave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isTrackingWalk = false;
                endMarker = map.addMarker(new MarkerOptions().position(myLocationMarker.getPosition()).title("End of walk"));
                endButton.setVisibility(View.GONE);
                Log.d("Alert", "Save Pressed");

                // get walk name, rating, distance, coordinates

                String name = walkName.getText().toString();
                int id = walkName.getId();
                Float rating = walkRating.getRating();

                Log.d("WalkDebug", "name : " + name + " rating : " + rating + "id : " + id);

                sqLiteHelper.saveWalk(name, rating, distanceWalked, coordList);
                sqLiteHelper.loadAllWalks();
            }
        });
        // Cancel alert
        // Do not stop tracking
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Alert", "Cancel Pressed");
            }
        });
        // Do not save the walk
        // Stop tracking
        builder.setNegativeButton(R.string.walkDialogNotSave, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isTrackingWalk = false;
                endMarker = map.addMarker(new MarkerOptions().position(myLocationMarker.getPosition()).title("End of walk"));
                endButton.setVisibility(View.GONE);
                distanceWalked = 0.0;
                Log.d("Alert", "Dont Save Pressed");
            }
        });
        // neutral and negative buttons are swapped
        // this is purely for cosmetic purposes

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);

        TextView distanceText = dialogView.findViewById(R.id.walkDistance);
        // if distance is under 1km, show in metres instead
        DecimalFormat kmFormat = new DecimalFormat("#.#");
        if (distanceWalked < 1) {
            distanceText.setText((int) (distanceWalked * 1000) + " m");
        } else {
            distanceWalked = Double.parseDouble(kmFormat.format(distanceWalked));
            distanceText.setText(distanceWalked + " km");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (!isTrackingWalk) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}