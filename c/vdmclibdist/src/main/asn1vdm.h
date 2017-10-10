/* The Send_Bus and HandleRecieved calls */

#ifndef ASN1VDM_H_
#define ASN1VDM_H_

#include "basicTypes.h"
#include "Vdm.h"
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

void fromVdmQuote2Quote(TVP *s, VdmInteger *d);

#endif
