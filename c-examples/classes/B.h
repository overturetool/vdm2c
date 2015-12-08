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


#define BCLASS struct B*
#define UNWRAP_CLASS_B(var,cls) BCLASS var=NULL;{UNWRAP_CLASS(localB,cls); var = (BCLASS)localB->value;};

struct B
{
	struct A A;
	//needs if to identify it
	int _id ;
	//needs refsCount for garbage collection
	unsigned int _refs;
	//--------- class fields

	TVP field2;
//	void (*print)(struct B *self);
//	TVP (*sum)(struct B *self);
};

void B_free(struct B *self);

extern const struct BClass
{
	TVP (*_new)();
} B;

#endif /* CLASSES_B_H_ */
