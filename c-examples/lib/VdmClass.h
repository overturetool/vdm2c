/*
 * VdmClass.h
 *
 *  Created on: Dec 14, 2015
 *      Author: kel
 */

#ifndef LIB_VDMCLASS_H_
#define LIB_VDMCLASS_H_

#include "TypedValue.h"
#include<stddef.h>

typedef TVP (*VirtualFunctionPointer)(void * self, ...);
//typedef void (*VirtualFunctionPointer)(void * self, ...);

/*
VTable structure used by the compiler to keep
track of the virtual functions associated with a class.
There is one instance of a VTable for every class
containing virtual functions. All instances of
a given class point to the same VTable.
*/
struct VTable
{
   /*
   d and i fields are used when multiple inheritance and virtual
   base classes are involved. We will be ignoring them for this
   discussion.
   */
   int d;
   int i;

   /*
   A function pointer to the virtual function to be called is
   stored here.
   */
   VirtualFunctionPointer pFunc;
};


/*
 * Base class definitions
 * 1. VTable
 * 2. class id
 * 3. reference count for smart pointer
 * */
#define VDM_CLASS_BASE_DEFINITIONS(className) 	struct VTable * _##className##_pVTable;\
int _##className##_id;\
unsigned int _##className##_refs

/*-------------------------------------------------
 *
 * VDM encapsulation
 *
 * ------------------------------------------------
 */
struct ClassType
{
	void* value;
	int classId;
	unsigned int* refs;
	freeVdmClassFunction freeClass;//TODO move to global map
};

struct ClassType* newClassValue(int id, unsigned int* refs, freeVdmClassFunction freeClass, void* value);

#endif /* LIB_VDMCLASS_H_ */
