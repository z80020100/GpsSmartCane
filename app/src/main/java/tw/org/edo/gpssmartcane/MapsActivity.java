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
import android.widget.TextView;

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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static tw.org.edo.gpssmartcane.Constant.ACTIVITY_LOGIN;
import static tw.org.edo.gpssmartcane.Constant.ACTIVITY_SETTING;
import static tw.org.edo.gpssmartcane.Constant.NAME_LOGIN_EMAIL;
import static tw.org.edo.gpssmartcane.Constant.NAME_LOGIN_PASSWORD;
import static tw.org.edo.gpssmartcane.Constant.NAME_QUERY_STATUS_USER_ID;
import static tw.org.edo.gpssmartcane.Constant.NAME_SEARCH_HISTORY_CANE_UID;
import static tw.org.edo.gpssmartcane.Constant.NAME_SEARCH_HISTORY_END_RANGE;
import static tw.org.edo.gpssmartcane.Constant.NAME_SEARCH_HISTORY_START_RANGE;
import static tw.org.edo.gpssmartcane.Constant.RESULT_LOGIN_FAIL;
import static tw.org.edo.gpssmartcane.Constant.RESULT_LOGIN_SUCCESS;
import static tw.org.edo.gpssmartcane.Constant.RESULT_LOGIN_SUCCESS_NO_GPS_SIGNAL;
import static tw.org.edo.gpssmartcane.Constant.RESULT_QUERY_STATUS_FAIL;
import static tw.org.edo.gpssmartcane.Constant.RESULT_QUERY_STATUS_SUCCESS;
import static tw.org.edo.gpssmartcane.Constant.RESULT_SEARCH_FAIL;
import static tw.org.edo.gpssmartcane.Constant.RETURN_VALUE_LOGIN;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_CHECK_FAIL;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_CANE_UID;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_USER_ID;
import static tw.org.edo.gpssmartcane.Constant.URL_LOGIN;
import static tw.org.edo.gpssmartcane.Constant.URL_QUERY_STATUS;
import static tw.org.edo.gpssmartcane.Constant.URL_SEARCH_HISTORY;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    final private  String TAG = this.getClass().getSimpleName();

    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private Context mContext = this;
    private SettingManager mSettingManager;

    private List<DataCurrentPosition> mDataCurrentPositionList = new ArrayList<>();
    private List<DataHistoryPosition> mDataHistoryPositionList = new ArrayList<>();
    private List<MarkerOptions> mHistiryMarkerOptions = new ArrayList<>();

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

    private ImageView mSettingImageView;

    private boolean mSearchVisiable = false;
    private TextView mStartDateTextView;
    private TextView mStartTimeTextView;
    private TextView mEndDateTextView;
    private TextView mEndTimeTextView;

    private String mStartDate;
    private String mStartTime;
    private String mEndDate;
    private String mEndTime;

    private View.OnClickListener mStartDateTextViewListener;
    private View.OnClickListener mStartTimeTextViewListener;
    private View.OnClickListener mEndDateTextViewListener;
    private View.OnClickListener mEndTimeTextViewListener;
    private View.OnClickListener mHistoryImageViewListener;
    private View.OnClickListener mSettingImageViewListener;

    int mNowYear;
    int mNowMounh;
    int mNowDay;
    int mNowHour;
    int mMinute;

    private Runnable mQueryStatusRunnable;
    private Thread mQueryStatusThread;
    private int mQueryStatusCheck = RESULT_QUERY_STATUS_FAIL;

    private Runnable mGetCurrentPositionRunnable;
    private Thread mGetCurrentPositionThread;
    private String mGetCurrentPositionData;
    private int mGetCurrentPositionCheck = RESULT_LOGIN_FAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Calendar c = Calendar.getInstance();
        mNowYear = c.get(Calendar.YEAR);
        mNowMounh = c.get(Calendar.MONTH); // index is 0 ~ 11
        mNowDay = c.get(Calendar.DAY_OF_MONTH);
        mNowHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

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
        mSettingImageView = findViewById(R.id.setting);

        mHistoryImageViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mSearchVisiable){
                    mSearchVisiable = true;
                    mStartDateTextView.setVisibility(View.VISIBLE);
                    mStartTimeTextView.setVisibility(View.VISIBLE);
                    mEndDateTextView.setVisibility(View.VISIBLE);
                    mEndTimeTextView.setVisibility(View.VISIBLE);
                }
                else{
                    mSearchVisiable = false;
                    mStartDateTextView.setVisibility(View.GONE);
                    mStartTimeTextView.setVisibility(View.GONE);
                    mEndDateTextView.setVisibility(View.GONE);
                    mEndTimeTextView.setVisibility(View.GONE);
                }
            }
        };
        mHistoryImageView.setOnClickListener(mHistoryImageViewListener);

        mSettingImageViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CaneSettingActivity.class);
                startActivityForResult(intent, ACTIVITY_SETTING);
            }
        };
        mSettingImageView.setOnClickListener(mSettingImageViewListener);

        updateStatusIcon();

        mStartDateTextView = findViewById(R.id.start_date);
        mStartDateTextViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView textView = (TextView) view;
                Log.i(TAG, "onClick: mStartDateTextView");
                Log.i(TAG, "Now Date: " + mNowYear + "/" + mNowMounh + "/" + mNowDay);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                mStartDate = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
                                textView.setText(mStartDate);
                                drawHistory();
                            }
                        }, mNowYear, mNowMounh, mNowDay);
                dpd.setTitle("Select Start Date");
                dpd.show(getFragmentManager(), "DatePickerDialog");
            }
        };
        mStartDateTextView.setOnClickListener(mStartDateTextViewListener);

        mStartTimeTextView = findViewById(R.id.start_time);
        mStartTimeTextViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView textView = (TextView) view;
                Log.i(TAG, "onClick: mStartTimeTextView");
                Log.i(TAG, "Now Time: " + mNowHour + ":" + mMinute);
                boolean is24HourView = false;
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                mStartTime = hourOfDay + ":" + minute + ":" + second;
                                textView.setText(mStartTime);
                                drawHistory();
                            }
                        }, mNowHour, 0, is24HourView);
                tpd.setTitle("Select Start Time");
                tpd.enableMinutes(false);
                tpd.enableSeconds(false);
                tpd.show(getFragmentManager(), "TimePickerDialog");
            }
        };
        mStartTimeTextView.setOnClickListener(mStartTimeTextViewListener);

        mEndDateTextView = findViewById(R.id.end_date);
        mEndDateTextViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView textView = (TextView) view;
                Log.i(TAG, "onClick: mEndDateTextView");
                Log.i(TAG, "Now Date: " + mNowYear + "/" + mNowMounh + "/" + mNowDay);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                mEndDate = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
                                textView.setText(mEndDate);
                                drawHistory();
                            }
                        }, mNowYear, mNowMounh, mNowDay);
                dpd.setTitle("Select End Date");
                dpd.show(getFragmentManager(), "DatePickerDialog");
            }
        };
        mEndDateTextView.setOnClickListener(mEndDateTextViewListener);

        mEndTimeTextView = findViewById(R.id.end_time);
        mEndTimeTextViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView textView = (TextView) view;
                Log.i(TAG, "onClick: mEndTimeTextView");
                Log.i(TAG, "Now Time: " + mNowHour + ":" + mMinute);
                boolean is24HourView = false;
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                mEndTime = hourOfDay + ":" + minute + ":" + second;
                                textView.setText(mEndTime);
                                drawHistory();
                            }
                        }, mNowHour, 0, is24HourView);
                tpd.setTitle("Select Start Time");
                tpd.enableMinutes(false);
                tpd.enableSeconds(false);
                tpd.show(getFragmentManager(), "TimePickerDialog");
            }
        };
        mEndTimeTextView.setOnClickListener(mEndTimeTextViewListener);

        mQueryStatusRunnable = new Runnable() {
            @Override
            public void run() {
                String url = URL_QUERY_STATUS;
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(NAME_QUERY_STATUS_USER_ID, SettingManager.sUserId));
                String returnData = DBConnector.executeQuery(params, url, SettingManager.sSessionIdFieldName, SettingManager.sSessionId);
                int queryStatusResult = Character.getNumericValue(returnData.charAt(0));
                Log.i(TAG, "queryStatusResult = " + queryStatusResult);
                if(queryStatusResult != RESULT_QUERY_STATUS_FAIL){
                    mQueryStatusCheck = RESULT_QUERY_STATUS_SUCCESS;
                }
            }
        };

        mGetCurrentPositionRunnable = new Runnable() {
            @Override
            public void run() {
                String url = URL_LOGIN;
                ArrayList<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(NAME_LOGIN_EMAIL, SettingManager.sEmaill));
                params.add(new BasicNameValuePair(NAME_LOGIN_PASSWORD, SettingManager.sPassword));
                String returnData = DBConnector.executeQuery(params, url);
                mGetCurrentPositionCheck = Character.getNumericValue(returnData.charAt(0));
                Log.i(TAG, "mGetCurrentPositionCheck = " + mGetCurrentPositionCheck);
                if(mGetCurrentPositionCheck != RESULT_LOGIN_FAIL){
                    mGetCurrentPositionData = returnData;
                }
                else{
                    mGetCurrentPositionData = null;
                }

            }
        };

        mSettingManager = new SettingManager(mContext);
        if(mSettingManager.checkData() == SHAREPREFERENCES_CHECK_FAIL){
            Log.e(TAG, "No login information!");
            changeUi(false);
        }
        else{
            Log.i(TAG, "Detect login information");
            Log.i(TAG, "Try to get cane status...");
            mQueryStatusThread = new Thread(mQueryStatusRunnable);
            mQueryStatusThread.start();
            try {
                mQueryStatusThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(mQueryStatusCheck == RESULT_QUERY_STATUS_SUCCESS){
                Utility.makeTextAndShow(mContext, "自動登入中...", 2);

                mGetCurrentPositionThread = new Thread(mGetCurrentPositionRunnable);
                mGetCurrentPositionThread.start();
                try {
                    mGetCurrentPositionThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(mGetCurrentPositionCheck != RESULT_LOGIN_FAIL){
                    Log.i(TAG, "Get current position success");
                    changeUi(true);
                    Utility.makeTextAndShow(mContext, "登入成功", 2);
                }
                else{
                    Log.e(TAG, "Get current position failed...");
                    changeUi(false);
                    Utility.makeTextAndShow(mContext, "登入失敗，請嘗試手動登入", 2);
                }
            }
            else{
                Log.e(TAG, "Get cane status failed...");
                changeUi(false);
            }
        }

        mStartDateTextView.setVisibility(View.GONE);
        mStartTimeTextView.setVisibility(View.GONE);
        mEndDateTextView.setVisibility(View.GONE);
        mEndTimeTextView.setVisibility(View.GONE);
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
        if(mGetCurrentPositionData != null){
            drawCurrent(mGetCurrentPositionData);
        }
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
            final Circle circle = mGoogleMap.addCircle(circleOptions);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(gps)
                    .alpha(0F)
                    .title(cane_name);
            mHistiryMarkerOptions.add(markerOptions);
            final Marker melbourne = mGoogleMap.addMarker(markerOptions);

            mGoogleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                @Override
                public void onCircleClick(Circle circle) {
                    melbourne.showInfoWindow();
                }
            });

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });

            if(camera_move){
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 18));
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
                    Utility.makeTextAndShow(mContext, "登入成功", 2);
                    String result = data.getExtras().getString(RETURN_VALUE_LOGIN);
                    Log.i(TAG, "Return from ACTIVITY_LOGIN: result = " + result);
                    drawCurrent(result);
                    changeUi(true);
                }
                else{
                    Log.e(TAG, "Return from ACTIVITY_LOGIN: resultCode = " + resultCode);
                }
        }
    }

    private void drawCurrent(String result){
        String[] splited_data = Utility.dataSplitter(result);
        Log.i(TAG, "User ID: " + splited_data[0]);
        mSettingManager.writeData(SHAREPREFERENCES_FIELD_USER_ID, splited_data[0]);
        Log.i(TAG, "Cane Quantity: " + splited_data[1]);

        for(int i = 0; i < Integer.parseInt(splited_data[1]); i++){
            String uid = splited_data[i*6+2];
            String cane_name = splited_data[i*6+3];
            String latitude_dmm = splited_data[i*6+4];
            if(latitude_dmm.equals(RESULT_LOGIN_SUCCESS_NO_GPS_SIGNAL)){
                Log.e(TAG, "No GPS signal");
                Utility.makeTextAndShow(mContext, "無GPS訊號", 2);
            }
            else{
                String position_n_s = splited_data[i*6+5];
                String longitude_dmm = splited_data[i*6+6];
                String position_e_w = splited_data[i*6+7];
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
        }

        for(int i = 0; i < mDataCurrentPositionList.size(); i++){
            Log.i(TAG, "" + mDataCurrentPositionList.get(i).toString());
        }

        if(mDataCurrentPositionList.size() > 0){
            DataCurrentPosition first_cane = mDataCurrentPositionList.get(0);
            mSettingManager.writeData(SHAREPREFERENCES_FIELD_CANE_UID, first_cane.uid);
            String first_cane_name = first_cane.caneName;
            double first_latitude_dd = Utility.latitudeDMMtoDD(first_cane.latitudeDMM, first_cane.position_N_S);
            double first_longitude_dd = Utility.longitudeDMMtoDD(first_cane.longitudeDMM, first_cane.position_E_W);
            drawMarkerCaneCurrent(first_cane_name, first_latitude_dd, first_longitude_dd);
            drawMarkerCaneHistory(first_cane_name, first_latitude_dd, first_longitude_dd, true);
        }
    }

    private void drawHistory(){
        if((mStartDate != null) && (mStartTime != null) && (mEndDate != null) && (mEndTime != null)){
            mHistiryMarkerOptions.clear();
            mDataHistoryPositionList.clear();
            mGoogleMap.clear();

            Utility.makeTextAndShow(mContext, "開始搜尋...", 2);

            //mStartDate = mStartTime = mEndDate = mEndTime = null;
            //mStartDateTextView.setText("起始日期");
            //mStartTimeTextView.setText("起始時間");
            //mEndDateTextView.setText("結束日期");
            //mEndTimeTextView.setText("結束時間");

            Runnable searchHistoryRunnable;
            Thread searchHistoryThread;
            searchHistoryRunnable = new Runnable() {
                @Override
                public void run() {
                    String url = URL_SEARCH_HISTORY;
                    String caneUid = SettingManager.sCaneId;
                    String startRange = mStartDate + " " + mStartTime;
                    String endRange = mEndDate + " " + mEndTime;
                    Log.i(TAG, "Query String: "+ startRange + "; " + endRange);
                    ArrayList<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair(NAME_SEARCH_HISTORY_CANE_UID, caneUid));
                    params.add(new BasicNameValuePair(NAME_SEARCH_HISTORY_START_RANGE, startRange));
                    params.add(new BasicNameValuePair(NAME_SEARCH_HISTORY_END_RANGE, endRange));
                    String returnData = DBConnector.executeQuery(params, url);
                    int searchResult = Character.getNumericValue(returnData.charAt(0));
                    if(searchResult != RESULT_SEARCH_FAIL){
                        String[] splitData = Utility.dataSplitter(returnData);
                        int offset = 0;
                        if(!splitData[0].equals("0<br>")){
                            int historyQuantity = Integer.valueOf(splitData[0]);
                            Log.i(TAG, "Quantity of History: " + historyQuantity);
                            for(int i = 0; i <  historyQuantity; i++){
                                if(splitData[i*5+1+offset].equals(RESULT_LOGIN_SUCCESS_NO_GPS_SIGNAL)){
                                    offset -= 3;
                                    continue;
                                }
                                DataHistoryPosition dhp = new DataHistoryPosition(splitData[i*5+1+offset], splitData[i*5+2+offset],
                                        splitData[i*5+3+offset], splitData[i*5+4+offset], splitData[i*5+5+offset]);
                                mDataHistoryPositionList.add(dhp);
                                //Log.i(TAG, "Data: " + dhp.toString());
                            }

                            for(int i = 0; i < mDataHistoryPositionList.size(); i++){
                                DataHistoryPosition position = mDataHistoryPositionList.get(i);
                                final double position_latitude_dd = Utility.latitudeDMMtoDD(position.latitudeDMM, position.position_N_S);
                                final double position_longitude_dd = Utility.longitudeDMMtoDD(position.longitudeDMM, position.position_E_W);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        drawMarkerCaneHistory("A001", position_latitude_dd, position_longitude_dd, false);
                                    }
                                });
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mHistiryMarkerOptions.size() > 0){
                                        MarkerOptions lastMarkOption = mHistiryMarkerOptions.get(mHistiryMarkerOptions.size()-1);
                                        LatLng lastPosition = lastMarkOption.getPosition();
                                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 18));
                                    }
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Utility.makeTextAndShow(mContext, "此段時間無歷史資料", 2);
                                }
                            });
                        }
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utility.makeTextAndShow(mContext, "查詢失敗", 2);
                            }
                        });
                    }
                }
            };
            searchHistoryThread = new Thread(searchHistoryRunnable);
            searchHistoryThread.start();

            /*
            try {
                searchHistoryThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */

        }
        else{
            Log.i(TAG, "Search condition not ready");
        }
    }

    private void changeUi(boolean login){
        if(login){
            mLoginButton.setVisibility(View.GONE);
            mBatteryImageView.setVisibility(View.VISIBLE);
            mLightImageView.setVisibility(View.VISIBLE);
            mCaneImageView.setVisibility(View.VISIBLE);
            mEmergencyImageView.setVisibility(View.VISIBLE);
            mHistoryImageView.setVisibility(View.VISIBLE);
        }
        else{
            mLoginButton.setVisibility(View.VISIBLE);
            mBatteryImageView.setVisibility(View.GONE);
            mLightImageView.setVisibility(View.GONE);
            mCaneImageView.setVisibility(View.GONE);
            mEmergencyImageView.setVisibility(View.GONE);
            mHistoryImageView.setVisibility(View.GONE);
        }
    }
}
