/*
 * #%~
 * The VDM to C Code Generator
 * %%
 * Copyright (C) 2015 - 2016 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

/*
 * VdmSeq.h
 *
 *  Created on: Dec 1, 2015
 *      Author: kel
 */

#ifndef LIB_VDMSEQ_H_
#define LIB_VDMSEQ_H_
#include "TypedValue.h"

#include "VdmSet.h"

#ifndef NO_SEQS

#define SET_SEQ(seq,index,val) {struct Collection* col =(struct Collection*) seq->value.ptr;col->value[index-1] =vdmClone(val);}

/*
 * New empty sequence
 */
struct TypedValue* newSeq(size_t size);
/*
 * New sequence from array of elements
 */
struct TypedValue* newSeqWithValues(size_t size,TVP* elements);
/*
 * Create new seq from variadic list of elements
 */
struct TypedValue* newSeqVar(size_t size,...);
struct TypedValue* newSeqVarToGrow(size_t size, size_t expected_size, ...);

void vdmSeqGrow(TVP seq, TVP element);
void vdmSeqFit(TVP seq);
TVP vdmSeqHd(TVP seq);
TVP vdmSeqTl(TVP seq);
TVP vdmSeqLen(TVP seq);

#ifndef NO_SETS
TVP vdmSeqElems(TVP seq);
TVP vdmSeqInds(TVP seq);
#endif

TVP vdmSeqConc(TVP seq,TVP seq2);
TVP vdmSeqReverse(TVP seq);
//TVP seqMod(TVP seq,TVP seq);
TVP vdmSeqIndex(TVP seq,TVP index);
TVP vdmSeqEqual(TVP seq,TVP seq2);
TVP vdmSeqInEqual(TVP seq,TVP seq2);

void vdmSeqUpdate(TVP seq, TVP index, TVP newValue);

#endif /* NO_SEQS */
#endif /* LIB_VDMSEQ_H_ */
