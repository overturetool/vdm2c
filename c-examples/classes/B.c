/*
 * B.c
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */


#include "B.h"


 void B_free(struct B *this)
{
	--this->_refs;
	if (this->_refs < 1)
	{
		A_free_fields(&this->A);//super
		//free class struct
		vdmFree(this->field2);
		free(this);
	}
}

static void print(ACLASS base)
{
	BCLASS this=
			  (void *)base-offsetof(struct B, A);
	//Loose translation
	//printf("%d %d",base->field1->value.intVal,this->field2->value.intVal);
}

static TVP sum(ACLASS base)
{
	BCLASS this=
				  (void *)base-offsetof(struct B, A);

	//Loose translation
	return newInt(base->field1->value.intVal+this->field2->value.intVal);
}

static TVP new()
{
	BCLASS ptr = (BCLASS) malloc(sizeof(struct B));
	*ptr = (struct B
	)
	{	._id = CLASS_ID_B_ID, ._refs = 0};

	//super
	A_init(&ptr->A);
//	TVP baseTvp= A._new();
//	UNWRAP_CLASS_A(base,baseTvp);
//	ptr->A = base;
	//FIXME memory leak from TVP and classtype

	//functions - change A's methods
	ptr->A.print = &print;
	ptr->A.sum = &sum;

	//All fields must be initialized
	ptr->field2 = newInt(5);
//	return ptr;
	return newTypeValue(VDM_CLASS, (TypedValueType){.ptr=newClassValue(ptr->_id, &ptr->_refs, (freeVdmClassFunction)&B_free, ptr)});
}


const struct BClass B =
{	._new = &new};
