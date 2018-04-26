package tw.org.edo.gpssmartcane;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class CaneSettingActivity extends AppCompatActivity {
    final private  String TAG = this.getClass().getSimpleName();

    private Context mContext = this;
    private SettingManager mSettingManager;

    private int[] mFreqArray = {1, 3, 5, 10, 15, 20, 30};
    private int[] mStepArray = {1, 3, 5, 10 ,20 ,30};
    private int[] mLowBatteryArray = {50, 40, 30 ,20 ,10};

    private SeekBar mFreqSeekBar, mStepSeekBar, mLowBatterySeekBar;

    private TextView mFreqCurrent, mStepCurrent, mLowBatteryCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cane_setting);

        mSettingManager = new SettingManager(mContext);

        mFreqCurrent = findViewById(R.id.textViewFrequencyValue);
        mStepCurrent = findViewById(R.id.textViewStepValue);
        mLowBatteryCurrent = findViewById(R.id.textViewLowBatteryValue);

        mFreqSeekBar = findViewById(R.id.seekBarFrequency);
        mFreqSeekBar.setMax(mFreqArray.length-1);
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
            }
        });

        mStepSeekBar = findViewById(R.id.seekBarStep);
        mStepSeekBar.setMax(mStepArray.length-1);
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
            }
        });

        mLowBatterySeekBar = findViewById(R.id.seekBarLowBattery);
        mLowBatterySeekBar.setMax(mLowBatteryArray.length-1);
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
            }
        });


        setFreqCurrent(Integer.valueOf(SettingManager.sFreqIndex));
        setStepCurrent(Integer.valueOf(SettingManager.sStepIndex));
        setLowBetteryCurrent(Integer.valueOf(SettingManager.sStepIndex));
    }

    private void setFreqCurrent(int index){
        mFreqSeekBar.setProgress(index);
        String nowValueString = "(" + mFreqArray[index] + " min.)";
        mFreqCurrent.setText(nowValueString);
    }

    private void setStepCurrent(int index){
        mFreqSeekBar.setProgress(index);
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
        mLowBatterySeekBar.setProgress(index);
        String nowValueString = "(" + mLowBatteryArray[index] + " %)";
        mLowBatteryCurrent.setText(nowValueString);
    }
}
