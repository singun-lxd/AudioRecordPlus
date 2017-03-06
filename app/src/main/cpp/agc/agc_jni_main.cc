#include <stdio.h>
#include "agc_jni_main.h"
#include "agc_wrapper.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_com_singun_wrapper_WebRTC_GainControl_initGainControl(JNIEnv *env, jobject instance, int sampleRate, int db, int dbfs) {
    agc_wrapper* wrapper = new agc_wrapper();
    int init = wrapper->agc_init(db, dbfs);
    if (init != 0) {
        delete wrapper;
        return 0;
    }
    return (int) wrapper;
}

JNIEXPORT jshortArray JNICALL
Java_com_singun_wrapper_WebRTC_GainControl_processGainControl(JNIEnv *env, jobject instance, jint handle, jshortArray sample, jint length) {

    jshort *in_sample = env->GetShortArrayElements(sample, 0);

    short out_sample[length];
    agc_wrapper* wrapper = (agc_wrapper*) handle;
    int ret = wrapper->agc_proc(in_sample, out_sample, length);

    env->ReleaseShortArrayElements(sample, in_sample, 0);

    if (ret == 0) {
        jshortArray ret_array = env->NewShortArray(length);
        env->SetShortArrayRegion(ret_array, 0, length, out_sample);
        return ret_array;
    } else {
        return NULL;
    }

}

JNIEXPORT void JNICALL
Java_com_singun_wrapper_WebRTC_GainControl_releaseGainControl(JNIEnv *env, jobject instance, jint handle) {

    if (handle) {
        agc_wrapper* wrapper = (agc_wrapper*) handle;
        delete wrapper;
    }
}

#ifdef __cplusplus
}
#endif
