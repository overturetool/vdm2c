/*
 * VdmSeq.c
 *
 *  Created on: Dec 1, 2015
 *      Author: kel
 */

#include "VdmSeq.h"

#define ASSERT_CHECK(s) assert(s->type == VDM_SEQ && "Value is not a sequence")

TVP seqHd(TVP seq)
{
	ASSERT_CHECK(seq);
	struct Collection* col = (struct Collection*)seq->value.ptr;
	return clone(col->value[0]);
}
TVP seqTl(TVP seq)
{
	ASSERT_CHECK(seq);
	struct Collection* col = (struct Collection*)seq->value.ptr;

	//malloc
	TVP tailVal = newSeq(col->size-1);
	struct Collection* tail = tailVal->value.ptr;

	//copy tail list
	for (int i = 1; i < col->size; i++)
	{
		tail->value[i-1] = clone(col->value[i]);
	}

	return tailVal;
}
TVP seqLen(TVP seq)
{
	ASSERT_CHECK(seq);
	struct Collection* col = (struct Collection*)seq->value.ptr;
	return newInt(col->size);
}
TVP seqElems(TVP seq)
{
	ASSERT_CHECK(seq);
	struct Collection* col = (struct Collection*)seq->value.ptr;

	//malloc
	TVP elemsVal = newSet(col->size);
	struct Collection* elems = elemsVal->value.ptr;

	//copy  list
	for (int i = 0; i < col->size; i++)
	{
		elems->value[i] = clone(col->value[i]);
	}

	//FIXME this should have been done using set functions checking dublicates

	return elemsVal;
}
TVP seqInds(TVP seq)
{
	ASSERT_CHECK(seq);
	struct Collection* col = (struct Collection*)seq->value.ptr;

	//malloc
	TVP indsVal = newSet(col->size);
	struct Collection* inds = indsVal->value.ptr;

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
	struct Collection* col = (struct Collection*)seq->value.ptr;
	struct Collection* col2 = (struct Collection*)seq2->value.ptr;

	//malloc
	TVP concVal = newSet(col->size+col2->size);
	struct Collection* concSeq = concVal->value.ptr;

	//copy  list
	for (int i = 0; i < col->size; i++)
	{
		concSeq->value[i] = clone(col->value[i]);
	}

	int offset = col->size;
	for (int i = 0; i < col2->size; i++)
	{
		concSeq->value[i+offset] = clone(col2->value[i]);
	}

	return concVal;
}

TVP seqReverse(TVP seq)
{
	ASSERT_CHECK(seq);
	struct Collection* col = (struct Collection*)seq->value.ptr;

	//malloc
	TVP elemsVal = newSeq(col->size);
	struct Collection* elems = elemsVal->value.ptr;

	int offset = col->size-1;
	//copy  list
	for (int i = 0; i < col->size; i++)
	{
		elems->value[i] = clone(col->value[offset-i]);
	}

	return elemsVal;
}

//TVP seqMod(TVP seq,TVP seq);

TVP seqIndex(TVP seq,TVP indexVal) //VDM uses 1 based index
{
	ASSERT_CHECK(seq);
	assert((indexVal->type == VDM_INT||indexVal->type == VDM_INT1) && "index is not a int");

	int index = indexVal->value.intVal;
	struct Collection* col = (struct Collection*)seq->value.ptr;

	assert(index-1>=0 && index-1<col->size && "invalid index");
	return clone(col->value[index-1]);
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
