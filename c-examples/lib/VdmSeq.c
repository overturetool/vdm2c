/*
 * VdmSeq.c
 *
 *  Created on: Dec 1, 2015
 *      Author: kel
 */

#include "VdmSeq.h"

#define ASSERT_CHECK(s) assert(s->type == VDM_SEQ && "Value is not a sequence")

struct TypedValue* newSeq(size_t size)
{
	return newCollection(size, VDM_SEQ);
}

struct TypedValue* newSeqWithValues(size_t size, TVP* elements)
{
	return newCollectionWithValues(VDM_SEQ,size,elements);
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

	return newCollectionWithValues(VDM_SEQ,size,elements);
}

TVP seqHd(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);
	return vdmClone(col->value[0]);
}
TVP seqTl(TVP seq)
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
TVP seqLen(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);
	return newInt(col->size);
}
TVP seqElems(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	//malloc
	TVP elemsVal = newSet(col->size);
	struct Collection* elems = elemsVal->value.ptr;

	//copy  list
	for (int i = 0; i < col->size; i++)
	{
		elems->value[i] = vdmClone(col->value[i]);
	}

	//FIXME this should have been done using set functions checking dublicates

	return elemsVal;
}
TVP seqInds(TVP seq)
{
	ASSERT_CHECK(seq);
	UNWRAP_COLLECTION(col,seq);

	//malloc
	TVP indsVal = newSet(col->size);
	UNWRAP_COLLECTION(inds,indsVal);

	//copy  list
	for (int i = 0; i < col->size; i++)
	{
		inds->value[i] = newInt(i+1);
	}

	return indsVal;
}
TVP seqConc(TVP seq,TVP seq2)
{
	ASSERT_CHECK(seq);
	ASSERT_CHECK(seq2);
	UNWRAP_COLLECTION(col,seq);
	UNWRAP_COLLECTION(col2,seq2);

	//malloc
	TVP concVal = newSet(col->size+col2->size);
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

TVP seqReverse(TVP seq)
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

TVP seqIndex(TVP seq,TVP indexVal) //VDM uses 1 based index
{
	ASSERT_CHECK(seq);
	assert((indexVal->type == VDM_INT||indexVal->type == VDM_INT1) && "index is not a int");

	int index = indexVal->value.intVal;
	UNWRAP_COLLECTION(col,seq);

	assert(index-1>=0 && index-1<col->size && "invalid index");
	return vdmClone(col->value[index-1]);
}
TVP seqEqual(TVP seq,TVP seq2)
{
	ASSERT_CHECK(seq);
	ASSERT_CHECK(seq2);

	return newBool(collectionEqual(seq,seq2));
}
TVP seqInEqual(TVP seq,TVP seq2)
{
	ASSERT_CHECK(seq);
	ASSERT_CHECK(seq2);

	return newBool(!collectionEqual(seq,seq2));
}
