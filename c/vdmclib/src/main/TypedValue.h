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
 * <http:XXXwww.gnu.org/licenses/gpl-3.0.html>.
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

#include "VdmDefines.h"

/* Eclipse hack  */
#if !defined(va_arg)
#define va_arg(ap,TVP) NULL /* just for Eclipse must not be defined  */
#endif

#define recursiveFree vdmFree


/* ,VDM_UNION  */
typedef enum
{
	VDM_INT,
	VDM_NAT,
	VDM_NAT1,
	VDM_BOOL,
	VDM_REAL,
	VDM_RAT,
	VDM_CHAR,
#ifndef NO_SETS
	VDM_SET,
#endif
#ifndef NO_SEQS
	VDM_SEQ,
#endif
#ifndef NO_MAPS
	VDM_MAP,
#endif
#ifndef NO_PRODUCTS
	VDM_PRODUCT,
#endif
	VDM_QUOTE,
	/* 	VDM_OPTIONAL, think we will handle this with a TVP == NULL  */
#ifndef NO_RECORDS
	VDM_RECORD,
#endif
	VDM_TOKEN,
	VDM_CLASS
} vdmtype;

/* typedef TypedValueType =  */
typedef union TypedValueType
{
	/* VDM_SET, SEQ, Map, Product and class  */
	void* ptr;

	/* VDM_INT + INT1  */
	int intVal;

	/* VDM_BOOL  */
	bool boolVal;

	/* VDM_REAL  */
	double doubleVal;

	/* VDM_CHAR  */
	char charVal;

	/* VDM_QUOTE  */
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
	TVP* value;
	int size;
};

int vdmCollectionSize(TVP collection);
TVP vdmCollectionIndex(TVP collection,int index);


#if defined(NO_SEQS) && defined(NO_SETS) && defined(NO_PRODUCTS)
#define ASSERT_CHECK_COLLECTION(s) assert(true)

#elif defined(NO_SEQS) && defined(NO_SETS) && !defined(NO_PRODUCTS)
#define ASSERT_CHECK_COLLECTION(s) assert((s->type == VDM_PRODUCT) &&"Value is not a collection")

#elif defined(NO_SEQS) && !defined(NO_SETS) && defined(NO_PRODUCTS)
#define ASSERT_CHECK_COLLECTION(s) assert((s->type == VDM_SET) &&"Value is not a collection")

#elif defined(NO_SEQS) && !defined(NO_SETS) && !defined(NO_PRODUCTS)
#define ASSERT_CHECK_COLLECTION(s) assert((s->type == VDM_SET || s->type == VDM_PRODUCT) &&"Value is not a collection")

#elif !defined(NO_SEQS) && defined(NO_SETS) && defined(NO_PRODUCTS)
#define ASSERT_CHECK_COLLECTION(s) assert((s->type == VDM_SEQ) &&"Value is not a collection")

#elif !defined(NO_SEQS) && defined(NO_SETS) && !defined(NO_PRODUCTS)
#define ASSERT_CHECK_COLLECTION(s) assert((s->type == VDM_SEQ || s->type == VDM_PRODUCT) &&"Value is not a collection")

#elif !defined(NO_SEQS) && !defined(NO_SETS) && defined(NO_PRODUCTS)
#define ASSERT_CHECK_COLLECTION(s) assert((s->type == VDM_SEQ || s->type == VDM_SET) &&"Value is not a collection")

#elif !defined(NO_SEQS) && !defined(NO_SETS) && !defined(NO_PRODUCTS)
#define ASSERT_CHECK_COLLECTION(s) assert((s->type == VDM_SEQ || s->type == VDM_SET || s->type == VDM_PRODUCT) &&"Value is not a collection")

#endif

#define UNWRAP_COLLECTION(var,collection) struct Collection* var = (struct Collection*)collection->value.ptr
#define UNWRAP_PRODUCT(var,product) struct Collection* var = (struct Collection*)product->value.ptr

struct OptionalType
{
	bool hasValue;
	struct TypedValue value;
};

TVP newTypeValue(vdmtype type, TypedValueType value);


/*  Basic - these should inline  */
TVP newInt(int x);
TVP newBool(bool x);
TVP newReal(double x);
TVP newChar(char x);
TVP newQuote(unsigned int x);
TVP newToken(TVP x);




/*  Complex  */


/* utils  */
TVP newCollectionWithValues(size_t size, vdmtype type, TVP* elements);
TVP newCollection(size_t size, vdmtype type);
TVP newCollectionGC(size_t size, vdmtype type, TVP *from);
TVP newCollectionWithValuesGC(size_t size, vdmtype type, TVP* elements, TVP *from);

TVP vdmClone(TVP x);

bool equals(TVP a, TVP b);
TVP vdmEquals(TVP a, TVP b);
TVP vdmEqualsGC(TVP a, TVP b, TVP *from);
TVP vdmInEquals(TVP a, TVP b);
TVP vdmInEqualsGC(TVP a, TVP b, TVP *from);
bool collectionEqual(TVP col1,TVP col2);

void vdmFree(TVP ptr);

extern TVP newSetVar(size_t size,...);

#ifndef NO_MAPS
extern TVP vdmMapEquals(TVP map1, TVP map2);
#endif

extern TVP vdmSetEquals(TVP set1, TVP set2);
extern void remove_allocd_mem_node_by_location(TVP loc);


#endif /* TYPEDVALUE_H_ */
