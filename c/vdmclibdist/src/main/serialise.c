// The Send_Bus and HandleRecieved calls

#include "serialise.h"
#include "asn1crt.h"

/** Serialisation functions **/
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
		if(t->type==VDM_QUOTE){
			printf("Serialised value is %d \n", t->value.quoteVal);
			encBuff[b] = VDM_QUOTE;
			size_t offset = VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1;
			serialiseQuote(encBuff, t, b);
			b = b + offset;
		}
		if(t->type==VDM_BOOL){
			printf("Serialised value is %d \n", t->value.boolVal);
			encBuff[b] = VDM_BOOL;
			encBuff[b+1] = t->value.boolVal;
			b = b + 2;
		}
		if(t->type==VDM_REAL){
			printf("Serialised value is %f \n", t->value.doubleVal);
			encBuff[b] = VDM_REAL;
			size_t offset = VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1;
			serialiseReal(encBuff, t, b);
			b = b + offset;
		}
	}
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
		if(type==VDM_REAL){
			size_t offset = VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1;
			deserialiseReal(decBuff, &args[i], b);
			printf("Deserialised real value is %d \n", args[i]->value.doubleVal);
			b = b + offset;
		}
		if(type==VDM_BOOL){
			args[i] = newBool(decBuff[b+1]);
			printf("Deserialised boolean value is %d \n", args[i]->value.boolVal);
			b = b + 2;
		}
	}
}

/**** Integer ****/
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

/**** Quote ****/
void serialiseQuote(byte* encBuff, TVP t, int pos){
	size_t offset = VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1;

	// from VDM to ANS1
	VdmInteger val;
	fromVdmQuote2Quote(&t, &val);

	// Encode the value
	int pErrCode;
	BitStream bitStrm;
	byte intBuff[VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1];
	BitStream_Init(&bitStrm, intBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING);
	VdmInteger_Encode(&val, &bitStrm, &pErrCode, TRUE);

	// Copy value back
	memcpy(encBuff+pos+1, intBuff, offset);
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

/**** Real ****/
void serialiseReal(byte* encBuff, TVP t, int pos){
	size_t offset = VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1;

	// from VDM to ANS1
	VdmReal val;
	fromVdmReal2Real(&t, &val);

	// Encode the value
	int pErrCode;
	BitStream bitStrm;
	byte intBuff[VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1];
	BitStream_Init(&bitStrm, intBuff, VdmReal_REQUIRED_BYTES_FOR_ENCODING);
	VdmReal_Encode(&val, &bitStrm, &pErrCode, TRUE);

	// Copy value back
	memcpy(encBuff+pos+1, intBuff, offset);
}

void deserialiseReal(byte* decBuff, TVP* t, int pos){
	size_t offset = VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1;
	int errCode;
	VdmInteger res_ser;
	byte intBuff[VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1];
	// Copy value back
	memcpy(intBuff, decBuff+pos+1, offset);
	BitStream bitStrmDec;
	BitStream_AttachBuffer(&bitStrmDec, intBuff, VdmReal_REQUIRED_BYTES_FOR_ENCODING);
	VdmReal_Decode(&res_ser, &bitStrmDec, &errCode);
	// Convert back to TVP
	fromReal2VdmReal(&res_ser, t);
}


/////////////////////////////
/**** Bool ****/
/*
void serialiseBool(byte* encBuff, TVP t, int pos){
}
*/
// TODO: Add maybe bool as seperate function too
/*
void deserialiseBool(byte* decBuff, TVP* t, int pos){
	*t = newBool(decBuff[pos]);
}
*/

/***** Result serialisation and deserialisation ******/

// Result
void serialiseRes(byte* encBuff, TVP t){

	if(t==NULL){
		printf("Invoked function as void");
		t = newBool(true);
	}

	if(t->type==VDM_INT){
		encBuff[0] = VDM_INT;
		VdmInteger val;
		fromVdmInt2Int(&t, &val);
		int pErrCode;
		BitStream bitStrm;
		char intBuff[VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1];
		BitStream_Init(&bitStrm, intBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING);
		VdmInteger_Encode(&val, &bitStrm, &pErrCode, TRUE);
		printf("Serialised Integer is: %d \n", t->value.intVal);
		int n;
		memcpy(encBuff+1, intBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1);
	}

	if(t->type==VDM_QUOTE){
		encBuff[0] = VDM_QUOTE;
		VdmInteger val;
		fromVdmQuote2Quote(&t, &val);
		int pErrCode;
		BitStream bitStrm;
		char intBuff[VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1];
		BitStream_Init(&bitStrm, intBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING);
		VdmInteger_Encode(&val, &bitStrm, &pErrCode, TRUE);
		printf("Serialised Quote is: %d \n", t->value.quoteVal);
		memcpy(encBuff+1, intBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1);
	}

	if(t->type==VDM_REAL){
		encBuff[0] = VDM_REAL;
		VdmReal val;
		fromVdmReal2Real(&t, &val);
		int pErrCode;
		BitStream bitStrm;
		char intBuff[VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1];
		BitStream_Init(&bitStrm, intBuff, VdmReal_REQUIRED_BYTES_FOR_ENCODING);
		VdmReal_Encode(&val, &bitStrm, &pErrCode, TRUE);
		printf("Serialised Real is: %f \n", t->value.doubleVal);
		int n;
		memcpy(encBuff+1, intBuff, VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1);
	}

	if(t->type==VDM_BOOL){
		encBuff[0] = VDM_BOOL;
		encBuff[1] = (byte) t->value.boolVal;
		printf("Serialised Boolean value is: %d \n", t->value.quoteVal);
	}

}

TVP deserialiseRes(byte* decFullBuff){

	if(decFullBuff[0]==VDM_INT){
		int errCode;
		VdmInteger res_ser;
		byte decBuff[VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1];

		memcpy(decBuff, decFullBuff+1, VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1);

		//res_ser = IntValue_dec(buffer, &errCode);
		BitStream bitStrmDec;
		BitStream_AttachBuffer(&bitStrmDec, decBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING);
		VdmInteger_Decode(&res_ser, &bitStrmDec, &errCode);

		// Convert back to TVP
		TVP res;
		//res->type = VDM_INT;
		fromInt2VdmInt(&res_ser, &res);

		printf("Deserialising Integer result: %d",  res->value.intVal);

		return res;
	}

	if(decFullBuff[0]==VDM_BOOL){
		int errCode;
		TVP res = newBool(decFullBuff[1]);
		printf("Deserialising Boolean result: %d",  res->value.boolVal);
		return res;
	}

	if(decFullBuff[0]==VDM_QUOTE){
		int errCode;
		VdmInteger res_ser;
		byte decBuff[VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1];

		memcpy(decBuff, decFullBuff+1, VdmInteger_REQUIRED_BYTES_FOR_ENCODING + 1);

		//res_ser = IntValue_dec(buffer, &errCode);
		BitStream bitStrmDec;
		BitStream_AttachBuffer(&bitStrmDec, decBuff, VdmInteger_REQUIRED_BYTES_FOR_ENCODING);
		VdmInteger_Decode(&res_ser, &bitStrmDec, &errCode);

		// Convert back to TVP
		TVP res;
		//res->type = VDM_QUOTE;
		fromQuote2VdmQuote(&res_ser, &res);

		printf("Deserialising Quote result: %d",  res->value.quoteVal);

		return res;
	}

	if(decFullBuff[0]==VDM_REAL){
		int errCode;
		VdmReal res_ser;
		byte decBuff[VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1];

		memcpy(decBuff, decFullBuff+1, VdmReal_REQUIRED_BYTES_FOR_ENCODING + 1);

		//res_ser = IntValue_dec(buffer, &errCode);
		BitStream bitStrmDec;
		BitStream_AttachBuffer(&bitStrmDec, decBuff, VdmReal_REQUIRED_BYTES_FOR_ENCODING);
		VdmReal_Decode(&res_ser, &bitStrmDec, &errCode);

		// Convert back to TVP
		TVP res;
		//res->type = VDM_REAL;
		fromReal2VdmReal(&res_ser, &res);

		printf("Deserialising Real result: %f",  res->value.doubleVal);

		return res;
	}
	//res->value.intVal = 2;

	return NULL;
}

