/*
 * A.h
 *
 *  Created on: Dec 7, 2015
 *      Author: kel
 */

#ifndef CLASSES_A_H_
#define CLASSES_A_H_

#include "../Globals.h"
#include "../lib/TypedValue.h"
#include "../lib/VdmClass.h"
#include "../lib/VdmBasicTypes.h"


#define ACLASS struct A*
#define UNWRAP_CLASS_A(var,cls) ACLASS var=NULL;{UNWRAP_CLASS(localA,cls); var = (ACLASS)localA->value;};

#define CLASS_A_calc 0
#define CLASS_A_sum 1

struct A
{
	VDM_CLASS_BASE_DEFINITIONS(A);
	/*vtable
	 * calc
	 * sum
	 * */

	TVP field1;

};


void A_free_fields(ACLASS);
ACLASS A_Constructor(ACLASS);


extern const struct AClass
{
	TVP (*_new)();
} A;

#endif /* CLASSES_A_H_ */
