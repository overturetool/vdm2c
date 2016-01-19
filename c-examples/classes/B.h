/*
 * B.h
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */

#ifndef CLASSES_B_H_
#define CLASSES_B_H_

#include "../lib/TypedValue.h"
#include "A.h"
#include "C.h"

#define CLASS_ID_B_ID 125

#define BCLASS struct B*

#define CLASS_B_sum2 1

struct B
{
	//---- A ----
	VDM_CLASS_BASE_DEFINITIONS(A);
	/*vtable
	 * calc --CLASS_A_calc
	 * sum  -- CLASS_A_sum
	 * */
	VDM_CLASS_FIELD_DEFINITION(A,field1);
	//-----end A ----


	// ---- C ----
	VDM_CLASS_BASE_DEFINITIONS(C);
	/*vtable
	 * getField1--CLASS_C_getField1
	 * */
	VDM_CLASS_FIELD_DEFINITION(C,field1c);

	//---- end C----

	VDM_CLASS_BASE_DEFINITIONS(B);
	/*vtable
	 * calc
	 * sum
	 * sum2
	 * getField2
	 * */
	VDM_CLASS_FIELD_DEFINITION(B,field2);
};

TVP B_ctor(BCLASS ptr);

void B_free(struct B *self);

extern const struct BClass
{
	TVP (*_new)();
}B;

#endif /* CLASSES_B_H_ */
