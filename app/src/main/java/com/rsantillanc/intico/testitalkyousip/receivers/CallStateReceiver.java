package com.rsantillanc.intico.testitalkyousip.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rsantillanc.intico.testitalkyousip.ReceiveCallActivity;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;

/**
 * Created by rsantillanc on 13/11/2015.
 */
public class CallStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        if(NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)){
            NgnInviteEventArgs args =
                    intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);

            if(args == null){
                Log.d("DEBUG", "Invalid event args");
                return;
            }

            NgnAVSession avSession
                    = NgnAVSession.getSession(args.getSessionId());
            if (avSession == null) {
                return;
            }


            final NgnInviteSession.InviteState callState = avSession.getState();
            NgnEngine mEngine = NgnEngine.getInstance();

            switch(callState){
                case NONE:
                default:
                    break;
                case INCOMING:
                    Log.i("DEBUG", "Incoming call");
                    Intent i = new Intent(context.getApplicationContext(),
                            ReceiveCallActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(Constants.SIP_SESSION_ID, avSession.getId());
                    i.putExtra(Constants.PHONE_NUMBER_EXTRA,
                            avSession.getRemotePartyDisplayName());
                    context.startActivity(i);
                    // Ring
                    mEngine.getSoundService().startRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    break;
                case INCALL:
                    Log.i("DEBUG", "Call connected");
                    mEngine.getSoundService().stopRingTone();
                    break;
                case TERMINATED:
                    Log.i("DEBUG", "Call terminated");
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    break;
            }

        }




    }
}
