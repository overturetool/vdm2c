/*
 * TypedValue.c
 *
 *  Created on: Dec 1, 2015
 *      Author: kel
 */

#include "lib/TypedValue.h"

struct TypedValue* newTypeValue(vdmtype type, TypedValueType value)
{
	struct TypedValue* ptr = (struct TypedValue*) malloc(sizeof(struct TypedValue));
	ptr->type = type;
	ptr->value = value;
	return ptr;
}

/// Basic
 struct TypedValue* newInt(int x){return newTypeValue(VDM_INT,(TypedValueType){.intVal= x});}
 struct TypedValue* newInt1(int x){return newTypeValue(VDM_INT1,(TypedValueType){.intVal= x});}
 struct TypedValue* newBool(bool x){return newTypeValue(VDM_BOOL,(TypedValueType){.boolVal= x});}
 struct TypedValue* newReal(double x){return newTypeValue(VDM_REAL,(TypedValueType){.doubleVal= x});}
 struct TypedValue* newChar(char x){return newTypeValue(VDM_CHAR,(TypedValueType){.charVal= x});}
 struct TypedValue* newQuote(unsigned int x){return newTypeValue(VDM_QUOTE,(TypedValueType){.uintVal= x});}


///

struct TypedValue* newSeq(size_t size)
{
	struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));
	ptr->size = size;
	ptr->value = (struct TypedValue**) malloc(sizeof(struct TypedValue*) * size);
	return newTypeValue(VDM_SEQ,(TypedValueType){.ptr= ptr});
}

struct TypedValue* newSet(size_t size)
{
	struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));
	ptr->size = size;
	ptr->value = (struct TypedValue**) malloc(sizeof(struct TypedValue) * size);
	return newTypeValue(VDM_SET, (TypedValueType){.ptr=ptr});
}

struct ClassType* newClassValue(int id, unsigned int* refs, freeVdmClassFunction freeClass, void* value)
{
	struct ClassType* ptr = (struct ClassType*) malloc(sizeof(struct ClassType));
	ptr->classId = id;
	ptr->value = value;
	ptr->freeClass = freeClass;
	ptr->refs = refs;
	(*refs)++;
	return ptr;
}

struct TypedValue* clone(struct TypedValue* x){

	//clone struct
	struct TypedValue* tmp = newTypeValue(x->type,x->value);

	//FIXME clone any pointers
	switch (tmp->type)
	{
	case VDM_BOOL:
	case VDM_CHAR:
	case VDM_INT:
	case VDM_INT1:
	case VDM_REAL:
	case VDM_QUOTE:
	{
		break;
	}
	case VDM_MAP:
		//todo
		break;
	case VDM_PRODUCT:
	case VDM_SEQ:
	case VDM_SET:
	{
		struct Collection* cptr = (struct Collection*) tmp->value.ptr;

		struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));

		//copy (size)
		*ptr = *cptr;
		ptr->value = (struct TypedValue**) malloc(sizeof(struct TypedValue) * ptr->size);

		for (int i = 0; i < cptr->size; i++)
		{
			ptr->value[i] = clone(cptr->value[i]);
		}

		tmp->value.ptr = ptr;
		break;
	}
	case VDM_CLASS:
	{
		//handle smart pointer
		struct ClassType* classTptr = (struct ClassType*) tmp->value.ptr;

		//improve using memcpy
		tmp->value.ptr = newClassValue(classTptr->classId,classTptr->refs,classTptr->freeClass,classTptr->value);
	}
	}

	return tmp;
}

void recursiveFree(struct TypedValue* ptr)
{
	switch (ptr->type)
	{
	case VDM_BOOL:
	case VDM_CHAR:
	case VDM_INT:
	case VDM_INT1:
	case VDM_REAL:
	case VDM_QUOTE:
	{
//		free(ptr->value);
//		ptr->value = NULL;
		break;
	}
	case VDM_MAP:
		//todo
		break;
	case VDM_PRODUCT:
	case VDM_SEQ:
	case VDM_SET:
	{
		struct Collection* cptr = (struct Collection*) ptr->value.ptr;
		for (int i = 0; i < cptr->size; i++)
		{
			recursiveFree(cptr->value[i]);
		}
		free(cptr);
		ptr->value.ptr = NULL;
		break;
	}
	case VDM_CLASS:
	{
		//handle smart pointer
		struct ClassType* classTptr = (struct ClassType*) ptr->value.ptr;
		classTptr->freeClass(classTptr->value);
		classTptr->value = NULL;
		classTptr->freeClass = NULL;

		//free classtype
		free(classTptr);
		ptr->value.ptr = NULL;
	}
	}

	//free typedvalue
	free(ptr);
}
