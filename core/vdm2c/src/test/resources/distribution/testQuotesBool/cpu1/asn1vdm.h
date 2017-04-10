// The Send_Bus and HandleRecieved calls

#ifndef SERIALISE_H_
#define SERIALISE_H_

//#include "intVal.h"
#include "basicTypes.h"
#include "Vdm.h"
#include "D.h"
#include <stdarg.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "asn1crt.h"


void fromVdmInt2Int(TVP *s, VdmInteger *d);
void fromInt2VdmInt(VdmInteger *s, TVP *d);

void fromVdmQuote2Int(TVP *s, VdmInteger *d);
void fromQuote2VdmQuote(VdmInteger *s, TVP *d);

void fromVdmBool2Bool(TVP *s, VdmInteger *d);
void fromBool2VdmBool(VdmInteger *s, TVP *d);

void fromVdmReal2Real(TVP *s, VdmInteger *d);
void fromReal2VdmReal(VdmInteger *s, TVP *d);

#endif
