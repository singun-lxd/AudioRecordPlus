package com.singun.audiorecordplus;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.singun.media.audio.AudioConfig;
import com.singun.media.audio.AudioPlayer;
import com.singun.media.audio.AudioRecordPlayer;
import com.singun.system.permission.PermissionRequest;
import com.singun.ui.audio.visualizers.RendererFactory;
import com.singun.ui.audio.visualizers.WaveformView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements PermissionRequest.PermissionRequestListener,
        AudioRecordPlayer.AudioProcessListener, AudioPlayer.PlayStateListener {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int CAPTURE_SIZE = 256;

    private PermissionRequest mPermissionRequest;
    private AudioRecordPlayerPlus mAudioRecordPlayer;

    private SeekBar mVolumeBar;
    private FloatingActionButton mButtonAction;
    private Button mButtonPlay;
    private WaveformView mWaveform;
    private Visualizer mVisualizer;
    private CheckBox mCheckPlayRecord;
    private CheckBox mCheckNs;
    private CheckBox mCheckAgc;
    private CheckBox mCheckAec;
    private long mLastTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecord();
        initView();
        initConfigView();
    }

    private void initRecord() {
        mPermissionRequest = new PermissionRequest(this,
                new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                PERMISSION_REQUEST_CODE);
        mPermissionRequest.setPermissionRequestListener(this);
        mAudioRecordPlayer = new AudioRecordPlayerPlus(this, getWindow(), false);
        mAudioRecordPlayer.setSpeakerOn(true);
        mAudioRecordPlayer.setFilePlayStateListener(this);
//        mAudioRecordPlayer.setAudioProcessListener(this);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mWaveform = (WaveformView) findViewById(R.id.audio_waveform);
        RendererFactory rendererFactory = new RendererFactory();
        mWaveform.setRenderer(rendererFactory.createSimpleWaveformRender(ContextCompat.getColor(this, R.color.colorPrimary), Color.WHITE));

        mButtonAction = (FloatingActionButton) findViewById(R.id.fab);
        mButtonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPermissionRequest.checkPermission();
            }
        });
        mButtonPlay = (Button) findViewById(R.id.play);
        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrStopPlaying();
                updatePlayUi();
            }
        });

        mAudioRecordPlayer.setTrackVolume(0.8f);
        mVolumeBar = (SeekBar) findViewById(R.id.audio_volume);
        mVolumeBar.setMax(100);
        mVolumeBar.setProgress(80);
        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float vol = (float) (seekBar.getProgress()) / (float) (seekBar.getMax());
                mAudioRecordPlayer.setTrackVolume(vol);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }

    private void initConfigView() {
        mCheckPlayRecord = (CheckBox) findViewById(R.id.check_play_sound);
        mCheckPlayRecord.setChecked(mAudioRecordPlayer.isTrackEnabled());
        mCheckPlayRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioRecordPlayer.setTrackEnabled(mCheckPlayRecord.isChecked());
            }
        });

        mCheckNs = (CheckBox) findViewById(R.id.check_ns);
        mCheckNs.setChecked(mAudioRecordPlayer.isNoiseSuppressDefaultEnabled());
        mCheckNs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioRecordPlayer.setNoiseSuppressEnabled(mCheckNs.isChecked());
            }
        });
        mCheckAgc = (CheckBox) findViewById(R.id.check_agc);
        mCheckAgc.setChecked(mAudioRecordPlayer.isGainControlDefaultEnabled());
        mCheckAgc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioRecordPlayer.setGainControlEnabled(mCheckAgc.isChecked());
            }
        });
        mCheckAec = (CheckBox) findViewById(R.id.check_aec);
        mCheckAec.setChecked(mAudioRecordPlayer.isEchoCancelDefaultEnabled());
        mCheckAec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioRecordPlayer.setEchoCancelEnabled(mCheckAec.isChecked());
            }
        });
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
        super.onDestroy();

        mAudioRecordPlayer.release();
        mAudioRecordPlayer = null;
        if (mVisualizer != null) {
            mVisualizer.release();
            mVisualizer = null;
        }
        mPermissionRequest = null;
    }

    @Override
    public void onPermissionAllGranted() {
        startOrStopRecording();
        startVisualiser();
        updateRecordUi();
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
        showDeniedDialog();
    }

    @Override
    public void onPermissionPartDenied() {
        showDeniedDialog();
    }

    private void showDeniedDialog() {
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

    private void updateRecordUi() {
        if (mAudioRecordPlayer.isWorking()) {
            setPlayEnable(false);
            mButtonAction.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            setPlayEnable(true);
            mButtonAction.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private void setPlayEnable(boolean enable) {
        mButtonPlay.setEnabled(enable);
    }

    private void startOrStopPlaying() {
        if (mAudioRecordPlayer.isFilePlaying()) {
            mAudioRecordPlayer.stopPlayFile();
        } else {
            mAudioRecordPlayer.startPlayFile();
        }
    }

    private void updatePlayUi() {
        if (mAudioRecordPlayer.isFilePlaying()) {
            setRecordEnable(false);
            Drawable drawable = getResources().getDrawable(android.R.drawable.ic_media_pause);
            mButtonPlay.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            mButtonPlay.setText(R.string.button_stop);
        } else {
            setRecordEnable(true);
            Drawable drawable = getResources().getDrawable(android.R.drawable.ic_media_play);
            mButtonPlay.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            mButtonPlay.setText(R.string.button_play);
        }
    }

    private void setRecordEnable(boolean enable) {
        mButtonAction.setEnabled(enable);

        mCheckPlayRecord.setEnabled(enable);
        mCheckNs.setEnabled(enable);
        mCheckAgc.setEnabled(enable);
//        mCheckAec.setEnabled(enable);
    }

    @Override
    public void onPlayComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setRecordEnable(true);
                Drawable drawable = getResources().getDrawable(android.R.drawable.ic_media_play);
                mButtonPlay.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                mButtonPlay.setText(R.string.button_play);
            }
        });
    }
}
