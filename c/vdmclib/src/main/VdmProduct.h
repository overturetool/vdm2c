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
 * VdmProduct.h
 *
 *  Created on: Dec 2, 2015
 *      Author: kel
 */

#ifndef LIB_VDMPRODUCT_H_
#define LIB_VDMPRODUCT_H_
#include "Vdm.h"
#include <assert.h>

#ifndef NO_PRODUCTS

TVP newProductVar(size_t size,...);
TVP newProductVarGC(size_t size, TVP *from, ...);
TVP newProductWithValues(size_t size,TVP* elements);

TVP productGet(TVP product, int index);
TVP productGetGC(TVP product, int index, TVP *from);
void productSet(TVP product, int index, TVP val);
/* bool productEqual(TVP product,TVP product2);  */

#endif /* NO_PRODUCTS */
#endif /* LIB_VDMPRODUCT_H_ */
