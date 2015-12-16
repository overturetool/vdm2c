/*
 * B.h
 *
 *  Created on: Dec 8, 2015
 *      Author: kel
 */

#ifndef CLASSES_B_H_
#define CLASSES_B_H_

#include "../Globals.h"
#include "../lib/TypedValue.h"
#include "A.h"
#include "C.h"

#define BCLASS struct B*
#define UNWRAP_CLASS_B(var,cls) BCLASS var=NULL;{UNWRAP_CLASS(localB,cls); var = (BCLASS)localB->value;};

#define CLASS_B_sum2 2

struct B
{
	//---- A ----
	VDM_CLASS_BASE_DEFINITIONS(A);

	TVP field1;
	//-----end A ----


	// ---- C ----
	VDM_CLASS_BASE_DEFINITIONS(C);

	TVP field1c;

	//---- end C----

	VDM_CLASS_BASE_DEFINITIONS(B);
	TVP field2;
};

void B_free(struct B *self);

extern const struct BClass
{
	TVP (*_new)();
}B;

#endif /* CLASSES_B_H_ */
