package tw.org.edo.gpssmartcane;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import static tw.org.edo.gpssmartcane.Constant.NAME_LOGIN_EMAIL;
import static tw.org.edo.gpssmartcane.Constant.NAME_LOGIN_PASSWORDL;
import static tw.org.edo.gpssmartcane.Constant.RESULT_LOGIN_FAIL;
import static tw.org.edo.gpssmartcane.Constant.RESULT_LOGIN_SUCCESS;
import static tw.org.edo.gpssmartcane.Constant.RETURN_VALUE_LOGIN;
import static tw.org.edo.gpssmartcane.Constant.URL_LOGIN;

public class LoginActivity extends AppCompatActivity {
    final private  String TAG = this.getClass().getSimpleName();


    private Context mContext = this;
    private TextView mSignUpTextView;
    private EditText mUserNameEditText, mPasswordEditText;
    String mUserName, mPassword;
    private Button mLoginButton;
    private Button.OnClickListener mLoginButtonListen;

    private Runnable mHttpRunnable;
    private Thread mHttpThread;

    private int mLoginResult = RESULT_LOGIN_FAIL;
    private String mReturnData;
    private Intent mIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setResult(mLoginResult);

        mSignUpTextView = findViewById(R.id.textViewSignUp);
        mLoginButton = findViewById(R.id.buttonLogin);
        mLoginButtonListen =  new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserName = mUserNameEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                if(mUserName.length() > 0 && mPassword.length() > 0){
                    Log.i(TAG, "mUserName = " + mUserName + ", mPassword = " + mPassword);
                    mHttpRunnable = new Runnable(){
                        @Override
                        public void run() {
                            String url = URL_LOGIN;
                            ArrayList<NameValuePair> params = new ArrayList<>();
                            params.add(new BasicNameValuePair(NAME_LOGIN_EMAIL, mUserName));
                            params.add(new BasicNameValuePair(NAME_LOGIN_PASSWORDL, mPassword));
                            String return_data = DBConnector.executeQuery(params, url);
                            Log.i(TAG, "return_data = " + return_data);
                            mLoginResult = Character.getNumericValue(return_data.charAt(0));
                            Log.i("TAG", "mLoginResult = " + mLoginResult);

                            if(mLoginResult != RESULT_LOGIN_FAIL){
                                mLoginResult = RESULT_LOGIN_SUCCESS;
                                mReturnData = return_data;
                                mIntent.putExtra(RETURN_VALUE_LOGIN, mReturnData);
                                setResult(mLoginResult, mIntent);
                                finish();
                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utility.makeTextAndShow(mContext, "登入失敗", 2);
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

        mUserNameEditText = findViewById(R.id.editTextUserName);
        mPasswordEditText = findViewById(R.id.editTextPassword);

        // Only for develop
        mUserNameEditText.setText("edomsn@gmail.com");
        mPasswordEditText.setText("z");

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SignUpActivity.class);
                startActivity(intent);
            }
        };

        mSignUpTextView.setOnClickListener(listener);
        mLoginButton.setOnClickListener(mLoginButtonListen);
    }
}
