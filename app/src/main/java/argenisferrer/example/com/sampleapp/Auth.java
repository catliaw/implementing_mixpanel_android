package argenisferrer.example.com.sampleapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Auth extends AppCompatActivity {

    private MixpanelAPI mixpanel;
    private Button mButtonEnter;
    private Context mContext;
    private EditText mEmailInput;
    private EditText mNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mixpanel = MixpanelAPI.getInstance(this, MainActivity.mpToken);

        mButtonEnter = (Button) findViewById(R.id.btnEnter);
        mEmailInput = (EditText) findViewById(R.id.emailInput);
        mNameInput = (EditText) findViewById(R.id.nameInput);

        mButtonEnter.setOnClickListener((v -> {
            if (mEmailInput.getText().toString().trim().length() > 0 && mNameInput.getText().toString().trim().length() > 0) {
                if (isValidEmail(mEmailInput.getText().toString().trim())) {
                    Log.d("Auth Debug", "Valid email.");
                    String email = mEmailInput.getText().toString().trim();

                    // all checked, let's log in and proceed
                    JSONObject props = new JSONObject();
                    try {
                        props.put("loggedIn", true);
                    } catch (JSONException e) {
                        Log.i("Auth Test", "loggedIn Error"+e.getMessage());
                    }
                    // register super properties
                    Log.d("Auth Debug", "Sign up - Set as loggedIn.");
                    mixpanel.registerSuperProperties(props);
                    // create the alias
                    Log.d("Auth Debug", "Sign up - Alias email to distinct_id");
                    mixpanel.alias(email, mixpanel.getDistinctId());
                    // mixpanel.track("Signed up");

                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                    df.setTimeZone(tz);
                    String nowAsISO = df.format(new Date());

                    Sticker sticker = new Sticker(true);
                    Intent intent = new Intent(this, StickerActivity.class);
                    intent.putExtra("stickerId", sticker.getSticketId());
                    intent.putExtra("email", email);
                    intent.putExtra("name", mNameInput.getText().toString().trim());
                    intent.putExtra("created", nowAsISO);
                    intent.putExtra("justSignedUp", true);
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("Auth Debug", "User entered invalid email.");
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("Auth Debug", "User entered invalid email and name.");
                Toast.makeText(this, "You need to input your email and name", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public final static boolean isValidEmail(String target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    };
}
