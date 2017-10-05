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
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include "asn1vdm.h"

void serialiseInt(byte* encBuff, TVP t, int pos);
void serialise(byte* encBuff, int size, va_list args);

void deserialiseInt(byte* decBuff, TVP* t, int pos);
void deserialise(byte* decBuff, int size, TVP args[]);

#endif
