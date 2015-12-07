/*
 * R1.c
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */
#include "R1.h"

#define UNWRAP_R1(var,record) struct R1* var = (struct R1*)record;

void freeR1(void* ptr)
{
	RECORD_R1 self = (RECORD_R1)ptr;
	vdmFree(self->a);
	vdmFree(self->b);
	vdmFree(self->c);
	free(self);
}

bool equalsR1(TVP a, TVP b)
{
	ASSERT_CHECK_RECORD(a);
	ASSERT_CHECK_RECORD(b);

	UNWRAP_RECORD(ar,a);
	UNWRAP_RECORD(br,b);

	assert(ar->recordId == br->recordId && "record ids differ");
	assert(ar->recordId== RECORD_ID_R1 && "record is not supported by this equals function");

	UNWRAP_R1(arr,ar);
	UNWRAP_R1(brr,br);

	return equals(arr->a,brr->a) &&
	equals(arr->b,brr->b)&&
	equals(arr->c,brr->c);
}

TVP cloneR1(TVP self)
{
	ASSERT_CHECK_RECORD(self);

	UNWRAP_RECORD(tmp,self);

	TVP cloneValue = mk_R1();

	UNWRAP_RECORD(tmp2,cloneValue);

	UNWRAP_R1(selfR,tmp);
	UNWRAP_R1(cloneR,tmp2);

	//copy record struct
	*cloneR = *selfR;

	//copy fields
	cloneR->a = clone(cloneR->a);
	cloneR->b = clone(cloneR->b);
	cloneR->c = clone(cloneR->c);

	return cloneValue;
}


TVP mk_R1()
{
	struct RecordType* ptr = (struct RecordType*) malloc(sizeof(struct RecordType));
	ptr->recordId=RECORD_ID_R1;
	ptr->freeRecord = &freeR1;
	ptr->equalFun=&equalsR1;
	ptr->cloneFun=&cloneR1;
	ptr->value =(struct R1*) malloc(sizeof(struct R1));
	return newTypeValue(VDM_RECORD, (TypedValueType
			)
			{	.ptr = ptr});
}

