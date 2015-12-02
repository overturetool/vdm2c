/*
 * VdmSeq.h
 *
 *  Created on: Dec 1, 2015
 *      Author: kel
 */

#ifndef LIB_VDMSEQ_H_
#define LIB_VDMSEQ_H_
#include "TypedValue.h"
#include <assert.h>

TVP seqHd(TVP seq);
TVP seqTl(TVP seq);
TVP seqLen(TVP seq);
TVP seqElems(TVP seq);
TVP seqInds(TVP seq);
TVP seqConc(TVP seq,TVP seq2);
TVP seqReverse(TVP seq);
//TVP seqMod(TVP seq,TVP seq);
TVP seqIndex(TVP seq,TVP index);
TVP seqEqual(TVP seq,TVP seq2);
TVP seqInEqual(TVP seq,TVP seq2);

#endif /* LIB_VDMSEQ_H_ */
