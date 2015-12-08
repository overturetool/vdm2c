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


#define ACLASS struct A*
#define UNWRAP_CLASS_A(var,cls) ACLASS var=NULL;{UNWRAP_CLASS(localA,cls); var = (ACLASS)localA->value;};

struct A
{
	//needs if to identify it
	int _id ;
	//needs refsCount for garbage collection
	unsigned int _refs;
	//--------- class fields

	TVP field1;
	void (*print)(struct A *self);
	TVP (*sum)(struct A *self);
};


void A_free_fields(struct A *self);
void A_init(struct A *self);


extern const struct AClass
{
	TVP (*_new)();
} A;

#endif /* CLASSES_A_H_ */
