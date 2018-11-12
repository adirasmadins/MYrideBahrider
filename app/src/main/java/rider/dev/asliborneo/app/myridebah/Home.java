package rider.dev.asliborneo.app.myridebah;



import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;

import android.widget.Button;

import android.widget.ImageView;



import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.google.android.gms.location.LocationListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rider.dev.asliborneo.app.myridebah.Commons.Commons;
import rider.dev.asliborneo.app.myridebah.Helper.Custom_info_Window;
import rider.dev.asliborneo.app.myridebah.Helper.bottom_sheet_rider_fragment;
import rider.dev.asliborneo.app.myridebah.Model.Notification;
import rider.dev.asliborneo.app.myridebah.Model.Token;
import rider.dev.asliborneo.app.myridebah.Model.User;
import rider.dev.asliborneo.app.myridebah.Model.fcm_response;
import rider.dev.asliborneo.app.myridebah.Model.sender;
import rider.dev.asliborneo.app.myridebah.Remote.FCMService;
import rider.dev.asliborneo.app.myridebah.Remote.RetrofitClient;


import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static rider.dev.asliborneo.app.myridebah.Helper.bottom_sheet_rider_fragment.newinstance;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,GoogleMap.OnMapClickListener {


    SupportMapFragment mapFragment;
    LatLng location;
    //Location
    private GoogleMap mMap;

    private static final int PLAY_SERVICE_RESOLUTION_REQUEST = 7002;
    private static final int MY_PERMISSION_REQUEST_CODE = 7003;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    PlaceAutocompleteFragment places;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    String destination;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;




    ImageView imgExpandable;
    bottom_sheet_rider_fragment brsf;
    Button btnPickupRequest;
    boolean isDriverFound = false;
    String driverId = "" ;
    int radius = 1;
    int distance = 1;
    private static final int LIMIT = 3;
    DatabaseReference driversAvailable;

    LatLng pickup_location;
    LatLng riderLat,riderLng;

    String pick_up_location,destination_location;
    Marker mUserMarker,destination_location_marker;

    NavigationView nav_view;
    AutocompleteFilter typefilter;

    PlaceAutocompleteFragment pick_up_place,destination_place;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pick_up_place=(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_location);
        pick_up_place.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
                pickup_location = place.getLatLng();
                mLastLocation.setLatitude(place.getLatLng().longitude);
                mLastLocation.setLongitude(place.getLatLng().longitude);
                pick_up_location = place.getAddress().toString();
                mUserMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("PickUp Here..").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f));

                Log.d("LOCATION", pick_up_location);
            }

            @Override
            public void onError(Status status) {

            }
        });
        destination_place=(PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_destination);
        destination_place.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
                destination=place.getAddress().toString();
                destination = destination.replace(" ", "+");
                destination_location_marker=mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15.0f));
                bottom_sheet_rider_fragment bsrf=newinstance(String.format("%f,%f",pickup_location.latitude + ","+pickup_location.longitude),destination,false);
                bsrf.show(getSupportFragmentManager(),bsrf.getTag());
            }

            @Override
            public void onError(Status status) {

            }
        });
        typefilter=new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(3)
                .build();

        btnPickupRequest = findViewById(R.id.btnpickuprequest);
        btnPickupRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDriverFound)
                    requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
                else
                    sendRequestToDriver(driverId);
            }


        });
        setUpLocation();
        update_firebase_token();
    }

    private void update_firebase_token() {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token=new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);

    }

    private void requestPickupHere(String uid) {
        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Commons.pickUpRequest_tbl);

        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (mUserMarker.isVisible())
                    mUserMarker.remove();

                mUserMarker = mMap.addMarker(new MarkerOptions()
                        .title("Pickup Here")
                        .snippet(" ")
                        .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                mUserMarker.showInfoWindow();
                findDriver();
                btnPickupRequest.setText( "GETTING DRIVER ...");
            }
        });

    }

    private void sendRequestToDriver(String driverId) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        tokens.orderByKey().equalTo(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postsnapshot:dataSnapshot.getChildren()){
                    String Lat_lng;
                    Token token=postsnapshot.getValue(Token.class);
                    Lat_lng=new Gson().toJson(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
                    String rider_token= FirebaseInstanceId.getInstance().getToken();
                    Notification data=new Notification(rider_token,Lat_lng);
                    sender content=new sender(data,token.getToken());
                    FCMService fcmService=RetrofitClient.getClient().create(FCMService.class);
                    Call<fcm_response> call=fcmService.send_message(content);
                    call.enqueue(new Callback<fcm_response>() {
                        @Override
                        public void onResponse(Call<fcm_response> call, Response<fcm_response> response) {
                            if(response.body().success==1){
                                Toast.makeText(Home.this,"Request Sent",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(Home.this,"Failed!",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<fcm_response> call, Throwable t) {
                            Log.e("fcm_error",t.getMessage());;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void findDriver() {
        DatabaseReference drivers = FirebaseDatabase.getInstance().getReference(Commons.driver_location);
        GeoFire gfDrivers = new GeoFire(drivers);
        GeoQuery geoQuery =gfDrivers.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!isDriverFound) {
                    isDriverFound = true;
                    driverId = key;
                    btnPickupRequest.setText("CALL DRIVER");
                    Toast.makeText(Home.this,""+key,Toast.LENGTH_SHORT).show();

                }
            }


            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!isDriverFound)
                {
                    radius++;
                    findDriver();
                    loadAllAvailableDriver(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private void startLocationUpdates() {
        //int a = PackageManager.PERMISSION_DENIED;  //careful with logic here

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    private void setUpLocation() {
        if(  (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED   ) &&
                ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED  ) )
        {
            //request runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, MY_PERMISSION_REQUEST_CODE );
        } else {
            if(checkPlayServices()){

                buildGoogleApiClient();
                createLocationRequest();

                displayLocation();
            }
        }

    }

    private void displayLocation() {
        if(  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED    &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED   )
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null)
        {

            LatLng center = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            LatLng northside = SphericalUtil.computeOffset(center, 100000,0);
            LatLng southside = SphericalUtil.computeOffset(center,100000,180);
            LatLngBounds bounds = LatLngBounds.builder().include(northside).include(southside).build();
            pick_up_place.setBoundsBias(bounds);
            pick_up_place.setFilter(typefilter);
            destination_place.setBoundsBias(bounds);
            pick_up_place.setFilter(typefilter);



            driversAvailable = FirebaseDatabase.getInstance().getReference(Commons.driver_location);
            driversAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loadAllAvailableDriver(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            final double latitude = pickup_location.longitude;
            final double longitude = pickup_location.latitude;
            //Add marker
            if(mUserMarker != null) {
                mUserMarker.remove();
            }//remove already marker
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Your Location"));
            //move cam to this position
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
            //Draw animation rotate marker

            loadAllAvailableDriver(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));



            Log.d("Welcome", String.format("Your location was changed: %f/%f",latitude,longitude));

        } else {
            Log.d("ERROR", "Cannot get your location");
        }

    }

    private void loadAllAvailableDriver(final LatLng mlocation) {
        mMap.clear();
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference(Commons.driver_location);
        GeoFire gf = new GeoFire(driverLocation);
        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),distance);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference(Commons.Registered_driver)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                                        .flat(true)
                                        .title(user.getName())
                                        .snippet("Phone: "+user.getPhone())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(distance <=LIMIT)
                    distance++;

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval( UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RESOLUTION_REQUEST).show();
            }else{
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap !=null) {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.setInfoWindowAdapter(new Custom_info_Window(this));
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            enableMyLocation();
            mMap.setOnMapClickListener(this);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),15.0f));
        }


    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
            buildGoogleApiClient();
            createLocationRequest();
            displayLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void Sign_Out() {
        Paper.init(this);
        Paper.book().destroy();
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(Home.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId()==R.id.nav_signout){
            Sign_Out();
        }
        return false;
    }



    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if(mMap !=null)

            destination_location_marker.remove();
        destination_location_marker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_marker)).title("Destination").position(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));
        bottom_sheet_rider_fragment bsrf=newinstance(String.format("%f,%f",pickup_location.latitude,pickup_location.longitude),String.format("%f,%f",latLng.latitude,latLng.longitude),true);
        bsrf.show(getSupportFragmentManager(),bsrf.getTag());
    }


}
