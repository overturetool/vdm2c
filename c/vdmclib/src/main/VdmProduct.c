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
 * <http:XXXwww.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

/*
 * VdmProduct.c
 *
 *  Created on: Dec 2, 2015
 *      Author: kel
 */


/*  VERSION: For the version of VDM2C used to generate this project, refer to one of the generated files.  */


#include <stdarg.h>
#include "VdmProduct.h"

#ifndef NO_PRODUCTS

#define ASSERT_CHECK(s) assert(s->type == VDM_PRODUCT && "Value is not a product")


TVP newProduct(size_t size)
{
	return newCollection(size, VDM_PRODUCT);
}


TVP newProductWithValues(size_t size,TVP* elements)
{
	return newCollectionWithValues(size, VDM_PRODUCT, elements);
}


TVP newProductVar(size_t size, ...)
{
	TVP *elements;
	TVP arg;
	TVP res;
	int i;

	elements = (TVP *)calloc(size, sizeof(TVP));

	va_list ap;
	va_start(ap, size);

	for (i = 0; i < size; i++)
	{
		arg = va_arg(ap, TVP);
		elements[i]=arg;
	}
	va_end(ap);

	res = newCollectionWithValues(size, VDM_PRODUCT, elements);
	free(elements);
	return res;
}



TVP newProductVarGC(size_t size, ...)
{
	TVP *elements;
	TVP arg;
	TVP res;
	int i;

	elements = (TVP *)calloc(size, sizeof(TVP));

	va_list ap;
	va_start(ap, size);

	for (i = 0; i < size; i++)
	{
		arg = va_arg(ap, TVP);
		elements[i]=arg;
	}
	va_end(ap);

	res = newCollectionWithValuesGC(size, VDM_PRODUCT, elements);
	free(elements);
	return res;
}


TVP productGet(TVP product, int index)
{
	ASSERT_CHECK(product);

	UNWRAP_PRODUCT(col,product);

	assert(index-1>=0 && index-1<col->size && "invalid index");
	return vdmClone(col->value[index-1]);
}


TVP productGetGC(TVP product, int index)
{
	ASSERT_CHECK(product);

	UNWRAP_PRODUCT(col,product);

	assert(index-1>=0 && index-1<col->size && "invalid index");
	return vdmCloneGC(col->value[index-1]);
}


void productSet(TVP product, int index, TVP val)
{
	ASSERT_CHECK(product);

	UNWRAP_PRODUCT(col,product);

	assert(index-1>=0 && index-1<col->size && "invalid index");

	TVP old = col->value[index-1];
	if(old !=NULL)
	{
		vdmFree(old);
	}
	col->value[index-1]=vdmClone(val);
}

/* TVP productEqual(TVP product,TVP product2)  */
/* {  */
/*   */
/* 	ASSERT_CHECK(product);  */
/* 	ASSERT_CHECK(product2);  */
/*   */
/* 	return newBool(collectionEqual(product,product2));  */
/* }  */

#endif /* NO_PRODUCTS */
