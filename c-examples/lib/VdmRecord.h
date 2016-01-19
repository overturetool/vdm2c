/*
 * VdmRecord.h
 *
 *  Created on: Jan 19, 2016
 *      Author: kel
 */

#ifndef LIB_VDMRECORD_H_
#define LIB_VDMRECORD_H_

#include "TypedValue.h"

typedef bool (*vdmRecordEqualityFunction)(TVP a, TVP b);

#define UNWRAP_RECORD(var,record) struct RecordType* var = (struct RecordType*)record->value.ptr
#define ASSERT_CHECK_RECORD(s) assert(s->type == VDM_RECORD && "Value is not a record")
#define RECORD_FIELD_ACCESS(record,recordType,field,var) TVP var = NULL;{ASSERT_CHECK_RECORD(record);UNWRAP_RECORD(ar,record);var=vdmClone(((recordType)ar)->field);}
#define RECORD_FIELD_SET(record,recordType,field,value) {ASSERT_CHECK_RECORD(record);UNWRAP_RECORD(ar,record);((recordType)ar)->field=vdmClone(value);}

struct RecordType
{
	void* value;
	int recordId;
	freeVdmClassFunction freeRecord;//TODO move to global map
	vdmRecordEqualityFunction equalFun; //TODO move to global map
	struct TypedValue* (*vdmCloneFun)(TVP self);
};


#endif /* LIB_VDMRECORD_H_ */
