#include "gtest/gtest.h"

extern "C"
{
#include "lib/TypedValue.h"
#include "lib/VdmSeq.h"
#include <stdio.h>
}

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

//TVP seqHd(TVP seq);
//TVP seqTl(TVP seq);
//TVP seqLen(TVP seq);
//TVP seqElems(TVP seq);
//TVP seqInds(TVP seq);
//TVP seqConc(TVP seq,TVP seq);
//TVP seqReverse(TVP seq);
////TVP seqMod(TVP seq,TVP seq);
//TVP seqIndex(TVP seq,int index);
//TVP seqEqual(TVP seq,TVP seq);
//TVP seqInEqual(TVP seq,TVP seq);

TEST(Expression_Seq, seqHd)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP res = vdmSeqHd(t);

	EXPECT_EQ(1, res->value.intVal);

	recursiveFree(res);
//
	recursiveFree(t);
}

TEST(Expression_Seq, seqTl)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP res = vdmSeqTl(t);

	struct Collection* col = (struct Collection*) res->value.ptr;

	EXPECT_EQ(2, col->value[0]->value.intVal);
	recursiveFree(res);
//
	recursiveFree(t);
}

TEST(Expression_Seq, seqLen)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP res = vdmSeqLen(t);

	EXPECT_EQ(2, res->value.intVal);
	recursiveFree(res);
//
	recursiveFree(t);
}

TEST(Expression_Seq, seqElems)
{
	//TODO
//	int arr[]={1,2};
//	TVP t = newSequence(2,arr);
//
//	TVP res = seqElems(t);
//
//	EXPECT_EQ (2,res->value.intVal);
//	recursiveFree(res);
////
//	recursiveFree(t);
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

	recursiveFree(res);
//
	recursiveFree(t);
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

	recursiveFree(res);
//
	recursiveFree(t);
	recursiveFree(t2);
}

TEST(Expression_Seq, seqReverse)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP res = vdmSeqReverse(t);

	struct Collection* col = (struct Collection*) res->value.ptr;

	EXPECT_EQ(2, col->value[0]->value.intVal);
	recursiveFree(res);
//
	recursiveFree(t);
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
//	recursiveFree(res);
////
//	recursiveFree(t);
//}

TEST(Expression_Seq, seqIndex)
{
	int arr[] =
	{ 1, 2 };
	TVP t = newSequence(2,arr);

	TVP index = newInt(2);
	TVP res = vdmSeqIndex(t,index);

	EXPECT_EQ(2, res->value.intVal);
	recursiveFree(res);
//
	recursiveFree(t);
	recursiveFree(index);
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

	EXPECT_EQ(true, res->value.boolVal);
	recursiveFree(res);
//
	recursiveFree(t);
	recursiveFree(t2);
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

	EXPECT_EQ(true, res->value.boolVal);
	recursiveFree(res);
	//
	recursiveFree(t);
	recursiveFree(t2);
}
