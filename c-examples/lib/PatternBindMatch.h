/*
 * PatternBindMatch.h
 *
 *  Created on: Dec 3, 2015
 *      Author: kel
 */

#ifndef LIB_PATTERNBINDMATCH_H_
#define LIB_PATTERNBINDMATCH_H_

#include "TypedValue.h"
#include "lib/VdmProduct.h"
#include <assert.h>

bool patternMatchBind(TVP patternBind, TVP value);

//for match value
bool patternMatch(TVP patternBind, TVP value);

#endif /* LIB_PATTERNBINDMATCH_H_ */
