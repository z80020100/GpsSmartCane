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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import static tw.org.edo.gpssmartcane.Constant.NAME_CLEAR_EMERGENCY_UID;
import static tw.org.edo.gpssmartcane.Constant.NAME_LOGIN_EMAIL;
import static tw.org.edo.gpssmartcane.Constant.NAME_LOGIN_PASSWORD;
import static tw.org.edo.gpssmartcane.Constant.NAME_QUERY_STATUS_USER_ID;
import static tw.org.edo.gpssmartcane.Constant.NAME_SEARCH_HISTORY_CANE_UID;
import static tw.org.edo.gpssmartcane.Constant.NAME_SEARCH_HISTORY_END_RANGE;
import static tw.org.edo.gpssmartcane.Constant.NAME_SEARCH_HISTORY_START_RANGE;
import static tw.org.edo.gpssmartcane.Constant.RESULT_CLEAR_EMERGENCY_SUCCESS;
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
import static tw.org.edo.gpssmartcane.Constant.URL_CLEAR_EMERGENCY;
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
    private DataStatus mDataStatus = new DataStatus();

    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;

    // UI
    private Button mLoginButton;
    private Button.OnClickListener mLoginButtonListen;

    private TextView mBatteryCapacity;
    private ImageView mBatteryImageView;
    private ImageView mLightImageView;
    private ImageView mCaneImageView;
    private ImageView mEmergencyImageView;
    private ImageView mHistoryImageView;
    private View.OnClickListener mBlankViewListener;

    private Animation mAnimBatteryFlashS1;
    private Animation mAnimBatteryFlashS2;
    private boolean mBatteryFlash = false;

    private Animation mAnimCaneFallBlinkS1;
    private Animation mAnimCaneFallBlinkS2;
    private boolean mCaneFallBlink = false;

    private Animation mAnimEmergencyBlinkS1;
    private Animation mAnimEmergencyBlinkS2;
    private boolean mEmergencyBlink = false;

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
    private View.OnClickListener mCaneImageViewViewListener;
    private View.OnClickListener mEmergencyImageViewListener;

    int mNowYear;
    int mNowMonth;
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

    private boolean mPollingStatusCtrl = false;
    private boolean mPollingThreadDebug = false;
    private Runnable mPollingStatusRunnable;
    private Thread mPollingStatusThread;
    private long mPollingPeriod = 1000L;

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        Log.i(TAG, "onPause");

        mPollingStatusCtrl = false;
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.i(TAG, "onResume");

        if(mGetCurrentPositionCheck != RESULT_LOGIN_FAIL){
            mPollingStatusCtrl = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Calendar c = Calendar.getInstance();
        mNowYear = c.get(Calendar.YEAR);
        mNowMonth = c.get(Calendar.MONTH); // index is 0 ~ 11
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

        mBatteryCapacity = findViewById(R.id.battery_status_capacity);
        mBatteryImageView = findViewById(R.id.battery_status);
        mLightImageView = findViewById(R.id.light_status);
        mCaneImageView = findViewById(R.id.cane_status);
        mEmergencyImageView = findViewById(R.id.emergency_status);
        mHistoryImageView = findViewById(R.id.history);
        mSettingImageView = findViewById(R.id.setting);

        mBlankViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Bind blank onClickListener for avoid affecting on map via click the icon
            }
        };
        mBatteryImageView.setOnClickListener(mBlankViewListener);
        mLightImageView.setOnClickListener(mBlankViewListener);
        mHistoryImageView.setOnClickListener(mBlankViewListener);

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
                if(mGetCurrentPositionCheck != RESULT_LOGIN_FAIL){
                    Intent intent = new Intent(mContext, CaneSettingActivity.class);
                    startActivityForResult(intent, ACTIVITY_SETTING);
                }
                else{
                    Utility.makeTextAndShow(mContext, "請先登入", 2);
                }

            }
        };
        mSettingImageView.setOnClickListener(mSettingImageViewListener);

        mCaneImageViewViewListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mGetCurrentPositionData != null){
                    drawCurrent(mGetCurrentPositionData, true);
                }
                else{
                    Utility.makeTextAndShow(mContext, "錯誤：無法取得拐杖現在位置", 2);
                }
            }
        };
        mCaneImageView.setOnClickListener(mCaneImageViewViewListener);

        mEmergencyImageViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(mDataStatus.emergencyNotice == true){
                            clearEmergency();
                        }
                        else{
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 Utility.makeTextAndShow(mContext, "緊急按鈕未被觸發，無須清除", 1);
                             }
                         });
                        }
                    }
                }).start();
            }
        };
        mEmergencyImageView.setOnClickListener(mEmergencyImageViewListener);

        mStartDateTextView = findViewById(R.id.start_date);
        mStartDateTextViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView textView = (TextView) view;
                Log.i(TAG, "onClick: mStartDateTextView");
                Log.i(TAG, "Now Date: " + mNowYear + "/" + (mNowMonth+1) + "/" + mNowDay);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                mStartDate = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
                                textView.setText(mStartDate);
                                drawHistory();
                            }
                        }, mNowYear, mNowMonth, mNowDay);
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
                Log.i(TAG, "Now Date: " + mNowYear + "/" + (mNowMonth+1) + "/" + mNowDay);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                mEndDate = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
                                textView.setText(mEndDate);
                                drawHistory();
                            }
                        }, mNowYear, mNowMonth, mNowDay);
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
                queryStatusData("[Status]", true);
            }
        };

        mPollingStatusRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "[Polling] Start polling thread");
                int pollingCnt = 0;
                //noinspection InfiniteLoopStatement
                while(true){
                    if(mPollingStatusCtrl){
                        if(mPollingThreadDebug)  Log.i(TAG, "[Polling] pollingCnt = " + pollingCnt);
                        queryStatusData("[Polling]", mPollingThreadDebug);
                        queryCurrentPosition(false);
                    }
                    else{
                        if(mPollingThreadDebug)  Log.i(TAG, "[Polling] Pause polling");
                    }

                    try {
                        Thread.sleep(mPollingPeriod);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pollingCnt++;
                }
            }
        };

        mGetCurrentPositionRunnable = new Runnable() {
            @Override
            public void run() {
                queryCurrentPosition(true);
            }
        };

        mSettingManager = new SettingManager(mContext);
        if(mSettingManager.checkData() == SHAREPREFERENCES_CHECK_FAIL){
            Log.e(TAG, "[CurrentPosition] No login information!");
            changeUi(false);
        }
        else{
            Log.i(TAG, "[CurrentPosition] Detect login information");
            Log.i(TAG, "[CurrentPosition] Try to get cane status...");
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
                    Log.i(TAG, "[CurrentPosition] Get current position success");
                    changeUi(true);
                    Utility.makeTextAndShow(mContext, "登入成功", 2);
                }
                else{
                    Log.e(TAG, "[CurrentPosition] Get current position failed...");
                    changeUi(false);
                    Utility.makeTextAndShow(mContext, "錯誤：登入失敗，請嘗試手動登入", 2);
                }
            }
            else{
                Utility.makeTextAndShow(mContext, "錯誤：無法取得拐杖狀態", 2);
                Log.e(TAG, "Get cane status failed...");
                changeUi(false);
            }
        }

        mStartDateTextView.setVisibility(View.GONE);
        mStartTimeTextView.setVisibility(View.GONE);
        mEndDateTextView.setVisibility(View.GONE);
        mEndTimeTextView.setVisibility(View.GONE);

        initialBatteryFlashAnimation();
        initialCaneFallBlinkAnimation();
        initialEmergencyBlinkAnimation();
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
        Log.i(TAG, "onMapReady");
        mGoogleMap = googleMap;
        mGoogleMap.clear();

        if(mGetCurrentPositionData != null){
            drawCurrent(mGetCurrentPositionData, true);

            mPollingStatusThread = new Thread(mPollingStatusRunnable);
            mPollingStatusCtrl = true;
            mPollingStatusThread.start();
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

    private void drawMarkerCaneCurrentIcon(String cane_name, double latitude_dd, double longitude_dd, boolean camera_move) {
        if (mGoogleMap != null) {
            //mGoogleMap.clear();
            LatLng gps = new LatLng(latitude_dd, longitude_dd);
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title(cane_name)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_man)));
            if(camera_move){
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gps, 18));
            }
        }
    }

    private void drawMarkerCaneCurrentCircle(final String cane_name, double latitude_dd, double longitude_dd, boolean camera_move) {
        if (mGoogleMap != null) {
            //mGoogleMap.clear();
            final LatLng gps = new LatLng(latitude_dd, longitude_dd);

            CircleOptions circleOptions = new CircleOptions()
                    .center(gps)
                    .strokeColor(Color.argb(255, 0, 0, 0))
                    .fillColor(Color.argb(0, 68, 114, 196))
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
        mBatteryCapacity.bringToFront();
        mBatteryCapacity.setText(String.valueOf(mDataStatus.batteryCapacity) + " ");
        if(mDataStatus.batteryCapacity <= mDataStatus.batteryAlertThreshold){
            mBatteryCapacity.setTextColor(Color.rgb(255, 0, 0));
            if(mBatteryFlash == false){
                Log.i(TAG, "Start to blink the battery icon");
                mBatteryFlash = true;
                mBatteryImageView.startAnimation(mAnimBatteryFlashS1);
            }
            else{
                Log.i(TAG, "[Update Status] Battery icon is blinking");
            }
        }
        else{
            mBatteryFlash = false;
            mBatteryCapacity.setTextColor(Color.rgb(0, 0, 0));
            mBatteryImageView.setImageResource(R.mipmap.battery_normal);
        }

        if(mDataStatus.lightStatus == false){
            mLightImageView.setImageResource(R.mipmap.light_off);
        }
        else{
            mLightImageView.setImageResource(R.mipmap.light_on);
        }

        if(mDataStatus.caneFall == false){
            mCaneFallBlink = false;
            mCaneImageView.setImageResource(R.mipmap.cane_normal);
        }
        else{
            if(mCaneFallBlink == false){
                Log.i(TAG, "Start to blink the cane icon");
                mCaneFallBlink = true;
                mCaneImageView.startAnimation(mAnimCaneFallBlinkS1);
            }
            else{
                Log.i(TAG, "[Update Status] Cane icon is blinking");
            }
        }

        if(mDataStatus.emergencyNotice == true){
            if(mEmergencyBlink == false){
                Log.i(TAG, "Start to blink the emergency icon");
                mEmergencyBlink = true;
                mEmergencyImageView.startAnimation(mAnimEmergencyBlinkS1);
            }
            else{
                Log.i(TAG, "[Update Status] Emergency icon is blinking");
            }
        }
        else{
            mEmergencyBlink = false;
            mEmergencyImageView.setImageResource(R.mipmap.emergency_normal);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case ACTIVITY_LOGIN:
                Log.i(TAG, "Back from ACTIVITY_LOGIN");
                if(resultCode == RESULT_LOGIN_SUCCESS){
                    mGetCurrentPositionCheck = RESULT_LOGIN_SUCCESS;
                    Utility.makeTextAndShow(mContext, "登入成功", 2);
                    String result = data.getExtras().getString(RETURN_VALUE_LOGIN);
                    Log.i(TAG, "Return from ACTIVITY_LOGIN: result = " + result);
                    drawCurrent(result, true);
                    changeUi(true);

                    mPollingStatusThread = new Thread(mPollingStatusRunnable);
                    mPollingStatusCtrl = true;
                    mPollingStatusThread.start();
                }
                else{
                    Log.e(TAG, "Return from ACTIVITY_LOGIN: resultCode = " + resultCode);
                }
        }
    }

    private void drawCurrent(String result, boolean cameraMove){
        String[] splited_data = Utility.dataSplitter(result);
        Log.i(TAG, "[drawCurrent] User ID: " + splited_data[0]);
        mSettingManager.writeData(SHAREPREFERENCES_FIELD_USER_ID, splited_data[0]);
        Log.i(TAG, "[drawCurrent] Cane Quantity: " + splited_data[1]);

        for(int i = 0; i < Integer.parseInt(splited_data[1]); i++){
            String uid = splited_data[i*6+2];
            String cane_name = splited_data[i*6+3];
            String latitude_dmm = splited_data[i*6+4];
            if(latitude_dmm.equals(RESULT_LOGIN_SUCCESS_NO_GPS_SIGNAL)){
                Log.e(TAG, "[drawCurrent] No GPS signal");
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
            Log.i(TAG, "[drawCurrent] " + mDataCurrentPositionList.get(i).toString());
        }

        if(mDataCurrentPositionList.size() > 0){
            DataCurrentPosition first_cane = mDataCurrentPositionList.get(0);
            mSettingManager.writeData(SHAREPREFERENCES_FIELD_CANE_UID, first_cane.uid);
            String first_cane_name = first_cane.caneName;
            double first_latitude_dd = Utility.latitudeDMMtoDD(first_cane.latitudeDMM, first_cane.position_N_S);
            double first_longitude_dd = Utility.longitudeDMMtoDD(first_cane.longitudeDMM, first_cane.position_E_W);
            drawMarkerCaneCurrentCircle(first_cane_name, first_latitude_dd, first_longitude_dd, false);
            drawMarkerCaneCurrentIcon(first_cane_name, first_latitude_dd, first_longitude_dd, cameraMove);
        }else{
            // Workaround: force write the uid of the first cane
            Log.i(TAG, "Workaround: force write the uid of the first cane when no GPS signal");
            mSettingManager.writeData(SHAREPREFERENCES_FIELD_CANE_UID, splited_data[2]);
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
                                Utility.makeTextAndShow(mContext, "錯誤：查詢失敗", 2);
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

    private void queryCurrentPosition(boolean debug){
        String url = URL_LOGIN;
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(NAME_LOGIN_EMAIL, SettingManager.sEmaill));
        params.add(new BasicNameValuePair(NAME_LOGIN_PASSWORD, SettingManager.sPassword));
        String returnData = DBConnector.executeQuery(params, url);
        mGetCurrentPositionCheck = Character.getNumericValue(returnData.charAt(0));
        if(debug) Log.i(TAG, "[queryCurrentPosition] mGetCurrentPositionCheck = " + mGetCurrentPositionCheck);
        if(mGetCurrentPositionCheck != RESULT_LOGIN_FAIL){
            mGetCurrentPositionData = returnData;
            if(debug){
                Log.i(TAG, "[queryCurrentPosition] Update mGetCurrentPositionData");
                Log.i(TAG, "[queryCurrentPosition] Session ID Field Name = " + DBConnector.getsAspSessionIdFieldName());
                Log.i(TAG, "[queryCurrentPosition] Session ID = " + DBConnector.getSessionIdValue());
            }
            mSettingManager.writeSessionData(DBConnector.getsAspSessionIdFieldName(), DBConnector.getSessionIdValue());
        }
        else{
            Log.e(TAG, "[queryCurrentPosition]mGetCurrentPositionCheck = RESULT_LOGIN_FAIL");
            Utility.makeTextAndShow(mContext, "錯誤：無法取得拐杖現在位置", 1);
            mGetCurrentPositionData = null;
        }
    }

    private void queryStatusData(String tag, boolean debug){
        String url = URL_QUERY_STATUS;
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(NAME_QUERY_STATUS_USER_ID, SettingManager.sUserId));
        String sessionData[] = mSettingManager.readSessionData();
        if(sessionData == null){
            Log.e(TAG, "sessionData is null!");
        }
        String returnData = DBConnector.executeQuery(params, url, sessionData[0], sessionData[1]);
        int queryStatusResult = Character.getNumericValue(returnData.charAt(0));
        if(debug) Log.i(TAG, tag + " queryStatusResult = " + queryStatusResult);
        if(queryStatusResult != RESULT_QUERY_STATUS_FAIL){
            mQueryStatusCheck = RESULT_QUERY_STATUS_SUCCESS;
            String[] splitData = Utility.dataSplitter(returnData);
            if(debug) Log.i(TAG, String.format(tag + " Cane Quantity: %s", splitData[0]));

            /*
            for(int i = 0; i < splitData.length; i++ ){
                Log.i(TAG, tag + " " + String.format("Status Data[%d] = %s", i, splitData[i]));
            }
            */

            if(Integer.valueOf(splitData[0]) == 1){
                mDataStatus.setData(splitData[1], splitData[2], Integer.valueOf(splitData[3]), Integer.valueOf(splitData[4]),
                        Boolean.parseBoolean(splitData[5]), Boolean.parseBoolean(splitData[6]), Boolean.parseBoolean(splitData[7]),
                        splitData[8], splitData[9], splitData[10],
                        splitData[11], splitData[12], Boolean.parseBoolean(splitData[13]));
                if(debug) Log.i(TAG, tag + " " + mDataStatus.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateStatusIcon();
                    }
                });
            }
            else{
                Log.e(TAG, tag + " Not support multi cane yet");
            }
        }
        else{
            Log.e(TAG, "[queryStatusData]queryStatusResult = RESULT_QUERY_STATUS_FAIL");
            Utility.makeTextAndShow(mContext, "錯誤：無法取得拐杖狀態", 1);
        }
    }

    private void clearEmergency(){
        String url = URL_CLEAR_EMERGENCY;
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(NAME_CLEAR_EMERGENCY_UID, SettingManager.sCaneId));
        String sessionData[] = mSettingManager.readSessionData();
        if(sessionData == null){
            Log.e(TAG, "sessionData is null!");
        }
        String returnData = DBConnector.executeQuery(params, url, sessionData[0], sessionData[1]);
        int clearEmergencyResult = Character.getNumericValue(returnData.charAt(0));
        if(clearEmergencyResult == RESULT_CLEAR_EMERGENCY_SUCCESS){
            Log.e(TAG, "[clearEmergency]Clear emergency status success");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utility.makeTextAndShow(mContext, "清除緊急狀態成功", 2);
                }
            });
        }
        else{
            Log.i(TAG, "[clearEmergency]clearEmergencyResult = " + clearEmergencyResult);
            Log.i(TAG, "[clearEmergency]returnData = " + returnData);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utility.makeTextAndShow(mContext, "錯誤：清除緊急狀態失敗", 2);
                }
            });
        }

    }

    private void initialBatteryFlashAnimation(){
        mAnimBatteryFlashS1 = new AlphaAnimation(1f, 1f);
        mAnimBatteryFlashS2 = new AlphaAnimation(1f, 1f);
        mAnimBatteryFlashS1.setDuration(500L);
        mAnimBatteryFlashS2.setDuration(500L);
        mAnimBatteryFlashS1.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {
                mBatteryImageView.setImageResource(R.mipmap.battery_warning_s1);
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                if(mBatteryFlash) mBatteryImageView.startAnimation(mAnimBatteryFlashS2);
            }
        });
        mAnimBatteryFlashS2.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
                mBatteryImageView.setImageResource(R.mipmap.battery_warning_s2);
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                if(mBatteryFlash) mBatteryImageView.startAnimation(mAnimBatteryFlashS1);
            }
        });
    }

    private void initialCaneFallBlinkAnimation(){
        mAnimCaneFallBlinkS1 = new AlphaAnimation(1f, 1f);
        mAnimCaneFallBlinkS2 = new AlphaAnimation(1f, 1f);
        mAnimCaneFallBlinkS1.setDuration(500L);
        mAnimCaneFallBlinkS2.setDuration(500L);
        mAnimCaneFallBlinkS1.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {
                mCaneImageView.setImageResource(R.mipmap.cane_falldown_s1);
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                if(mCaneFallBlink) mCaneImageView.startAnimation(mAnimCaneFallBlinkS2);
            }
        });
        mAnimCaneFallBlinkS2.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
                mCaneImageView.setImageResource(R.mipmap.cane_falldown_s2);
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                if(mCaneFallBlink) mCaneImageView.startAnimation(mAnimCaneFallBlinkS1);
            }
        });
    }

    private void initialEmergencyBlinkAnimation(){
        mAnimEmergencyBlinkS1 = new AlphaAnimation(1f, 1f);
        mAnimEmergencyBlinkS2 = new AlphaAnimation(1f, 1f);
        mAnimEmergencyBlinkS1.setDuration(500L);
        mAnimEmergencyBlinkS2.setDuration(500L);
        mAnimEmergencyBlinkS1.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {
                mEmergencyImageView.setImageResource(R.mipmap.emergency_alert_s1);
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                if(mEmergencyBlink) mEmergencyImageView.startAnimation(mAnimEmergencyBlinkS2);
            }
        });
        mAnimEmergencyBlinkS2.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {
                mEmergencyImageView.setImageResource(R.mipmap.emergency_alert_s2);
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                if(mEmergencyBlink) mEmergencyImageView.startAnimation(mAnimEmergencyBlinkS1);
            }
        });
    }
}
