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
 * VdmSeq.c
 *
 *  Created on: Dec 1, 2015
 *      Author: kel
 */
#include <stdarg.h>
#include "VdmSeq.h"
#include "VdmGC.h"
#include <assert.h>

#ifndef NO_SEQS

#define ASSERT_CHECK(s) assert(s->type == VDM_SEQ && "Value is not a sequence")

#define DEFAULT_SEQ_COMP_BUFFER 2
#define DEFAULT_SEQ_COMP_BUFFER_STEPSIZE 10

/* Utility functions.  */
/* ------------------------------------------------  */
static void vdmSeqAdd(TVP* value, int* index, TVP newValue)
{
	value[*index] = newValue;
	*index = (*index) + 1;
}
/* End utility functions  */
/* ------------------------------------------------  */





TVP newSeq(size_t size)
{
	return newCollection(size, VDM_SEQ);
}

TVP newSeqWithValues(size_t size, TVP* elements)
{
	return newCollectionWithValues(size, VDM_SEQ,elements);
}

TVP newSeqVar(size_t size, ...)
{
	int i;
	TVP arg;
	TVP *elements;
	TVP res;

	elements = (TVP *)calloc(size, sizeof(TVP));

	va_list ap;
	va_start(ap, size);

	for (i = 0; i < size; i++)
	{
		arg = va_arg(ap, TVP);
		elements[i] = arg;
	}
	va_end(ap);

	res = newCollectionWithValues(size, VDM_SEQ, elements);
	free(elements);

	return res;
}

TVP newSeqVarGC(size_t size, TVP *from, ...)
{
	int i;
	TVP arg;
	TVP res;
	TVP *elements;

	elements = (TVP *)calloc(size, sizeof(TVP));

	va_list ap;
	va_start(ap, from);

	for (i = 0; i < size; i++)
	{
		arg = va_arg(ap, TVP);
		elements[i] = arg;
	}
	va_end(ap);

	res = newCollectionWithValuesGC(size, VDM_SEQ, elements, from);
	free(elements);

	return res;
}

/* Just like newSeqVar, but with memory preallocated to an expected  */
/* result sequence length.  */
TVP newSeqVarToGrow(size_t size, size_t expected_size, ...)
{
	int i;

	va_list ap;
	va_start(ap, expected_size);

	int count = 0;

	int bufsize = expected_size;

	TVP* value = (TVP*) calloc(bufsize, sizeof(TVP));
	assert(value != NULL);

	TVP arg;
	TVP v;

	for(i = 0; i < size; i++)
	{
		arg = va_arg(ap, TVP);
		v = vdmClone(arg); /*  set binding  */


		/* Extra security measure.  Will only be true if size >= expected_size.  */
		if(count >= bufsize)
		{
			/* buffer too small add memory chunk  */
			bufsize += DEFAULT_SEQ_COMP_BUFFER_STEPSIZE;
			value = (TVP*)realloc(value, bufsize * sizeof(TVP));
			assert(value != NULL);
		}
		vdmSeqAdd(value, &count, v);
	}

	va_end(ap);

	TVP res = newCollectionWithValuesPrealloc(count, bufsize, VDM_SEQ, value);
	free(value);
	return res;
}

void vdmSeqGrow(TVP seq, TVP element)
{
	UNWRAP_COLLECTION(col, seq);

	if(col->size >= col->buf_size)
	{
		col->buf_size += DEFAULT_SEQ_COMP_BUFFER_STEPSIZE;
		col->value = (TVP*)realloc(col->value, col->buf_size * sizeof(TVP));
		assert(col->value != NULL);
	}
	vdmSeqAdd(col->value, &(col->size), element);
}

void vdmSeqFit(TVP seq)
{
	UNWRAP_COLLECTION(col, seq);

	/* Assumes that more memory is allocated in the col->value array than there are elements.  */
	col->value = (TVP*)realloc(col->value, col->size * sizeof(TVP));
	assert(col->value != NULL);
}

TVP vdmSeqHd(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	assert(col->size != 0 && "Can not take head of empty sequence.\n");

	return vdmClone(col->value[0]);
}

TVP vdmSeqHdGC(TVP seq, TVP *from)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	assert(col->size != 0 && "Can not take head of empty sequence.\n");

	return vdmCloneGC(col->value[0], from);
}

TVP vdmSeqTl(TVP seq)
{
	int i;

	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	assert(col->size != 0 && "Can not take tail of empty sequence.\n");

	/* malloc  */
	TVP tailVal = newSeq(col->size-1);
	UNWRAP_COLLECTION(tail,tailVal);

	/* copy tail list  */
	for (i = 1; i < col->size; i++)
	{
		tail->value[i-1] = vdmClone(col->value[i]);
	}

	return tailVal;
}

TVP vdmSeqTlGC(TVP seq, TVP *from)
{
	TVP tmp;
	TVP res;

	tmp = vdmSeqTl(seq);
	res = vdmCloneGC(tmp, from);
	vdmFree(tmp);

	return res;
}

TVP vdmSeqLen(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);
	return newInt(col->size);
}

TVP vdmSeqLenGC(TVP seq, TVP *from)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);
	return newIntGC(col->size, from);
}

#ifndef NO_SETS
TVP vdmSeqElems(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	TVP elemsVal = newSetWithValues(col->size, col->value);

	return elemsVal;
}


TVP vdmSeqElemsGC(TVP seq, TVP *from)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	TVP elemsVal = newSetWithValuesGC(col->size, col->value, from);

	return elemsVal;
}


TVP vdmSeqInds(TVP seq)
{
	int i;

	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	TVP* value = (TVP*) calloc(col->size, sizeof(TVP));
	assert(value != NULL);

	/* copy  list  */
	for (i = 0; i < col->size; i++)
	{
		value[i] = newInt(i+1);
	}

	TVP indsVal = newSetWithValues(col->size, value);

	return indsVal;
}


TVP vdmSeqIndsGC(TVP seq, TVP *from)
{
	TVP tmp;
	TVP res;

	tmp = vdmSeqInds(seq);
	res = vdmCloneGC(tmp, from);
	vdmFree(tmp);

	return res;
}
#endif

TVP vdmSeqConc(TVP seq,TVP seq2)
{
	int i;

	ASSERT_CHECK(seq);
	ASSERT_CHECK(seq2);
	UNWRAP_COLLECTION(col,seq);
	UNWRAP_COLLECTION(col2,seq2);

	/* malloc  */
	TVP concVal = newSeq(col->size+col2->size);
	UNWRAP_COLLECTION(concSeq,concVal);

	/* copy  list  */
	for (i = 0; i < col->size; i++)
	{
		concSeq->value[i] = vdmClone(col->value[i]);
	}

	int offset = col->size;
	for (i = 0; i < col2->size; i++)
	{
		concSeq->value[i+offset] = vdmClone(col2->value[i]);
	}

	return concVal;
}

TVP vdmSeqConcGC(TVP seq, TVP seq2, TVP *from)
{
	TVP tmp;
	TVP res;

	tmp = vdmSeqConc(seq, seq2);
	res = vdmCloneGC(tmp, from);
	vdmFree(tmp);

	return res;
}

TVP vdmSeqReverse(TVP seq)
{
	int i;

	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	/* malloc  */
	TVP elemsVal = newSeq(col->size);
	UNWRAP_COLLECTION(elems,elemsVal);

	int offset = col->size-1;
	/* copy  list  */
	for (i = 0; i < col->size; i++)
	{
		elems->value[i] = vdmClone(col->value[offset - i]);
	}

	return elemsVal;
}

TVP vdmSeqReverseGC(TVP seq, TVP *from)
{
	TVP tmp;
	TVP res;

	tmp = vdmSeqReverse(seq);
	res = vdmCloneGC(tmp, from);
	vdmFree(tmp);

	return res;
}

TVP vdmSeqMod(TVP seq,TVP map)
{
	ASSERT_CHECK(seq);
	assert((map->type == VDM_MAP) && "Overriding expression is not a map.\n");

	UNWRAP_COLLECTION(s,seq);

	TVP res = newSeq(s->size);

	UNWRAP_COLLECTION(r,res);

	int i;
	for (i = 0; i < s->size; i++) {
		r->value[i] = vdmClone(s->value[i]);
	}

	TVP dom = vdmMapDom(map);
	UNWRAP_COLLECTION(d,dom);

	TVP key;
	TVP val;
	for (i = 0; i < d->size; i++) {

		key = d->value[i];
		assert((key->type == VDM_INT || key->type == VDM_NAT || key->type == VDM_NAT1) && "Overriding expression key is not an integer.\n");
		assert(key->value.intVal >= 1 && key->value.intVal <= s->size && "Overriding expression key not in sequence index range.\n");
		val = vdmMapApply(map,key);
		r->value[key->value.intVal - 1] = val;
	}

	return res;
}


TVP vdmSeqModGC(TVP seq,TVP map, TVP *from)
{
	TVP tmp;
	TVP res;

	tmp = vdmSeqMod(seq, map);
	res = vdmCloneGC(tmp, from);
	vdmFree(tmp);

	return res;
}


TVP vdmSeqIndex(TVP seq, TVP indexVal) /* VDM uses 1 based index  */
{
	ASSERT_CHECK(seq);
	assert((indexVal->type == VDM_INT||indexVal->type == VDM_NAT||indexVal->type == VDM_NAT1) && "index is not a int");

	int index = indexVal->value.intVal;
	UNWRAP_COLLECTION(col,seq);

	assert(index - 1 >= 0 && index - 1 < col->size && "invalid index");
	return vdmClone(col->value[index-1]);
}

TVP vdmSeqIndexGC(TVP seq, TVP indexVal, TVP *from) /* VDM uses 1 based index  */
{
	TVP tmp;
	TVP res;

	tmp = vdmSeqIndex(seq, indexVal);
	res = vdmCloneGC(tmp, from);
	vdmFree(tmp);

	return res;
}

void vdmSeqUpdate(TVP seq, TVP indexVal, TVP newValue)
{
	ASSERT_CHECK(seq);
	assert((indexVal->type == VDM_INT||indexVal->type == VDM_NAT||indexVal->type == VDM_NAT1) && "index is not a int");

	int index = indexVal->value.intVal;
	UNWRAP_COLLECTION(col, seq);

	assert(index - 1 >= 0 && index - 1 < col->size && "invalid index");
	col->value[index - 1] = vdmClone(newValue);
}

#endif /* NO_SEQS */
