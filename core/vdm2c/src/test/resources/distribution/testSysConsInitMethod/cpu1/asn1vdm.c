// The Send_Bus and HandleRecieved calls

#include "asn1vdm.h"


void fromVdmInt2Int(TVP *s, IntValue *d){
	*d = (*s)->value.intVal;
}

void fromInt2VdmInt(IntValue *s, TVP *d){
	*d = newInt(*s);
}
