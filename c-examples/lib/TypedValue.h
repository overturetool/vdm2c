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
#include "VdmMap.h"



#define vdmFree recursiveFree

//#define ALLOC(t,n) (t *) malloc((n)*sizeof(t))

//,VDM_UNION
typedef enum
{
	VDM_INT,
	VDM_INT1,
	VDM_BOOL,
	VDM_REAL,
	VDM_CHAR,
	VDM_SET,
	VDM_SEQ,
	VDM_MAP,
	VDM_PRODUCT,
	VDM_QUOTE,
	VDM_OPTIONAL,
	VDM_RECORD,
	VDM_CLASS
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



struct OptionalType
{
	bool hasValue;
	struct TypedValue value;
};

struct ClassType
{
	void* value;
	int classId;
	unsigned int* refs;
	freeVdmClassFunction freeClass;
};

struct RecordType
{
	void* value;
	int recordId;
	freeVdmClassFunction freeRecord;
};

struct TypedValue* newTypeValue(vdmtype type, TypedValueType value);


// Basic - these should inline
struct TypedValue* newInt(int x);
struct TypedValue* newInt1(int x);
struct TypedValue* newBool(bool x);
struct TypedValue* newReal(double x);
struct TypedValue* newChar(char x);
struct TypedValue* newQuote(unsigned int x);


// Complex


// Class

struct ClassType* newClassValue(int id, unsigned int* refs, freeVdmClassFunction freeClass, void* value);

//struct TypedValue* newInt(int x);
//struct TypedValue* newDouble(double x);
//struct TypedValue* newChar(char x);

//utils
struct TypedValue* newCollectionWithValues(vdmtype type,size_t size,TVP* elements);
struct TypedValue* newCollection(size_t size, vdmtype type);

struct TypedValue* clone(struct TypedValue* x);
bool equals(struct TypedValue* a, struct TypedValue* b);
bool collectionEqual(TVP col1,TVP col2);

void recursiveFree(struct TypedValue* ptr);

#endif /* TYPEDVALUE_H_ */
