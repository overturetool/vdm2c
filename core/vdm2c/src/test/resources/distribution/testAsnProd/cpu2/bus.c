// The Send_Bus and HandleRecieved calls

#include "bus.h"
#include "asn1crt.h"
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


// TODO: Implemented by user, lower-level part
TVP deconstructData(byte* data, int len){

	// Simple deserialisation
	int buf_size = (int) data[0];
	int objID = (int) data[1];
	int funID = (int) data[2];
	int supID = (int) data[3];
	int nr_args = (int) data[4];

	int max_args = 10;

	//TVP args[max_args];

	TVP args[2] = {newInt(2), newInt(2)};

	deserialise(data, 2, args);

	// Obtain the result from function call
	// The getResult function is supported by the code generator
	// So this is the high level function
	TVP res = getRes(objID, funID, supID, nr_args, args);

	//TVP res = newInt(5);
	return res;

}

/** Read from the UART */
// TODO: To be replaced with the real UART read
int busRead(byte *buffer, int len){ // Just for own testing
	/*
	s[0] = (byte) 8; // Buffer size is 8
	s[1] = (byte)  1; // Object ID is 1
	s[2] = (byte)  4; // Function ID is 4
	s[3] = (byte)  2; // Number of arguments is 2
	s[4] = (byte)  11; // ID of type is 11 (e.g. a quote)
	s[5] = (byte)  6; // Value of type is 6 (e.g. FREE quote)
	s[6] = (byte)  11; // ID of type is 11 (e.g. a quote)
	s[7] = (byte)  6; // Value of type is 6 (e.g. FREE quote)
	 */
	int fd, ret_val, count, numread;
 
    /* Create the named - pipe */
    ret_val = mkfifo(HALF_DUPLEX1, 0666);
 
    if ((ret_val == -1) && (errno != EEXIST)) {
        perror("Error creating the named pipe");
        exit (1);
    }
 
    /* Open the pipe for reading */
    fd = open(HALF_DUPLEX1, O_RDONLY);

    /*
    FILE* fptr;
	fptr = fopen("../sync.txt", "w");
	fclose(fptr);
	*/
 
    /* Read from the pipe */
    numread = read(fd, buffer, BUF_SIZE);

	return 1;



}

/*
IntValue iu = 4;
int pErrCode;
BitStream bitStrm;
char encBuff[IntValue_REQUIRED_BYTES_FOR_ENCODING + 1];
BitStream_Init(&bitStrm, encBuff, IntValue_REQUIRED_BYTES_FOR_ENCODING);
IntValue_Encode(&iu, &bitStrm, &pErrCode, TRUE);
*/

// TODO: Implement with lower send of message
void sendRes(TVP res, int newsockfd){

	// send result back
	//char buf[1];
	//buf[0] = (char) 5;

	//char buf[IntValue_REQUIRED_BYTES_FOR_ENCODING + 1];

	IntValue val;
	fromVdmInt2Int(&res, &val);

	int pErrCode;
	BitStream bitStrm;
	char encBuff[IntValue_REQUIRED_BYTES_FOR_ENCODING + 1];
	BitStream_Init(&bitStrm, encBuff, IntValue_REQUIRED_BYTES_FOR_ENCODING);
	IntValue_Encode(&val, &bitStrm, &pErrCode, TRUE);

	int n;
	int fd;

	fd = open(HALF_DUPLEX2, O_WRONLY, O_NONBLOCK);
 
    /* Write to the pipe */
   	write(fd, encBuff, BUF_SIZE);
}

void handleReciever(){
	// Array contains result from reading UART
	byte recArr[BUF_SIZE];

	int soc;

	// Read the whole buffer size
	soc = busRead(recArr, BUF_SIZE);

	// Deconstruct data
	TVP res = deconstructData(recArr, BUF_SIZE);

	if(res==NULL)
		printf("Invoked function is a void \n");
	else
		sendRes(res, soc); // Send result back

}

TVP bus_send(int objID, int funID, int supID, int nrArgs, va_list args){
	TVP res;

	byte sendArr[BUF_SIZE]; // Array to be send

	// Simple serialization of the known types
	sendArr[1] = (byte) objID;
	sendArr[2] = (byte) funID;
	sendArr[3] = (byte) supID;
	sendArr[4] = (byte) nrArgs;

	int i;
	// Loop through all arguments and serialise them
	int b = 4;
	/*
		for (i = 3; i < nrArgs + 3; i++) {
			TVP arg = va_arg(args, TVP);


			// TODO: Move this in a serialization function
			if(arg->type==VDM_QUOTE) {
				b++;
				sendArr[b] = (byte) VDM_QUOTE; // add type to array
				b++;
				sendArr[b] = (byte) arg->value.quoteVal; // add value to array
			}
		}
	 */
	byte buf_size = (byte) b + 1;

	sendArr[0] = buf_size;

	printf("Buffer size is: %d \n", buf_size);

	// print serialised values
	for (i = 0; i < buf_size; i++)
		printf("Hello UART: %d \n", sendArr[i]);

	// TODO: While, wait for result
	// If we need to wait for a result

	int sockfd, portno, n;
	struct sockaddr_in serv_addr;
	struct hostent *server;

	char buffer[256];

	//buffer = encBuf ;

	//if (argc < 3) {
	// fprintf(stderr,"usage %s hostname port\n", argv[0]);
	//exit(0);
	//}
	portno = atoi("51717");

	sockfd = socket(AF_INET, SOCK_STREAM, 0);

	if (sockfd < 0)
		error("ERROR opening socket");

	server = gethostbyname("localhost");

	if (server == NULL) {
		fprintf(stderr,"ERROR, no such host\n");
		exit(0);
	}

	bzero((char *) &serv_addr, sizeof(serv_addr));

	serv_addr.sin_family = AF_INET;

	bcopy((char *)server->h_addr,
			(char *)&serv_addr.sin_addr.s_addr,
			server->h_length);

	serv_addr.sin_port = htons(portno);

	if (connect(sockfd,(struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0)
		error("ERROR connecting");

	//printf("Please enter the message: ");

	//bzero(buffer,256);

	//fgets(buffer,255,stdin);

	n = write(sockfd,sendArr,strlen(sendArr));

	if (n < 0)
		error("ERROR writing to socket");

	bzero(buffer,256);

	n = read(sockfd,buffer,255);

	if (n < 0)
		error("ERROR reading from socket");

	printf("%s\n",buffer);

	close(sockfd);

	// TODO: Receive the result, e.g. change to receive value
	byte res_ser = buffer[0];

	res = vdmClone(newInt( (int) res_ser));

	return res;
}
