/*
 ============================================================================
 Name        : PrototypeCGMacro.c
 Author      : 
 Version     :
 Copyright   : Your copyright notice
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

#include <stdarg.h>
#include <stdio.h>
#include "D.h"
#include "A.h"
#include "B.h"
#include "World.h"
#include <string.h>
#include <stdlib.h>
#include <stdarg.h>
#include <limits.h>
#include <unistd.h>

/** In this example this the recieve will never be true for CPU1 */

#define SIZE 100

// Pr. CPU main
int main(void) {
	/*
	TVP a = newInt(4);
	TVP b = newInt(8);

	byte encBuff[SIZE];

	// Encode values
	sendBus(encBuff, 2, a, b);

	// Decode values
	TVP args[2] = {newInt(2), newInt(2)};

	deserialise(encBuff, 2, args);

	printf("decoded value is %d \n", args[0]->value.intVal);
	 */

	//sleep(10);

	vdm_gc_init();

	// Receive flag, becomes true when remote invocation need to be handled
	bool rec_flag = false;

	// 1. System initialization
	TVP w = _Z5WorldEV(NULL); // gets ID 0, due to being local
	D_static_init();

	// 2. Run loop + handle receive values

	printf("Hello World! \n");

	// A local call
	TVP ret = CALL_FUNC(World, World, w, CLASS_World__Z3RunEV); // Sequential code: CALL_FUNC(World, World, w, CLASS_World__Z3RunEV);
	printf("Value is %d \n", ret->value.boolVal);

	return (ret->value.boolVal==false)?0:1;
}
