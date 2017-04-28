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
 * VdmRecord.h
 *
 *  Created on: Jan 19, 2016
 *      Author: kel
 */

#ifndef LIB_VDMRECORD_H_
#define LIB_VDMRECORD_H_


typedef bool (*vdmRecordEqualityFunction)(TVP a, TVP b);
typedef void (*freeVdmRecordFunction)(void*);

#define UNWRAP_RECORD(var,record) struct RecordType* var = (struct RecordType*)record->value.ptr
#define ASSERT_CHECK_RECORD(s) assert(s->type == VDM_RECORD && "Value is not a record")
#define RECORD_FIELD_ACCESS(record,recordType,field,var) TVP var = NULL;{ASSERT_CHECK_RECORD(record);UNWRAP_RECORD(ar,record);var=vdmClone(((recordType)ar)->field);}
#define RECORD_FIELD_SET(record,recordType,field,value) {ASSERT_CHECK_RECORD(record);UNWRAP_RECORD(ar,record);((recordType)ar)->field=vdmClone(value);}

#ifndef NO_RECORDS

struct RecordType
{
	void* value;
	int recordId;
	freeVdmRecordFunction freeRecord;/* TODO move to global map  */
	vdmRecordEqualityFunction equalFun; /* TODO move to global map  */
	TVP (*vdmCloneFun)(TVP self);
};

#endif /* NO_RECORDS */
#endif /* LIB_VDMRECORD_H_ */
