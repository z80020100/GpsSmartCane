package tw.org.edo.gpssmartcane;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static tw.org.edo.gpssmartcane.Constant.ACTIVITY_LOGIN;
import static tw.org.edo.gpssmartcane.Constant.RESULT_LOGIN_SUCCESS;
import static tw.org.edo.gpssmartcane.Constant.RETURN_VALUE_LOGIN;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    final private  String TAG = this.getClass().getSimpleName();


    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private Context mContext = this;

    private List<DataCurrentPosition> mDataCurrentPositionList = new ArrayList<>();

    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;

    // UI
    private Button mLoginButton;
    private Button.OnClickListener mLoginButtonListen;

    private ImageView mBatteryImageView;
    private ImageView mLightImageView;
    private ImageView mCaneImageView;
    private ImageView mEmergencyImageView;
    private ImageView mHistoryImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Only for Test
        //Log.i(TAG, String.valueOf(Utility.latitudeDMMtoDD("4124.2028", "N"))); // 41.40338
        //Log.i(TAG, String.valueOf(Utility.longitudeDMMtoDD("00210.4418", "E"))); // 2.17403

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getCurrentLocation();

        mLoginButton = findViewById(R.id.login);
        mLoginButtonListen = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivityForResult(intent, ACTIVITY_LOGIN);
            }
        };
        mLoginButton.setOnClickListener(mLoginButtonListen);

        mBatteryImageView = findViewById(R.id.battery_status);
        mLightImageView = findViewById(R.id.light_status);
        mCaneImageView = findViewById(R.id.cane_status);
        mEmergencyImageView = findViewById(R.id.emergency_status);
        mHistoryImageView = findViewById(R.id.history);

        mBatteryImageView.setVisibility(View.GONE);
        mLightImageView.setVisibility(View.GONE);
        mCaneImageView.setVisibility(View.GONE);
        mEmergencyImageView.setVisibility(View.GONE);
        mHistoryImageView.setVisibility(View.GONE);

        updateStatusIcon();
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                //Logger.d(String.format("%f, %f", location.getLatitude(), location.getLongitude()));
                //drawMarkerCurrent(location);
                //mLocationManager.removeUpdates(mLocationListener);
            } else {

            }
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


    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled)) {

        }
        else{
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null)
            drawMarkerCurrent(location);
    }

    private void drawMarkerCurrent(Location location) {
        if (mGoogleMap != null) {
            //mGoogleMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Current Position"));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 12));
        }
    }

    private void drawMarkerCaneCurrent(String cane_name, double latitude_dd, double longitude_dd) {
        if (mGoogleMap != null) {
            //mGoogleMap.clear();
            LatLng gps = new LatLng(latitude_dd, longitude_dd);
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title(cane_name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.little_man)));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 16));
        }
    }

    private void drawMarkerCaneHistory(final String cane_name, double latitude_dd, double longitude_dd, boolean camera_move) {
        if (mGoogleMap != null) {
            //mGoogleMap.clear();
            final LatLng gps = new LatLng(latitude_dd, longitude_dd);

            CircleOptions circleOptions = new CircleOptions()
                    .center(gps)
                    .strokeColor(Color.argb(255, 68, 114, 196))
                    .fillColor(Color.argb(255, 68, 114, 196))
                    .radius(5) // In meters
                    .clickable(true);
            // Get back the mutable Circle
            Circle circle = mGoogleMap.addCircle(circleOptions);

            mGoogleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                @Override
                public void onCircleClick(Circle circle) {
                    // Flip the r, g and b components of the circle's
                    // stroke color.
                    int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
                    circle.setStrokeColor(strokeColor);

                    Marker melbourne = mGoogleMap.addMarker(new MarkerOptions()
                            .position(gps)
                            .alpha(0F)
                            .title(cane_name)
                            .snippet(""));
                    melbourne.showInfoWindow();
                }
            });

            if(camera_move){
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 21));
            }
        }
    }

    private void updateStatusIcon(){
        mBatteryImageView.setImageResource(R.mipmap.battery_normal);
        mLightImageView.setImageResource(R.mipmap.light_off);
        mCaneImageView.setImageResource(R.mipmap.cane_normal);
        mEmergencyImageView.setImageResource(R.mipmap.emergency_normal);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case ACTIVITY_LOGIN:
                Log.i(TAG, "Back from ACTIVITY_LOGIN");
                if(resultCode == RESULT_LOGIN_SUCCESS){
                    String result = data.getExtras().getString(RETURN_VALUE_LOGIN);
                    Log.i(TAG, "Return from ACTIVITY_LOGIN: result = " + result);

                    String[] splited_data = Utility.dataSplitter(result);
                    Log.i(TAG, splited_data[0]);
                    for(int i = 0; i < Integer.parseInt(splited_data[0]); i++){
                        String uid = splited_data[i*6+1];
                        String cane_name = splited_data[i*6+2];
                        String latitude_dmm = splited_data[i*6+3];
                        String position_n_s = splited_data[i*6+4];
                        String longitude_dmm = splited_data[i*6+5];
                        String position_e_w = splited_data[i*6+6];

                        /*
                        Log.i(TAG, uid);
                        Log.i(TAG, cane_name);
                        Log.i(TAG, latitude_dmm);
                        Log.i(TAG, position_n_s);
                        Log.i(TAG, longitude_dmm);
                        Log.i(TAG, position_e_w);
                        */

                        mDataCurrentPositionList.add(
                                new DataCurrentPosition(uid, cane_name, latitude_dmm,
                                        position_n_s, longitude_dmm, position_e_w));
                    }

                    for(int i = 0; i < mDataCurrentPositionList.size(); i++){
                        Log.i(TAG, "" + mDataCurrentPositionList.get(i).toString());
                    }

                    DataCurrentPosition first_cane = mDataCurrentPositionList.get(0);
                    String first_cane_name = first_cane.caneName;
                    double first_latitude_dd = Utility.latitudeDMMtoDD(first_cane.latitudeDMM, first_cane.position_N_S);
                    double first_longitude_dd = Utility.longitudeDMMtoDD(first_cane.longitudeDMM, first_cane.position_E_W);
                    drawMarkerCaneCurrent(first_cane_name, first_latitude_dd, first_longitude_dd);
                    drawMarkerCaneHistory(first_cane_name, first_latitude_dd, first_longitude_dd, true);


                    mLoginButton.setVisibility(View.GONE);
                    mBatteryImageView.setVisibility(View.VISIBLE);
                    mLightImageView.setVisibility(View.VISIBLE);
                    mCaneImageView.setVisibility(View.VISIBLE);
                    mEmergencyImageView.setVisibility(View.VISIBLE);
                    mHistoryImageView.setVisibility(View.VISIBLE);

                    Utility.makeTextAndShow(mContext, "登入成功", 2);
                }
                else{
                    Log.e(TAG, "Return from ACTIVITY_LOGIN: resultCode = " + resultCode);
                }
        }
    }
}
