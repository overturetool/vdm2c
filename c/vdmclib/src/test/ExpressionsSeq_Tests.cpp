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

#include "gtest/gtest.h"

extern "C"
{
#include "Vdm.h"
#include <stdio.h>
}

#define DEFAULT_SEQ_COMP_BUFFER 1
#define DEFAULT_SEQ_COMP_BUFFER_STEPSIZE 10


TVP newSequence(int size, int* arr)
{
	TVP seq = newSeq(size);

	struct Collection* col =(struct Collection*) seq->value.ptr;

	for (int i = 0; i < size; i++)
	{
		col->value[i] = newInt(arr[i]);
	}

	return seq;
}


TEST(Expression_Seq, seqGrow)
{
	TVP seq1;
	TVP seq2;
	TVP res;

	//Create test set.
	seq1 = newSeqVarToGrow(1, 2, newInt(1));
	seq2 = newSeqVar(2, newInt(1), newInt(2));

	vdmSeqGrow(seq1, newInt(2));

	res = vdmSeqEqual(seq1, seq2);
	EXPECT_TRUE(res->value.boolVal);

	vdmSeqGrow(seq1, newInt(3));

	UNWRAP_COLLECTION(col, seq1);
	vdmFree(res);
	res = vdmSeqEqual(seq1, seq2);
	EXPECT_FALSE(res->value.boolVal);

	//Clean up.
	vdmFree(res);
	vdmFree(seq1);
	vdmFree(seq2);
}


TEST(Expression_Seq, seqFit)
{
	TVP seq1;
	TVP seq2;
	TVP res;

	//Create test set.
	seq1 = newSeqVarToGrow(1, 2, newInt(1));
	seq2 = newSeqVar(2, newInt(1), newInt(2));

	vdmSeqGrow(seq1, newInt(2));

	res = vdmSeqEqual(seq1, seq2);
	EXPECT_TRUE(res->value.boolVal);

	vdmSeqGrow(seq1, newInt(3));

	UNWRAP_COLLECTION(col, seq1);
	vdmFree(res);
	res = vdmSeqEqual(seq1, seq2);
	EXPECT_FALSE(res->value.boolVal);
	vdmFree(res);

//	vdmSeqFit(seq1);

	//vdmSeqGrow works for any sequence, but if it wasn't preallocated with
	//vdmSeqVarToGrow it is not as efficient.
	vdmSeqGrow(seq2, newInt(3));
	res = vdmSeqEqual(seq1, seq2);
	EXPECT_TRUE(res->value.boolVal);

	//Clean up.
	vdmFree(res);
	vdmFree(seq1);
	vdmFree(seq2);
}


TEST(Expression_Seq, seqHd)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP res = vdmSeqHd(t);

	EXPECT_EQ(1, res->value.intVal);

	vdmFree(res);
//
	vdmFree(t);
}

TEST(Expression_Seq, seqTl)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP res = vdmSeqTl(t);

	struct Collection* col = (struct Collection*) res->value.ptr;

	EXPECT_EQ(2, col->value[0]->value.intVal);
	vdmFree(res);
//
	vdmFree(t);
}

TEST(Expression_Seq, seqLen)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP res = vdmSeqLen(t);

	EXPECT_EQ(2, res->value.intVal);
	vdmFree(res);
//
	vdmFree(t);
}

TEST(Expression_Seq, seqElems)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP a = newInt(1);
	TVP b = newInt(2);
	TVP elems = newSetVar(2, a,b);

	TVP res = vdmSeqElems(t);

	TVP tmp = vdmEquals(res,elems);
	EXPECT_TRUE(tmp->value.boolVal);
	vdmFree(res);
	vdmFree(a);
	vdmFree(b);
	vdmFree(tmp);
	vdmFree(elems);
//
	vdmFree(t);
}

TEST(Expression_Seq, seqInds)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP res = vdmSeqInds(t);

	EXPECT_EQ(VDM_SET, res->type);
	struct Collection* col = (struct Collection*) res->value.ptr;

	EXPECT_TRUE(VDM_SET == res->type);
	//TODO this is actually not the VDM version of inds [1,2] = {1,2}, the second set and the set,set comp is missing, I didnt do sets yet
	EXPECT_TRUE(2 == col->value[0]->value.intVal || 2 == col->value[1]->value.intVal);
	EXPECT_TRUE(1 == col->value[0]->value.intVal || 1 == col->value[1]->value.intVal);

	vdmFree(res);
//
	vdmFree(t);
}

TEST(Expression_Seq, seqConc)
{
	int arr[] =
	{ 1 };
	TVP t = newSequence(1,arr);

	int arr2[] =
	{ 2 };
	TVP t2 = newSequence(1,arr2);

	TVP res = vdmSeqConc(t,t2);

	struct Collection* col = (struct Collection*) res->value.ptr;

	EXPECT_EQ(1, col->value[0]->value.intVal);
	EXPECT_EQ(2, col->value[1]->value.intVal);

	vdmFree(res);
//
	vdmFree(t);
	vdmFree(t2);
}

TEST(Expression_Seq, seqReverse)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP res = vdmSeqReverse(t);

	struct Collection* col = (struct Collection*) res->value.ptr;

	EXPECT_EQ(2, col->value[0]->value.intVal);
	vdmFree(res);
//
	vdmFree(t);
}

//TEST(Expression_Seq, seqMod)
//{
//	int arr[] =
//	{ 1, 2 };
//	TVP t = newSequence(2,arr);
//
//	TVP res = seqReverse(t);
//
//	struct Collection* col = (struct Collection*) res->value.ptr;
//
//	EXPECT_EQ(2, col->value[0]->value.intVal);
//	vdmFree(res);
////
//	vdmFree(t);
//}

TEST(Expression_Seq, seqIndex)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP index = newInt(2);
	TVP res = vdmSeqIndex(t,index);

	EXPECT_EQ(2, res->value.intVal);
	vdmFree(res);
//
	vdmFree(t);
	vdmFree(index);
}

TEST(Expression_Seq, seqEqual)
{
	int arr[] =
	{ 1 };
	TVP t = newSequence(1,arr);

	int arr2[] =
	{ 1 };
	TVP t2 = newSequence(1,arr2);

	TVP res = vdmSeqEqual(t,t2);

	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
//
	vdmFree(t);
	vdmFree(t2);
}

TEST(Expression_Seq, seqInEqual)
{
	int arr[] =
	{ 1 };
	TVP t = newSequence(1,arr);

	int arr2[] =
	{ 2 };
	TVP t2 = newSequence(1,arr2);

	TVP res = vdmSeqInEqual(t,t2);

	EXPECT_TRUE(res->value.boolVal);
	vdmFree(res);
	//
	vdmFree(t);
	vdmFree(t2);
}
