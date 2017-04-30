#include "Vdm.h"
#include "VdmGC.h"
#include "VdmClass.h"


extern void vdmFree_GCInternal(TVP ptr);

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
		/* GC list empty.  */
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
		/* This memory is not under GC control.  */
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
		/* GC list empty.  */
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

	/* Nothing to do if no memory currently allocated.  */
	if(current->loc == NULL && current->next == NULL)
		return;

	while(current != allocd_mem_tail)
	{
		tmp = current->next;
		tmp_loc = current->loc;

		/* No information was passed about where the reference was assigned.  */
		/* This is the case when the value is created in-place or when freed using vdmFree().  */
		if(current->loc->ref_from == NULL)
		{
			remove_allocd_mem_node(current);
			vdmFree_GCInternal(tmp_loc);
		}
		else if(*(current->loc->ref_from) != current->loc)
		{
			/* For compatibility with vdmFree().  */
			/* Check that there is no interference between this call's stack  */
			/* variables and the reference to the memory we are freeing  */
			/* Before NULLing the referencing location for vdmFree.  */
			if(!((((void *)&tmp) <= ((void *)current->loc->ref_from) && ((void *)current->loc->ref_from) <= ((void *)(&tmp + 1))) ||
					(((void *)&current) <= ((void *)current->loc->ref_from) && ((void *)current->loc->ref_from) <= ((void *)(&current + 1))) ||
					(((void *)&tmp_loc) <= ((void *)current->loc->ref_from) && ((void *)current->loc->ref_from) <= ((void *)(&tmp_loc + 1)))))
				*(current->loc->ref_from) = NULL;


			vdmFree_GCInternal(current->loc);
			remove_allocd_mem_node(current);
		}
		current = tmp;
	}
}

/* #ifdef WITH_GC  */
/* ===============  Garbage collected versions  ==============  */
TVP newTypeValueGC(vdmtype type, TypedValueType value, TVP *ref_from)
{
	TVP ptr = (TVP) malloc(sizeof(struct TypedValue));
	ptr->type = type;
	ptr->value = value;
	add_allocd_mem_node(ptr, ref_from);

	return ptr;
}

/* / Basic  */
TVP newIntGC(int x, TVP *from)
{
	return newTypeValueGC(VDM_INT, (TypedValueType
	)
			{ .intVal = x }, from);
}

TVP newBoolGC(bool x, TVP *from)
{
	return newTypeValueGC(VDM_BOOL, (TypedValueType
	)
			{ .boolVal = x }, from);
}

TVP newRealGC(double x, TVP *from)
{
	return newTypeValueGC(VDM_REAL, (TypedValueType
	)
			{ .doubleVal = x }, from);
}

TVP newCharGC(char x, TVP *from)
{
	return newTypeValueGC(VDM_CHAR, (TypedValueType
	)
			{ .charVal = x }, from);
}

TVP newQuoteGC(unsigned int x, TVP *from)
{
	return newTypeValueGC(VDM_QUOTE, (TypedValueType
	)
			{ .quoteVal = x }, from);
}

TVP newTokenGC(TVP x, TVP *from)
{
	char *str = unpackString(x);
	char *strTmp = str;
	int hashVal = 5381;
	int c;

	while ((c = *str++))
		hashVal = ((hashVal << 2) + hashVal) + c;

	free(strTmp);

	return newTypeValueGC(VDM_TOKEN, (TypedValueType
	)
			{ .intVal = hashVal }, from);
}

TVP vdmCloneGC(TVP x, TVP *from)
{
	TVP tmp;

	if(x == NULL)
	{
		return NULL;
	}

	tmp = newTypeValueGC(x->type, x->value, from);

	/* FIXME vdmClone any pointers  */
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
	case VDM_TOKEN:
	{
		/* encoded as values so the initial copy line handles these  */
		break;
	}
#ifndef NO_MAPS
	case VDM_MAP:
	{
		UNWRAP_MAP(m, x);
		struct Map *map = cloneMap(m);
		tmp->value.ptr = map;
		break;
	}
#endif
#ifndef NO_PRODUCTS
	case VDM_PRODUCT:
	{
		int i;
		UNWRAP_COLLECTION(cptr, tmp);

		struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));

		/* copy (size)  */
		*ptr = *cptr;
		ptr->value = (TVP*) malloc(sizeof(TVP) * ptr->size);

		for (i = 0; i < cptr->size; i++)
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
		int i;

		UNWRAP_COLLECTION(cptr, tmp);

		struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));

		/* copy (size)  */
		*ptr = *cptr;
		ptr->value = (TVP*) malloc(sizeof(TVP) * ptr->size);

		for (i = 0; i < cptr->size; i++)
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
		int i;

		UNWRAP_COLLECTION(cptr, tmp);

		struct Collection* ptr = (struct Collection*) malloc(sizeof(struct Collection));

		/* copy (size)  */
		*ptr = *cptr;
		ptr->value = (TVP*) malloc(sizeof(TVP) * ptr->size);

		for (i = 0; i < cptr->size; i++)
		{
			ptr->value[i] = vdmClone(cptr->value[i]);
		}

		tmp->value.ptr = ptr;
		break;
	}
#endif
	/* 	case VDM_OPTIONAL:  */
	/* 		TODO  */
	/* 		break;  */
#ifndef NO_RECORDS
	case VDM_RECORD:
	{
		ASSERT_CHECK_RECORD(x);

		int i;
		TVP tmpField = NULL;
		int numFields;

		/* Create a shell for a new class and populate it with the information  */
		/* that can be used from the one being cloned, but all of it should be  */
		/* irrelevant for records.  */
		(tmp->value).ptr = newClassValue(((struct ClassType*)(x->value.ptr))->classId,
				((struct ClassType*)(x->value.ptr))->refs,
				NULL,
				NULL);

		/* Generic way of accessing the number-of-fields field.  The name of the record type is  */
		/* hard-coded into the corresponding struct name.  */
		numFields = (*((TVP*)((char*)(((struct ClassType*)x->value.ptr)->value) + \
				sizeof(struct VTable*) + \
				sizeof(int) + \
				sizeof(unsigned int))))->value.intVal;

		/* Allocate memory to be populated with the pointers pointing to the cloned fields.  */
		((struct ClassType*)((tmp->value).ptr))->value = malloc(sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(TVP) + sizeof(TVP) * numFields);

		for(i = 0; i <= numFields; i++)
		{
			/* Start cloning the fields one by one, including the number-of-fields field,  */
			/* since it is just a TVP.  */
			tmpField = vdmClone(*((TVP*)((char*)(((struct ClassType*)x->value.ptr)->value) + sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(TVP) * i)));

			/* Only copy the address stored in tmpField so that that memory is now addressed by the current field in the struct.  */
			memcpy(((TVP*)((char*)(((struct ClassType*)tmp->value.ptr)->value) + sizeof(struct VTable*) + sizeof(int) + sizeof(unsigned int) + sizeof(TVP) * i)), &tmpField, sizeof(TVP));
		}

		break;
	}
#endif
	case VDM_CLASS:
	{
		/* handle smart pointer  */
		struct ClassType* classTptr = (struct ClassType*) tmp->value.ptr;

		/* improve using memcpy  */
		tmp->value.ptr = newClassValue(classTptr->classId, classTptr->refs, classTptr->freeClass, classTptr->value);
		break;
	}
	}

	return tmp;
}
