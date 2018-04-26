package tw.org.edo.gpssmartcane;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_CHECK_FAIL;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_CHECK_OK;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_CANE_UID;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_FREQ_INDEX;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_LOGIN_EMAIL;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_LOGIN_PASSWORD;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID_FIELD_NAME;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_LOW_BATTERY_INDEX;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_STEP_INDEX;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_USER_ID;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FILE_NAME;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_WRITE_FAIL;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_WRITE_OK;

/**
 * Created by CLIFF on 2018/4/24.
 */

public class SettingManager {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;

    public static String sEmaill;
    public static String sPassword;
    public static String sSessionIdFieldName;
    public static String sSessionId;
    public static String sUserId;
    public static String sCaneId;
    public static String sFreqIndex;
    public static String sStepIndex;
    public static String sLowBatteryIndex;

    public SettingManager(Context context){
        mContext = context;
        mSettings = mContext.getSharedPreferences(SHAREPREFERENCES_FILE_NAME, MODE_PRIVATE);
        mEditor = mSettings.edit();
        readData();
    }

    public void readData(){
        sEmaill = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_EMAIL, "");
        sPassword = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_PASSWORD, "");
        sSessionIdFieldName = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID_FIELD_NAME, "");
        sSessionId = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID, "");
        sUserId = mSettings.getString(SHAREPREFERENCES_FIELD_USER_ID, "");
        sCaneId = mSettings.getString(SHAREPREFERENCES_FIELD_CANE_UID, "");
        sFreqIndex = mSettings.getString(SHAREPREFERENCES_FIELD_FREQ_INDEX, "0");
        sStepIndex = mSettings.getString(SHAREPREFERENCES_FIELD_STEP_INDEX, "0");
        sLowBatteryIndex = mSettings.getString(SHAREPREFERENCES_FIELD_LOW_BATTERY_INDEX, "0");

        Log.i(TAG, "Email: " + sEmaill);
        Log.i(TAG, "PWD: " + sPassword);
        Log.i(TAG, "Session ID Field Name: " + sSessionIdFieldName);
        Log.i(TAG, "Session ID: " + sSessionId);
        Log.i(TAG, "User ID: " + sUserId);
        Log.i(TAG, "Cane UID: " + sCaneId);
        Log.i(TAG, "Frequency Index: " + sFreqIndex);
        Log.i(TAG, "Step Index: " + sStepIndex);
        Log.i(TAG, "Low Battery Index: " + sLowBatteryIndex);
    }

    public int checkData(){
        if(sSessionId.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
        else if(sSessionIdFieldName.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
        else if(sEmaill.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
        else if(sPassword.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
        else if(sUserId.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
        else return SHAREPREFERENCES_CHECK_OK;
    }

    public int writeData(String fieldName, String value){
        if(fieldName == null || value == null) return SHAREPREFERENCES_WRITE_FAIL;
        mEditor.putString(fieldName, value);
        mEditor.commit();
        readData();
        return SHAREPREFERENCES_WRITE_OK;
    }
}
