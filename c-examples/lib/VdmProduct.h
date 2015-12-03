/*
 * VdmProduct.h
 *
 *  Created on: Dec 2, 2015
 *      Author: kel
 */

#ifndef LIB_VDMPRODUCT_H_
#define LIB_VDMPRODUCT_H_
#include "TypedValue.h"
#include <assert.h>

TVP productGet(TVP product, int index);
void productSet(TVP product, int index, TVP val);
//bool productEqual(TVP product,TVP product2);

#endif /* LIB_VDMPRODUCT_H_ */
