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

/** In this example this the recieve will never be true for CPU1 */

// Pr. CPU main
int main(void) {

	// 1. System initialization
	cpu2_init();
	TVP w = _Z5WorldEV(NULL); // gets ID 0, due to being local
	D_static_init();

	// 2. Run loop + handle receive values

	printf("Hello World! \n");


	// Handle the reception

	handleReciever();

	// A local call
	//TVP ret = CALL_FUNC(World, World, w, CLASS_World__Z3RunEV); // Sequential code: CALL_FUNC(World, World, w, CLASS_World__Z3RunEV);
	//printf("Value is %f \n", ret->value.doubleVal);


	return 0;
}
