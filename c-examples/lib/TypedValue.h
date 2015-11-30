/*
 * TypedValue.h
 *
 *  Created on: Nov 20, 2015
 *      Author: kel
 */

#ifndef TYPEDVALUE_H_
#define TYPEDVALUE_H_

#include "Globals.h"
#include <stdlib.h>
#include "classes/ModelVarBOOL.h"

//,VDM_UNION
typedef enum
{
	VDM_INT, VDM_INT1, VDM_BOOL, VDM_REAL, VDM_CHAR, VDM_SET, VDM_SEQ, VDM_MAP, VDM_PRODUCT, VDM_QUOTE, VDM_CLASS
} vdmtype;

struct TypedValue
{
	void* value;
	vdmtype type;
};

struct Collection
{
	struct TypedValue** value;
	int size;
};

struct ClassType
{
	void* value;
	int classId;
};


//static struct TypedValue* newTypeValue(vdmtype type, void* value);
//static struct TypedValue* newSeq(size_t size);
//static struct TypedValue* newSet(size_t size);
//static struct ClassType* newClassValue(int id, unsigned int* refs, void* value);
//static void recursiveFree(struct TypedValue* ptr);

static struct TypedValue* newTypeValue(vdmtype type, void* value)
{
	struct TypedValue* ptr =(struct TypedValue*) malloc(sizeof(struct TypedValue*));
	ptr->type = type;
	ptr->value = value;
	return ptr;
}

static struct TypedValue* newSeq(size_t size)
{
	struct Collection* ptr =(struct Collection*) malloc(sizeof(struct Collection*));
	ptr->size = size;
	ptr->value =(struct TypedValue**) malloc(sizeof(struct TypedValue*) * size);
	return newTypeValue(VDM_SEQ, ptr);
}

static struct TypedValue* newSet(size_t size)
{
	struct Collection* ptr = (struct Collection*)malloc(sizeof(struct Collection*));
	ptr->size = size;
	ptr->value = (struct TypedValue**)malloc(sizeof(struct TypedValue*) * size);
	return newTypeValue(VDM_SET, ptr);
}

static struct ClassType* newClassValue(int id, unsigned int* refs, void* value)
{
	struct ClassType* ptr = (struct ClassType*)malloc(sizeof(struct ClassType*));
	ptr->classId = id;
	ptr->value = value;
	(*refs)++;
	return ptr;
}


static void recursiveFree(struct TypedValue* ptr)
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
		free(ptr->value);
		ptr->value = NULL;
		break;
	}
	case VDM_MAP:
		//todo
		break;
	case VDM_PRODUCT:
	case VDM_SEQ:
	case VDM_SET:
	{
		struct Collection* cptr =(struct Collection*) ptr->value;
		for (int i = 0; i < cptr->size; i++)
		{
			recursiveFree(cptr->value[i]);
		}
		free(cptr);
		ptr->value = NULL;
		break;
	}
	case VDM_CLASS:
	{
		//handle smart pointer
		struct ClassType* classTptr =(struct ClassType*) ptr->value;
		int count = 0;

		switch (classTptr->classId)
		//maybe global lookup map is better since it is dynamic
		{
		case ModelVarBOOL_ID:
		{
			struct ModelVarBOOL* cptr = (struct ModelVarBOOL*) classTptr->value;
			count = --cptr->_refs;
			break;
		}
//TODO add any other classes

		}

		if (count < 1)
		{
			//free class struct
			free(classTptr->value);
			classTptr->value=NULL;
		}
		//free class type struct
		free(classTptr);
		ptr->value=NULL;
	}
	}

	free(ptr);
}





#endif /* TYPEDVALUE_H_ */
