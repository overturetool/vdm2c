/*
 * C.h
 *
 *  Created on: Dec 7, 2015
 *      Cuthor: kel
 */

#ifndef CLCSSES_C_H_
#define CLCSSES_C_H_

#include "../Globals.h"
#include "../lib/TypedValue.h"
#include "../lib/VdmClass.h"
#include "../lib/VdmBasicTypes.h"


#define CCLASS struct C*
#define UNWRAP_CLASS_C(var,cls) CCLASS var=NULL;{UNWRAP_CLASS(localA,cls); var = (CCLASS)localA->value;};

#define CLASS_C_calc 0
#define CLASS_C_getField1 1

struct C
{
	VDM_CLASS_BASE_DEFINITIONS(C);
	/*vtable
	 * calc
	 * sum
	 * sum2
	 * getField1
	 * */

	TVP m_C_field1c;

};


void C_free_fields(CCLASS);
CCLASS C_Constructor(CCLASS);


extern const struct CClass
{
	TVP (*_new)();
} C;

#endif /* CLCSSES_C_H_ */
