/*
 * #%~
 * The VDM to C Code Generator
 * %%
 * Copyright (C) 2015 - 2016 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http:XXXwww.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

/*
 * VdmClass.h
 *
 *  Created on: Dec 14, 2015
 *      Author: kel
 */

#ifndef LIB_VDMCLASS_H_
#define LIB_VDMCLASS_H_

#include<stddef.h>

#include "Vdm.h"

typedef void (*freeVdmClassFunction)(void*);

typedef TVP (*VirtualFunctionPointer)(void * self, ...);
/* typedef void (*VirtualFunctionPointer)(void * self, ...);  */

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

#define VDM_CLASS_FIELD_DEFINITION(className, name) TVP m_##className##_##name

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
	freeVdmClassFunction freeClass;/* TODO move to global map  */
};

struct ClassType* newClassValue(int id, unsigned int* refs, freeVdmClassFunction freeClass, void* value);

/*---------------------------------------------------
 *
 * CLASS access function macros
 *
 * - Class handling macros for calling functions and obtaining fields
 * --------------------------------------------------
 */
 
 /*  ############ PRIVATE INTERNAL MACROS #####################  */
 
 

/*
 * Make a FOREACH macro for call cast
 * TODO: change void* into the concrete type of the struct
 */
#define TVP_VARG_CAST_FE_1(WHAT, X) void*
#define TVP_VARG_CAST_FE_2(WHAT, X, ...) TVP_VARG_CAST_FE_1(WHAT, __VA_ARGS__)WHAT(X)
#define TVP_VARG_CAST_FE_3(WHAT, X, ...) TVP_VARG_CAST_FE_2(WHAT, __VA_ARGS__)WHAT(X)
#define TVP_VARG_CAST_FE_4(WHAT, X, ...) TVP_VARG_CAST_FE_3(WHAT, __VA_ARGS__)WHAT(X)
#define TVP_VARG_CAST_FE_5(WHAT, X, ...) TVP_VARG_CAST_FE_4(WHAT, __VA_ARGS__)WHAT(X)
/* ... repeat as needed  */

#define TVP_VARG_CAST_GET_MACRO(_1,_2,_3,_4,_5,NAME,...) NAME
#define TVP_VARG_CAST_FOR_EACH(action,...) \
  TVP_VARG_CAST_GET_MACRO(__VA_ARGS__,TVP_VARG_CAST_FE_5,TVP_VARG_CAST_FE_4,TVP_VARG_CAST_FE_3,TVP_VARG_CAST_FE_2,TVP_VARG_CAST_FE_1)(action,__VA_ARGS__)

/*
 * Make a FOREACH macro for call cast base argument type
 */
#define TVP_VARG_CAST_ARG_TYPE(N) , TVP

/*
 * Macro to generate VTable function cast to match original function. This is to avoid undefined behaviour in the C compiler
 */
#define CREATE_CALL_VARG_CAST( ...) (TVP (*)( TVP_VARG_CAST_FOR_EACH(TVP_VARG_CAST_ARG_TYPE , ##__VA_ARGS__)))

/*  ############ PUBLIC CLASS VALUE ACCESS #####################  */
/*
 * Class Value unwrap macro - TODO: How to assert inline
 */
#define TO_CLASS_PTR(tv, type) ((struct type *) ( ((struct ClassType*)tv->value.ptr)->value))

/*
 * Cast to class pointer by moving it forward to the class specific VTable
 * Note that we only adjust the pointer if a subtype is given (i.e. not the type of it self)
 */
#define CLASS_CAST(ptr, from, to) ((struct to *)(((unsigned char*)ptr) + (SAME_ARGS(from,to)?0: offsetof(struct from, _##to##_pVTable))))

/*
 * Down-casting a super class pointer to the concrete class. i.e if A extends B and we have a 'b' pointer we can downcast it to an 'a'
 */
#define CLASS_DOWNCAST(thisClassName, upCastClassName, ptr) (\
		(struct upCastClassName *)\
		(\
((unsigned char*)ptr) - offsetof(struct upCastClassName, _##thisClassName##_pVTable)\
		)\
		)

/*
 * Macro to obtain the (sub-)class specific field from a class struct
 */
#define GET_STRUCT_FIELD(tname, ptr, fieldtype, fieldname) (*((fieldtype*)((((unsigned char*)ptr) + offsetof(struct tname, fieldname)))))

/*
 * Macro to set the (sub-)class specific field from a class struct
 */
#define SET_STRUCT_FIELD(tname,ptr,fieldtype,fieldname,newValue)\
	vdmFree(*((fieldtype*)(((unsigned char*)ptr) + offsetof(struct tname, fieldname))));\
	(*((fieldtype*)(((unsigned char*)ptr) + offsetof(struct tname, fieldname))) = vdmClone(newValue))

#define SET_STRUCT_FIELD_GC(tname,ptr,fieldtype,fieldname,newValue)\
	vdmFree(*((fieldtype*)(((unsigned char*)ptr) + offsetof(struct tname, fieldname))));\
	(*((fieldtype*)(((unsigned char*)ptr) + offsetof(struct tname, fieldname))) = vdmCloneGC(newValue,\
			(TVP *)((fieldtype*)(((unsigned char*)ptr) + offsetof(struct tname, fieldname)))))

/*
 * Macro to obtain the (sub-)class specific VTable from a class struct
 */
#define GET_VTABLE_FUNC(thisTypeName, funcTname, ptr, id)   GET_STRUCT_FIELD(thisTypeName,ptr,struct VTable*,_##funcTname##_pVTable)[id].pFunc

/*
 * Macro to obtain a function from a (sub-)class specific VTable from a class struct
 */

#define CALL_FUNC(thisTypeName,funcTname,classValue,id, ... )     (CREATE_CALL_VARG_CAST(struct thisTypeName*, ## __VA_ARGS__ )GET_VTABLE_FUNC( thisTypeName,funcTname,TO_CLASS_PTR(classValue,thisTypeName),id))(CLASS_CAST(TO_CLASS_PTR(classValue,thisTypeName),thisTypeName,funcTname), ##  __VA_ARGS__)



/*
 * Macro to obtain a field from a (sub-)class specific class struct. We clone to preserve value semantics and the rule of freeing
 */
#define GET_FIELD(thisTypeName, fieldTypeName, classValue, fieldName) vdmClone(GET_STRUCT_FIELD(fieldTypeName,CLASS_CAST(TO_CLASS_PTR(classValue,thisTypeName),thisTypeName,fieldTypeName) ,TVP,m_##fieldTypeName##_##fieldName))
#define GET_FIELD_GC(thisTypeName, fieldTypeName, classValue, fieldName) vdmCloneGC(GET_STRUCT_FIELD(fieldTypeName,CLASS_CAST(TO_CLASS_PTR(classValue,thisTypeName),thisTypeName,fieldTypeName) ,TVP,m_##fieldTypeName##_##fieldName), NULL)

/*
 * Macro to set a field from a (sub-)class specific class struct. We clone to preserve value semantics and the rule of freeing
 */
#define SET_FIELD(thisTypeName, fieldTypeName, classValue, fieldName, newValue) SET_FIELD_PTR(thisTypeName,\
																							fieldTypeName,\
																							 TO_CLASS_PTR(classValue,thisTypeName),\
																							 fieldName,\
																							 newValue)

#define SET_FIELD_GC(thisTypeName, fieldTypeName, classValue, fieldName, newValue) SET_FIELD_PTR_GC(thisTypeName,\
																							fieldTypeName,\
																							 TO_CLASS_PTR(classValue,thisTypeName),\
																							 fieldName,\
																							 newValue)


/*  old stuff  */

/* Call function from VTable and change ptr to the correct offset. With obtional arguments  */

/*  ############ PRIVATE CLASS POINTER ACCESS #####################  */

/*
 * Macro to obtain a field from a (sub-)class specific class struct. We clone to preserve value semantics and the rule of freeing
 */
#define GET_FIELD_PTR(thisTypeName, fieldTypeName, ptr, fieldName) vdmClone(GET_STRUCT_FIELD(fieldTypeName,CLASS_CAST(ptr,thisTypeName,fieldTypeName) ,TVP,m_##fieldTypeName##_##fieldName))
#define GET_FIELD_PTR_GC(thisTypeName, fieldTypeName, ptr, fieldName) vdmCloneGC(GET_STRUCT_FIELD(fieldTypeName,CLASS_CAST(ptr,thisTypeName,fieldTypeName) ,TVP,m_##fieldTypeName##_##fieldName), NULL)

/*
 * Macro to obtain a field from a (sub-)class specific class struct. This macro is intended to be used for updating sequences and maps, which is why the field is not cloned.
 */
#define GET_FIELD_PTR_BYREF(thisTypeName, fieldTypeName, ptr, fieldName) GET_STRUCT_FIELD(fieldTypeName,CLASS_CAST(ptr,thisTypeName,fieldTypeName) ,TVP,m_##fieldTypeName##_##fieldName)

/*
 * Macro to set a field from a (sub-)class specific class struct.
 */
#define SET_FIELD_PTR(thisTypeName, fieldTypeName, ptr, fieldName, newValue) SET_STRUCT_FIELD(fieldTypeName,\
																							 CLASS_CAST(ptr,thisTypeName,fieldTypeName) ,\
																							 TVP,\
																							 m_##fieldTypeName##_##fieldName,\
																							 newValue)

#define SET_FIELD_PTR_GC(thisTypeName, fieldTypeName, ptr, fieldName, newValue) SET_STRUCT_FIELD_GC(fieldTypeName,\
																							 CLASS_CAST(ptr,thisTypeName,fieldTypeName) ,\
																							 TVP,\
																							 m_##fieldTypeName##_##fieldName,\
																							 newValue)

/*
 * Macro to obtain a function from a (sub-)class specific VTable from a class struct
 */
#define CALL_FUNC_PTR(thisTypeName,funcTname,ptr,id, ... )     (CREATE_CALL_VARG_CAST(struct thisTypeName*, ## __VA_ARGS__ )GET_VTABLE_FUNC( thisTypeName,funcTname,ptr,id))(CLASS_CAST(ptr,thisTypeName,funcTname), ##  __VA_ARGS__)


/*  ############ UTILITIES #####################  */
/*
 * compare arguments. Techinically we wated: typeid(from)==typeid(to)
 * but this is not valid C. However it can be ifdeed for C++. But the name compare if ok
 */
#define SAME_ARGS(x, y) !strcmp(#x, #y)

#define SELF(objName) newTypeValue(VDM_CLASS, (TypedValueType){.ptr=newClassValue(this->_##objName##_id, &this->_##objName##_refs, (freeVdmClassFunction)&objName##_free, this)});


#define SELF_GC(objName, varName) newTypeValueGC(VDM_CLASS, (TypedValueType){.ptr=newClassValue(this->_##objName##_id, &this->_##objName##_refs, (freeVdmClassFunction)&objName##_free, this)}, &varName);

#endif /* LIB_VDMCLASS_H_ */
