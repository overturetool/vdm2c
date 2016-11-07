#ifndef VDMGC_H_
#define VDMGC_H_

struct alloc_list_node
{
	TVP loc;
	struct alloc_list_node *prev;
	struct alloc_list_node *next;
};


void vdm_gc_init();
void vdm_gc();
void vdm_gc_shutdown();

extern struct alloc_list_node *allocd_mem_head;
extern struct alloc_list_node *allocd_mem_current;

#endif /*VDMGC_H_*/
