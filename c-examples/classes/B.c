/*
 * B.c
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */

#include "B.h"
#include <string.h>

void B_free(struct B *this)
{
	--this->_B_refs;
	if (this->_B_refs < 1)
	{
		vdmFree(this->m_B_field2);
		free(this);
	}
}

static TVP B_sum(BCLASS this)
{
	TVP tmp1 = GET_FIELD_PTR(B,A,this,field1);
	TVP tmp2 = GET_FIELD_PTR(B,B,this,field2);
	TVP ret = vdmSum(tmp1,tmp2);

	vdmFree(tmp1);vdmFree(tmp2);
	return ret;
}


static TVP B_A_sum(ACLASS base) //override for A
{
	BCLASS this = CLASS_DOWNCAST(A,B,base);

	return B_sum(this);
}

static TVP B_sum2(BCLASS this)
{
	TVP tmp1 = GET_FIELD_PTR(B,A,this,field1);
	TVP tmp2 = GET_FIELD_PTR(B,B,this,field2);
	TVP ret = vdmSum(tmp1,tmp2);

	vdmFree(tmp1);vdmFree(tmp2);
	return ret;
}

static TVP B_getField2(BCLASS this)
{
	return GET_FIELD_PTR(B,B,this,field2);
}

struct VTable VTableArrayForB[] =
{
//{ 0, 0, (VirtualFunctionPointer) A_calc },
{ 0, 0, (VirtualFunctionPointer) B_sum },
{ 0, 0, (VirtualFunctionPointer) B_sum2 },
{ 0, 0, (VirtualFunctionPointer) B_getField2 }, };

#define GET_STRUCT_FIELD_PTR(tname,ptr,fieldname) (( (void*) (  ((unsigned char*)ptr) + offsetof(struct tname, fieldname) )  ))
BCLASS B_Constructor(BCLASS this_ptr)
{

	if(this_ptr==NULL)
	{
		this_ptr = (BCLASS) malloc(sizeof(struct B));
	}

	if(this_ptr!=NULL)
	{

		//init base A
		A_Constructor((ACLASS)GET_STRUCT_FIELD_PTR(B,this_ptr,_A_pVTable));

		//init base C
		C_Constructor((CCLASS)GET_STRUCT_FIELD_PTR(B,this_ptr,_C_pVTable));

		//replace vTable

		struct VTable* tmp = this_ptr->_A_pVTable;
		this_ptr->_A_pVTable = malloc(sizeof(struct VTable)*2);
		memcpy(this_ptr->_A_pVTable, tmp, sizeof(struct VTable)*2);
		this_ptr->_A_pVTable[CLASS_A_sum].pFunc = (VirtualFunctionPointer)B_A_sum;//override

		this_ptr->_B_pVTable = VTableArrayForB;

		this_ptr->m_B_field2 = newInt(5);
	}

	return this_ptr;
}

static TVP new()
{
	BCLASS ptr = B_Constructor(NULL);
	return newTypeValue(VDM_CLASS, (TypedValueType)
			{	.ptr=newClassValue(ptr->_B_id, &ptr->_B_refs, (freeVdmClassFunction)&B_free, ptr)});
}

const struct BClass B =
{ ._new = &new };
