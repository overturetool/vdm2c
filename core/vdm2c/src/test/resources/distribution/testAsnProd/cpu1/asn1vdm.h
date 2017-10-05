// The Send_Bus and HandleRecieved calls

#ifndef SERIALISE_H_
#define SERIALISE_H_

#include "intVal.h"
#include "Vdm.h"
#include "D.h"
#include <stdarg.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "asn1crt.h"


void fromVdmInt2Int(TVP *s, IntValue *d);

void fromInt2VdmInt(IntValue *s, TVP *d);

#endif
