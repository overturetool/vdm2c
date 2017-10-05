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

	//int i;

	// Loop through all arguments and serialise them
	int b = 4;

	serialise(sendArr, nrArgs, args);

	byte buf_size = (byte) b + 1;

	sendArr[0] = buf_size;

	printf("Buffer size is: %d \n", buf_size);


	char buffer[256];
	int fd1;

	// Write to cpu2
	/* Open the pipe for writing */
    fd1 = open(HALF_DUPLEX1, O_WRONLY, O_NONBLOCK);
 
    /* Write to the pipe */
    write(fd1, sendArr, BUF_SIZE);

    int ret_val;
    // Get result
    // ret_val = mkfifo(HALF_DUPLEX2, 0666);
 
    // if ((ret_val == -1) && (errno != EEXIST)) {
    //     perror("Error creating the named pipe");
    //     exit (1);
    // }
 	
 	int fd2;
    /* Open the pipe for reading */
    fd2 = open(HALF_DUPLEX2, O_RDONLY);
 
    int numread;

    /* Read from the pipe */
    numread = read(fd2, buffer, BUF_SIZE);

	// Convert back to TVP
	TVP res;
	res = deserialiseRes(buffer);

	printf("Result is : %d \n", res->value.boolVal);

	return res;
}
