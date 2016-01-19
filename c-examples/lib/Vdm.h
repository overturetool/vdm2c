/*
 * Vdm.h
 *
 *  Created on: Jan 19, 2016
 *      Author: kel
 */

#ifndef LIB_VDM_H_
#define LIB_VDM_H_

/*
 * Support for google test in e.g. pattern matching
 * The VDM semantics requires the program to SEQ-fault,
 *  but this is not appreciated in the testing framework
 */
#ifndef FATAL_ERROR
#define FATAL_ERROR(message) exit(EXIT_FAILURE)
#endif

#include "TypedValue.h"



#include "VdmBasicTypes.h"
#include "VdmClass.h"
#include "VdmSet.h"
#include "VdmSeq.h"
#include "VdmMap.h"
#include "VdmProduct.h"
#include "VdmRecord.h"
#include "PatternBindMatch.h"

#endif /* LIB_VDM_H_ */
