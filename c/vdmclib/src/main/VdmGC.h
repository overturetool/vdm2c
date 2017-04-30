#ifndef VDMGC_H_
#define VDMGC_H_

#include "Vdm.h"
#include "VdmUnpackString.h"

struct alloc_list_node
{
	TVP loc;
	struct alloc_list_node *next;
};


void vdm_gc_init();
void vdm_gc();
void vdm_gc_shutdown();
void add_allocd_mem_node(TVP l, TVP *from);
void remove_allocd_mem_node(struct alloc_list_node *node);
void remove_allocd_mem_node_by_location(TVP loc);

extern struct alloc_list_node *allocd_mem_head;
extern struct alloc_list_node *allocd_mem_current;


/* ====  Gargabe collected versions ======  */

TVP newTypeValueGC(vdmtype type, TypedValueType value, TVP *ref_from);
TVP vdmCloneGC(TVP x, TVP *from);
TVP newIntGC(int x, TVP *from);
TVP newBoolGC(bool x, TVP *from);
TVP newRealGC(double x, TVP *from);
TVP newCharGC(char x, TVP *from);
TVP newQuoteGC(unsigned int x, TVP *from);
TVP newTokenGC(TVP x, TVP *from);
TVP vdmEqualsGC(TVP a, TVP b, TVP *from);


#endif /*VDMGC_H_*/
