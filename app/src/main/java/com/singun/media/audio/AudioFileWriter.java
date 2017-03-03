package com.singun.media.audio;

import android.text.TextUtils;

import com.singun.media.audio.encode.AudioEncodeUtil;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by singun on 2017/3/1 0001.
 */

public class AudioFileWriter {
    private AudioConfig mAudioConfig;
    private DataOutputStream mOutputStream;

    public AudioFileWriter(AudioConfig audioConfig) {
        mAudioConfig = audioConfig;
    }

    public boolean createAudioFile(boolean forceCreate) {
        if (TextUtils.isEmpty(mAudioConfig.audioDirPath) || TextUtils.isEmpty(mAudioConfig.audioName)) {
            return false;
        }
        File dir = new File(mAudioConfig.audioDirPath);
        if (dir.isFile()) {
            dir.delete();
        }
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        File pcmFile = checkFile(forceCreate);
        if (pcmFile == null) {
            return false;
        }

        try {
            int cacheByte = mAudioConfig.audioDataSize * 2;
            FileOutputStream fileOutputStream = new FileOutputStream(pcmFile);
            mOutputStream = new DataOutputStream(new BufferedOutputStream(fileOutputStream, cacheByte));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private File checkFile(boolean forceCreate) {
        File pcmFile = new File(mAudioConfig.audioDirPath, mAudioConfig.audioName + ".pcm");
        if (pcmFile.exists()) {
            if (forceCreate) {
                pcmFile.delete();
            } else {
                return null;
            }
        }

        File wavFile = new File(mAudioConfig.audioDirPath, mAudioConfig.audioName + ".wav");
        if (wavFile.exists()) {
            if (forceCreate) {
                wavFile.delete();
            } else {
                return null;
            }
        }
        return pcmFile;
    }

    public void saveRecordData(int length) {
        if (mOutputStream == null) {
            return;
        }
        if (length > mAudioConfig.audioDataOut.length) {
            length = mAudioConfig.audioDataOut.length;
        }
        try {
            DataOutputStream dataOutputStream = mOutputStream;
            if (dataOutputStream == null) {
                return;
            }
            for (int i = 0; i < length; i++) {
                short data = mAudioConfig.audioDataOut[i];
                dataOutputStream.writeShort(data);
            }
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
