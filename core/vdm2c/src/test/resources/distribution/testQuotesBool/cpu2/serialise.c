// The Send_Bus and HandleRecieved calls

#include "serialise.h"
#include "asn1crt.h"
//#include "intVal.h"

/** Serialisation functions **/

void serialiseInt(byte* encBuff, TVP t, int pos){
	size_t offset = VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1;

	// from VDM to ANS1
	VdmInteger val;
	fromVdmInt2Int(&t, &val);

	// Encode the value
	int pErrCode;
	BitStream bitStrm;
	byte intBuff[VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1];
	BitStream_Init(&bitStrm, intBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING);
	VdmInteger_Encode(&val, &bitStrm, &pErrCode, TRUE);

	// Copy value back
	memcpy(encBuff+pos+1, intBuff, offset);
}

void serialise(byte* encBuff, int size, va_list args){
	int i = 0;
	int b = 5;
	for(i=0; i<size; i++){
		TVP t = va_arg(args,TVP);
		if(t->type==VDM_INT){
			printf("Serialised value is %d \n", t->value.intVal);
			encBuff[b] = VDM_INT;
			size_t offset = VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1;
			serialiseInt(encBuff, t, b);
			b = b + offset;
		}
	}
}

/** Deserialisaton functions **/

void deserialiseInt(byte* decBuff, TVP* t, int pos){
	size_t offset = VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1;
	int errCode;
	VdmInteger res_ser;
	byte intBuff[VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1];
	// Copy value back
	memcpy(intBuff, decBuff+pos+1, offset);
	BitStream bitStrmDec;
	BitStream_AttachBuffer(&bitStrmDec, intBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING);
	VdmInteger_Decode(&res_ser, &bitStrmDec, &errCode);
	// Convert back to TVP
	fromInt2VdmInt(&res_ser, t);
}

void deserialiseQuote(byte* decBuff, TVP* t, int pos){
	size_t offset = VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1;
	int errCode;
	VdmInteger res_ser;
	byte intBuff[VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1];
	// Copy value back
	memcpy(intBuff, decBuff+pos+1, offset);
	BitStream bitStrmDec;
	BitStream_AttachBuffer(&bitStrmDec, intBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING);
	VdmInteger_Decode(&res_ser, &bitStrmDec, &errCode);
	// Convert back to TVP
	fromQuote2VdmQuote(&res_ser, t);
}

void deserialise(byte* decBuff, int size, TVP args[]){
	int i = 0;
	int b = 5;
	for(i=0; i<size; i++){
		int type = decBuff[b];
		if(type==VDM_INT){
			size_t offset = VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1;
			deserialiseInt(decBuff, &args[i], b);
			printf("Deserialised int value is %d \n", args[i]->value.intVal);
			b = b + offset;
		}
		if(type==VDM_QUOTE){
			size_t offset = VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1;
			deserialiseQuote(decBuff, &args[i], b);
			printf("Deserialised quote value is %d \n", args[i]->value.quoteVal);
			b = b + offset;
		}
	}
}
