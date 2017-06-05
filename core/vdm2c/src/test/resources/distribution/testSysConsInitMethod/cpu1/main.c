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
#include "intVal.h"
 #include <unistd.h>

/** In this example this the recieve will never be true for CPU1 */

#define SIZE 100

// Pr. CPU main
int main(void) {

	sleep(5);

	vdm_gc_init();

	// 1. System initialization
	D_static_init();
	cpu1_init();

	TVP w = _Z5WorldEV(NULL); // gets ID 0, due to being local

	// 2. Run loop + handle receive values
	printf("Hello World! \n");

	// A local call
	TVP ret = CALL_FUNC(World, World, w, CLASS_World__Z3RunEV); // Sequential code: CALL_FUNC(World, World, w, CLASS_World__Z3RunEV);
	printf("Value is %d \n", ret->value.intVal);

	return (ret->value.intVal==38)?0:1;
}
