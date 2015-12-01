/*
 * TypedValue.h
 *
 *  Created on: Nov 20, 2015
 *      Author: kel
 */

#ifndef TYPEDVALUE_H_
#define TYPEDVALUE_H_

#include "Globals.h"
#include <stdlib.h>
#include <stdbool.h>

#define TVP struct TypedValue*

//#define ALLOC(t,n) (t *) malloc((n)*sizeof(t))

//,VDM_UNION
typedef enum
{
	VDM_INT, VDM_INT1, VDM_BOOL, VDM_REAL, VDM_CHAR, VDM_SET, VDM_SEQ, VDM_MAP, VDM_PRODUCT, VDM_QUOTE, VDM_CLASS
} vdmtype;

//typedef TypedValueType =
typedef union TypedValueType
{
	//VDM_SET, SEQ, Map, Product and class
	void* ptr;

	//VDM_INT + INT1
	int intVal;

	//VDM_BOOL
	bool boolVal;

	//VDM_REAL
	double doubleVal;

	//VDM_CHAR
	char charVal;

	//VDM_QUOTE
	unsigned int uintVal;
} TypedValueType;

struct TypedValue
{
	vdmtype type;
	TypedValueType value;
};



struct Collection
{
	struct TypedValue** value;
	int size;
};

struct ClassType
{
	void* value;
	int classId;
	freeVdmClassFunction freeClass;
};

struct TypedValue* newTypeValue(vdmtype type, TypedValueType value);



//static struct TypedValue* newInt(int x){return newTypeValue(VDM_INT,(TypedValueType){.intVal= x});}
//static struct TypedValue* newInt1(int x){return newTypeValue(VDM_INT,(TypedValueType){.intVal= x});}
//static struct TypedValue* newBool(bool x){return newTypeValue(VDM_INT,(TypedValueType){.intVal= x});}
//static struct TypedValue* newReal(double x){return newTypeValue(VDM_INT,(TypedValueType){.intVal= x});}
//static struct TypedValue* newChar(char x){return newTypeValue(VDM_INT,(TypedValueType){.intVal= x});}
//static struct TypedValue* newQuote(unsigned int x){return newTypeValue(VDM_INT,(TypedValueType){.intVal= x});}

// Basic - these should inline
struct TypedValue* newInt(int x);
struct TypedValue* newInt1(int x);
struct TypedValue* newBool(bool x);
struct TypedValue* newReal(double x);
struct TypedValue* newChar(char x);
struct TypedValue* newQuote(unsigned int x);

// Complex
struct TypedValue* newSeq(size_t size);
struct TypedValue* newSet(size_t size);

// Class
struct ClassType* newClassValue(int id, unsigned int* refs, freeVdmClassFunction freeClass, void* value);

struct TypedValue* newInt(int x);
struct TypedValue* newDouble(double x);
struct TypedValue* newChar(char x);

//utils

struct TypedValue* clone(struct TypedValue* x);

void recursiveFree(struct TypedValue* ptr);

#endif /* TYPEDVALUE_H_ */
