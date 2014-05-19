#include "extcode.h"
#ifdef __cplusplus
extern "C" {
#endif
typedef struct {
	LVBoolean status;
	int32_t code;
	LStrHandle source;
	} TD1;


/*!
 * DirettoreClose
 */
void __cdecl DirettoreClose(uintptr_t *FPGAReference, TD1 *ErrorOut);
/*!
 * DirettoreOpen
 */
void __cdecl DirettoreOpen(uint32_t TriggerFIFODepth, 
	int32_t MatrixFIFODepth, uintptr_t *FPGAReference, TD1 *ErrorOut);
/*!
 * DirettoreStart
 */
void __cdecl DirettoreStart(uintptr_t *FPGAReference, TD1 *ErrorOut);
/*!
 * DirettorePlay
 */
void __cdecl DirettorePlay(uintptr_t *FPGAReference, 
	int32_t DeltaTimeArray[], int32_t DeltaTimeArrayLength, 
	int32_t NumberOfTimePointsToPlayArray[], 
	int32_t NumberofTimePointsToPlayArrayLength, int32_t SyncArray[], 
	int32_t SyncArrayLength, int32_t NumberOfMatrices, int16_t MatricesArray[], 
	int32_t MatricesArrayLength, uint32_t *SpaceLeftInQueue, TD1 *ErrorOut);
/*!
 * DirettoreStop
 */
void __cdecl DirettoreStop(uintptr_t *FPGAReference, TD1 *ErrorOut);

long __cdecl LVDLLStatus(char *errStr, int errStrLen, void *module);

#ifdef __cplusplus
} // extern "C"
#endif

