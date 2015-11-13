package com.rsantillanc.intico.testitalkyousip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.rsantillanc.intico.testitalkyousip.receivers.Constants;

import org.doubango.ngn.sip.NgnAVSession;

public class ReceiveCallActivity extends AppCompatActivity {

    private NgnAVSession mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_call);


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            mSession = NgnAVSession.getSession(extras.getLong(Constants.SIP_SESSION_ID));
//             ContactsContract.CommonDataKinds.Phone number -> extras.getString(Constants.PHONE_NUMBER_EXTRA)
        }


        // Wake the screen and ignore "face touches"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES);



    }


}
