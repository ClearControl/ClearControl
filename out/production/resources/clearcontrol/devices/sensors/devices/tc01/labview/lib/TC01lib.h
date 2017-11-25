#include "extcode.h"
#ifdef __cplusplus
extern "C" {
#endif

/*!
 * TC01lib
 */
double __cdecl TC01lib(char physicalChannel[], int32_t thermocoupleType);

long __cdecl LVDLLStatus(char *errStr, int errStrLen, void *module);

#ifdef __cplusplus
} // extern "C"
#endif

