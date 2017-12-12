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
 * B.c
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */

#include "B.h"
#include <string.h>

void B_free(struct B *this)
{
	vdmFree(this->m_B_field2);
	free(this);
}

static TVP B_sum(BCLASS this)
{
	TVP tmp1 = GET_FIELD_PTR(B,A,this,field1);
	TVP tmp2 = GET_FIELD_PTR(B,B,this,field2);
	TVP ret = vdmSum(tmp1,tmp2);

	vdmFree(tmp1);vdmFree(tmp2);
	return ret;
}


static TVP B_A_sum(ACLASS base) /* override for A */
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
		/* { 0, 0, (VirtualFunctionPointer) A_calc }, */
		{ 0, 0, (VirtualFunctionPointer) B_sum },
		{ 0, 0, (VirtualFunctionPointer) B_sum2 },
		{ 0, 0, (VirtualFunctionPointer) B_getField2 }, };

BCLASS B_Constructor(BCLASS this_ptr)
{

	if(this_ptr==NULL)
	{
		this_ptr = (BCLASS) malloc(sizeof(struct B));
	}

	if(this_ptr!=NULL)
	{

		/* init base A */
		A_Constructor((ACLASS)CLASS_CAST(this_ptr,B,A));

		/* init base C */
		C_Constructor((CCLASS)CLASS_CAST(this_ptr,B,C));

		/* replace vTable */

		struct VTable* tmp = this_ptr->_A_pVTable;
		this_ptr->_A_pVTable = malloc(sizeof(struct VTable)*2);
		memcpy(this_ptr->_A_pVTable, tmp, sizeof(struct VTable)*2);
		this_ptr->_A_pVTable[CLASS_A_sum].pFunc = (VirtualFunctionPointer)B_A_sum;/* override */

		this_ptr->_B_pVTable = VTableArrayForB;

		this_ptr->m_B_field2 = newInt(5);
	}

	return this_ptr;
}

static TVP new()
		{
	BCLASS ptr = B_Constructor(NULL);
	return newTypeValue(VDM_CLASS, (TypedValueType)
			{	.ptr=newClassValue(ptr->_B_id, (freeVdmClassFunction)&B_free, ptr)});
		}

const struct BClass B =
{ ._new = &new };


TVP B_ctor(BCLASS this)
{
	TVP buf = NULL;
	if(this ==NULL)
	{
		/* root call i.e. not called from other constructor */
		buf = new();
		this = TO_CLASS_PTR(buf,B);
	}

	/* ctor code */

	A_ctor( (ACLASS) CLASS_CAST(this,B,A) );
	C_ctor( (CCLASS) CLASS_CAST(this,B,C));

	return buf;
}
