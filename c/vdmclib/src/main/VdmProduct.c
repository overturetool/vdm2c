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
 * VdmProduct.c
 *
 *  Created on: Dec 2, 2015
 *      Author: kel
 */

#include "VdmProduct.h"

#define ASSERT_CHECK(s) assert(s->type == VDM_PRODUCT && "Value is not a product")


struct TypedValue* newProduct(size_t size)
{
	return newCollection(size, VDM_PRODUCT);
}

struct TypedValue* newProductWithValues(size_t size,TVP* elements)
{
	return newCollectionWithValues(size, VDM_PRODUCT, elements);
}

TVP productGet(TVP product, int index)
{
	ASSERT_CHECK(product);

	UNWRAP_PRODUCT(col,product);

	assert(index-1>=0 && index-1<col->size && "invalid index");
	return vdmClone(col->value[index-1]);
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

//TVP productEqual(TVP product,TVP product2)
//{
//
//	ASSERT_CHECK(product);
//	ASSERT_CHECK(product2);
//
//	return newBool(collectionEqual(product,product2));
//}
