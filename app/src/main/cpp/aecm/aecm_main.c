#include "aecm_main.h"
#include <android/log.h>
#include "echo_control_mobile.h"

#define TAG "singun echo cancel"

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jint JNICALL Java_com_singun_wrapper_WebRTC_EchoCancel_initEchoCancel
  (JNIEnv *env, jobject obj, jint samp_freq){

	int ret ;
	void* AECM_instance;

	if ((ret = WebRtcAecm_Create(&AECM_instance) )) {
		return ret;
	}

	if ((ret = WebRtcAecm_Init(AECM_instance, samp_freq) )) {
		return ret;
	}

#if 0	//config
	AecmConfig aecm_config;
	aecm_config.cngMode = AECM_TRUE;
	aecm_config.echoMode = 3;

	if(( ret = WebRtcAecm_set_config(AECM_instance, aecm_config) )){
		__android_log_print(ANDROID_LOG_ERROR ,TAG ,"WebRtcAecm_set_config failed with error code = %d", ret);
		return ret;
	}
#endif

	return (jint)((int *)AECM_instance );
}

JNIEXPORT jshortArray JNICALL Java_com_singun_wrapper_WebRTC_EchoCancel_processEchoCancel
  (JNIEnv *env, jobject obj, jint handle, jshortArray nearendNoisy, jshortArray nearendClean){  //nearendNoisy , nearendClean
	//get handle
	void* AECM_instance = (void* )handle;
	//create native shorts from java shorts
	jshort *native_nearend_noisy = (*env)->GetShortArrayElements(env, nearendNoisy, NULL);
	jshort *native_nearend_clean = (*env)->GetShortArrayElements(env, nearendClean, NULL);
	//allocate memory for output data
	jint length = (*env)->GetArrayLength(env, nearendNoisy);
	jshortArray temp = (*env)->NewShortArray(env, length);
	jshort *native_output_frame = (*env)->GetShortArrayElements(env, temp, 0);

	short farend[160] = {0};
	if( 0!=WebRtcAecm_BufferFarend(AECM_instance,farend,160) ){  //farend value??? audio
		__android_log_print(ANDROID_LOG_ERROR ,TAG ,"WebRtcAec_BufferFarend failed");
	}

	if( 0!=WebRtcAecm_Process(AECM_instance,native_nearend_noisy,native_nearend_clean,native_output_frame,160,0) ){
		__android_log_print(ANDROID_LOG_ERROR ,TAG ,"WebRtcAec_Process failed ");
	}

	//convert native output to java layer output
	jshortArray output_shorts = (*env)->NewShortArray(env, length);
	(*env)->SetShortArrayRegion(env, output_shorts, 0, length, native_output_frame);

	//cleanup and return
	(*env)->ReleaseShortArrayElements(env, nearendNoisy, native_nearend_noisy, 0);
	(*env)->ReleaseShortArrayElements(env, nearendClean, native_nearend_clean, 0);
	(*env)->ReleaseShortArrayElements(env, temp, native_output_frame, 0);

	return output_shorts;

}

JNIEXPORT jint JNICALL Java_com_singun_wrapper_WebRTC_EchoCancel_releaseEchoCancel
  (JNIEnv *env, jobject obj, jint handle){
	if (handle) {
		void* AECM_instance = (void* )handle;
		WebRtcAecm_Free(AECM_instance);
	}
	return 0;
}

#ifdef __cplusplus
}
#endif
