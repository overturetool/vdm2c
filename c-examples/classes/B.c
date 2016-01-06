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
//		A_free_fields(&this->A);//super
		//free class struct
		vdmFree(this->m_B_field2);
		free(this);
	}
}

//static TVP B_calc(ACLASS this,TVP x, TVP y)
//{
//	return vdmProduct(x,y);
//}
static TVP B_sum(BCLASS this)
{
	TVP tmp1 = GET_FIELD_PTR(B,A,this,field1);
	TVP tmp2 = GET_FIELD_PTR(B,B,this,field2);
	TVP ret = vdmSum(tmp1,tmp2);

	vdmFree(tmp1);vdmFree(tmp2);
	return ret;
}
//#define GET_STRUCT_FIELD(tname,ptr,fieldtype,fieldname) (*( (fieldtype*) (  ((unsigned char*)ptr) + offsetof(struct tname, fieldname) )  ))


// A vtable ptr in B i know avtable is in B at a higher number
// ptr - _A_VTable offset in B
// ptr - offsetof(struct B, _A_pVTable)
#define DOWNCAST(thisClassName, upCastClassName, ptr) (\
		(struct upCastClassName *)\
		(\
((unsigned char*)ptr) - offsetof(struct upCastClassName, _##thisClassName##_pVTable)\
		)\
		)

static TVP B_A_sum(ACLASS base)//override for A
{
	BCLASS this = DOWNCAST(A,B,base);

	return B_sum(this);
//	return newInt(1);
}



static TVP B_sum2(BCLASS this)
{
	TVP tmp1 = GET_FIELD_PTR(B,A,this,field1);
	TVP tmp2 = GET_FIELD_PTR(B,B,this,field2);
	TVP ret = vdmSum(tmp1,tmp2);// newInt(base->m_A_field1->value.intVal+this->m_B_field2->value.intVal);//newInt(this->m_B_field2->value.intVal);

	vdmFree(tmp1);vdmFree(tmp2);
	return ret;
}

static TVP B_getField2(BCLASS this)
{
	return GET_FIELD_PTR(B,B,this,field2);
}

struct VTable VTableArrayForB[] =
{
{ 0, 0, (VirtualFunctionPointer) A_calc },
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
	//super
//	A_Constructor(&ptr->A);
//	TVP baseTvp= A._new();
//	UNWRAP_CLASS_A(base,baseTvp);
//	ptr->A = base;
	//FIXME memory leak from TVP and classtype

	//functions - change A's methods
//	ptr->A.print = &print;
//	ptr->A.sum = &sum;

	//All fields must be initialized

//	ptr->sum2 = &sum2;
//	return ptr;
	return newTypeValue(VDM_CLASS, (TypedValueType)
			{	.ptr=newClassValue(ptr->_B_id, &ptr->_B_refs, (freeVdmClassFunction)&B_free, ptr)});
}

const struct BClass B =
{ ._new = &new };
