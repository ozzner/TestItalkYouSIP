package com.rsantillanc.intico.testitalkyousip.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnUriUtils;

/**
 * Created by rsantillanc on 13/11/2015.
 */
public class OutgoingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String phoneNumber = getResultData();
        if (phoneNumber == null) {
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }

        setResultData(null);

        final String validUri = NgnUriUtils.makeValidSipUri(
                String.format("sip:%s@%s", phoneNumber, Constants.SIP_DOMAIN));
        if(validUri == null){
            Log.e("DEBUG", "Invalid number");
            return;
        }

        NgnAVSession avSession = NgnAVSession.createOutgoingSession(
                NgnEngine.getInstance().getSipService().getSipStack(), NgnMediaType.Audio);
        avSession.makeCall(validUri);
    }

}
