package com.android.noweaj.sensebotbattery;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;

import com.android.noweaj.sensebotbatterylibrary.SenseBotBatteryMeter;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    SenseBotBatteryMeter my_sbbm1, my_sbbm2, my_sbbm3;

    Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        my_sbbm1 = (SenseBotBatteryMeter) findViewById(R.id.sbbm1);
        my_sbbm1.setProductImage(R.drawable.handy);
        my_sbbm2 = (SenseBotBatteryMeter) findViewById(R.id.sbbm2);
        my_sbbm2.setProductImage(R.drawable.base);
        my_sbbm3 = (SenseBotBatteryMeter) findViewById(R.id.sbbm3);
        my_sbbm3.setProductImage(R.drawable.robot);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator anim1 = ObjectAnimator.ofFloat(my_sbbm1, "progress", 0, 23);
                        anim1.setInterpolator(new DecelerateInterpolator());
                        anim1.setDuration(1500);
                        anim1.start();

                        ObjectAnimator anim2 = ObjectAnimator.ofFloat(my_sbbm2, "progress", 0, 55);
                        anim2.setInterpolator(new DecelerateInterpolator());
                        anim2.setDuration(1500);
                        anim2.start();

                        ObjectAnimator anim3 = ObjectAnimator.ofFloat(my_sbbm3, "progress", 0, 85);
                        anim3.setInterpolator(new DecelerateInterpolator());
                        anim3.setDuration(1500);
                        anim3.start();
                    }
                });
            }
        }, 0, 2000);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mTimer.cancel();
    }
}
