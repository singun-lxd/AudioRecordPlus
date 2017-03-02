package com.singun.audiorecordplus;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.singun.system.permission.PermissionRequest;

public class MainActivity extends AppCompatActivity implements PermissionRequest.PermissionRequestListener {
    private static final int PERMISSION_REQUEST_CODE = 100;

    private PermissionRequest mPermissionRequest;
    private AudioRecordPlayerPlus mAudioRecordPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRecord();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPermissionRequest.checkPermission();
            }
        });

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText("Hello World");
    }

    private void initRecord() {
        mPermissionRequest = new PermissionRequest(this,
                new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                PERMISSION_REQUEST_CODE);
        mPermissionRequest.setPermissionRequestListener(this);
        mAudioRecordPlayer = new AudioRecordPlayerPlus(this, getWindow());
        mAudioRecordPlayer.setSpeakerOn(true);
        mAudioRecordPlayer.setNoiseProcessEnabled(false);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        mPermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        mAudioRecordPlayer.release();
        mAudioRecordPlayer = null;
        mPermissionRequest = null;
    }

    @Override
    public void onPermissionAllGranted() {
        startOrStopRecording();
    }

    @Override
    public void onPermissionAllDenied() {

    }

    @Override
    public void onPermissionPartDenied() {
        Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionGranted(String permission) {

    }

    @Override
    public void onPermissionDenied(String permission) {

    }

    private void startOrStopRecording() {
        if (mAudioRecordPlayer.isWorking()) {
            mAudioRecordPlayer.stop();
        } else {
            mAudioRecordPlayer.startWorking();
        }
    }
}
