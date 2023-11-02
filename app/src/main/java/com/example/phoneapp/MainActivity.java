package com.example.phoneapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // private variable to track the static/singleton version of the sms forwarder instance
    private SmsForwarder smsf;

    // Standard On Create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // registerTheService();

        // The button toggles the registration and deregistration of the service
        Button b = findViewById(R.id.button);
        b.setOnClickListener(v -> {
            if(this.smsf == null) registerTheService();
            else unregisterTheService();
        });
    }

    // In case permission is not provided ask for the permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            registerTheService();
        }
    }

    // Simple method to register the SMS forwarder service
    private void registerTheService() {
        try {
            this.smsf = new SmsForwarder();
            IntentFilter smsReceivedIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(smsf, smsReceivedIntentFilter);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Received Error while registering : " + e.getStackTrace(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterTheService();
    }

    // Simple method to unregister the SMS forwarder service
    private void unregisterTheService() {
        try {
            unregisterReceiver(this.smsf);

            // Also make the singleton variable null, so it can be rec-created next time
            this.smsf = null;
        } catch (Exception e) {
            Toast.makeText(this, "Received Error while unregistering: " + e.getStackTrace(), Toast.LENGTH_LONG).show();
        }
    }


    // This broadcaster service is listening to the any change with the intent name => updateViewIntent
    // This intent emits when the sms forwarder service tries to send data back to this main activity
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("updateViewIntent".equals(intent.getAction())) {
                String data = intent.getStringExtra("data");
                TextView textView = findViewById(R.id.textView);
                textView.setText(data);
            }
        }
    };

    // The standard Start and stop methods to ensure proper closure
    // happens and no memory-leak happens
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("updateViewIntent");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

}

