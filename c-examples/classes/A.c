/*
 * A.c
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */

#include "A.h"
#include <stdio.h>

void A_free_fields(struct A *this)
{
	//free class struct
	vdmFree(this->m_A_field1);
}

static void A_free(struct A *this)
{
	--this->_A_refs;
	if (this->_A_refs < 1)
	{
		A_free_fields(this);
		free(this);
	}
}

TVP A_calc(ACLASS this,TVP x, TVP y)
{
	return vdmSum(x,y);
}

static TVP A_sum(ACLASS this)
{
	return GET_FIELD_PTR(A,A,this,field1);
}

struct VTable VTableArrayForA[] =
{
{ 0, 0, (VirtualFunctionPointer) A_calc },
{ 0, 0, (VirtualFunctionPointer) A_sum }, };

ACLASS A_Constructor(ACLASS this_ptr)
{

	if(this_ptr==NULL)
	{
		this_ptr = (ACLASS) malloc(sizeof(struct A));
	}

	if(this_ptr!=NULL)
	{
		this_ptr->_A_id = CLASS_ID_A_ID;
		this_ptr->_A_refs = 0;
		this_ptr->_A_pVTable=VTableArrayForA;

		this_ptr->m_A_field1= newInt(4);
	}

	return this_ptr;
}

static TVP new()
{
	ACLASS ptr=A_Constructor(NULL);

	return newTypeValue(VDM_CLASS, (TypedValueType)
			{	.ptr=newClassValue(ptr->_A_id, &ptr->_A_refs, (freeVdmClassFunction)&A_free, ptr)});
}

const struct AClass A =
{ ._new = &new };

TVP A_ctor(ACLASS this)
{
	TVP buf = NULL;
	if(this ==NULL)
	{
		//root call i.e. not called from other constructor
		buf = new();
		this = TO_CLASS_PTR(buf,A);
	}

	//ctor code

	return buf;
}
