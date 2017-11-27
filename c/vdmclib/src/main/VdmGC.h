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
void add_allocd_mem_node(TVP l);
void remove_allocd_mem_node(struct alloc_list_node *node);
void remove_allocd_mem_node_by_location(TVP loc);

extern struct alloc_list_node *allocd_mem_head;
extern struct alloc_list_node *allocd_mem_current;


/* ====  Gargabe collected versions ======  */

TVP newTypeValueGC(vdmtype type, TypedValueType value);
TVP vdmCloneGC(TVP x);
TVP newIntGC(int x);
TVP newBoolGC(bool x);
TVP newRealGC(double x);
TVP newCharGC(char x);
TVP newQuoteGC(unsigned int x);
TVP newTokenGC(TVP x);
TVP newUnknownGC();
TVP vdmEqualsGC(TVP a, TVP b);


#endif /*VDMGC_H_*/
