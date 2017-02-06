#include "Vdm.h"

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

void remove_allocd_mem_node(struct alloc_list_node *node)
{
	struct alloc_list_node *tmp, *prev;

	tmp = allocd_mem_head;

	while(tmp != node)
	{
		prev = tmp;
		tmp = tmp->next;
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
			vdmFree(tmp->loc);
			remove_allocd_mem_node(tmp);
		}

		tmp = tmp->next;
	}
	free(allocd_mem_head);
}

void vdm_gc()
{
	struct alloc_list_node *current, *tmp;

	current = allocd_mem_head;

	//Nothing to do if no memory currently allocated.
	if(current->loc == NULL && current->next == NULL)
		return;

	while(current != allocd_mem_tail)
	{
		tmp = current->next;

		//No information was passed about where the reference was assigned.
		//This is the case when the value is created in-place (or when vdmFreed???)
		if(current->loc->ref_from == NULL)
		{
			vdmFree(current->loc);
			remove_allocd_mem_node(current);
		}
		else if(*(current->loc->ref_from) != current->loc)
		{
			vdmFree(current->loc);
			remove_allocd_mem_node(current);
		}

		current = tmp;
	}
}
