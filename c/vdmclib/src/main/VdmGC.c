#include "Vdm.h"

extern void vdmFree_GCInternal(struct TypedValue* ptr);


struct alloc_list_node *allocd_mem_head, *allocd_mem_tail;

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
		//This is the case when the value is created in-place (or when vdmFreed???)
		if(current->loc->ref_from == NULL)
		{
			remove_allocd_mem_node(current);
			vdmFree_GCInternal(tmp_loc);
		}
		else if(*(current->loc->ref_from) != current->loc)
		{
			//For compatibility with vdmFree().
			*(current->loc->ref_from) = NULL;

			vdmFree_GCInternal(current->loc);
			remove_allocd_mem_node(current);
		}

		current = tmp;
	}
}
