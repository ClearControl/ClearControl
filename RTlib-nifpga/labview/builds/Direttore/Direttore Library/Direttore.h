#include "extcode.h"
#pragma pack(push)
#pragma pack(1)

#ifdef __cplusplus
extern "C" {
#endif
typedef struct {
	LVBoolean status;
	int32_t code;
	LStrHandle source;
	} TD1;

typedef struct {
	int32_t dimSizes[2];
	int32_t dimensionSize[1];
	} TD2;
typedef TD2 **TD2Hdl;


void __cdecl DirettoreClose(uintptr_t *FPGAVIReferenceIn, TD1 *errorOut);
void __cdecl PlayMatrix(uintptr_t *FPGAVIReferenceIn, uint32_t dtUSec, 
	LVBoolean *WriteOnly, TD1 *errorIn, uintptr_t *FPGAVIReferenceOut, 
	TD1 *errorOut);
void __cdecl SetOutputMatrix(uintptr_t *FPGAVIReferenceIn, 
	int16_t MatrixIn[], TD1 *errorIn, uintptr_t *FPGAVIReferenceOut, 
	TD1 *errorOut, int32_t len);
void __cdecl GetInputMatrix(uintptr_t *FPGAVIReferenceIn, int16_t MatrixIn[], 
	TD1 *errorIn, uintptr_t *FPGAVIReferenceOut, int16_t MatrixOut[], 
	TD1 *errorOut, int32_t len, int32_t len2);
void __cdecl DirettoreInitMatrix(int32_t matrixLengthIn, 
	int32_t dimensionSize, TD2Hdl *matrixBank);
void __cdecl DirettoreOpen(uintptr_t *FPGAVIReferenceOut, TD1 *errorOut);

long __cdecl LVDLLStatus(char *errStr, int errStrLen, void *module);

#ifdef __cplusplus
} // extern "C"
#endif

#pragma pack(pop)

