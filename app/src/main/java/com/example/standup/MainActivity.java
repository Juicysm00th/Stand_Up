package com.example.standup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.NoRouteToHostException;

public class MainActivity extends AppCompatActivity {
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notifaction_channel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToggleButton alarmToggle = findViewById(R.id.alarmToggle);
        Intent notifyIntent = new Intent(this, DetonationReceiver.class);
        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent,
                PendingIntent.FLAG_NO_CREATE) != null);
        alarmToggle.setChecked(alarmUp);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this,NOTIFICATION_ID,
                notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String toastMessage;
                if(isChecked) {
                    //long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                    long repeatInterval = 15000;
                    long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;
                    if(alarmManager != null) {
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime,
                                repeatInterval, notifyPendingIntent);
                    }
                    toastMessage = getString(R.string.detonActive);
                }else{
                    if(alarmManager != null){
                        alarmManager.cancel(notifyPendingIntent);
                    }
                    notificationManager.cancelAll();
                    toastMessage = getString(R.string.detonDeactive);
                }
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        createNotifactionChannel();
    }
    public void createNotifactionChannel(){
        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Stand up notification",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.YELLOW);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Notifies every 15 minutes to stand up and walk");
        }
        }

}