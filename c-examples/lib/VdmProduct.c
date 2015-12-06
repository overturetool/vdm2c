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
	return newCollectionWithValues(VDM_PRODUCT,size,elements);
}

TVP productGet(TVP product, int index)
{
	ASSERT_CHECK(product);

	UNWRAP_PRODUCT(col,product);

	assert(index-1>=0 && index-1<col->size && "invalid index");
	return clone(col->value[index-1]);
}

void productSet(TVP product, int index, TVP val)
{
	ASSERT_CHECK(product);

	UNWRAP_PRODUCT(col,product);

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
