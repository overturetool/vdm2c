

/*  VERSION: For the version of VDM2C used to generate this project, refer to one of the generated files.  */


/* The Send_Bus and HandleRecieved calls */

#include "asn1vdm.h"


void fromVdmInt2Int(TVP *s, VdmInteger *d){
	*d = (*s)->value.intVal;
}

void fromInt2VdmInt(VdmInteger *s, TVP *d){
	*d = newInt(*s);
}

void fromQuote2VdmQuote(VdmInteger *s, TVP *d){
	*d = newQuote(*s);
}

void fromVdmQuote2Quote(TVP *s, VdmInteger *d){
	*d = (*s)->value.quoteVal;
}

void fromBool2VdmBool(VdmInteger *s, TVP *d){
	*d = newBool(*s);
}

void fromVdmBool2Bool(TVP *s, VdmInteger *d){
	*d = (*s)->value.boolVal;
}

void fromVdmReal2Real(TVP *s, VdmInteger *d){
	*d = (*s)->value.doubleVal;
}

void fromReal2VdmReal(VdmInteger *s, TVP *d){
	*d = newReal(*s);
}
