#include <stdio.h>
#include "aecm_jni_main.h"
#include "aecm_wrapper.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_com_singun_wrapper_WebRTC_EchoCancel_initEchoCancel(JNIEnv *env, jobject obj, jint samp_freq){
    aecm_wrapper* wrapper = new aecm_wrapper();
    int init = wrapper->aecm_init(samp_freq);
    if (init != 0) {
        delete wrapper;
        return 0;
    }
    return (int) wrapper;
}

JNIEXPORT jshortArray JNICALL
Java_com_singun_wrapper_WebRTC_EchoCancel_processEchoCancel(JNIEnv *env, jobject obj,
                jint handle, jshortArray nearendNoisy, jshortArray nearendClean, jint length){

	jshort *nearend_noisy = env->GetShortArrayElements(nearendNoisy, NULL);
	jshort *nearend_clean = env->GetShortArrayElements(nearendClean, NULL);

    short out_sample[length];
    aecm_wrapper* wrapper = (aecm_wrapper*) handle;
    int ret = wrapper->aecm_proc(nearend_noisy, nearend_clean, out_sample, length);

	env->ReleaseShortArrayElements(nearendNoisy, nearend_noisy, 0);
	env->ReleaseShortArrayElements(nearendClean, nearend_clean, 0);

    if (ret == 0) {
        jshortArray ret_array = env->NewShortArray(length);
        env->SetShortArrayRegion(ret_array, 0, length, out_sample);
        return ret_array;
    } else {
        return NULL;
    }
}

JNIEXPORT void JNICALL
Java_com_singun_wrapper_WebRTC_EchoCancel_releaseEchoCancel(JNIEnv *env, jobject obj, jint handle){
    if (handle) {
        aecm_wrapper* wrapper = (aecm_wrapper*) handle;
        delete wrapper;
    }
}

#ifdef __cplusplus
}
#endif
