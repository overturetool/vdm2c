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
	vdmFree(this->field1);
}

static void A_free(struct A *this)
{
	--this->_refs;
	if (this->_refs < 1)
	{
		A_free_fields(this);
		free(this);
	}
}

static void print(ACLASS this)
{
	//Loose translation
	printf("%d",this->field1->value.intVal);
}

static TVP sum(ACLASS this)
{
	return vdmClone(this->field1);
}

void A_init(ACLASS this)
{
	*this = (struct A
	)
	{	._id = CLASS_ID_A_ID, ._refs = 0};

	//functions
	this->print = &print;
	this->sum = &sum;

	//All fields must be initialized
	this->field1 = newInt(4);
}

static TVP new()
{
	ACLASS ptr = (ACLASS) malloc(sizeof(struct A));

	A_init(ptr);
//	return ptr;
	return newTypeValue(VDM_CLASS, (TypedValueType)
			{	.ptr=newClassValue(ptr->_id, &ptr->_refs, (freeVdmClassFunction)&A_free, ptr)});
}

const struct AClass A =
{ ._new = &new };
