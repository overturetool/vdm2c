/*
 * C.c
 *
 *  Created on: Dec 7, 2015
 *      Cuthor: kel
 */

#include "C.h"
#include <stdio.h>



void C_free_fields(struct C *this)
{
	//free class struct
	vdmFree(this->m_C_field1c);
}

static void C_free(struct C *this)
{
	--this->_C_refs;
	if (this->_C_refs < 1)
	{
		C_free_fields(this);
		free(this);
	}
}


static TVP C_getField1(CCLASS this)
{
	return GET_FIELD_PTR(C,C,this,field1c);
}



struct VTable VTableCrrayForC[] =
{
    { 0, 0, (VirtualFunctionPointer) C_getField1 },
};

CCLASS C_Constructor(CCLASS this_ptr)
{

	if(this_ptr==NULL)
	{
		this_ptr = (CCLASS) malloc(sizeof(struct C));
	}

	if(this_ptr!=NULL)
	{
		this_ptr->_C_id = CLASS_ID_C_ID;
		this_ptr->_C_refs = 0;
		this_ptr->_C_pVTable=VTableCrrayForC;

		this_ptr->m_C_field1c= newReal(12.34);
	}

	return this_ptr;
}


static TVP new()
{
	CCLASS ptr=C_Constructor(NULL);

	return newTypeValue(VDM_CLASS, (TypedValueType)
			{	.ptr=newClassValue(ptr->_C_id, &ptr->_C_refs, (freeVdmClassFunction)&C_free, ptr)});
}

const struct CClass C =
{ ._new = &new };


TVP C_ctor(CCLASS this)
{
	TVP buf = NULL;
	if(this ==NULL)
	{
		//root call i.e. not called from other constructor
		buf = new();
		this = TO_CLASS_PTR(buf,C);
	}

	//ctor code

	return buf;
}
