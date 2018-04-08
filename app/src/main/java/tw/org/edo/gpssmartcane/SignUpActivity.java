package tw.org.edo.gpssmartcane;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends AppCompatActivity {

    private Button mSignUpButton;
    private Button.OnClickListener mSignUpButtonListen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignUpButton = findViewById(R.id.buttonSignUp);
        mSignUpButtonListen =  new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        };

        mSignUpButton.setOnClickListener(mSignUpButtonListen);
    }
}
