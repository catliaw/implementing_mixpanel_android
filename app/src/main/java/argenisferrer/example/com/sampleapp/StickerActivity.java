package argenisferrer.example.com.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

public class StickerActivity extends AppCompatActivity {

    ImageView mImageView;
    MixpanelAPI mixpanel;
    private JSONObject mCurrentProps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);
        mixpanel = MixpanelAPI.getInstance(this, MainActivity.mpToken);

        Intent intent = getIntent();

        if (intent.getBooleanExtra("justSignedUp", false)) {
            Log.d("StickerActivity Debug", "justSignedUp - set people properties and Signed up event.");
            // changes the ID in local storage for events
            mixpanel.identify(intent.getStringExtra("email"));
            // changes the ID in local storage for people updates and lets people updates flush
            mixpanel.getPeople().identify(intent.getStringExtra("email"));
            mixpanel.getPeople().set("$email", intent.getStringExtra("email"));
            mixpanel.getPeople().set("$name", intent.getStringExtra("name"));
            mixpanel.getPeople().set("$created", intent.getStringExtra("created"));
            mixpanel.getPeople().set("Views", 0);
            mixpanel.getPeople().set("Shares", 0);

            JSONObject props = new JSONObject();
            try {
                props.put("Views", 0);
                props.put("Shares", 0);
            } catch (JSONException e) {
                Log.i("StickerActivity Test", "Views/Shares Super Property Error"+e.getMessage());
            }
            mixpanel.registerSuperProperties(props);
            mixpanel.track("Signed up");
        }

        mCurrentProps = mixpanel.getSuperProperties();

        mImageView = (ImageView) findViewById(R.id.mainImageView);
        mImageView.setImageResource(intent.getIntExtra("stickerId", 0));

        if (mCurrentProps.has("Views")) {
            try {
                int i = mCurrentProps.getInt("Views");
                i +=1;
                mCurrentProps.put("Views", i);
                mixpanel.registerSuperProperties(mCurrentProps);
            } catch (JSONException e) {
                Log.i("StickerActivity Test", "Views event property error"+e.getMessage());
            }
        }
        mixpanel.track("Sticker viewed");
        mixpanel.getPeople().increment("Views", 1);
        Log.d("StickerActivity Debug", "Sticker viewed.");


        Button btnShare = (Button) findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentProps.has("Shares")) {
                    try {
                        int i = mCurrentProps.getInt("Shares");
                        i +=1;
                        mCurrentProps.put("Shares", i);
                        mixpanel.registerSuperProperties(mCurrentProps);
                    } catch (JSONException e) {
                        Log.i("StickerActivity Test", "Shares event property error"+e.getMessage());
                    }
                }
                mixpanel.track("Sticker shared");
                mixpanel.getPeople().increment("Shares", 1);
                Log.d("StickerActivity Debug", "Sticker shared.");
            }
        });
    }
}
