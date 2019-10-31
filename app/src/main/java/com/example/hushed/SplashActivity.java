package com.example.hushed;

import android.content.Intent;
import android.os.Handler;
import android.app.Activity;
import android.os.Bundle;

public class SplashActivity extends Activity {
    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mWaitHandler.postDelayed(() -> {
            try {

                //Go to next page i.e, start the next activity.
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);

                //Let's Finish Splash Activity since we don't want to show this when user press back button.
                finish();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }, 111);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Remove all the callbacks otherwise navigation will execute even after activity is killed or closed.
        mWaitHandler.removeCallbacksAndMessages(null);
    }
}
