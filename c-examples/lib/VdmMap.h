/*
 * VdmMap.h
 *
 *  Created on: Dec 6, 2015
 *      Author: kel
 */

#ifndef LIB_VDMMAP_H_
#define LIB_VDMMAP_H_

#include "TypedValue.h"
#include <glib.h>

#include <assert.h>


struct Map
{
	GHashTable *table;
};

struct TypedValue* newMap();

//util method for adding maplets
void vdmMapAdd(TVP map,TVP key, TVP value);

//VDM map operators
TVP vdmMapApply(TVP map, TVP key);

guint vdm_typedvalue_hash(gconstpointer v);
gboolean vdm_typedvalue_equal(gconstpointer v1, gconstpointer v2);
void vdm_g_free(gpointer mem);

#endif /* LIB_VDMMAP_H_ */
