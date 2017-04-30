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
 * R1.c
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */
#include "R1.h"
#include <assert.h>

#ifndef NO_RECORDS

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

TVP vdmCloneR1(TVP self)
{
	ASSERT_CHECK_RECORD(self);

	UNWRAP_RECORD(tmp,self);

	TVP vdmCloneValue = mk_R1();

	UNWRAP_RECORD(tmp2,vdmCloneValue);

	UNWRAP_R1(selfR,tmp);
	UNWRAP_R1(vdmCloneR,tmp2);

	*vdmCloneR = *selfR;

	vdmCloneR->a = vdmClone(vdmCloneR->a);
	vdmCloneR->b = vdmClone(vdmCloneR->b);
	vdmCloneR->c = vdmClone(vdmCloneR->c);

	return vdmCloneValue;
}


TVP mk_R1()
{
	struct RecordType* ptr = (struct RecordType*) malloc(sizeof(struct RecordType));
	ptr->recordId=RECORD_ID_R1;
	ptr->freeRecord = &freeR1;
	ptr->equalFun=&equalsR1;
	ptr->vdmCloneFun=&vdmCloneR1;
	ptr->value =(struct R1*) calloc(1,sizeof(struct R1));
	return newTypeValue(VDM_RECORD, (TypedValueType
			)
			{	.ptr = ptr});
}

#endif /* NO_RECORDS */
