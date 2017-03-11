#ifndef VDMGC_H_
#define VDMGC_H_

#include "Vdm.h"

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


//#ifdef WITH_GC
//====  Gargabe collected versions ======

struct TypedValue* newTypeValueGC(vdmtype type, TypedValueType value, TVP *ref_from);
struct TypedValue* vdmCloneGC(TVP x, TVP *from);
struct TypedValue* newIntGC(int x, TVP *from);
struct TypedValue* newBoolGC(bool x, TVP *from);
struct TypedValue* newRealGC(double x, TVP *from);
struct TypedValue* newCharGC(char x, TVP *from);
struct TypedValue* newQuoteGC(unsigned int x, TVP *from);
struct TypedValue* vdmEqualsGC(struct TypedValue* a, struct TypedValue* b, TVP *from);
//#endif /* WITH_GC */


#endif /*VDMGC_H_*/
