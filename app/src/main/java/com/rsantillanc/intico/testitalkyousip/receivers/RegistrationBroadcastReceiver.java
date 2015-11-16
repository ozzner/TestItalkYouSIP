package com.rsantillanc.intico.testitalkyousip.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;

/**
 * Created by rsantillanc on 13/11/2015.
 */
public class RegistrationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        // Registration Event
        if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
            NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
            if (args == null) {
                Log.d("INTICO", "Invalid event args");
                return;
            }

            switch (args.getEventType()) {
                case REGISTRATION_NOK:
                    Log.d("INTICO", "Failed to register :(");
                    break;
                case UNREGISTRATION_OK:
                    Log.d("INTICO", "You are now unregistered :)");
                    break;
                case REGISTRATION_OK:
                    Log.d("INTICO", "You are now registered :)");
                    break;
                case REGISTRATION_INPROGRESS:
                    Log.d("INTICO", "Trying to register...");
                    break;
                case UNREGISTRATION_INPROGRESS:
                    Log.d("INTICO", "Trying to unregister...");
                    break;
                case UNREGISTRATION_NOK:
                    Log.d("INTICO", "Failed to unregister :(");
                    break;
            }

        }
    }
}
