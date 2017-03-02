package com.singun.media.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.support.annotation.CallSuper;
import android.view.Window;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class AudioRecordPlayer {
    private static final int AUDIO_MODE = AudioManager.MODE_IN_COMMUNICATION;

    private AudioManager mAudioManager;
    private Window mCurrentWindow;

    private MiniAudioRecorder mAudioRecorder;
    private MiniAudioTrack mAudioTrack;
    private AudioFileWriter mAudioFileWriter;
    private AudioConfig mAudioConfig;
    private Thread mRecordPlayThread;

    protected AudioProcessListener mListener;

    private int mOldMode;
    private boolean mWorking;
    private boolean mReleased;
    private boolean mTrackEnabled;

    public AudioRecordPlayer(Context context, Window window) {
        this(context, window, false);
    }

    public AudioRecordPlayer(Context context, Window window, boolean trackEnabled) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mCurrentWindow = window;
        mReleased = false;
        mTrackEnabled = trackEnabled;

        init();
    }

    public void init() {
        if (mCurrentWindow != null) {
            mCurrentWindow.setVolumeControlStream(AUDIO_MODE);
        }

        mAudioConfig = new AudioConfig();
        mAudioConfig.streamType = AudioManager.MODE_IN_COMMUNICATION;
        mAudioConfig.audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
        mAudioConfig.sampleRateInHz = 8000;
        mAudioConfig.channelInConfig = AudioFormat.CHANNEL_IN_MONO;
        mAudioConfig.channelOutConfig = AudioFormat.CHANNEL_OUT_MONO;
        mAudioConfig.audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        mAudioConfig.audioDataIn = new byte[1024];

        updateAudioConfig(mAudioConfig);

        mAudioRecorder = new MiniAudioRecorder(mAudioConfig);
        mAudioTrack = new MiniAudioTrack(mAudioConfig);
        mAudioFileWriter = new AudioFileWriter(mAudioConfig);

        mRecordPlayThread = new AudioRecordPlayThread();
        mRecordPlayThread.start();
    }

    protected void updateAudioConfig(AudioConfig config) {

    }

    public void setAudioProcessListener(AudioProcessListener listener) {
        mListener = listener;
    }

    public void setSpeakerOn(boolean speakerOn) {
        mAudioManager.setSpeakerphoneOn(speakerOn);
    }

    public boolean isSpeakerOn() {
        return mAudioManager.isSpeakerphoneOn();
    }

    public void setTrackEnabled(boolean trackEnabled) {
        if (mTrackEnabled == trackEnabled) {
            return;
        }
        this.mTrackEnabled = trackEnabled;
        if (trackEnabled) {
            mAudioTrack.startPlaying();
        } else {
            mAudioTrack.stop();
        }
    }

    public boolean isTrackEnabled() {
        return mTrackEnabled;
    }

    public void startWorking() {
        if (mWorking) {
            return;
        }
        mOldMode = mAudioManager.getMode();
        mAudioManager.setMode(AUDIO_MODE);

        mAudioFileWriter.createAudioFile(true);
        mAudioRecorder.startRecording();
        if (mTrackEnabled) {
            mAudioTrack.startPlaying();
        }

        mWorking = true;
    }

    public void stop() {
        if (!mWorking) {
            return;
        }
        mAudioManager.setMode(mOldMode);
        mOldMode = 0;

        mAudioRecorder.stop();
        if (mTrackEnabled) {
            mAudioTrack.stop();
        }
        mAudioFileWriter.saveAudioFormat(mAudioRecorder.getSampleRate(), mAudioRecorder.getChannelCount());

        mWorking = false;
    }

    public boolean isWorking() {
        return mWorking;
    }

    public void release() {
        mReleased = true;
        mAudioRecorder.release();
        mAudioTrack.release();
        mAudioFileWriter.release();
        mAudioRecorder = null;
        mAudioTrack = null;
        mAudioFileWriter = null;
        mCurrentWindow = null;
        mAudioManager = null;
    }

    protected void processAudioData(AudioConfig audioConfig, int length) {
        audioConfig.audioDataOut = audioConfig.audioDataIn;
    }

    private class AudioRecordPlayThread extends Thread {
        public AudioRecordPlayThread() {
            super("AudioRecordPlayThread");
//            setPriority();
        }

        @Override
        public void run() {
            while (!mReleased) {
                int length = 0;
                if (mAudioRecorder != null && mAudioRecorder.isRecording()) {
                    length = mAudioRecorder.readAudioData();
                }
                if (length > 0) {
                    processAudioData(mAudioConfig, length);
                    if (mListener != null) {
                        mListener.onAudioDataProcessed(mAudioConfig.audioDataOut);
                    }
                    if (mTrackEnabled && mAudioTrack != null) {
                        mAudioTrack.writeAudioData(length);
                    }
                    if (mAudioFileWriter != null) {
                        mAudioFileWriter.saveRecordData(length);
                    }
                }
            }
        }
    }

    public interface AudioProcessListener {
        void onAudioDataProcessed(byte[] audioData);
    }
}
