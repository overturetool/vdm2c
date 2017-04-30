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
 * <http:XXXwww.gnu.org/licenses/gpl-3.0.html>.
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
/*
 * New sequence from array of elements
 */
TVP newSeqWithValues(size_t size,TVP* elements);
/*
 * Create new seq from variadic list of elements
 */
TVP newSeqVar(size_t size,...);
TVP newSeqVarGC(size_t size, TVP *from, ...);
TVP newSeqVarToGrow(size_t size, size_t expected_size, ...);

void vdmSeqGrow(TVP seq, TVP element);
void vdmSeqFit(TVP seq);
TVP vdmSeqHd(TVP seq);
TVP vdmSeqHdGC(TVP seq, TVP *from);
TVP vdmSeqTl(TVP seq);
TVP vdmSeqTlGC(TVP seq, TVP *from);
TVP vdmSeqLen(TVP seq);
TVP vdmSeqLenGC(TVP seq, TVP *from);
TVP vdmSeqConcGC(TVP seq, TVP seq2, TVP *from);
TVP vdmSeqReverseGC(TVP seq, TVP *from);
TVP vdmSeqIndexGC(TVP seq, TVP indexVal, TVP *from);

#ifndef NO_SETS
TVP vdmSeqElems(TVP seq);
TVP vdmSeqElemsGC(TVP seq, TVP *from);
TVP vdmSeqInds(TVP seq);
TVP vdmSeqIndsGC(TVP seq, TVP *from);
#endif

TVP vdmSeqConc(TVP seq,TVP seq2);
TVP vdmSeqReverse(TVP seq);
/* TVP seqMod(TVP seq,TVP seq);  */
TVP vdmSeqIndex(TVP seq,TVP index);

void vdmSeqUpdate(TVP seq, TVP index, TVP newValue);

#endif /* NO_SEQS */
#endif /* LIB_VDMSEQ_H_ */
