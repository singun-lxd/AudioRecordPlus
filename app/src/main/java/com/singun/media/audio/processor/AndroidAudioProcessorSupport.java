package com.singun.media.audio.processor;

import android.annotation.TargetApi;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;

/**
 * Created by singun on 2017/3/3 0003.
 */

class AndroidAudioProcessorSupport {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isSupportNoiseSuppressor() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && NoiseSuppressor.isAvailable();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isSupportGainControl() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && AutomaticGainControl.isAvailable();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isSupportEchoCanceler() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && AcousticEchoCanceler.isAvailable();
    }
}
