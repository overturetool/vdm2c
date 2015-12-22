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

/*---------------------------------------------------
 *
 * CLASS access function macros
 *
 * - Class handling macros for calling functions and obtaining fields
 * --------------------------------------------------
 */

// ############ PUBLIC CLASS VALUE ACCESS #####################
/*
 * Class Value unwrap macro - TODO: How to assert inline
 */
#define TO_CLASS_PTR(tv,type) ((struct type *) ( ((struct ClassType*)tv->value.ptr)->value))

/*
 * Cast to class pointer by moving it forward to the class specific VTable
 * Note that we only adjust the pointer if a subtype is given (i.e. not the type of it self)
 */
#define CLASS_CAST(ptr,from,to) ((unsigned char*)ptr) + (typeid(from)==typeid(to)?0: offsetof(struct from, _##to##_pVTable))

/*
 * Macro to obtain the (sub-)class specific field from a class struct
 */
#define GET_STRUCT_FIELD(tname,ptr,fieldtype,fieldname) (*( (fieldtype*) (  ((unsigned char*)ptr) + offsetof(struct tname, fieldname) )  ))

/*
 * Macro to obtain the (sub-)class specific VTable from a class struct
 */
#define GET_VTABLE_FUNC(thisTypeName,funcTname,ptr,id)   GET_STRUCT_FIELD(thisTypeName,ptr,struct VTable*,_##funcTname##_pVTable)[id].pFunc

/*
 * Macro to obtain a function from a (sub-)class specific VTable from a class struct
 */
#define CALL_FUNC(thisTypeName,funcTname,classValue,id, args... )     GET_VTABLE_FUNC( thisTypeName,funcTname,TO_CLASS_PTR(classValue,thisTypeName),id)(CLASS_CAST(TO_CLASS_PTR(classValue,thisTypeName),thisTypeName,funcTname), ## args)

/*
 * Macro to obtain a field from a (sub-)class specific class struct. We clone to preserve value semantics and the rule of freeing
 */
#define GET_FIELD(thisTypeName,fieldTypeName,classValue,fieldName) vdmClone(GET_STRUCT_FIELD(fieldTypeName,CLASS_CAST(TO_CLASS_PTR(classValue,thisTypeName),thisTypeName,fieldTypeName) ,struct TypedValue*,m_##fieldTypeName##_##fieldName))

// old stuff

//Call function from VTable and change ptr to the correct offset. With obtional arguments
//#define CCCALL(thisTypeName,funcTname,ptr,id,...) GET_VTABLE_FUNC( thisTypeName,funcTname,ptr,id)(CLASS_CAST(ptr,thisTypeName,funcTname), ## __VA_ARGS__)

// ############ PRIVATE CLASS POINTER ACCESS #####################

/*
 * Macro to obtain a field from a (sub-)class specific class struct. We clone to preserve value semantics and the rule of freeing
 */
#define GET_FIELD_PTR(thisTypeName,fieldTypeName,ptr,fieldName) vdmClone(GET_STRUCT_FIELD(fieldTypeName,CLASS_CAST(ptr,thisTypeName,fieldTypeName) ,struct TypedValue*,m_##fieldTypeName##_##fieldName))

/*
 * Macro to obtain a function from a (sub-)class specific VTable from a class struct
 */
#define CALL_FUNC_PTR(thisTypeName,funcTname,ptr,id, args... )     GET_VTABLE_FUNC( thisTypeName,funcTname,ptr,id)(CLASS_CAST(ptr,thisTypeName,funcTname), ## args)


#endif /* LIB_VDMCLASS_H_ */
