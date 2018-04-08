package tw.org.edo.gpssmartcane;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Context mContext = this;
    private TextView mSignUpTextView;
    private Button mLoginButton;
    private Button.OnClickListener mLoginButtonListen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignUpTextView = findViewById(R.id.textViewSignUp);
        mLoginButton = findViewById(R.id.buttonLogin);
        mLoginButtonListen =  new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        };

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
