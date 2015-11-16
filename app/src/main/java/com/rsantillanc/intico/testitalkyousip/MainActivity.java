package com.rsantillanc.intico.testitalkyousip;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rsantillanc.intico.testitalkyousip.receivers.CallStateReceiver;
import com.rsantillanc.intico.testitalkyousip.receivers.Constants;
import com.rsantillanc.intico.testitalkyousip.receivers.RegistrationBroadcastReceiver;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnUriUtils;

public class MainActivity extends AppCompatActivity {

    private NgnEngine mEngine;
    private INgnSipService mSipService;
    private RegistrationBroadcastReceiver regBroadcastReceiver;
    private CallStateReceiver callStateReceiver;
    private Button btAnnex;
    private Button btPhone;
    private Button btCancel;
    private NgnAVSession avSession;
    private EditText etPhoneToCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAnnex = (Button) findViewById(R.id.bt_annex);
        btPhone = (Button) findViewById(R.id.bt_phone);
        btCancel = (Button) findViewById(R.id.bt_cancel);
        etPhoneToCall = (EditText) findViewById(R.id.et_phone_to_call);


        btAnnex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String annex = etPhoneToCall.getText().toString();
                makeVoiceCall("9916" + Constants.SIP_USERNAME + "00003" + annex);
            }
        });

        btPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String annex = etPhoneToCall.getText().toString();
                makeVoiceCall("9916" + Constants.SIP_USERNAME + "00001" + annex);
            }
        });


        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avSession.hangUpCall();
            }
        });


        // Get engines
        mEngine = NgnEngine.getInstance();
        mSipService = mEngine.getSipService();

        // Register broadcast receivers
        regBroadcastReceiver = new RegistrationBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        registerReceiver(regBroadcastReceiver, intentFilter);

        // Incoming call broadcast receiver
        final IntentFilter intentRFilter = new IntentFilter();
        callStateReceiver = new CallStateReceiver();
        intentRFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        registerReceiver(callStateReceiver, intentRFilter);


        INgnConfigurationService mConfigurationService = mEngine.getConfigurationService();

        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, Constants.SIP_USERNAME);
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s",  Constants.SIP_USERNAME, Constants.SIP_DOMAIN));
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, Constants.SIP_PASSWORD);
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, Constants.SIP_DOMAIN);
        mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, 5060);
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, Constants.SIP_DOMAIN);

        // By default, using 3G for calls disabled
        mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G, true);
        // You may want to leave the registration timeout to the default 1700 seconds
        mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT, 3600);
        mConfigurationService.commit();

        initializeManager();

    }

    public void initializeManager() {
        if (!mEngine.isStarted()) {
            if (!mEngine.start()) {
                Log.e("INTICO", "Failed to start the engine :(");
                return;
            }
        }

        // Register
        if (!mSipService.isRegistered()) {
            mSipService.register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mEngine.isStarted()) {
            mEngine.stop();
            Log.e("DEBUG", "onDestroy mEngine.stop()");
        }

        if (mSipService.isRegistered()) {
            mSipService.unRegister();
            Log.e("DEBUG", "onDestroy  mSipService.unRegister()");
        }
    }


    boolean makeVoiceCall(String phoneNumber) {
        final String validUri = NgnUriUtils.makeValidSipUri(String.format("sip:%s@%s", phoneNumber, Constants.SIP_DOMAIN));
        if (validUri == null) {
            Log.e("DEBUG", "failed to normalize sip uri '" + phoneNumber + "'");
            return false;
        } else
            Log.e("DEBUG", "validUri: " + validUri + "'");


        avSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);

//        Intent i = new Intent();
//        i.setClass(this, CallScreen.class);
//        i.putExtra(EXTRAT_SIP_SESSION_ID, avSession.getId());
//        startActivity(i);

        return avSession.makeCall(validUri);
    }
}


