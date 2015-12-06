/*
 * Globals.h
 *
 *  Created on: Nov 24, 2015
 *      Author: kel
 */

#ifndef GLOBALS_H_
#define GLOBALS_H_

#define ModelVarBOOL_ID 123


#define TVP struct TypedValue*

#define UNWRAP_PRODUCT(var,product) struct Collection* var = (struct Collection*)product->value.ptr
#define UNWRAP_COLLECTION(var,collection) struct Collection* var = (struct Collection*)collection->value.ptr
#define UNWRAP_MAP(var,map) struct Map* var = (struct Map*)map->value.ptr

#ifndef FATAL_ERROR
#define FATAL_ERROR(message) exit(EXIT_FAILURE)
#endif

typedef void (*freeVdmClassFunction)(void*);

#endif /* GLOBALS_H_ */
