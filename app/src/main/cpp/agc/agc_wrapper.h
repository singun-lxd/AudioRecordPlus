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

    int agc_init(int agcDb, int agcDbfs);
    int agc_proc(short *input, int pcmLen);
private:
    void reset_data();

    // agc part
    void* agc;      //agc instance
    short **agcIn;  //agc input[band][data]
    short **agcOut; //agc output[band][data]
    // temp result
    short *temp;    //temp: used to store result from last step
};

#ifdef __cplusplus
}
#endif

#endif //AGC_WRAPPER_H
