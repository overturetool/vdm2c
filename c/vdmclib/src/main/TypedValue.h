/*
 * #%~
 * The VDM to C Code Generator
 * %%
 * Copyright (C) 2015 - 2016 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

/*
 * TypedValue.h
 *
 *  Created on: Nov 20, 2015
 *      Author: kel
 */

#ifndef TYPEDVALUE_H_
#define TYPEDVALUE_H_

#include <stdlib.h>
#include <stdbool.h>


//Eclipse hack
#if !defined(va_arg)
#define va_arg(ap,TVP) NULL //just for Eclipse must not be defined
#endif

#define recursiveFree vdmFree

//#define ALLOC(t,n) (t *) malloc((n)*sizeof(t))

//,VDM_UNION
typedef enum
{
	VDM_INT,
	VDM_NAT,
	VDM_NAT1,
	VDM_BOOL,
	VDM_REAL,
	VDM_RAT,
	VDM_CHAR,
	VDM_SET,
	VDM_SEQ,
	VDM_MAP,
	VDM_PRODUCT,
	VDM_QUOTE,
	//	VDM_OPTIONAL, think we will handle this with a TVP == NULL
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
	unsigned int quoteVal;
} TypedValueType;


struct TypedValue
{
	vdmtype type;
	struct TypedValue **ref_from;
	TypedValueType value;
};

#define TVP struct TypedValue*

struct Collection
{
	struct TypedValue** value;
	int size;
};

int vdmCollectionSize(TVP collection);
TVP vdmCollectionIndex(TVP collection,int index);


#define ASSERT_CHECK_COLLECTION(s) assert((s->type == VDM_SEQ || s->type == VDM_SET || s->type == VDM_PRODUCT) &&"Value is not a collection")
#define UNWRAP_COLLECTION(var,collection) struct Collection* var = (struct Collection*)collection->value.ptr
#define UNWRAP_PRODUCT(var,product) struct Collection* var = (struct Collection*)product->value.ptr

struct OptionalType
{
	bool hasValue;
	struct TypedValue value;
};

struct TypedValue* newTypeValue(vdmtype type, TypedValueType value);
struct TypedValue* newTypeValueGC(vdmtype type, TypedValueType value, TVP *ref_from);


// Basic - these should inline
struct TypedValue* newInt(int x);
struct TypedValue* newInt1(int x);
struct TypedValue* newNat(int x);
struct TypedValue* newNat1(int x);
struct TypedValue* newBool(bool x);
struct TypedValue* newReal(double x);
struct TypedValue* newChar(char x);
struct TypedValue* newQuote(unsigned int x);

//====  Gargabe collected versions ======
struct TypedValue* newIntGC(int x, TVP *from);
struct TypedValue* newInt1GC(int x, TVP *from);
struct TypedValue* newNatGC(int x, TVP *from);
struct TypedValue* newNat1GC(int x, TVP *from);
struct TypedValue* newBoolGC(bool x, TVP *from);
struct TypedValue* newRealGC(double x, TVP *from);
struct TypedValue* newCharGC(char x, TVP *from);
struct TypedValue* newQuoteGC(unsigned int x, TVP *from);
struct TypedValue* vdmEqualsGC(struct TypedValue* a, struct TypedValue* b, TVP *from);


// Complex



//utils
struct TypedValue* newCollectionWithValues(size_t size, vdmtype type, TVP* elements);
struct TypedValue* newCollection(size_t size, vdmtype type);

struct TypedValue* vdmClone(struct TypedValue* x);
struct TypedValue* vdmCloneGC(TVP x, TVP *from);
bool equals(struct TypedValue* a, struct TypedValue* b);
TVP vdmEquals(struct TypedValue* a, struct TypedValue* b);
TVP vdmInEquals(struct TypedValue* a, struct TypedValue* b);
bool collectionEqual(TVP col1,TVP col2);

void vdmFree(struct TypedValue* ptr);



#endif /* TYPEDVALUE_H_ */
