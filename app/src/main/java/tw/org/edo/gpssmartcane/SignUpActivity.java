package tw.org.edo.gpssmartcane;

import android.content.Context;
import android.content.Intent;
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

import static tw.org.edo.gpssmartcane.Constant.NAME_REGISTER_EMAIL;
import static tw.org.edo.gpssmartcane.Constant.NAME_REGISTER_PASSWORD;
import static tw.org.edo.gpssmartcane.Constant.RESULT_REGISTER_FAIL_EXISTS;
import static tw.org.edo.gpssmartcane.Constant.RESULT_REGISTER_FAIL_UNKNOWN;
import static tw.org.edo.gpssmartcane.Constant.RESULT_REGISTER_SUCCESS;
import static tw.org.edo.gpssmartcane.Constant.RETURN_VALUE_LOGIN;
import static tw.org.edo.gpssmartcane.Constant.RETURN_VALUE_REGISTER_EMAIL;
import static tw.org.edo.gpssmartcane.Constant.RETURN_VALUE_REGISTER_PASSWORD;
import static tw.org.edo.gpssmartcane.Constant.URL_REGISTER;

public class SignUpActivity extends AppCompatActivity {
    final private  String TAG = this.getClass().getSimpleName();

    private Context mContext = this;
    private EditText mUserNameEditText, mPasswordEditText,
            mPhoneEditText, mCaneIdEdidText;
    private String mUserName, mPassword;

    private TextView mPhoneTextView, mCaneIdTextView;

    private Button mSignUpButton;
    private Button.OnClickListener mSignUpButtonListen;

    private Runnable mHttpRunnable;
    private Thread mHttpThread;
    private int mRegisterResult = RESULT_REGISTER_FAIL_UNKNOWN;

    private SettingManager mSettingManager;
    private Intent mIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setResult(mRegisterResult);

        mSettingManager = new SettingManager(mContext);

        mUserNameEditText = findViewById(R.id.editTextUserName);
        mPasswordEditText = findViewById(R.id.editTextPassword);
        mPhoneEditText = findViewById(R.id.editTextPhone);
        mCaneIdEdidText = findViewById(R.id.editTextCaneId);

        // Only for develop
        mUserNameEditText.setText("test@gmail.com.1");
        mPasswordEditText.setText("z");

        mPhoneTextView = findViewById(R.id.textViewPhone);
        mCaneIdTextView = findViewById(R.id.textViewCaneId);

        mPhoneEditText.setVisibility(View.GONE);
        mCaneIdEdidText.setVisibility(View.GONE);
        mPhoneTextView.setVisibility(View.GONE);
        mCaneIdTextView.setVisibility(View.GONE);

        mSignUpButton = findViewById(R.id.buttonSignUp);
        mSignUpButtonListen =  new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                mUserName = mUserNameEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                if(mUserName.length() > 0 && mPassword.length() > 0){
                    Log.i(TAG, "mUserName = " + mUserName + ", mPassword = " + mPassword);
                    mHttpRunnable = new Runnable(){
                        @Override
                        public void run() {
                            String url = URL_REGISTER;
                            ArrayList<NameValuePair> params = new ArrayList<>();
                            params.add(new BasicNameValuePair(NAME_REGISTER_EMAIL, mUserName));
                            params.add(new BasicNameValuePair(NAME_REGISTER_PASSWORD, mPassword));
                            String return_data = DBConnector.executeQuery(params, url);
                            Log.i(TAG, "return_data = " + return_data);
                            mRegisterResult = Character.getNumericValue(return_data.charAt(0));
                            Log.i(TAG, "mRegisterResult = " + mRegisterResult);

                            if(mRegisterResult != RESULT_REGISTER_SUCCESS){
                                mRegisterResult = mRegisterResult * Character.getNumericValue(return_data.charAt(1));
                            }

                            if(mRegisterResult == RESULT_REGISTER_SUCCESS){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utility.makeTextAndShow(mContext, "註冊成功", 2);
                                    }
                                });
                                mIntent.putExtra(RETURN_VALUE_REGISTER_EMAIL, mUserName);
                                mIntent.putExtra(RETURN_VALUE_REGISTER_PASSWORD, mPassword);
                                setResult(mRegisterResult, mIntent);
                                finish();
                            }
                            else if(mRegisterResult == RESULT_REGISTER_FAIL_EXISTS){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utility.makeTextAndShow(mContext, "註冊失敗：帳號已存在", 2);
                                    }
                                });
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utility.makeTextAndShow(mContext, "註冊失敗：請洽系統管理員", 2);
                                    }
                                });
                            }
                        }
                    };
                    mHttpThread = new Thread(mHttpRunnable);
                    mHttpThread.start();
                }
                else{
                    Utility.makeTextAndShow(mContext, "使用者名稱或密碼不可空白", 2);
                }
            }
        };
        mSignUpButton.setOnClickListener(mSignUpButtonListen);
    }
}
