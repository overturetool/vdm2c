// The Send_Bus and HandleRecieved calls

#include "bus.h"
#include "asn1crt.h"
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <ctype.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define HALF_DUPLEX1     "../halfduplex1"
#define HALF_DUPLEX2     "../halfduplex2"


void error(const char *msg)
{
	perror(msg);
	exit(1);
}

TVP bus_send(int objID, int funID, int supID, int nrArgs, va_list args){
	//TVP res;

	byte sendArr[BUF_SIZE]; // Array to be send

	// Simple serialization of the known types
	sendArr[1] = (byte) objID;
	sendArr[2] = (byte) funID;
	sendArr[3] = (byte) supID;
	sendArr[4] = (byte) nrArgs;

	int i;
	// Loop through all arguments and serialise them
	int b = 4;

	serialise(sendArr, nrArgs, args);

	byte buf_size = (byte) b + 1;

	sendArr[0] = buf_size;

	printf("Buffer size is: %d \n", buf_size);

	// print serialised values
	//for (i = 0; i < buf_size; i++)
	//	printf("Hello UART: %d \n", sendArr[i]);

	// TODO: While, wait for result
	// If we need to wait for a result

	int sockfd, portno, n;
	struct sockaddr_in serv_addr;
	struct hostent *server;

	char buffer[256];

	int fd1;

	// Write to cpu2
	/* Open the pipe for writing */
    fd1 = open(HALF_DUPLEX1, O_WRONLY, O_NONBLOCK);
 
    /* Write to the pipe */
    write(fd1, sendArr, BUF_SIZE);

	bzero(buffer,256);

	int ret_val;
    // Get result
    ret_val = mkfifo(HALF_DUPLEX2, 0666);
 
    if ((ret_val == -1) && (errno != EEXIST)) {
        perror("Error creating the named pipe");
        exit (1);
    }
 	
 	int fd2;
    /* Open the pipe for reading */
    fd2 = open(HALF_DUPLEX2, O_RDONLY);
 
    int numread;

    /* Read from the pipe */
    numread = read(fd2, buffer, BUF_SIZE);

	/** Test code**/
	/*
	IntValue iu = 4;
	int pErrCode;
	BitStream bitStrm;
	char encBuff[IntValue_REQUIRED_BYTES_FOR_ENCODING + 1];
	BitStream_Init(&bitStrm, encBuff, IntValue_REQUIRED_BYTES_FOR_ENCODING);
    IntValue_Encode(&iu, &bitStrm, &pErrCode, TRUE);
	 */
	// TODO: Receive the result, e.g. change to receive value
	//byte res_ser = buffer[0];

	int errCode;
	IntValue res_ser;

	//res_ser = IntValue_dec(buffer, &errCode);
	BitStream bitStrmDec;
	BitStream_AttachBuffer(&bitStrmDec, buffer, IntValue_REQUIRED_BYTES_FOR_ENCODING);
	IntValue_Decode(&res_ser, &bitStrmDec, &errCode);

	// Convert back to TVP
	TVP res;
	fromInt2VdmInt(&res_ser, &res);

	//printInt(res);

	//res = vdmClone(newInt( (int) res_ser));

	return res;
}
