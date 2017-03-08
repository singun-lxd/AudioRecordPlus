package com.singun.media.audio;

import com.singun.media.audio.player.MiniAudioTrack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by singun on 2017/3/8 0008.
 */

public class AudioPlayer {
    private AudioConfig mAudioConfig;
    private MiniAudioTrack mAudioTrack;
    private PlayStateListener mListener;

    private boolean mReleased;
    private int mPrimePlaySize = 0;
    private byte[] mCacheData;

    public AudioPlayer(AudioConfig audioConfig) {
        mAudioConfig = audioConfig;

        init(audioConfig);
    }

    private void init(AudioConfig audioConfig) {
        mAudioTrack = new MiniAudioTrack(audioConfig);

        mPrimePlaySize = audioConfig.audioDataSize * 2;
        mCacheData = new byte[mPrimePlaySize];
    }

    public void release() {
        mReleased = true;
        mAudioTrack.release();
        mAudioTrack = null;
    }

    public void startPlaying() {
        if (mAudioTrack.isPlaying()) {
            return;
        }
        AudioPlayThread thread = new AudioPlayThread();
        thread.start();
        mAudioTrack.startPlaying();
    }

    public void stop() {
        if (!mAudioTrack.isPlaying()) {
            return;
        }
        mAudioTrack.stop();
    }

    public boolean isPlaying() {
        return mAudioTrack.isPlaying();
    }

    public void setPlayStateListener(PlayStateListener listener) {
        mListener = listener;
    }

    private class AudioPlayThread extends Thread {
        private InputStream mInputStream;

        public AudioPlayThread() {
            super("AudioPlayThread");
//            setPriority();
        }

        @Override
        public void run() {
            playStart();
            try {
                mInputStream.skip(44);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while(mAudioTrack.isPlaying() && !mReleased) {
                try {
                    int size = mInputStream.read(mCacheData, 0, mPrimePlaySize);
                    if (size <= 0) {
                        break;
                    }
                    int length = mAudioTrack.writeAudioData(mCacheData, 0, size);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            playComplete();
        }

        private void playStart() {
            File file = new File(mAudioConfig.audioDirPath, mAudioConfig.audioName + ".wav");
            openAudioFile(file);
        }

        private void openAudioFile(File audioFile) {
            if (mInputStream != null) {
                closeAudioFile();
            }
            try {
                mInputStream = new BufferedInputStream(new FileInputStream(audioFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void playComplete() {
            mAudioTrack.stop();
            closeAudioFile();

            if (mListener != null) {
                mListener.onPlayComplete();
            }
        }

        private void closeAudioFile() {
            if (mInputStream == null) {
                return;
            }
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mInputStream = null;
        }
    }

    public interface PlayStateListener {
        void onPlayComplete();
    }
}
