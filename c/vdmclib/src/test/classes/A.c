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
	/* free class struct */
	vdmFree(this->m_A_field1);
}

static void A_free(struct A *this)
{

	A_free_fields(this);
	free(this);
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
		this_ptr->_A_pVTable=VTableArrayForA;

		this_ptr->m_A_field1= newInt(4);
	}

	return this_ptr;
}

static TVP new()
		{
	ACLASS ptr=A_Constructor(NULL);

	return newTypeValue(VDM_CLASS, (TypedValueType)
			{	.ptr=newClassValue(ptr->_A_id, (freeVdmClassFunction)&A_free, ptr)});
		}

const struct AClass A =
{ ._new = &new };

TVP A_ctor(ACLASS this)
{
	TVP buf = NULL;
	if(this ==NULL)
	{
		/* root call i.e. not called from other constructor */
		buf = new();
		this = TO_CLASS_PTR(buf,A);
	}

	/* ctor code */

	return buf;
}
