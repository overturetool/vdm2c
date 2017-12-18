

/*  VERSION: For the version of VDM2C used to generate this project, refer to one of the generated files.  */


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
	assert(allocd_mem_head != NULL);

	allocd_mem_head->loc = NULL;
	allocd_mem_head->next = NULL;

	allocd_mem_tail = allocd_mem_head;
}

void add_allocd_mem_node(TVP l)
{
	allocd_mem_tail->loc = l;

	allocd_mem_tail->next = (struct alloc_list_node*)malloc(sizeof(struct alloc_list_node));
	assert(allocd_mem_tail->next != NULL);
	allocd_mem_tail = allocd_mem_tail->next;

	allocd_mem_tail->loc = NULL;
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
	struct alloc_list_node *tmp, *tmp2;

	tmp = allocd_mem_head;

	while(tmp != allocd_mem_tail)
	{
		tmp2 = tmp->next;

		if(tmp->loc != NULL)
		{
			vdmFree_GCInternal(tmp->loc);
			remove_allocd_mem_node(tmp);
		}
		tmp = tmp2;
	}
	free(allocd_mem_tail);
	allocd_mem_head = NULL;
	allocd_mem_tail = NULL;
}

void vdm_gc()
{
	struct alloc_list_node *current, *tmp;

	current = allocd_mem_head;

	/* Nothing to do if no memory currently allocated.  */
	if(current->loc == NULL && current->next == NULL)
		return;

	while(current != allocd_mem_tail)
	{
		tmp = current->next;

		vdmFree_GCInternal(current->loc);
		remove_allocd_mem_node(current);
		current = tmp;
	}
}

/* #ifdef WITH_GC  */
/* ===============  Garbage collected versions  ==============  */
TVP newTypeValueGC(vdmtype type, TypedValueType value)
{
	TVP res;

	res = newTypeValue(type, value);
	add_allocd_mem_node(res);

	return res;
}

/* / Basic  */
TVP newIntGC(int x)
{
	return newTypeValueGC(VDM_INT, (TypedValueType
	)
			{ .intVal = x });
}

TVP newBoolGC(bool x)
{
	return newTypeValueGC(VDM_BOOL, (TypedValueType
	)
			{ .boolVal = x });
}

TVP newRealGC(double x)
{
	return newTypeValueGC(VDM_REAL, (TypedValueType
	)
			{ .doubleVal = x });
}

TVP newCharGC(char x)
{
	return newTypeValueGC(VDM_CHAR, (TypedValueType
	)
			{ .charVal = x });
}

TVP newQuoteGC(unsigned int x)
{
	return newTypeValueGC(VDM_QUOTE, (TypedValueType
	)
			{ .quoteVal = x });
}

TVP newTokenGC(TVP x)
{
	char *str = unpackString(x);
	char *strTmp = str;
	int hashVal = 5381;
	int c;

	while ((c = *str++))
		hashVal = ((hashVal << 2) + hashVal) + c;

	free(strTmp);
	vdmFree(x);

	return newTypeValueGC(VDM_TOKEN, (TypedValueType
	)
			{ .intVal = hashVal });
}

TVP newUnknownGC()
{
	return newTypeValueGC(VDM_UNKNOWN, (TypedValueType)
			{});
}

TVP vdmCloneGC(TVP x)
{
	TVP res;

	res = vdmClone(x);
	add_allocd_mem_node(res);

	return res;
}
