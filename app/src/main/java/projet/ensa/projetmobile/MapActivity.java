package projet.ensa.projetmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import projet.ensa.projetmobile.adapters.RequestAdapter;
import projet.ensa.projetmobile.connexion.NetworkChangeListenner;
import projet.ensa.projetmobile.connexion.RetrofitInstance;
import projet.ensa.projetmobile.models.Position;
import projet.ensa.projetmobile.models.User;
import projet.ensa.projetmobile.service.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private GoogleMap mMap;
    Location mLastLocation;
    private Marker mCurrLocationMarker;
    private Marker markerBrisbane;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    int REQUEST_CHECK_SETTINGS = 100;
    NetworkChangeListenner networkChangeListenner = new NetworkChangeListenner();
    private DataService service;
    private int id;
    private SharedPreferences mySharedPreferences;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    String d = formatter.format(date);
    private com.getbase.floatingactionbutton.FloatingActionButton menu;
    private final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    View v;
    private androidx.appcompat.widget.SearchView search;
    private double r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        sharedPreferences= this.getSharedPreferences("LIST", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mySharedPreferences = this.getSharedPreferences("MYPREFERENCENAME", Context.MODE_PRIVATE);
        id = mySharedPreferences.getInt("ID", 0);
        service = RetrofitInstance.getInstance().create(DataService.class);
        if (ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(MapActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        getList();
        menu = findViewById(R.id.btnMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MenuActivity.class);

                startActivity(intent);

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));

        buildGoogleApiClient();




    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );

        result.setResultCallback(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLngD = new LatLng(location.getLatitude(), location.getLongitude());
        Call<List<User>> friends = service.getAllFriends(id);
        friends.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> requestResponse = response.body();
                List<Position> ppp=new ArrayList<Position>();
                for(User u:requestResponse){
                    Call<Position> positions = service.getLastPositionOfFriend(u.getId());
                    positions.enqueue(new Callback<Position>() {
                        @Override
                        public void onResponse(Call<Position> call, Response<Position> response) {
                            Position p = response.body();
                            ppp.add(p);
                            setList("map",ppp);
                            LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
                            markerBrisbane = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .snippet("Télephone:"+u.getTelephone())
                                    .title(""+u.getPrenom()+" "+u.getNom()));


                        }

                        @Override
                        public void onFailure(Call<Position> call, Throwable t) {

                        }
                    });



                }



            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

        User u = new User(id);
        Position p = new Position(location.getLatitude(), location.getLongitude(), d, u);
        Call<Position> positions = service.createPosition(p);
        positions.enqueue(new Callback<Position>() {
            @Override
            public void onResponse(Call<Position> call, Response<Position> response) {

            }

            @Override
            public void onFailure(Call<Position> call, Throwable t) {

            }
        });

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngD);
        markerOptions.title("oubari elmehdi");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        markerBrisbane = mMap.addMarker(new MarkerOptions()
                .position(SYDNEY)
                .title("Sydney"));
        r=getIntent().getDoubleExtra("rayon",100);
        CircleOptions circleOptions = new CircleOptions()
                .center(latLngD)
                .radius(r).strokeWidth(10)
                .strokeColor(Color.YELLOW)
                .fillColor(Color.argb(128, 145, 0, 0))
                .clickable(true);



        Circle circle = mMap.addCircle(circleOptions);

        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {

                int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
                circle.setStrokeColor(strokeColor);
            }
        });
        float[] distance = new float[2];
       List<Position> postionOFfriend=getList();
       if(postionOFfriend==null){
           Toast.makeText(MapActivity.this,"rien trouvée",Toast.LENGTH_LONG).show();
       }
       else{
           for(Position pf:postionOFfriend){
               Location.distanceBetween(pf.getLatitude(), pf.getLongitude(), circle.getCenter().latitude,circle.getCenter().longitude,distance);

               if ( distance[0] <= circle.getRadius())
               {
                   notify(v,""+pf.getUser().getNom()+" "+pf.getUser().getPrenom(),""+distance[0]);
                   //   Toast.makeText(MapActivity.this,""+distance[0],Toast.LENGTH_LONG).show();

               }
               else
               {

               }
           }
       }



        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        search= findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location=search.getQuery().toString();

                List<Position> pp=getList();
                if(location!=null || !location.equals(""))
                {
                    for (Position p:pp){
                        if(p.getUser().getPrenom().equals(location) || p.getUser().getNom().equals(location)){
                            LatLng latLng=new LatLng(p.getLatitude(),p.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( latLng,20));
                            //Toast.makeText(MapActivity.this,""+p.getUser().getNom(),Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngD));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngD));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        mMap.setOnMarkerClickListener(this);
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {


        Integer clickCount = (Integer) marker.getTag();


        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListenner, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListenner);
        super.onStop();
    }

    @Override
    public void onResult(@NonNull Result locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                try {

                    status.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {


                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {

                Toast.makeText(getApplicationContext(), "GPS activé", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "Le GPS n'est pas activé", Toast.LENGTH_LONG).show();
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MapActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void notify(View view,String ami,String distance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel =
                    new NotificationChannel("id1", "Sipmle Notification",
                            NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Description");
            notificationChannel.setShowBadge(true);
            nm.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, "id1")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Nouveau!")
                            .setContentText(ami+" est très proche de "+distance+" m vous")
                            .setNumber(1)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            mBuilder.setVibrate(new long[]{500, 1000, 500, 1000, 500});
            nm.notify(1, mBuilder.build());
        }
    }

    public <T> void setList(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        set(key, json);
    }
    public static void set(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public List<Position> getList(){
        List<Position> arrayItems;
        String serializedObject = sharedPreferences.getString("map", null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Position>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
            for(Position p:arrayItems){
            //  Log.d("eeee","dd"+p.getUser().getNom());
            }
            return arrayItems;
        }
        return null ;
    }







}