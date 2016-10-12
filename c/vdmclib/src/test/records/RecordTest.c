// The template for class
#include "RecordTest.h"
#include <stdio.h>
#include <string.h>


/* -------------------------------
 *
 * Memory management methods
 *
 --------------------------------- */

void RecordTest_free_fields(struct RecordTest *this)
{
}

static void RecordTest_free(struct RecordTest *this)
{
	--this->_RecordTest_refs;
	if (this->_RecordTest_refs < 1)
	{
		RecordTest_free_fields(this);
		free(this);
	}
}


/* -------------------------------
 *
 * Member methods 
 *
 --------------------------------- */

/* ExpressionRecord.vdmrt 21:12 */
static  TVP _Z5test1EV(RecordTestCLASS this)	{
	/* ExpressionRecord.vdmrt 24:13 */
	TVP r1 = _Z5MyRecEIC(NULL, newInt(1), newChar('a'));
	/* ExpressionRecord.vdmrt 24:13 */
	TVP r2 = vdmClone(r1);
	/* ExpressionRecord.vdmrt 26:19 */
	TVP ret_1 = vdmClone(vdmEquals(r2, r1));
	/* ExpressionRecord.vdmrt 26:9 */
	vdmFree(r1);
	vdmFree(r2);
	return ret_1;
}

/* ExpressionRecord.vdmrt 30:12 */
static  TVP _Z5test2EV(RecordTestCLASS this)	{
	/* ExpressionRecord.vdmrt 33:13 */
	TVP r1 = _Z5MyRecEIC(NULL, newInt(1), newChar('a'));
	/* ExpressionRecord.vdmrt 33:13 */
	TVP r2 = vdmClone(r1);
	/* ExpressionRecord.vdmrt 35:9 */
	r1 = _Z5MyRecEIC(NULL, newInt(2), newChar('b'));
	/* ExpressionRecord.vdmrt 37:19 */
	TVP ret_2 = vdmClone(vdmNot(vdmEquals(r2, r1)));
	/* ExpressionRecord.vdmrt 37:9 */
	return ret_2;
}


/* ExpressionRecord.vdmrt 41:12 */
static  TVP _Z5test3EV(RecordTestCLASS this)	{
	/* ExpressionRecord.vdmrt 44:13 */
	TVP r1 = _Z5MyRecEIC(NULL, newInt(1), newChar('a'));
	/* ExpressionRecord.vdmrt 44:13 */
	TVP r2 = vdmClone(r1);
	/* ExpressionRecord.vdmrt 45:13 */
	TVP r3 = vdmClone(r1);
	/* ExpressionRecord.vdmrt 47:9 */
	r1 = _Z5MyRecEIC(NULL, newInt(2), newChar('b'));
	/* ExpressionRecord.vdmrt 49:19 */
	TVP ret_3 = vdmClone(vdmEquals(r3, r2));
	/* ExpressionRecord.vdmrt 49:9 */
	return ret_3;
}


/* ExpressionRecord.vdmrt 53:12 */
static  TVP _Z5test4EV(RecordTestCLASS this)	{
	/* ExpressionRecord.vdmrt 56:13 */
	TVP r1 = _Z6MyRec2EI1SI(NULL, newInt(1), newSetVar(2, newInt(2), newInt(3)));
	/* ExpressionRecord.vdmrt 57:13 */
	TVP r2 = vdmClone(r1);
	/* ExpressionRecord.vdmrt 58:13 */
	TVP r3 = vdmClone(r1);
	/* ExpressionRecord.vdmrt 60:9 */
	r1 = _Z6MyRec2EI1SI(NULL, newInt(2), newSetVar(2, newInt(4), newInt(5)));
	/* ExpressionRecord.vdmrt 62:19 */
	TVP ret_4 = vdmClone(vdmEquals(r3, r2));
	/* ExpressionRecord.vdmrt 62:9 */
	return ret_4;
}


/* ExpressionRecord.vdmrt 65:12 */
static  TVP _Z5test5EV(RecordTestCLASS this)	{
	/* ExpressionRecord.vdmrt 68:13 */
	TVP r1 = _Z6MyRec2EI1SI(NULL, newInt(1), newSetVar(2, newInt(2), newInt(3)));
	/* ExpressionRecord.vdmrt 69:13 */
	TVP r2 = _Z6MyRec2EI1SI(NULL, newInt(2), newSetVar(2, newInt(4), newInt(5)));
	/* ExpressionRecord.vdmrt 71:19 */
	TVP ret_5 = vdmClone(vdmNot(vdmEquals(r1, r2)));
	/* ExpressionRecord.vdmrt 71:9 */
	return ret_5;
}

void RecordTest_const_init()	{

	return ;
}



void RecordTest_const_shutdown()	{

	return ;
}



void RecordTest_static_init()	{

	return ;
}



void RecordTest_static_shutdown()	{

	return ;
}




/* -------------------------------
 *
 * VTable
 *
 --------------------------------- */

// VTable for this class
static  struct VTable VTableArrayForRecordTest  [] ={

		{0,0,((VirtualFunctionPointer) _Z5test1EV),},
		{0,0,((VirtualFunctionPointer) _Z5test2EV),},
		{0,0,((VirtualFunctionPointer) _Z5test3EV),},
		{0,0,((VirtualFunctionPointer) _Z5test4EV),},
		{0,0,((VirtualFunctionPointer) _Z5test5EV),},
}  ;

// Overload VTables


/* -------------------------------
 *
 * Internal memory constructor
 *
 --------------------------------- */


RecordTestCLASS RecordTest_Constructor(RecordTestCLASS this_ptr)
{

	if(this_ptr==NULL)
	{
		this_ptr = (RecordTestCLASS) malloc(sizeof(struct RecordTest));
	}

	if(this_ptr!=NULL)
	{


		// RecordTest init
		this_ptr->_RecordTest_id = CLASS_ID_RecordTest_ID;
		this_ptr->_RecordTest_refs = 0;
		this_ptr->_RecordTest_pVTable=VTableArrayForRecordTest;

	}

	return this_ptr;
}

// Method for creating new "class"
static TVP new()
						{
	RecordTestCLASS ptr=RecordTest_Constructor(NULL);

	return newTypeValue(VDM_CLASS, (TypedValueType)
			{	.ptr=newClassValue(ptr->_RecordTest_id, &ptr->_RecordTest_refs, (freeVdmClassFunction)&RecordTest_free, ptr)});
						}



/* -------------------------------
 *
 * Public class constructors
 *
 --------------------------------- */ 


/* ExpressionRecord.vdmrt 16:7 */
TVP _Z10RecordTestEV(RecordTestCLASS this)	{

	TVP __buf = NULL;

	if ( this == NULL )

	{

		__buf = new();

		this = TO_CLASS_PTR(__buf, RecordTest);
	}
	;

	return __buf;
}




/* -------------------------------
 *
 * Global class fields
 *
 --------------------------------- */

// initialize globals - this is done last since they are declared in the header but uses init functions which are printet in any order

