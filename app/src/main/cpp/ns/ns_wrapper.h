//
// Created by singun on 2017/3/6 0006.
//

#ifndef NS_WRAPPER_H
#define NS_WRAPPER_H

#include "../common/base_wrapper.h"

#ifdef __cplusplus
extern "C" {
#endif

class ns_wrapper : base_wrapper {
public:
    ns_wrapper();
    ~ns_wrapper();

    int ns_init(int sampleHz, int mode);
    int ns_proc(const short* input, short *output, int pcmLen);
private:
    void reset_data();

    void* ns;       //ns instance
    float **nsIn;   //ns input[band][data]
    float **nsOut;  //ns output[band][data]
};

#ifdef __cplusplus
}
#endif

#endif //NS_WRAPPER_H
