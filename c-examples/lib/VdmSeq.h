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

TVP vdmSeqHd(TVP seq);
TVP vdmSeqTl(TVP seq);
TVP vdmSeqLen(TVP seq);
TVP vdmSeqElems(TVP seq);
TVP vdmSeqInds(TVP seq);
TVP vdmSeqConc(TVP seq,TVP seq2);
TVP vdmSeqReverse(TVP seq);
//TVP seqMod(TVP seq,TVP seq);
TVP vdmSeqIndex(TVP seq,TVP index);
TVP vdmSeqEqual(TVP seq,TVP seq2);
TVP vdmSeqInEqual(TVP seq,TVP seq2);

#endif /* LIB_VDMSEQ_H_ */
