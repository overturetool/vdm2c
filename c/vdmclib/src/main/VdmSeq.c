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
 * VdmSeq.c
 *
 *  Created on: Dec 1, 2015
 *      Author: kel
 */
#include <stdarg.h>
#include "VdmSeq.h"
#include <assert.h>

#define ASSERT_CHECK(s) assert(s->type == VDM_SEQ && "Value is not a sequence")

struct TypedValue* newSeq(size_t size)
{
	return newCollection(size, VDM_SEQ);
}

struct TypedValue* newSeqWithValues(size_t size, TVP* elements)
{
	return newCollectionWithValues(size, VDM_SEQ,elements);
}

struct TypedValue* newSeqVar(size_t size, ...)
{
	TVP elements[size];

	va_list ap;
	va_start(ap, size);

	for (int i = 0; i < size; i++)
	{
		TVP arg = va_arg(ap, TVP);
		elements[i]=arg;
	}
	va_end(ap);

	return newCollectionWithValues(size, VDM_SEQ, elements);
}

TVP vdmSeqHd(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);
	return vdmClone(col->value[0]);
}
TVP vdmSeqTl(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	//malloc
	TVP tailVal = newSeq(col->size-1);
	UNWRAP_COLLECTION(tail,tailVal);

	//copy tail list
	for (int i = 1; i < col->size; i++)
	{
		tail->value[i-1] = vdmClone(col->value[i]);
	}

	return tailVal;
}
TVP vdmSeqLen(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);
	return newInt(col->size);
}
TVP vdmSeqElems(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	TVP elemsVal = newSetWithValues(col->size, col->value);

	return elemsVal;
}
TVP vdmSeqInds(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	struct TypedValue** value = (struct TypedValue**) calloc(col->size, sizeof(struct TypedValue*));

	//copy  list
	for (int i = 0; i < col->size; i++)
	{
		value[i] = newInt(i+1);
	}

	TVP indsVal = newSetWithValues(col->size, value);

	return indsVal;
}
TVP vdmSeqConc(TVP seq,TVP seq2)
{
	ASSERT_CHECK(seq);
	ASSERT_CHECK(seq2);
	UNWRAP_COLLECTION(col,seq);
	UNWRAP_COLLECTION(col2,seq2);

	//malloc
	TVP concVal = newSeq(col->size+col2->size);
	UNWRAP_COLLECTION(concSeq,concVal);

	//copy  list
	for (int i = 0; i < col->size; i++)
	{
		concSeq->value[i] = vdmClone(col->value[i]);
	}

	int offset = col->size;
	for (int i = 0; i < col2->size; i++)
	{
		concSeq->value[i+offset] = vdmClone(col2->value[i]);
	}

	return concVal;
}

TVP vdmSeqReverse(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	//malloc
	TVP elemsVal = newSeq(col->size);
	UNWRAP_COLLECTION(elems,elemsVal);

	int offset = col->size-1;
	//copy  list
	for (int i = 0; i < col->size; i++)
	{
		elems->value[i] = vdmClone(col->value[offset-i]);
	}

	return elemsVal;
}

//TVP seqMod(TVP seq,TVP seq);

TVP vdmSeqIndex(TVP seq,TVP indexVal) //VDM uses 1 based index
{
	ASSERT_CHECK(seq);
	assert((indexVal->type == VDM_INT||indexVal->type == VDM_NAT||indexVal->type == VDM_NAT1) && "index is not a int");

	int index = indexVal->value.intVal;
	UNWRAP_COLLECTION(col,seq);

	assert(index-1>=0 && index-1<col->size && "invalid index");
	return vdmClone(col->value[index-1]);
}
TVP vdmSeqEqual(TVP seq,TVP seq2)
{
	ASSERT_CHECK(seq);
	ASSERT_CHECK(seq2);

	return newBool(collectionEqual(seq,seq2));
}
TVP vdmSeqInEqual(TVP seq,TVP seq2)
{
	ASSERT_CHECK(seq);
	ASSERT_CHECK(seq2);

	return newBool(!collectionEqual(seq,seq2));
}

void vdmSeqUpdate(TVP seq, TVP indexVal, TVP newValue)
{
	ASSERT_CHECK(seq);
	assert((indexVal->type == VDM_INT||indexVal->type == VDM_NAT||indexVal->type == VDM_NAT1) && "index is not a int");

	int index = indexVal->value.intVal;
	UNWRAP_COLLECTION(col,seq);

	assert(index-1>=0 && index-1<col->size && "invalid index");
	col->value[index-1] = vdmClone(newValue);
}
