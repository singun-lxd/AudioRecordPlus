//
// Created by singun on 2017/3/6 0006.
//

#ifndef AGC_WRAPPER_H
#define AGC_WRAPPER_H

#include "../common/base_wrapper.h"

#ifdef __cplusplus
extern "C" {
#endif

class agc_wrapper : base_wrapper {
public:
    agc_wrapper();
    ~agc_wrapper();

    int agc_init();
    int agc_config(int agcDb, int agcDbfs);
    int agc_proc(const short *input, short *output, int pcmLen);
private:
    void reset_data();

    // agc part
    void* agc;      //agc instance
    short **agcIn;  //agc input[band][data]
    short **agcOut; //agc output[band][data]
};

#ifdef __cplusplus
}
#endif

#endif //AGC_WRAPPER_H
