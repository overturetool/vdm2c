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
struct TypedValue* newInt(int x)
{
	return newTypeValue(VDM_INT, (TypedValueType
			)
			{ .intVal = x });
}
struct TypedValue* newInt1(int x)
{
	return newTypeValue(VDM_INT1, (TypedValueType
			)
			{ .intVal = x });
}
struct TypedValue* newBool(bool x)
{
	return newTypeValue(VDM_BOOL, (TypedValueType
			)
			{ .boolVal = x });
}
struct TypedValue* newReal(double x)
{
	return newTypeValue(VDM_REAL, (TypedValueType
			)
			{ .doubleVal = x });
}
struct TypedValue* newChar(char x)
{
	return newTypeValue(VDM_CHAR, (TypedValueType
			)
			{ .charVal = x });
}
struct TypedValue* newQuote(unsigned int x)
{
	return newTypeValue(VDM_QUOTE, (TypedValueType
			)
			{ .uintVal = x });
}

///

struct TypedValue* newCollection(size_t size, vdmtype type)
{
	struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));
	ptr->size = size;
	ptr->value = (struct TypedValue**) calloc(size,sizeof(struct TypedValue*));//I know this is slower thank malloc but better for products
	return newTypeValue(type, (TypedValueType
			)
			{ .ptr = ptr });
}

struct TypedValue* newSeq(size_t size)
{
	return newCollection(size, VDM_SEQ);
}

struct TypedValue* newSet(size_t size)
{
	return newCollection(size, VDM_SET);
}

struct TypedValue* newProduct(size_t size)
{
	return newCollection(size, VDM_PRODUCT);
}


struct TypedValue* newCollectionWithValues(vdmtype type,size_t size,TVP* elements)
{
	TVP product = newCollection(size,type);
	struct Collection* col = (struct Collection*)product->value.ptr;

	for (int i = 0; i < size; i++) {
		col->value[i]= clone(elements[i]);
	}
	return product;
}

struct TypedValue* newSeqWithValues(size_t size,TVP* elements)
{
	return newCollectionWithValues(VDM_SEQ,size,elements);
}

struct TypedValue* newSetWithValues(size_t size,TVP* elements)
{
	//FIXME
	return newCollectionWithValues(VDM_SET,size,elements);
}

struct TypedValue* newProductWithValues(size_t size,TVP* elements)
{
	return newCollectionWithValues(VDM_PRODUCT,size,elements);
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

struct TypedValue* clone(struct TypedValue* x)
{

	//clone struct
	struct TypedValue* tmp = newTypeValue(x->type, x->value);

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
	case VDM_OPTIONAL:
		//TODO
		break;
	case VDM_RECORD:
	{
		//TODO duplicate (memcpy) and duplicate what ever any pointers points to except if a class
		break;
	}
	case VDM_CLASS:
	{
		//handle smart pointer
		struct ClassType* classTptr = (struct ClassType*) tmp->value.ptr;

		//improve using memcpy
		tmp->value.ptr = newClassValue(classTptr->classId, classTptr->refs, classTptr->freeClass, classTptr->value);
	}
	}

	return tmp;
}

bool equals(struct TypedValue* a, struct TypedValue* b)
{
	if (a->type != b->type) //is this correct for optional types too
	{
		return false;
	}

	switch (a->type)
	{
	case VDM_BOOL:
	{
		return a->value.boolVal == b->value.boolVal;
	}
	case VDM_CHAR:
	{
		return a->value.charVal == b->value.charVal;
	}
	case VDM_INT:
	case VDM_INT1:
	{
		return a->value.intVal == b->value.intVal;
	}
	case VDM_REAL:
	{
		return a->value.doubleVal == b->value.doubleVal;
	}
	case VDM_QUOTE:
	{
		return a->value.uintVal == b->value.uintVal;
	}
	case VDM_MAP:
	{
		//			return mapEqual(a, b);
		break;
	}
	case VDM_PRODUCT:
	case VDM_SEQ:
	{
		return collectionEqual(a, b);
	}
	case VDM_SET:
	{
		//FIXME
		return collectionEqual(a, b);
	}
	case VDM_OPTIONAL:
	{
		break;
	}
	case VDM_RECORD:
	{
		//like class but by value
		//TODO
		break;
	}
	case VDM_CLASS:
	{
		struct ClassType* ac = a->value.ptr;
		struct ClassType* bc = b->value.ptr;

		//reference compare does the pointer point to the same instance
		return ac->value == bc->value;
	}

	}
	return false;
}

bool collectionEqual(TVP col1,TVP col2)
{
	//internal function do not call except if args points to a collection

	struct Collection* aCol = (struct Collection*) col1->value.ptr;
	struct Collection* bCol = (struct Collection*) col2->value.ptr;

	if(aCol->size!=bCol->size)
	{
		//wrong sizes
		return false;
	}

	bool match = true;

	for (int i = 0; i < aCol->size; i++)
	{
		match &= equals(aCol->value[i],aCol->value[i]);
	}
	return match;
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
		break;
	}
	case VDM_MAP:
		//TODO:
		break;
	case VDM_PRODUCT:
	case VDM_SEQ:
	case VDM_SET:
	{
		struct Collection* cptr = (struct Collection*) ptr->value.ptr;
		for (int i = 0; i < cptr->size; i++)
		{
			if (cptr->value[i] != NULL)
			{
				recursiveFree(cptr->value[i]);
			}
		}
		free(cptr);
		ptr->value.ptr = NULL;
		break;
	}
	case VDM_OPTIONAL:
		//TODO
		break;
	case VDM_RECORD:
	{
		//handle smart pointer
		struct RecordType* recordTptr = (struct RecordType*) ptr->value.ptr;
		recordTptr->freeRecord(recordTptr->value);
		recordTptr->value = NULL;
		recordTptr->freeRecord = NULL;

		//free record type
		free(recordTptr);
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
		break;
	}
	}

	//free typedvalue
	free(ptr);
}
