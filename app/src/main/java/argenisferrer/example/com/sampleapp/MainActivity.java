package argenisferrer.example.com.sampleapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String mpToken = "c58484ea7b8a527309dffbcf728c3d1a";
    MixpanelAPI mixpanel;
    private JSONObject mCurrentProps;
    private Button mMainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity Debug", "Instantiating Mixpanel Android SDK.");
        mixpanel = MixpanelAPI.getInstance(this, mpToken);
        mixpanel.track("App launched");

        mMainButton = (Button)findViewById(R.id.btnMain);

        mMainButton.setOnClickListener(((View v) -> {
            Log.d("MainActivity Debug", "Main button clicked.");
            Boolean loggedIn = false;
            mCurrentProps = mixpanel.getSuperProperties();
            try {
                Log.d("MainActivity Debug", "Checking for user's loggedIn status.");
                if(mCurrentProps.has("loggedIn") && mCurrentProps.getBoolean("loggedIn")){
                    loggedIn = true;
                }else{
                    mCurrentProps.put("loggedIn", loggedIn);
                }
            } catch (JSONException e) {
                Log.i("MainActivity Test", "loggedIn Error"+e.getMessage());
            }
            Intent intent;
            if(loggedIn){
                Log.d("MainActivity Debug", "Setting the user to the correct Activity based on loggedIn state.");
                intent = new Intent(this, StickerActivity.class);
                Sticker mSticker = new Sticker(true);
                intent.putExtra("stickerId", mSticker.getSticketId());
            } else {
                intent = new Intent(this, Auth.class);
                mixpanel.track("Auth started");
            }
            startActivity(intent);
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // every time the app comes into from the background
        Log.d("MainActivity Debug", "App is loaded from memory to the foreground.");
        mixpanel.track("App is in the foreground");
        mixpanel.flush();
    }

    @Override
    protected void onPause(){
        super.onPause();
        // app going into the background
        Log.d("MainActivity Debug", "App is put into the background");
        mixpanel.track("App is in the background");
    }

    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }
}
