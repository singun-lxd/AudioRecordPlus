package com.singun.media.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class AudioFileWriter {
    private AudioConfig mAudioConfig;
    private OutputStream mOutputStream;

    public AudioFileWriter(AudioConfig audioConfig) {
        mAudioConfig = audioConfig;
    }

    public boolean createAudioFile(boolean forceCreate) {
        File dir = new File(mAudioConfig.audioDirPath);
        if (dir.isFile()) {
            dir.delete();
        }
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        File pcmFile = new File(mAudioConfig.audioDirPath, mAudioConfig.audioName + ".pcm");
        if (pcmFile.exists()) {
            if (forceCreate) {
                pcmFile.delete();
            } else {
                return false;
            }
        }
        try {
            mOutputStream = new FileOutputStream(pcmFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void saveRecordData(int length) {
        if (mOutputStream == null) {
            return;
        }
        try {
            mOutputStream.write(mAudioConfig.audioCache, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAudioFormat(int sampleRate, int channelCount) {
        if (mOutputStream == null) {
            return;
        }
        try {
            mOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mOutputStream = null;

        File pcmFile = new File(mAudioConfig.audioDirPath, mAudioConfig.audioName + ".pcm");
        File wavFile = new File(mAudioConfig.audioDirPath, mAudioConfig.audioName + ".wav");

        AudioEncodeUtil.convertPcmToWav(pcmFile, wavFile, sampleRate, channelCount);
    }

    public void release() {
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
