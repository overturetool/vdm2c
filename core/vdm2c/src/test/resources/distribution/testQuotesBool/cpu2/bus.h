// The Send_Bus and HandleRecieved calls

#ifndef DISTCALL_H_
#define DISTCALL_H_

//#include "intVal.h"
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
#include "serialise.h"
#include "asn1vdm.h"

// Assume buffer size 100
#define BUF_SIZE 100
//#define byte char
// TODO: Can be replaced with
//#define byte uint8_t;

// Write and Read hardware functions pr. BUS
TVP busWrite(int objID, int funID, int supID, int nrArgs, va_list args);
int busRead(byte *buffer, int len);

TVP deconstructData(byte* data, int len);

TVP bus_send(int objID, int funID, int supID, int nrArgs, va_list args);

void sendRes(TVP res, int newsockfd);

void handleReciever();

#endif
