package tw.org.edo.gpssmartcane;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_CHECK_FAIL;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_CHECK_OK;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_CANE_NAME;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_CANE_UID;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_FREQ_INDEX;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_LOGIN_EMAIL;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_LOGIN_PASSWORD;
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
    final private Object mLocker;

    public static String sEmaill;
    public static String sPassword;
    private static String mSessionIdFieldName;
    private static String mSessionId;
    public static String sUserId;
    public static String sCaneId;
    public static String sFreqIndex;
    public static String sStepIndex;
    public static String sLowBatteryIndex;
    public static String sCaneName;

    private static final String SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID = "session_id";
    private static final String SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID_FIELD_NAME = "session_id_field_name";

    public SettingManager(Context context){
        mContext = context;
        mSettings = mContext.getSharedPreferences(SHAREPREFERENCES_FILE_NAME, MODE_PRIVATE);
        mEditor = mSettings.edit();
        mLocker = new Object();
        readData();
    }

    public void readData(){
        synchronized (mLocker){
            sEmaill = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_EMAIL, "");
            sPassword = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_PASSWORD, "");
            mSessionIdFieldName = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID_FIELD_NAME, "");
            mSessionId = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID, "");
            sUserId = mSettings.getString(SHAREPREFERENCES_FIELD_USER_ID, "");
            sCaneId = mSettings.getString(SHAREPREFERENCES_FIELD_CANE_UID, "");
            sFreqIndex = mSettings.getString(SHAREPREFERENCES_FIELD_FREQ_INDEX, "0");
            sStepIndex = mSettings.getString(SHAREPREFERENCES_FIELD_STEP_INDEX, "0");
            sLowBatteryIndex = mSettings.getString(SHAREPREFERENCES_FIELD_LOW_BATTERY_INDEX, "0");
            sCaneName = mSettings.getString(SHAREPREFERENCES_FIELD_CANE_NAME, "");

            Log.i(TAG, "[readData]Email: " + sEmaill);
            Log.i(TAG, "[readData]PWD: " + sPassword);
            Log.i(TAG, "[readData]Session ID Field Name: " + mSessionIdFieldName);
            Log.i(TAG, "[readData]Session ID: " + mSessionId);
            Log.i(TAG, "[readData]User ID: " + sUserId);
            Log.i(TAG, "[readData]Cane UID: " + sCaneId);
            Log.i(TAG, "[readData]Frequency Index: " + sFreqIndex);
            Log.i(TAG, "[readData]Step Index: " + sStepIndex);
            Log.i(TAG, "[readData]Low Battery Index: " + sLowBatteryIndex);
        }
    }

    public int checkData(){
        synchronized (mLocker){
            if(mSessionId.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
            else if(mSessionIdFieldName.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
            else if(sEmaill.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
            else if(sPassword.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
            else if(sUserId.equals("")) return SHAREPREFERENCES_CHECK_FAIL;
            else return SHAREPREFERENCES_CHECK_OK;
        }
    }

    public int writeData(String fieldName, String value){
        synchronized (mLocker){
            if(fieldName == null || value == null) return SHAREPREFERENCES_WRITE_FAIL;
            if(fieldName == SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID_FIELD_NAME ||
                    fieldName == SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID) {
                Log.e(TAG, "[writeData]Please function writeSessionData() to write session data");
                return SHAREPREFERENCES_WRITE_FAIL;
            }
            mEditor.putString(fieldName, value);
            mEditor.commit();
            readData();
            return SHAREPREFERENCES_WRITE_OK;
        }
    }

    public int writeSessionData(String sessionIdFieldName, String sessionId) {
        synchronized (mLocker){
            if (sessionIdFieldName != null && sessionId != null) {
                String fieldName = SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID_FIELD_NAME;
                String value = sessionIdFieldName;
                mEditor.putString(fieldName, value);
                mEditor.commit();

                fieldName = SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID;
                value = sessionId;
                mEditor.putString(fieldName, value);
                mEditor.commit();
                updateSessionData();

                Log.i(TAG, "[writeSessionData]Update ASP session field name");
                Log.i(TAG, "[writeSessionData]Update ASP session value");

                return SHAREPREFERENCES_WRITE_OK;
            } else {
                return SHAREPREFERENCES_WRITE_FAIL;
            }
        }
    }

    public String[] readSessionData(){
        synchronized (mLocker){
            String[] sessionData = new String[2];
            sessionData[0] = mSessionIdFieldName;
            sessionData[1] = mSessionId;
            return sessionData;
        }
    }

    private void updateSessionData(){
        mSessionIdFieldName = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID_FIELD_NAME, "");
        mSessionId = mSettings.getString(SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID, "");
    }
}
