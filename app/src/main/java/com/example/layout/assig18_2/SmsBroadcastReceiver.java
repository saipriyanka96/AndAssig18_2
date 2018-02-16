package com.example.layout.assig18_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SmsBroadcastReceiver extends BroadcastReceiver {
//Base class for code that receives and handles broadcast intents sent by sendBroadcast(Intent).
    //declaration
    public static final String SMS_BUNDLE = "pdus";

    //Method onReceive()
    public void onReceive(Context context, Intent intent) {
        //This method is called when the BroadcastReceiver is receiving an Intent broadcast
        //Parameters context	Context: The Context in which the receiver is running.
       // intent	Intent: The Intent being received
        //creating bundle and getting info
        //A mapping from String keys to various Parcelable values.
        //Retrieves a map of extended data from the intent.
        //Returns Bundle	the map of all extras previously added with putExtra(), or null if none have been added.

        Bundle intentExtras = intent.getExtras();
        //Applying Condition
        if (intentExtras != null) {
            //if null is not equal to extras
            //object will get the sms bundle
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            //initializating Sms
            for (int i = 0; i < sms.length; ++i) {
                //Creating object of smsMessage
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                //Taking String
                //here we will see the message
                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();
                //here we will get the senders the number
                smsMessageStr += "senderNum:  " + address + ",\n";
                smsMessageStr += smsBody + "\n";
            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();//showing toast

            //this will update the UI with message
            MainActivity inst = MainActivity.instance();
            inst.updateList(smsMessageStr);
            //it will update the list
        }
    }
}