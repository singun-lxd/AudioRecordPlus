#ifndef _AUDIO_NS_H_
#define _AUDIO_NS_H_

int NsWrap_Init(int sample_rate);


int NsWrap_Process(int ns_handle, short *src_audio_data, short *dest_audio_data);


void NsWrap_Destroy(int ns_handle);

#endif
