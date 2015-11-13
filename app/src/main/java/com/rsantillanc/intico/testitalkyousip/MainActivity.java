package com.rsantillanc.intico.testitalkyousip;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rsantillanc.intico.testitalkyousip.receivers.CallStateReceiver;
import com.rsantillanc.intico.testitalkyousip.receivers.RegistrationBroadcastReceiver;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

public class MainActivity extends AppCompatActivity {

    private NgnEngine mEngine;
    private INgnSipService mSipService;
    private RegistrationBroadcastReceiver regBroadcastReceiver;
    private CallStateReceiver callStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, "635651");
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", "635651", "sip.italkyou.com"));
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, "wAnObtxpGH5i4dP");
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, "sip.italkyou.com");
        mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, 5060);
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, "sip.italkyou.com");

    // By default, using 3G for calls disabled
        mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G, true);
    // You may want to leave the registration timeout to the default 1700 seconds
        mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT, 3600);
        mConfigurationService.commit();

        initializeManager();

    }

    public void initializeManager (){
        if(!mEngine.isStarted()){
            if(!mEngine.start()){
                Log.e("DEBUG", "Failed to start the engine :(");
                return;
            }
        }
        // Register
        if(!mSipService.isRegistered()){
            mSipService.register(this);
        }

    }
}


