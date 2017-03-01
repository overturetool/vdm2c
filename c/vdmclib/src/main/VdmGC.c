#include "Vdm.h"
#include "VdmGC.h"
#include "VdmClass.h"


extern void vdmFree_GCInternal(struct TypedValue* ptr);

#define ASSERT_CHECK_RECORD(s) assert(s->type == VDM_RECORD && "Value is not a record")

struct alloc_list_node *allocd_mem_head = NULL;
struct alloc_list_node *allocd_mem_tail = NULL;

void vdm_gc_init()
{
	allocd_mem_head = (struct alloc_list_node*)malloc(sizeof (struct alloc_list_node));
	allocd_mem_tail = allocd_mem_head;

	allocd_mem_head->loc = NULL;
	allocd_mem_head->next = NULL;
}

void add_allocd_mem_node(TVP l, TVP *from)
{
	allocd_mem_tail->loc = l;
	allocd_mem_tail->loc->ref_from = from;

	allocd_mem_tail->next = (struct alloc_list_node*)malloc(sizeof(struct alloc_list_node));
	allocd_mem_tail = allocd_mem_tail->next;
	allocd_mem_tail->next = NULL;
}



void remove_allocd_mem_node_by_location(TVP loc)
{
	struct alloc_list_node *tmp, *prev;

	prev = NULL;
	tmp = allocd_mem_head;

	if(tmp == NULL)
	{
		//GC list empty.
		return;
	}

	while(tmp->loc != loc)
	{
		prev = tmp;
		tmp = tmp->next;

		if(tmp == NULL)
		{
			break;
		}
	}

	if(tmp == NULL)
	{
		//This memory is not under GC control.
		return;
	}
	else if(tmp == allocd_mem_head)
	{
		allocd_mem_head = allocd_mem_head->next;
		if(allocd_mem_tail == tmp)
		{
			allocd_mem_tail = allocd_mem_head;
		}

		free(tmp);
		return;
	}
	else
	{
		prev->next = tmp->next;
		free(tmp);
		return;
	}
	return;
}



void remove_allocd_mem_node(struct alloc_list_node *node)
{
	struct alloc_list_node *tmp, *prev;

	tmp = allocd_mem_head;
	prev = NULL;

	if(tmp == NULL)
	{
		//GC list empty.
		return;
	}

	while(tmp != node)
	{
		prev = tmp;
		tmp = tmp->next;

		if(tmp == NULL)
		{
			return;
		}
	}

	if(tmp == allocd_mem_head)
	{
		allocd_mem_head = allocd_mem_head->next;
		if(allocd_mem_tail == tmp)
		{
			allocd_mem_tail = allocd_mem_head;
		}

		free(node);
		return;
	}
	else
	{
		prev->next = tmp->next;
		free(tmp);
		return;
	}
	return;
}

void vdm_gc_shutdown()
{
	struct alloc_list_node *tmp;

	tmp = allocd_mem_head;

	while(tmp != allocd_mem_tail)
	{
		if(tmp->loc != NULL)
		{
			vdmFree_GCInternal(tmp->loc);
			remove_allocd_mem_node(tmp);
		}

		tmp = tmp->next;
	}
	free(allocd_mem_head);
	allocd_mem_head = NULL;
}

void vdm_gc()
{
	struct alloc_list_node *current, *tmp;
	TVP tmp_loc;

	current = allocd_mem_head;

	//Nothing to do if no memory currently allocated.
	if(current->loc == NULL && current->next == NULL)
		return;

	while(current != allocd_mem_tail)
	{
		tmp = current->next;
		tmp_loc = current->loc;

		//No information was passed about where the reference was assigned.
		//This is the case when the value is created in-place or when freed using vdmFree().
		if(current->loc->ref_from == NULL)
		{
			remove_allocd_mem_node(current);
			vdmFree_GCInternal(tmp_loc);
		}
		else if(*(current->loc->ref_from) != current->loc)
		{
			//Check that there is no interference between this call's stack
			//variables and the reference to the memory we are freeing.
			//If there is, then postpone reclamation to a later pass.
			if(!(((void *)(current->loc->ref_from) == (void *)(&tmp)) || ((void *)(current->loc->ref_from) == (void *)&current) || (current->loc->ref_from == &tmp_loc)))
			{
				//For compatibility with vdmFree().
				*(current->loc->ref_from) = NULL;

				vdmFree_GCInternal(current->loc);
				remove_allocd_mem_node(current);
			}
		}

		current = tmp;
	}
}

//#ifdef WITH_GC
//===============  Garbage collected versions  ==============
struct TypedValue* newTypeValueGC(vdmtype type, TypedValueType value, TVP *ref_from)
{
	struct TypedValue* ptr = (struct TypedValue*) malloc(sizeof(struct TypedValue));
	ptr->type = type;
	ptr->value = value;
	add_allocd_mem_node(ptr, ref_from);

	return ptr;
}

/// Basic
struct TypedValue* newIntGC(int x, TVP *from)
{
	return newTypeValueGC(VDM_INT, (TypedValueType
	)
			{ .intVal = x }, from);
}

struct TypedValue* newBoolGC(bool x, TVP *from)
{
	return newTypeValueGC(VDM_BOOL, (TypedValueType
	)
			{ .boolVal = x }, from);
}
struct TypedValue* newRealGC(double x, TVP *from)
{
	return newTypeValueGC(VDM_REAL, (TypedValueType
	)
			{ .doubleVal = x }, from);
}
struct TypedValue* newCharGC(char x, TVP *from)
{
	return newTypeValueGC(VDM_CHAR, (TypedValueType
	)
			{ .charVal = x }, from);
}
struct TypedValue* newQuoteGC(unsigned int x, TVP *from)
{
	return newTypeValueGC(VDM_QUOTE, (TypedValueType
	)
			{ .quoteVal = x }, from);
}

struct TypedValue* newCollectionGC(size_t size, vdmtype type)
{
	struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));
	ptr->size = size;
	ptr->value = (struct TypedValue**) calloc(size, sizeof(struct TypedValue*)); //I know this is slower than malloc but better for products
	return newTypeValue(type, (TypedValueType
	)
			{ .ptr = ptr });
}

struct TypedValue* newCollectionWithValuesGC(size_t size, vdmtype type, TVP* elements)
{
	TVP product = newCollection(size,type);
	UNWRAP_COLLECTION(col,product);

	for (int i = 0; i < size; i++)
	{
		col->value[i]= vdmClone(elements[i]);
	}
	return product;
}

TVP vdmCloneGC(TVP x, TVP *from)
{
	TVP tmp;

	if(x == NULL)
	{
		return NULL;
	}

	tmp = newTypeValueGC(x->type, x->value, from);

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
#endif
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


//=============  Garbage collected versions  ================
//#endif /* WITH_GC */
