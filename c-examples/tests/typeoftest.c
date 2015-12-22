/*
 * typeoftest.c
 *
 *  Created on: Dec 22, 2015
 *      Author: kel
 */
#include "classes/A.h"
#include "classes/B.h"
#include "classes/C.h"

#include <stdio.h>

#ifdef __cplusplus
#else
//#define X(a,b) (&a==&b?1:0)
//#define X(a,b) (sizeof(a)==sizeof(b)?1:0)

#define min(x,y) ({ \
    #x == #y; })

#define SAME_ARGS(x,y) #x==#y
#endif

bool typeoftest(int id)
{
	bool i = 0;
	switch (id)
	{
	case 0:
		i = true;
		break;
	case 1:
		printf("A = A:");
		i = SAME_ARGS(A, A);
		printf(" %s\n", i ? "true" : "false");
		break;

	case 2:
		printf("A = B:", i);
		i = SAME_ARGS(A, B);
		printf(" %s\n", i ? "true" : "false");
		i = !i;
		break;
	case 3:
		printf("A = B:", i);
		i = SAME_ARGS(A, B);
		printf(" %s\n", i ? "true" : "false");
		i = !i;
		break;
	case 4:
		printf("A = A:", i);
		i = SAME_ARGS(A, A);
		printf(" %s\n", i ? "true" : "false");
		break;
	case 5:
		printf("C = A:", i);
		i = !SAME_ARGS(C, A);
		printf(" %s\n", i ? "true" : "false");
		i = !i;
		break;
	case 6:
		printf("C = C:", i);
		i = SAME_ARGS(C, C);
		printf(" %s\n", i ? "true" : "false");
		break;
	case 7:
		printf("C = B:", i);
		i = !SAME_ARGS(C, B);
		printf(" %s\n", i ? "true" : "false");
		i = !i;
		break;
	case 8:
		printf("B = B:", i);
		i = SAME_ARGS(B, B);
		printf(" %s\n", i ? "true" : "false");

		break;
	}

	return i;

}
