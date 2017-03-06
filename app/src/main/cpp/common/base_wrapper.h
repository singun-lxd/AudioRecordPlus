//
// Created by singun on 2017/3/6 0006.
//

#ifndef BASE_WRAPPER_H
#define BASE_WRAPPER_H

#ifdef __cplusplus
extern "C" {
#endif

#define BUFFERSIZE  (1024*1024)

class base_wrapper {
public:
    base_wrapper();
    ~base_wrapper();
protected:
    int init(int sampleHzIn);
    void free_buffer(void **buffer);

protected:
    int sampleHz;   //语音采样率
    int frameMs;    //一帧语音长度，以毫秒为单位
    int frameSh;    //一帧short的个数， 默认一帧10ms
    int nBands;     //num of band, including high and low, we only use low band
};

#ifdef __cplusplus
}
#endif

#endif //NS_WRAPPER_H