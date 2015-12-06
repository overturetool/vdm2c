/*
 * VdmSet.h
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */

#ifndef LIB_VDMSET_H_
#define LIB_VDMSET_H_

#include "TypedValue.h"

struct TypedValue* newSet(size_t size);


struct TypedValue* newSetWithValues(size_t size,TVP* elements);


#endif /* LIB_VDMSET_H_ */
