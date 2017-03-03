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
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

/*
 * TypedValue.c
 *
 *  Created on: Dec 1, 2015
 *      Author: kel
 */

#include "Vdm.h"
#include "TypedValue.h"
#include "VdmClass.h"

#define ASSERT_CHECK_BOOL(s) assert(s->type == VDM_BOOL && "Value is not a boolean")
#define ASSERT_CHECK_NUMERIC(s) assert((s->type == VDM_INT||s->type == VDM_NAT||s->type == VDM_NAT1||s->type == VDM_REAL||s->type == VDM_RAT) && "Value is not numeric")
#define ASSERT_CHECK_REAL(s) assert((s->type ==  VDM_REAL) && "Value is not real")
#define ASSERT_CHECK_INT(s) assert((s->type ==  VDM_INT) && "Value is not integer")
#define ASSERT_CHECK_CHAR(s) assert((s->type ==  VDM_CHAR) && "Value is not a character")
#define ASSERT_CHECK_RECORD(s) assert(s->type == VDM_RECORD && "Value is not a record")




struct TypedValue* newTypeValue(vdmtype type, TypedValueType value)
{
	struct TypedValue* ptr = (struct TypedValue*) malloc(sizeof(struct TypedValue));
	ptr->type = type;
	ptr->value = value;
	ptr->ref_from = NULL;

	return ptr;
}

//#ifndef WITH_GC
/// Basic
struct TypedValue* newInt(int x)
{
	return newTypeValue(VDM_INT, (TypedValueType
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
			{ .quoteVal = x });
}

struct TypedValue* newCollection(size_t size, vdmtype type)
{
	struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));
	ptr->size = size;
	ptr->value = (struct TypedValue**) calloc(size, sizeof(struct TypedValue*)); //I know this is slower than malloc but better for products
	return newTypeValue(type, (TypedValueType
	)
			{ .ptr = ptr });
}

struct TypedValue* newCollectionWithValues(size_t size, vdmtype type, TVP* elements)
{
	TVP product = newCollection(size,type);
	UNWRAP_COLLECTION(col,product);

	for (int i = 0; i < size; i++)
	{
		col->value[i]= vdmClone(elements[i]);
	}
	return product;
}
//#endif /* ifndef WITH_GC */



int vdmCollectionSize(TVP collection)
{
	ASSERT_CHECK_COLLECTION(collection);
	UNWRAP_COLLECTION(col,collection);
	return col->size;
}

TVP vdmCollectionIndex(TVP collection,int index)
{

	ASSERT_CHECK_COLLECTION(collection);

	UNWRAP_COLLECTION(col,collection);

	assert(index>=0 && index<col->size && "invalid index");
	return vdmClone(col->value[index]);

}

TVP vdmClone(TVP x)
{
	TVP tmp;

	if(x == NULL)
	{
		return NULL;
	}

	tmp = newTypeValue(x->type, x->value);

	//FIXME vdmClone any pointers
	switch (tmp->type)
	{
	case VDM_BOOL:
	case VDM_CHAR:
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
	case VDM_REAL:
	case VDM_RAT:
	case VDM_QUOTE:
	{
		//encoded as values so the initial copy line handles these
		break;
	}
#ifndef NO_MAPS
	case VDM_MAP:
		//todo
		break;
#endif
#ifndef NO_PRODUCTS
	case VDM_PRODUCT:
	{
		UNWRAP_COLLECTION(cptr, tmp);

		struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));

		//copy (size)
		*ptr = *cptr;
		ptr->value = (struct TypedValue**) malloc(sizeof(struct TypedValue) * ptr->size);

		for (int i = 0; i < cptr->size; i++)
		{
			ptr->value[i] = vdmClone(cptr->value[i]);
		}

		tmp->value.ptr = ptr;
		break;
	}
#endif
#ifndef NO_SEQS
	case VDM_SEQ:
	{
		UNWRAP_COLLECTION(cptr, tmp);

		struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));

		//copy (size)
		*ptr = *cptr;
		ptr->value = (struct TypedValue**) malloc(sizeof(struct TypedValue) * ptr->size);

		for (int i = 0; i < cptr->size; i++)
		{
			ptr->value[i] = vdmClone(cptr->value[i]);
		}

		tmp->value.ptr = ptr;
		break;
	}
#endif
#ifndef NO_SETS
	case VDM_SET:
	{
		UNWRAP_COLLECTION(cptr, tmp);

		struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));

		//copy (size)
		*ptr = *cptr;
		ptr->value = (struct TypedValue**) malloc(sizeof(struct TypedValue) * ptr->size);

		for (int i = 0; i < cptr->size; i++)
		{
			ptr->value[i] = vdmClone(cptr->value[i]);
		}

		tmp->value.ptr = ptr;
		break;
	}
#endif
	//	case VDM_OPTIONAL:
	//		//TODO
	//		break;
#ifndef NO_RECORDS
	case VDM_RECORD:
	{
		ASSERT_CHECK_RECORD(x);

		int i;
		TVP tmpField = NULL;
		int numFields;

		//Create a shell for a new class and populate it with the information
		//that can be used from the one being cloned, but all of it should be
		//irrelevant for records.
		(tmp->value).ptr = newClassValue(((struct ClassType*)(x->value.ptr))->classId,
				((struct ClassType*)(x->value.ptr))->refs,
				NULL,
				NULL);

		//Generic way of accessing the number-of-fields field.  The name of the record type is
		//hard-coded into the corresponding struct name.
		numFields = (*((struct TypedValue**)((char*)(((struct ClassType*)x->value.ptr)->value) + \
				sizeof(struct VTable*) + \
				sizeof(int) + \
				sizeof(unsigned int))))->value.intVal;

		//Allocate memory to be populated with the pointers pointing to the cloned fields.
		((struct ClassType*)((tmp->value).ptr))->value = malloc(sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(struct TypedValue*) + sizeof(struct TypedValue*) * numFields);

		for(i = 0; i <= numFields; i++)
		{
			//Start cloning the fields one by one, including the number-of-fields field,
			//since it is just a TVP.
			tmpField = vdmClone(*((struct TypedValue**)((char*)(((struct ClassType*)x->value.ptr)->value) + sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(struct TypedValue*) * i)));

			//Only copy the address stored in tmpField so that that memory is now addressed by the current field in the struct.
			memcpy(((struct TypedValue**)((char*)(((struct ClassType*)tmp->value.ptr)->value) + sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(struct TypedValue*) * i)), &tmpField, sizeof(struct TypedValue*));
		}

		break;
	}
#endif /* NO_RECORDS */
	case VDM_CLASS:
	{
		//handle smart pointer
		struct ClassType* classTptr = (struct ClassType*) tmp->value.ptr;

		//improve using memcpy
		tmp->value.ptr = newClassValue(classTptr->classId, classTptr->refs, classTptr->freeClass, classTptr->value);
		break;
	}
	}

	return tmp;
}



bool equals(struct TypedValue* a, struct TypedValue* b)
{
	if(isNumber(a)&& isNumber(b))
	{
		return toDouble(a)==toDouble(b);
	}
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
	case VDM_NAT:
	case VDM_NAT1:
	{
		return a->value.intVal == b->value.intVal;
	}
	case VDM_RAT:
	case VDM_REAL:
	{
		return a->value.doubleVal == b->value.doubleVal;
	}
	case VDM_QUOTE:
	{
		return a->value.quoteVal == b->value.quoteVal;
	}
#ifndef NO_MAPS
	case VDM_MAP:
	{
		TVP r0 = vdmMapEquals(a, b);
		bool r = toBool(r0);
		vdmFree(r0);
		return r;
	}
#endif
#ifndef NO_PRODUCTS
	case VDM_PRODUCT:
	{
		return collectionEqual(a, b);
	}
#endif
#ifndef NO_SEQS
	case VDM_SEQ:
	{
		return collectionEqual(a, b);
	}
#endif
#ifndef NO_SETS
	case VDM_SET:
	{
		TVP r0 = vdmSetEquals(a, b);
		bool r = toBool(r0);
		vdmFree(r0);
		return r;
	}
#endif
	//	case VDM_OPTIONAL:
	//	{
	//		break;
	//	}
#ifndef NO_RECORDS
	case VDM_RECORD:
	{
		ASSERT_CHECK_RECORD(a);
		ASSERT_CHECK_RECORD(b);

		int i;
		TVP res;
		int numFields_a, numFields_b;

		numFields_a = (*((struct TypedValue**)((char*)(((struct ClassType*)a->value.ptr)->value) + \
				sizeof(struct VTable*) + \
				sizeof(int) + \
				sizeof(unsigned int))))->value.intVal;

		numFields_b = (*((struct TypedValue**)((char*)(((struct ClassType*)b->value.ptr)->value) + \
				sizeof(struct VTable*) + \
				sizeof(int) + \
				sizeof(unsigned int))))->value.intVal;

		if(numFields_a != numFields_b)
		{
			return false;
		}

		for(i = 0; i < numFields_a; i++)
		{
			res = vdmEquals(*((struct TypedValue**)((char*)(((struct ClassType*)a->value.ptr)->value) + sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(struct TypedValue*) + sizeof(struct TypedValue*) * i)), \
					*((struct TypedValue**)((char*)(((struct ClassType*)b->value.ptr)->value) + sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(struct TypedValue*) + sizeof(struct TypedValue*) * i)));
			if(!res->value.boolVal)
			{
				vdmFree(res);
				return false;
			}
		}

		vdmFree(res);
		return true;

	}
#endif /* NO_RECORDS */
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

	UNWRAP_COLLECTION(aCol,col1);
	UNWRAP_COLLECTION(bCol,col2);

	if(aCol->size!=bCol->size)
	{
		//wrong sizes
		return false;
	}

	bool match = true;

	for (int i = 0; i < aCol->size; i++)
	{
		match &= equals(aCol->value[i],bCol->value[i]);
	}
	return match;
}


void vdmFree_GCInternal(struct TypedValue* ptr)
{
	if (ptr == NULL)
		return;

	switch (ptr->type)
	{
	case VDM_BOOL:
	case VDM_CHAR:
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
	case VDM_REAL:
	case VDM_RAT:
	case VDM_QUOTE:
	{
		break;
	}
#ifndef NO_MAPS
	case VDM_MAP:
		//TODO:
		break;
#endif
#ifndef NO_PRODUCTS
	case VDM_PRODUCT:
	{
		UNWRAP_COLLECTION(cptr, ptr);
		for (int i = 0; i < cptr->size; i++)
		{
			if (cptr->value[i] != NULL)
			{
				vdmFree_GCInternal(cptr->value[i]);
			}
		}
		free(cptr->value);
		free(cptr);
		ptr->value.ptr = NULL;
		break;
	}
#endif
#ifndef NO_SEQS
	case VDM_SEQ:
	{
		UNWRAP_COLLECTION(cptr, ptr);
		for (int i = 0; i < cptr->size; i++)
		{
			if (cptr->value[i] != NULL)
			{
				vdmFree_GCInternal(cptr->value[i]);
			}
		}
		free(cptr->value);
		free(cptr);
		ptr->value.ptr = NULL;
		break;
	}
#endif
#ifndef NO_SETS
	case VDM_SET:
	{
		UNWRAP_COLLECTION(cptr, ptr);
		for (int i = 0; i < cptr->size; i++)
		{
			if (cptr->value[i] != NULL)
			{
				vdmFree_GCInternal(cptr->value[i]);
			}
		}
		free(cptr->value);
		free(cptr);
		ptr->value.ptr = NULL;
		break;
	}
#endif
	//	case VDM_OPTIONAL:
	//		//TODO
	//		break;
#ifndef NO_RECORDS
	case VDM_RECORD:
		ASSERT_CHECK_RECORD(ptr);

		int i;
		int numFields;

		numFields = (*((struct TypedValue**)((char*)(((struct ClassType*)ptr->value.ptr)->value) + \
				sizeof(struct VTable*) + \
				sizeof(int) + \
				sizeof(unsigned int))))->value.intVal;

		//We include the numFields field here, since it is just a TVP.
		for(i = 0; i <= numFields; i++)
		{
			vdmFree_GCInternal(*((struct TypedValue**)((char*)(((struct ClassType*)ptr->value.ptr)->value) + sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(struct TypedValue*) * i)));
		}

		//Free the virtual function table.
		free(((struct ClassType*)ptr->value.ptr)->value);

		break;
#endif /* NO_RECORDS */
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



void vdmFree(struct TypedValue* ptr)
{
	TVP *tmp;

	if (ptr == NULL)
		return;

	switch (ptr->type)
	{
	case VDM_BOOL:
	case VDM_CHAR:
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
	case VDM_REAL:
	case VDM_RAT:
	case VDM_QUOTE:
	{
		break;
	}
#ifndef NO_MAPS
	case VDM_MAP:
		//TODO:
		break;
#endif
#ifndef NO_PRODUCTS
	case VDM_PRODUCT:
	{
			UNWRAP_COLLECTION(cptr, ptr);
			for (int i = 0; i < cptr->size; i++)
			{
				if (cptr->value[i] != NULL)
				{
					vdmFree(cptr->value[i]);
				}
			}
			free(cptr->value);
			free(cptr);
			ptr->value.ptr = NULL;
			break;
		}
#endif
#ifndef NO_SEQS
	case VDM_SEQ:
	{
			UNWRAP_COLLECTION(cptr, ptr);
			for (int i = 0; i < cptr->size; i++)
			{
				if (cptr->value[i] != NULL)
				{
					vdmFree(cptr->value[i]);
				}
			}
			free(cptr->value);
			free(cptr);
			ptr->value.ptr = NULL;
			break;
		}
#endif
#ifndef NO_SETS
	case VDM_SET:
	{
		UNWRAP_COLLECTION(cptr, ptr);
		for (int i = 0; i < cptr->size; i++)
		{
			if (cptr->value[i] != NULL)
			{
				vdmFree(cptr->value[i]);
			}
		}
		free(cptr->value);
		free(cptr);
		ptr->value.ptr = NULL;
		break;
	}
#endif
	//	case VDM_OPTIONAL:
	//		//TODO
	//		break;
#ifndef NO_RECORDS
	case VDM_RECORD:
		ASSERT_CHECK_RECORD(ptr);

		int i;
		int numFields;

		numFields = (*((struct TypedValue**)((char*)(((struct ClassType*)ptr->value.ptr)->value) + \
				sizeof(struct VTable*) + \
				sizeof(int) + \
				sizeof(unsigned int))))->value.intVal;

		//We include the numFields field here, since it is just a TVP.
		for(i = 0; i <= numFields; i++)
		{
			vdmFree(*((struct TypedValue**)((char*)(((struct ClassType*)ptr->value.ptr)->value) + sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(struct TypedValue*) * i)));
		}

		//Free the virtual function table.
		free(((struct ClassType*)ptr->value.ptr)->value);

		break;
#endif /* NO_RECORDS */
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
	remove_allocd_mem_node_by_location(ptr);
	tmp = ptr->ref_from;
	free(ptr);
	if(tmp != NULL)
	{
		*tmp = NULL;
	}
}

TVP vdmEquals(struct TypedValue* a, struct TypedValue* b)
{	return newBool(equals(a,b));}

TVP vdmEqualsGC(struct TypedValue* a, struct TypedValue* b, TVP *from)
{	return newBoolGC(equals(a,b), from);}

TVP vdmInEquals(struct TypedValue* a, struct TypedValue* b)
{	return newBool(!equals(a,b));}

