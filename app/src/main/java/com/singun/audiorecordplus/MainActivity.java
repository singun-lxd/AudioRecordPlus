package com.singun.audiorecordplus;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.singun.media.audio.AudioConfig;
import com.singun.media.audio.AudioRecordPlayer;
import com.singun.system.permission.PermissionRequest;
import com.singun.ui.audio.visualizers.RendererFactory;
import com.singun.ui.audio.visualizers.WaveformView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements PermissionRequest.PermissionRequestListener,
        AudioRecordPlayer.AudioProcessListener {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int CAPTURE_SIZE = 256;

    private PermissionRequest mPermissionRequest;
    private AudioRecordPlayerPlus mAudioRecordPlayer;

    private WaveformView mWaveform;
    private Visualizer mVisualizer;
    private long mLastTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mWaveform = (WaveformView) findViewById(R.id.audio_waveform);
        RendererFactory rendererFactory = new RendererFactory();
        mWaveform.setRenderer(rendererFactory.createSimpleWaveformRender(ContextCompat.getColor(this, R.color.colorPrimary), Color.WHITE));

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
        mAudioRecordPlayer = new AudioRecordPlayerPlus(this, getWindow(), true);
        mAudioRecordPlayer.setSpeakerOn(true);
        mAudioRecordPlayer.setNoiseProcessEnabled(false);
//        mAudioRecordPlayer.setAudioProcessListener(this);
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
        mVisualizer.release();
        mAudioRecordPlayer = null;
        mPermissionRequest = null;
    }

    @Override
    public void onPermissionAllGranted() {
        startOrStopRecording();
        startVisualiser();
    }

    @Override
    public void onAudioDataProcessed(byte[] audioData) {
        if (mLastTime < System.currentTimeMillis() - 500) {
            mLastTime = System.currentTimeMillis();

            final byte[] displayAudio = audioData;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWaveform != null) {
                        mWaveform.setWaveform(displayAudio);
                    }
                }
            });
        }
    }

    // 设置音频线
    private void startVisualiser() {
        if (mVisualizer == null) {
            mVisualizer = new Visualizer(0); // 初始化
            mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                    if (mWaveform != null) {
                        mWaveform.setWaveform(waveform);
                    }
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

                }
            }, Visualizer.getMaxCaptureRate(), true, false);
            mVisualizer.setCaptureSize(CAPTURE_SIZE);
            mVisualizer.setEnabled(true);
        }
    }

    @Override
    public void onPermissionAllDenied() {

    }

    @Override
    public void onPermissionPartDenied() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_permission_denied)
                .setMessage(R.string.dialog_content_permission_denied)
                .setNegativeButton(R.string.dialog_button_cancel, null)
                .setPositiveButton(R.string.dialog_button_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPermissionRequest.checkPermission();
                    }
                }).show();
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
