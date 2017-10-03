/* The Send_Bus and HandleRecieved calls*/

#ifndef SERIALISE_H_
#define SERIALISE_H_

#include "basicTypes.h"
#include "Vdm.h"
#include <stdarg.h>
#include <stdio.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include "asn1vdm.h"

/* General serialisation function*/
void serialise(byte* encBuff, int size, va_list args);
void deserialise(byte* decBuff, int size, TVP args[]);

/**** Inividual Types ****/
/* Integer*/
void serialiseInt(byte* encBuff, TVP t, int pos);
void deserialiseInt(byte* decBuff, TVP* t, int pos);

/* Quote*/
void serialiseQuote(byte* encBuff, TVP t, int pos);
void deserialiseQuote(byte* decBuff, TVP* t, int pos);

/* Real*/
void serialiseReal(byte* encBuff, TVP t, int pos);
void deserialiseReal(byte* decBuff, TVP* t, int pos);

/* Result*/
void serialiseRes(byte* encBuff, TVP t);
TVP deserialiseRes(byte* decFullBuff);

#endif
