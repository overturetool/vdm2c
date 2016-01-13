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

#define CLASS_ID_C_ID 126

#define CCLASS struct C*

#define CLASS_C_getField1 0

struct C
{
	VDM_CLASS_BASE_DEFINITIONS(C);
	/*vtable
	 * getField1
	 * */

	VDM_CLASS_FIELD_DEFINITION(C,field1c);
};


void C_free_fields(CCLASS);
CCLASS C_Constructor(CCLASS);


extern const struct CClass
{
	TVP (*_new)();
} C;

#endif /* CLCSSES_C_H_ */

