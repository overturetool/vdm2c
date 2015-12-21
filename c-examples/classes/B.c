/*
 * B.c
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */


#include "B.h"


 void B_free(struct B *this)
{
	--this->_B_refs;
	if (this->_B_refs < 1)
	{
//		A_free_fields(&this->A);//super
		//free class struct
		vdmFree(this->field2);
		free(this);
	}
}



static TVP B_calc(ACLASS this,TVP x, TVP y)
{
	return vdmProduct(x,y);
}

static TVP B_sum(ACLASS base)
{
	BCLASS this=(BCLASS)base;
				  //(void *)base-offsetof(struct B, A);

	//Loose translation
	return newInt(base->field1->value.intVal+this->field2->value.intVal);
}

static TVP B_sum2(BCLASS this)
{
	//Loose translation
	return newInt(this->field2->value.intVal);
}

static TVP B_getField1(BCLASS this)
{
	return vdmClone(this->field1c);
}

struct VTable VTableArrayForB[] =
{
    /*
    Vtable entry virtual function sum.
    */
    { 0, 0, (VirtualFunctionPointer) B_calc },

    /*
    This vtable entry invokes the base class's
    MoveTo method.
    */
    { 0, 0, (VirtualFunctionPointer) B_sum },

	{ 0, 0, (VirtualFunctionPointer) B_sum2 },
	{ 0, 0, (VirtualFunctionPointer) B_getField1 },

    /* Entry for the virtual destructor */
//    { 0, 0, (VirtualFunctionPointer) Shape_Destructor }
};

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
		this_ptr->_A_pVTable = VTableArrayForB;
		this_ptr->_B_pVTable = VTableArrayForB;//this_ptr->_A_pVTable;

		this_ptr->field2 = newInt(5);
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
	return newTypeValue(VDM_CLASS, (TypedValueType){.ptr=newClassValue(ptr->_B_id, &ptr->_B_refs, (freeVdmClassFunction)&B_free, ptr)});
}


const struct BClass B =
{	._new = &new};
