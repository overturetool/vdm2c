// The Send_Bus and HandleRecieved calls

#include "distCall.h"
#include "asn1crt.h"
#include <unistd.h>


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

	//int i;

	// Loop through all arguments and serialise them
	int b = 4;

	serialise(sendArr, nrArgs, args);

	byte buf_size = (byte) b + 1;

	sendArr[0] = buf_size;

	printf("Buffer size is: %d \n", buf_size);

	int sockfd, portno, n;
	struct sockaddr_in serv_addr;
	struct hostent *server;

	char buffer[256];

	int nb = -1;

	while(nb<0){
		sleep(2);
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

	}
	n = write(sockfd,sendArr,BUF_SIZE);

	if (n < 0)
		error("ERROR writing to socket");

	bzero(buffer,256);

	n = read(sockfd,buffer,255);

	if (n < 0)
		error("ERROR reading from socket");

	//printf("%s\n",buffer);

	close(sockfd);

	// Convert back to TVP
	TVP res;

	res = deserialiseRes(buffer);

	printf("Result is : %d \n", res->value.boolVal);

	return res;
}
