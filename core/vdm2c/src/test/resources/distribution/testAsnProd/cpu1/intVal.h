#ifndef GENERATED_ASN1SCC_INTVAL_H
#define GENERATED_ASN1SCC_INTVAL_H
/*
Code automatically generated by asn1scc tool
*/
#include "asn1crt.h"

#ifdef  __cplusplus
extern "C" {
#endif



typedef asn1SccSint IntValue;

#define IntValue_REQUIRED_BYTES_FOR_ENCODING       9 
#define IntValue_REQUIRED_BITS_FOR_ENCODING        72
#define IntValue_REQUIRED_BYTES_FOR_ACN_ENCODING   9 
#define IntValue_REQUIRED_BITS_FOR_ACN_ENCODING    72
#define IntValue_REQUIRED_BYTES_FOR_XER_ENCODING   41

void IntValue_Initialize(IntValue* pVal);
flag IntValue_IsConstraintValid(const IntValue* val, int* pErrCode);
flag IntValue_Equal(const IntValue* val1, const IntValue* val2);



 

/* ================= Encoding/Decoding function prototypes =================
 * These functions are placed at the end of the file to make sure all types
 * have been declared first, in case of parameterized ACN encodings
 * ========================================================================= */

flag IntValue_Encode(const IntValue* val, BitStream* pBitStrm, int* pErrCode, flag bCheckConstraints);
flag IntValue_Decode(IntValue* pVal, BitStream* pBitStrm, int* pErrCode); 


#ifdef  __cplusplus
}

#endif

#endif
