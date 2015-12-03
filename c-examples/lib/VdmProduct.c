/*
 * VdmProduct.c
 *
 *  Created on: Dec 2, 2015
 *      Author: kel
 */

#include "VdmProduct.h"

#define ASSERT_CHECK(s) assert(s->type == VDM_PRODUCT && "Value is not a product")

TVP productGet(TVP product, int index)
{
	ASSERT_CHECK(product);

	struct Collection* col = (struct Collection*)product->value.ptr;

	assert(index-1>=0 && index-1<col->size && "invalid index");
	return clone(col->value[index-1]);
}

void productSet(TVP product, int index, TVP val)
{
	ASSERT_CHECK(product);

	struct Collection* col = (struct Collection*)product->value.ptr;

	assert(index-1>=0 && index-1<col->size && "invalid index");

	TVP old = col->value[index-1];
	if(old !=NULL)
	{
		recursiveFree(old);
	}
	col->value[index-1]=clone(val);
}

//TVP productEqual(TVP product,TVP product2)
//{
//
//	ASSERT_CHECK(product);
//	ASSERT_CHECK(product2);
//
//	return newBool(collectionEqual(product,product2));
//}
