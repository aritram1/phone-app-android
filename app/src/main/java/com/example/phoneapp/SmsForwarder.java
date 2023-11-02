package com.example.phoneapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import androidx.appcompat.app.WindowDecorActionBar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SmsForwarder extends BroadcastReceiver {
    private Handler handler;

    @Override
    public void onReceive(Context context, Intent intent) {
        for (SmsMessage sms : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            String sender = sms.getDisplayOriginatingAddress();
            String message = sms.getDisplayMessageBody();
            Toast.makeText(context, "Received SMS from " + sender + ": " + message, Toast.LENGTH_LONG).show();

            // Do a call-out and receive the data
            CallOutHandler.ResponseWrapper rw = new CallOutHandler().doCallOut();

            // Create another intent to send the data to the main activity
            Intent intent2 = new Intent("updateViewIntent");
            intent2.putExtra("data", rw.status + " || " + rw.message);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
        }
    }
}
