package tw.org.edo.gpssmartcane;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import static tw.org.edo.gpssmartcane.Constant.NAME_BIND_CANE_CONFIRM_CODE;
import static tw.org.edo.gpssmartcane.Constant.NAME_BIND_CANE_CONFIRM_UID;
import static tw.org.edo.gpssmartcane.Constant.NAME_BIND_CANE_CONFIRM_USER_ID;
import static tw.org.edo.gpssmartcane.Constant.NAME_BIND_CANE_EMERGY_CALL;
import static tw.org.edo.gpssmartcane.Constant.NAME_BIND_CANE_EMERGY_MAIL;
import static tw.org.edo.gpssmartcane.Constant.NAME_BIND_CANE_UID;
import static tw.org.edo.gpssmartcane.Constant.RESULT_BINDING_FAIL;
import static tw.org.edo.gpssmartcane.Constant.RESULT_BINDING_OK;
import static tw.org.edo.gpssmartcane.Constant.RESULT_BINDING_WAIT_CONFIRM_CODE;
import static tw.org.edo.gpssmartcane.Constant.RESULT_BINDING_WAIT_CONFIRM_CODE_ERROR;
import static tw.org.edo.gpssmartcane.Constant.URL_BINDING_CANE;
import static tw.org.edo.gpssmartcane.Constant.URL_BINDING_CANE_CHECK;

public class BindingActivity extends AppCompatActivity {
    final private  String TAG = this.getClass().getSimpleName();
    private Context mContext = this;
    private SettingManager mSettingManager;

    private int mBindResult = RESULT_BINDING_FAIL;

    private TextView mConfirmTextView;

    private EditText mCaneUidEditText, mPhoneEditText, mConfirmEditText;
    private Button mBindingButton;
    private Button.OnClickListener mBindingButtonListen;
    private Button.OnClickListener mConfirmButtonListen;

    private String mCaneUid;
    private String mPhone;
    private String mConfirm;

    private Runnable mHttpRunnable;
    private Thread mHttpThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);

        setResult(mBindResult);

        mSettingManager = new SettingManager(mContext);

        mConfirmTextView = findViewById(R.id.textViewConfirm);

        mCaneUidEditText = findViewById(R.id.editTextCaneUid);
        mPhoneEditText = findViewById(R.id.editTextPhone);
        mConfirmEditText = findViewById(R.id.editTextConfirm);

        mBindingButton = findViewById(R.id.buttonBinding);
        mBindingButtonListen = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCaneUid = mCaneUidEditText.getText().toString();
                mPhone = mPhoneEditText.getText().toString();

                if(mCaneUid.length() > 0 && mPhone.length() > 0){
                    Log.i(TAG, "[onClick-binding] mCaneUid = " + mCaneUid + ", mPhone = " + mPhone + ", SettingManager.sEmaill = " + SettingManager.sEmaill);
                    mHttpRunnable = new Runnable(){
                        @Override
                        public void run() {
                            String url = URL_BINDING_CANE;
                            ArrayList<NameValuePair> params = new ArrayList<>();
                            params.add(new BasicNameValuePair(NAME_BIND_CANE_UID, mCaneUid));
                            params.add(new BasicNameValuePair(NAME_BIND_CANE_EMERGY_CALL, mPhone));
                            params.add(new BasicNameValuePair(NAME_BIND_CANE_EMERGY_MAIL, SettingManager.sEmaill));
                            String sessionData[] = mSettingManager.readSessionData();
                            if(sessionData == null){
                                Log.e(TAG, "[onClick-binding] sessionData is null!");
                            }
                            String return_data = DBConnector.executeQuery(params, url, sessionData[0], sessionData[1]);
                            Log.i(TAG, "return_data = " + return_data);
                            mBindResult = Character.getNumericValue(return_data.charAt(0));
                            Log.i(TAG, "[onClick-binding] mBindResult = " + mBindResult);
                        }
                    };
                    mHttpThread = new Thread(mHttpRunnable);
                    mHttpThread.start();

                    try {
                        mHttpThread.join();
                        if(mBindResult == RESULT_BINDING_OK){
                            Utility.makeTextAndShow(mContext, "請從簡訊讀取認證碼", 2);
                            mConfirmTextView.setVisibility(View.VISIBLE);
                            mConfirmEditText.setVisibility(View.VISIBLE);

                            mBindingButton.setOnClickListener(null);
                            mBindingButton.setOnClickListener(mConfirmButtonListen);
                            mBindingButton.setText("送出認證碼");

                            mBindResult = RESULT_BINDING_WAIT_CONFIRM_CODE;
                        }
                        else{

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else{
                    Utility.makeTextAndShow(mContext, "手杖序號或手機號碼不可空白", 2);
                }

            }
        };
        mBindingButton.setOnClickListener(mBindingButtonListen);

        mConfirmButtonListen = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConfirm = mConfirmEditText.getText().toString();

                if(mConfirm.length() > 0){
                    Log.i(TAG, "[onClick-confirm] mConfirm = " + mConfirm);
                    Log.i(TAG, "[onClick-confirm] SettingManager.sUserId = " + SettingManager.sUserId);
                    mHttpRunnable = new Runnable() {
                        @Override
                        public void run() {
                            String url = URL_BINDING_CANE_CHECK;
                            ArrayList<NameValuePair> params = new ArrayList<>();
                            params.add(new BasicNameValuePair(NAME_BIND_CANE_CONFIRM_UID, mCaneUid));
                            //params.add(new BasicNameValuePair(NAME_BIND_CANE_CONFIRM_USER_ID, SettingManager.sUserId));
                            params.add(new BasicNameValuePair(NAME_BIND_CANE_CONFIRM_CODE, mConfirm));
                            String sessionData[] = mSettingManager.readSessionData();
                            if(sessionData == null){
                                Log.e(TAG, "[onClick-binding] sessionData is null!");
                            }
                            String return_data = DBConnector.executeQuery(params, url, sessionData[0], sessionData[1]);
                            Log.i(TAG, "return_data = " + return_data);
                            mBindResult = Character.getNumericValue(return_data.charAt(0));
                            if(mBindResult == -1){
                                mBindResult = -1*Character.getNumericValue(return_data.charAt(1));
                            }
                            Log.i(TAG, "[onClick-binding] mBindResult = " + mBindResult);
                        }
                    };
                    mHttpThread = new Thread(mHttpRunnable);
                    mHttpThread.start();

                    try {
                        mHttpThread.join();
                        if(mBindResult == RESULT_BINDING_WAIT_CONFIRM_CODE_ERROR){
                            Utility.makeTextAndShow(mContext, "認證碼錯誤", 2);
                        }
                        else if(mBindResult == RESULT_BINDING_OK){
                            Log.i(TAG, "[onClick-binding] mBindResult == RESULT_BINDING_OK");
                            Utility.makeTextAndShow(mContext, "綁定成功", 2);
                            setResult(mBindResult);
                            finish();
                        }
                        else{
                            Log.i(TAG, "[onClick-binding] mBindResult = " + mBindResult);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Utility.makeTextAndShow(mContext, "認證碼不可空白", 2);
                }
            }
        };

        mCaneUidEditText.setText("A001");
        mPhoneEditText.setText("0955031053");

        mConfirmTextView.setVisibility(View.GONE);
        mConfirmEditText.setVisibility(View.GONE);
    }
}
