#ifndef GENERATED_ASN1SCC_BASICTYPES_H
#define GENERATED_ASN1SCC_BASICTYPES_H
/*
Code automatically generated by asn1scc tool
*/
#include "asn1crt.h"

#ifdef  __cplusplus
extern "C" {
#endif



typedef flag VdmBoolean;

#define VdmBoolean_REQUIRED_BYTES_FOR_ENCODING       1 
#define VdmBoolean_REQUIRED_BITS_FOR_ENCODING        1
#define VdmBoolean_REQUIRED_BYTES_FOR_ACN_ENCODING   1 
#define VdmBoolean_REQUIRED_BITS_FOR_ACN_ENCODING    1
#define VdmBoolean_REQUIRED_BYTES_FOR_XER_ENCODING   38

void VdmBoolean_Initialize(VdmBoolean* pVal);
flag VdmBoolean_IsConstraintValid(const VdmBoolean* val, int* pErrCode);
flag VdmBoolean_Equal(const VdmBoolean* val1, const VdmBoolean* val2);



typedef asn1SccSint VdmInteger;

#define VdmInteger_REQUIRED_BYTES_FOR_ENCODING       9 
#define VdmInteger_REQUIRED_BITS_FOR_ENCODING        72
#define VdmInteger_REQUIRED_BYTES_FOR_ACN_ENCODING   9 
#define VdmInteger_REQUIRED_BITS_FOR_ACN_ENCODING    72
#define VdmInteger_REQUIRED_BYTES_FOR_XER_ENCODING   45

void VdmInteger_Initialize(VdmInteger* pVal);
flag VdmInteger_IsConstraintValid(const VdmInteger* val, int* pErrCode);
flag VdmInteger_Equal(const VdmInteger* val1, const VdmInteger* val2);



typedef double VdmReal;

#define VdmReal_REQUIRED_BYTES_FOR_ENCODING       13 
#define VdmReal_REQUIRED_BITS_FOR_ENCODING        104
#define VdmReal_REQUIRED_BYTES_FOR_ACN_ENCODING   13 
#define VdmReal_REQUIRED_BITS_FOR_ACN_ENCODING    104
#define VdmReal_REQUIRED_BYTES_FOR_XER_ENCODING   69

void VdmReal_Initialize(VdmReal* pVal);
flag VdmReal_IsConstraintValid(const VdmReal* val, int* pErrCode);
flag VdmReal_Equal(const VdmReal* val1, const VdmReal* val2);



 

/* ================= Encoding/Decoding function prototypes =================
 * These functions are placed at the end of the file to make sure all types
 * have been declared first, in case of parameterized ACN encodings
 * ========================================================================= */

flag VdmBoolean_Encode(const VdmBoolean* val, BitStream* pBitStrm, int* pErrCode, flag bCheckConstraints);
flag VdmBoolean_Decode(VdmBoolean* pVal, BitStream* pBitStrm, int* pErrCode);
flag VdmInteger_Encode(const VdmInteger* val, BitStream* pBitStrm, int* pErrCode, flag bCheckConstraints);
flag VdmInteger_Decode(VdmInteger* pVal, BitStream* pBitStrm, int* pErrCode);
flag VdmReal_Encode(const VdmReal* val, BitStream* pBitStrm, int* pErrCode, flag bCheckConstraints);
flag VdmReal_Decode(VdmReal* pVal, BitStream* pBitStrm, int* pErrCode); 


#ifdef  __cplusplus
}

#endif

#endif