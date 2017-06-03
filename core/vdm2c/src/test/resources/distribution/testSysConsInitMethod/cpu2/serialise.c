// The Send_Bus and HandleRecieved calls

#include "serialise.h"
#include "asn1crt.h"
#include "intVal.h"


void serialiseInt(byte* encBuff, TVP t, int pos){
	size_t offset = IntValue_REQUIRED_BYTES_FOR_ENCODING + 1;

	// from VDM to ANS1
	IntValue val;
	fromVdmInt2Int(&t, &val);

	// Encode the value
	int pErrCode;
	BitStream bitStrm;
	byte intBuff[IntValue_REQUIRED_BYTES_FOR_ENCODING + 1];
	BitStream_Init(&bitStrm, intBuff, IntValue_REQUIRED_BYTES_FOR_ENCODING);
	IntValue_Encode(&val, &bitStrm, &pErrCode, TRUE);

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
			size_t offset = IntValue_REQUIRED_BYTES_FOR_ENCODING + 1;
			serialiseInt(encBuff, t, b);
			b = b + offset;
		}
	}
}


void deserialiseInt(byte* decBuff, TVP* t, int pos){
	size_t offset = IntValue_REQUIRED_BYTES_FOR_ENCODING + 1;
	int errCode;
	IntValue res_ser;
	byte intBuff[IntValue_REQUIRED_BYTES_FOR_ENCODING + 1];
	// Copy value back
	memcpy(intBuff, decBuff+pos+1, offset);
	BitStream bitStrmDec;
	BitStream_AttachBuffer(&bitStrmDec, intBuff, IntValue_REQUIRED_BYTES_FOR_ENCODING);
	IntValue_Decode(&res_ser, &bitStrmDec, &errCode);
	// Convert back to TVP
	fromInt2VdmInt(&res_ser, t);
}

void deserialise(byte* decBuff, int size, TVP args[]){
	int i = 0;
	int b = 5;
	for(i=0; i<size; i++){
		int type = decBuff[b];
		if(type==VDM_INT){
			size_t offset = IntValue_REQUIRED_BYTES_FOR_ENCODING + 1;
			deserialiseInt(decBuff, &args[i], b);
			printf("Deserialised value is %d \n", args[i]->value.intVal);
			b = b + offset;
		}
	}
}
