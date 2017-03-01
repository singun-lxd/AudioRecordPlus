package com.singun.audiorecordplus;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.singun.media.audio.AudioConfig;
import com.singun.media.audio.AudioRecordPlayer;
import com.singun.system.permission.PermissionRequest;

import java.io.File;

public class MainActivity extends AppCompatActivity implements PermissionRequest.PermissionRequestListener {
    private static final int PERMISSION_REQUEST_CODE = 100;

    private PermissionRequest mPermissionRequest;
    private AudioRecordPlayer mAudioRecordPlayer;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPermissionRequest = new PermissionRequest(this,
                new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                PERMISSION_REQUEST_CODE);
        mPermissionRequest.setPermissionRequestListener(this);
        mAudioRecordPlayer = new AudioRecordPlayer(this, getWindow()) {
            @Override
            protected void updateAudioConfig(AudioConfig config) {
                config.audioDirPath = new File(Environment.getExternalStorageDirectory(), "record").getAbsolutePath();
                config.audioName = "testAudio";
            }
        };
        mAudioRecordPlayer.setSpeakerOn(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPermissionRequest.checkPermission();
            }
        });

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mPermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        mAudioRecordPlayer.release();
        mAudioRecordPlayer = null;
        mPermissionRequest = null;
    }

    @Override
    public void onPermissionGranted() {
        startOrStopRecording();
    }

    @Override
    public void onPermissionDenied() {

    }


    private void startOrStopRecording() {
        if (mAudioRecordPlayer.isWorking()) {
            mAudioRecordPlayer.stop();
        } else {
            mAudioRecordPlayer.startWorking();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
