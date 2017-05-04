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

static TVP newSeqGC(size_t size, TVP *from)
{
	return newCollectionGC(size, VDM_SEQ, from);
}

TVP newSeqWithValues(size_t size, TVP* elements)
{
	return newCollectionWithValues(size, VDM_SEQ,elements);
}

TVP newSeqVar(size_t size, ...)
{
	int i;
	TVP elements[size];

	va_list ap;
	va_start(ap, size);

	for (i = 0; i < size; i++)
	{
		TVP arg = va_arg(ap, TVP);
		elements[i]=arg;
	}
	va_end(ap);

	return newCollectionWithValues(size, VDM_SEQ, elements);
}

TVP newSeqVarGC(size_t size, TVP *from, ...)
{
	int i;
	TVP elements[size];

	va_list ap;
	va_start(ap, from);

	for (i = 0; i < size; i++)
	{
		TVP arg = va_arg(ap, TVP);
		elements[i] = arg;
	}
	va_end(ap);

	return newCollectionWithValuesGC(size, VDM_SEQ, elements, from);
}

/* Just like newSeqVar, but with memory preallocated to an expected  */
/* result sequence length.  */
TVP newSeqVarToGrow(size_t size, size_t expected_size, ...)
{
	int i;

	va_list ap;
	va_start(ap, expected_size);

	int count = 0;

	int bufsize = expected_size;  /* DEFAULT_SEQ_COMP_BUFFER;  */
	TVP* value = (TVP*) calloc(bufsize, sizeof(TVP));

	for(i = 0; i < size; i++)
	{
		TVP arg = va_arg(ap, TVP);
		TVP v= vdmClone(arg); /*  set binding  */


		/* Extra security measure.  Will only be true if size >= expected_size.  */
		if(count>=bufsize)
		{
			/* buffer too small add memory chunk  */
			bufsize += DEFAULT_SEQ_COMP_BUFFER_STEPSIZE;
			value = (TVP*)realloc(value, bufsize * sizeof(TVP));
		}
		vdmSeqAdd(value,&count,v);
	}

	va_end(ap);

	TVP res = newCollectionWithValues(count, VDM_SEQ, value);
	free(value);
	return res;
}

void vdmSeqGrow(TVP seq, TVP element)
{
	int bufsize = DEFAULT_SEQ_COMP_BUFFER;

	UNWRAP_COLLECTION(col, seq);

	if(col->size >= bufsize)
	{
		/* buffer too small add memory chunk  */
		bufsize += DEFAULT_SEQ_COMP_BUFFER_STEPSIZE;
		col->value = (TVP*)realloc(col->value, bufsize * sizeof(TVP));
	}
	vdmSeqAdd(col->value, &(col->size), element);
}

void vdmSeqFit(TVP seq)
{
	UNWRAP_COLLECTION(col, seq);

	/* Assumes that more memory is allocated in the col->value array than there are elements.  */
	col->value = (TVP*)realloc(col->value, col->size * sizeof(TVP));
}

TVP vdmSeqHd(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);
	return vdmClone(col->value[0]);
}

TVP vdmSeqHdGC(TVP seq, TVP *from)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);
	return vdmCloneGC(col->value[0], from);
}

TVP vdmSeqTl(TVP seq)
{
	int i;

	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

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
	int i;

	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	/* malloc  */
	TVP tailVal = newSeqGC(col->size - 1, from);
	UNWRAP_COLLECTION(tail,tailVal);

	/* copy tail list  */
	for (i = 1; i < col->size; i++)
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
	int i;

	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	TVP* value = (TVP*) calloc(col->size, sizeof(TVP));

	/* copy  list  */
	for (i = 0; i < col->size; i++)
	{
		value[i] = newInt(i+1);
	}

	TVP indsVal = newSetWithValuesGC(col->size, value, from);

	return indsVal;
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
	int i;

	ASSERT_CHECK(seq);
	ASSERT_CHECK(seq2);
	UNWRAP_COLLECTION(col,seq);
	UNWRAP_COLLECTION(col2,seq2);

	/* malloc  */
	TVP concVal = newSeqGC(col->size+col2->size, from);
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
	int i;

	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	/* malloc  */
	TVP elemsVal = newSeqGC(col->size, from);
	UNWRAP_COLLECTION(elems,elemsVal);

	int offset = col->size-1;
	/* copy  list  */
	for (i = 0; i < col->size; i++)
	{
		elems->value[i] = vdmClone(col->value[offset - i]);
	}

	return elemsVal;
}

/* TVP seqMod(TVP seq,TVP seq);  */

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
	ASSERT_CHECK(seq);
	assert((indexVal->type == VDM_INT||indexVal->type == VDM_NAT||indexVal->type == VDM_NAT1) && "index is not a int");

	int index = indexVal->value.intVal;
	UNWRAP_COLLECTION(col,seq);

	assert(index - 1 >= 0 && index - 1 < col->size && "invalid index");
	return vdmCloneGC(col->value[index-1], from);
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
