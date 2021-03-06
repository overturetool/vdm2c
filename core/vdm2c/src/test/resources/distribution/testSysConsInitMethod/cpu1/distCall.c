// The Send_Bus and HandleRecieved calls

#include "distCall.h"
#include "asn1crt.h"
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>


void error(const char *msg)
{
	perror(msg);
	exit(1);
}

TVP bus(int objID, int funID, int supID, int nrArgs, va_list args){
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

	//buffer = encBuf ;

	//if (argc < 3) {
	// fprintf(stderr,"usage %s hostname port\n", argv[0]);
	//exit(0);
	//}

	int nb = -1;

	FILE* fptr = NULL;

	while(fptr == NULL){
		sleep(2);
		printf("File does not exist yet baby!\n");
		fptr = fopen("../sync.txt", "r");

	}
	remove("../sync.txt");
	
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

	nb = connect(sockfd,(struct sockaddr *) &serv_addr,sizeof(serv_addr));

	n = write(sockfd,sendArr,BUF_SIZE);

	if (n < 0)
		error("ERROR writing to socket");

	bzero(buffer,256);

	n = read(sockfd,buffer,255);

	if (n < 0)
		error("ERROR reading from socket");

	printf("%s\n",buffer);

	close(sockfd);

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
