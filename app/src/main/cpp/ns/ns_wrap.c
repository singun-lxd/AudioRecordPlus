#include "ns_wrap.h"
#include "noise_suppression.h"

#include <stdio.h>

int NsWrap_Init(int sample_rate){

    int ret = -1;
	NsHandle* NS_instance = WebRtcNs_Create();
	if (NS_instance == NULL) {
		printf("WebRtcNs_Create failed, null instance returned");
		return -1;
	}
	if ((ret = WebRtcNs_Init(NS_instance, sample_rate) )) {
		printf("WebRtcNs_Init failed with error code = %d", ret);
		return ret;
	}

	if (( ret = WebRtcNs_set_policy(NS_instance, 2))){
		printf("WebRtcNs_set_policy failed with error code = %d", ret);
		return ret;
	}

	return (int)NS_instance;
}


int NsWrap_Process(int ns_handle, short *src_audio_data, short *dest_audio_data){
	//get handle
	NsHandle* NS_instance = (NsHandle* )ns_handle;

	//noise suppression
    WebRtcNs_Process(NS_instance, src_audio_data, 1, dest_audio_data);

	return 0;
}


void NsWrap_Destroy(int ns_handle){
    WebRtcNs_Free((NsHandle *) ns_handle);
}
