package tw.org.edo.gpssmartcane;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import static tw.org.edo.gpssmartcane.Constant.ACTIVITY_ADD_CANE;
import static tw.org.edo.gpssmartcane.Constant.NAME_EDIT_PARAMETERS_CANE_UID;
import static tw.org.edo.gpssmartcane.Constant.NAME_EDIT_PARAMETERS_LOW_BATTERY_ALERT;
import static tw.org.edo.gpssmartcane.Constant.NAME_EDIT_PARAMETERS_SET_FREQ;
import static tw.org.edo.gpssmartcane.Constant.NAME_EDIT_PARAMETERS_SET_STEP;
import static tw.org.edo.gpssmartcane.Constant.NAME_EDIT_PARAMETERS_USER_ID;
import static tw.org.edo.gpssmartcane.Constant.RESULT_SETTING_FAIL;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_FREQ_INDEX;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_LOW_BATTERY_INDEX;
import static tw.org.edo.gpssmartcane.Constant.SHAREPREFERENCES_FIELD_STEP_INDEX;
import static tw.org.edo.gpssmartcane.Constant.URL_EDIT_PARAMETERS;
import static tw.org.edo.gpssmartcane.Constant.sFreqArray;
import static tw.org.edo.gpssmartcane.Constant.sLowBatteryArray;
import static tw.org.edo.gpssmartcane.Constant.sStepArray;

public class CaneSettingActivity extends AppCompatActivity {
    final private  String TAG = this.getClass().getSimpleName();

    private Context mContext = this;
    private SettingManager mSettingManager;

    private Runnable mHttpRunnable;
    private Thread mHttpThread;
    private String mParaNames;
    private String mParaValue;
    private String mParaIndex;

    private int[] mFreqArray = sFreqArray; // minutes
    private int[] mStepArray = sStepArray; // steps
    private int[] mLowBatteryArray = sLowBatteryArray; // percent

    private SeekBar mFreqSeekBar, mStepSeekBar, mLowBatterySeekBar;

    private TextView mFreqCurrent, mStepCurrent, mLowBatteryCurrent;
    private TextView mFreqMin, mStepMin, mLowBatteryMin;
    private TextView mFreqMax, mStepMax, mLowBatteryMax;

    private Spinner mPhoneListSpinner;
    private String mPhoneList[] = {"0955031053", "0912345678"};

    private Spinner mMailListSpinner;
    private String mMailList[] = {"z800201002005@gmail.com", "test1@gmail.com"};

    private TextView mAddCaneTextView;
    private TextView mNotifySettingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cane_setting);

        mSettingManager = new SettingManager(mContext);

        mFreqCurrent = findViewById(R.id.textViewFrequencyValue);
        mStepCurrent = findViewById(R.id.textViewStepValue);
        mLowBatteryCurrent = findViewById(R.id.textViewLowBatteryValue);

        mFreqMin = findViewById(R.id.textViewFrequencyMin);
        mStepMin = findViewById(R.id.textViewStepMin);
        mLowBatteryMin = findViewById(R.id.textViewLowBatteryMin);

        mFreqMax = findViewById(R.id.textViewFrequencyMax);
        mStepMax = findViewById(R.id.textViewStepMax);
        mLowBatteryMax = findViewById(R.id.textViewLowBatteryMax);

        mFreqMin.setText(String.valueOf(mFreqArray[0]));
        mStepMin.setText(String.valueOf(mStepArray[0]));
        mLowBatteryMin.setText(String.valueOf(mLowBatteryArray[mLowBatteryArray.length-1]));

        mFreqMax.setText(String.valueOf(mFreqArray[mFreqArray.length-1]));
        mStepMax.setText(String.valueOf(mStepArray[mStepArray.length-1]));
        mLowBatteryMax.setText(String.valueOf(mLowBatteryArray[0]));

        mFreqSeekBar = findViewById(R.id.seekBarFrequency);
        mFreqSeekBar.setMax(mFreqArray.length-1);
        mFreqSeekBar.setProgress(Integer.valueOf(SettingManager.sFreqIndex));
        setFreqCurrent(Integer.valueOf(SettingManager.sFreqIndex));
        mFreqSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: progress = " + progress);
                String nowValueString = "(" + mFreqArray[progress] + " min.)";
                mFreqCurrent.setText(nowValueString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStartTrackingTouch: progress = " + seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStopTrackingTouch: progress = " + seekBar.getProgress());

                mParaNames = NAME_EDIT_PARAMETERS_SET_FREQ;
                mParaValue = String.valueOf(mFreqArray[seekBar.getProgress()]);
                mParaIndex = String.valueOf(seekBar.getProgress());
                mHttpThread = new Thread(mHttpRunnable);
                mHttpThread.start();
            }
        });

        mStepSeekBar = findViewById(R.id.seekBarStep);
        mStepSeekBar.setMax(mStepArray.length-1);
        mStepSeekBar.setProgress(Integer.valueOf(SettingManager.sStepIndex));
        setStepCurrent(Integer.valueOf(SettingManager.sStepIndex));
        mStepSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: progress = " + progress);
                String nowValueString;
                if(progress == 0){
                    nowValueString = "(" + mStepArray[progress] + " step)";
                }
                else{
                    nowValueString = "(" + mStepArray[progress] + " steps)";
                }
                mStepCurrent.setText(nowValueString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStartTrackingTouch: progress = " + seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStopTrackingTouch: progress = " + seekBar.getProgress());

                mParaNames = NAME_EDIT_PARAMETERS_SET_STEP;
                mParaValue = String.valueOf(mStepArray[seekBar.getProgress()]);
                mParaIndex = String.valueOf(seekBar.getProgress());
                mHttpThread = new Thread(mHttpRunnable);
                mHttpThread.start();
            }
        });

        mLowBatterySeekBar = findViewById(R.id.seekBarLowBattery);
        mLowBatterySeekBar.setMax(mLowBatteryArray.length-1);
        mLowBatterySeekBar.setProgress(Integer.valueOf(SettingManager.sLowBatteryIndex));
        setLowBetteryCurrent(Integer.valueOf(SettingManager.sLowBatteryIndex));
        mLowBatterySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: progress = " + progress);
                String nowValueString = "(" + mLowBatteryArray[progress] + " %)";
                mLowBatteryCurrent.setText(nowValueString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStartTrackingTouch: progress = " + seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStopTrackingTouch: progress = " + seekBar.getProgress());

                mParaNames = NAME_EDIT_PARAMETERS_LOW_BATTERY_ALERT;
                mParaValue = String.valueOf(mLowBatteryArray[seekBar.getProgress()]);
                mParaIndex = String.valueOf(seekBar.getProgress());
                mHttpThread = new Thread(mHttpRunnable);
                mHttpThread.start();
            }
        });

        mHttpRunnable = new Runnable(){
            @Override
            public void run() {
                if(mParaNames != null && mParaValue != null && mParaIndex != null){
                    Log.i(TAG, "Set " + mParaNames + " = " + mParaValue);
                    String url = URL_EDIT_PARAMETERS;
                    ArrayList<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair(NAME_EDIT_PARAMETERS_USER_ID, SettingManager.sUserId));
                    params.add(new BasicNameValuePair(NAME_EDIT_PARAMETERS_CANE_UID, SettingManager.sCaneId));
                    params.add(new BasicNameValuePair(mParaNames, mParaValue));
                    String sessionData[] = mSettingManager.readSessionData();
                    if(sessionData == null){
                        Log.e(TAG, "sessionData is null!");
                    }
                    String return_data = DBConnector.executeQuery(params, url, sessionData[0], sessionData[1]);
                    Log.i(TAG, "return_data = " + return_data);
                    final int settingResult = Character.getNumericValue(return_data.charAt(0));
                    Log.i(TAG, "settingResult = " + settingResult);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(settingResult == RESULT_SETTING_FAIL){
                                Utility.makeTextAndShow(mContext, "錯誤：設定失敗", 2);

                                setFreqCurrent(Integer.valueOf(SettingManager.sFreqIndex));
                                mFreqSeekBar.setProgress(Integer.valueOf(SettingManager.sFreqIndex));

                                setStepCurrent(Integer.valueOf(SettingManager.sStepIndex));
                                mStepSeekBar.setProgress(Integer.valueOf(SettingManager.sStepIndex));

                                setLowBetteryCurrent(Integer.valueOf(SettingManager.sLowBatteryIndex));
                                mLowBatterySeekBar.setProgress(Integer.valueOf(SettingManager.sLowBatteryIndex));
                            }
                            else{
                                if(mParaNames == NAME_EDIT_PARAMETERS_SET_FREQ){
                                    mSettingManager.writeData(SHAREPREFERENCES_FIELD_FREQ_INDEX, mParaIndex);
                                }
                                else if(mParaNames == NAME_EDIT_PARAMETERS_SET_STEP){
                                    mSettingManager.writeData(SHAREPREFERENCES_FIELD_STEP_INDEX, mParaIndex);
                                }
                                else if(mParaNames == NAME_EDIT_PARAMETERS_LOW_BATTERY_ALERT){
                                    mSettingManager.writeData(SHAREPREFERENCES_FIELD_LOW_BATTERY_INDEX, mParaIndex);
                                }
                            }

                            mParaNames = mParaValue = mParaIndex = null;
                        }
                    });
                }
                else{
                    Log.e(TAG, "No parameters need to be set");
                }
            }
        };

        mAddCaneTextView = findViewById(R.id.textViewAddCane);
        mAddCaneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddCaneActivity.class);
                startActivityForResult(intent, ACTIVITY_ADD_CANE);
            }
        });

        mPhoneListSpinner = findViewById(R.id.spinnerPhoneList);
        ArrayAdapter<String> phoneList = new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                mPhoneList);
        mPhoneListSpinner.setAdapter(phoneList);

        mMailListSpinner = findViewById(R.id.spinnerMailList);
        ArrayAdapter<String> mailList = new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                mMailList);
        mMailListSpinner.setAdapter(mailList);
    }

    private void setFreqCurrent(int index){
        String nowValueString = "(" + mFreqArray[index] + " min.)";
        mFreqCurrent.setText(nowValueString);
    }

    private void setStepCurrent(int index){
        String nowValueString;
        if(index == 0){
            nowValueString = "(" + mStepArray[index] + " step)";
        }
        else{
            nowValueString = "(" + mStepArray[index] + " steps)";
        }
        mStepCurrent.setText(nowValueString);
    }

    private void setLowBetteryCurrent(int index){
        String nowValueString = "(" + mLowBatteryArray[index] + " %)";
        mLowBatteryCurrent.setText(nowValueString);
    }
}
