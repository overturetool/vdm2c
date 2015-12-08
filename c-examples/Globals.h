/*
 * Globals.h
 *
 *  Created on: Nov 24, 2015
 *      Author: kel
 */

#ifndef GLOBALS_H_
#define GLOBALS_H_

//classes
#define CLASS_ID_ModelVarBOOL_ID 123
#define CLASS_ID_A_ID 124
#define CLASS_ID_B_ID 125

//records
#define RECORD_ID_R1 1


#define TVP struct TypedValue*

#define UNWRAP_PRODUCT(var,product) struct Collection* var = (struct Collection*)product->value.ptr
#define UNWRAP_COLLECTION(var,collection) struct Collection* var = (struct Collection*)collection->value.ptr
#define UNWRAP_MAP(var,map) struct Map* var = (struct Map*)map->value.ptr
#define UNWRAP_RECORD(var,record) struct RecordType* var = (struct RecordType*)record->value.ptr
#define UNWRAP_CLASS(var,cls) struct ClassType* var = (struct ClassType*)cls->value.ptr

#ifndef FATAL_ERROR
#define FATAL_ERROR(message) exit(EXIT_FAILURE)
#endif

typedef void (*freeVdmClassFunction)(void*);


#endif /* GLOBALS_H_ */
