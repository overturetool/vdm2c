/*
 * PrettyPrint.c
 *
 *  Created on: Mar 9, 2016
 *      Author: Victor Bandur
 */

#include "PrettyPrint.h"
#include <assert.h>
#include <stdio.h>
#include <string.h>

#if !defined(NO_IO) || !defined(NO_CSV) || defined(CUSTOM_IO)

#define ASSERT_CHECK_BOOL(s) assert(s->type == VDM_BOOL && "Value is not a boolean")
#define ASSERT_CHECK_NUMERIC(s) assert((s->type == VDM_INT||s->type == VDM_NAT||s->type == VDM_NAT1||s->type == VDM_REAL||s->type == VDM_RAT) && "Value is not numeric")
#define ASSERT_CHECK_REAL(s) assert((s->type ==  VDM_REAL) && "Value is not real")
#define ASSERT_CHECK_INT(s) assert((s->type ==  VDM_INT) && "Value is not integer")
#define ASSERT_CHECK_CHAR(s) assert((s->type ==  VDM_CHAR) && "Value is not a character")

char* printBool(TVP val)
{
	ASSERT_CHECK_BOOL(val);

	char* str;

	if(val->value.boolVal)
	{
		str = (char*)malloc(5 * sizeof(char));
		assert(str != NULL);
		sprintf(str, "true");
	}
	else
	{
		str = (char*)malloc(6 * sizeof(char));
		assert(str != NULL);
		sprintf(str, "false");
	}

	return str;
}



char* printChar(TVP val)
{
	ASSERT_CHECK_CHAR(val);

	char* str;

	str = (char*)malloc(4 * sizeof(char));
	assert(str != NULL);

	str[0] = '\'';
	str[1] = val->value.charVal;
	str[2] = '\'';
	str[3] = 0;

	return str;
}



char* printInt(TVP val)
{
	ASSERT_CHECK_NUMERIC(val);

	char* str;
	int tmpval = val->value.intVal;
	int numdigits;

	numdigits = 0;
	while(tmpval != 0)
	{
		numdigits += 1;
		tmpval = tmpval / 10;
	}

	/* Allow one extra space for negative numbers.  */
	str = (char*)malloc((numdigits + 2) * sizeof(char));
	assert(str != NULL);
	sprintf(str, "%d", val->value.intVal);

	return str;
}



char* printDouble(TVP val)
{
	ASSERT_CHECK_NUMERIC(val);

	char* str;

	/* Rounding behaviour for doubles in the less significant digits  */
	/* makes it impossible to count digits using subtraction and multiplication by 10.  */
	str = (char*)malloc(1000 * sizeof(char));
	assert(str != NULL);
	sprintf(str, "%f", val->value.doubleVal);

	return str;
}


char* toString(TVP val)
{
	char ldelim, rdelim;
	char* str;
	char* strtmp;
	char** strcol;
	struct Collection* col;
	int i;
	int totallen;

	/* For compiler warnings.  */
	ldelim = 'a';
	rdelim = 'a';

	/* Set up delimiters in case of collections.  */
	switch(val->type)
	{
#ifndef NO_SETS
	case VDM_SET:
		ldelim = '{';
		rdelim = '}';
		break;
#endif
#ifndef NO_SEQS
	case VDM_SEQ:
		ldelim = '[';
		rdelim = ']';
		break;
#endif
#ifndef NO_PRODUCTS
	case VDM_PRODUCT:
		ldelim = '(';
		rdelim = ')';
		break;
#endif
	default:
		break;
	}

	/* Main operation.  */
	switch(val->type)
	{
	case VDM_BOOL:
		str = printBool(val);
		break;
	case VDM_CHAR:
		str = printChar(val);
		break;
	case VDM_INT:
	case VDM_NAT:
	case VDM_NAT1:
		str = printInt(val);
		break;
	case VDM_RAT:
	case VDM_REAL:
		str = printDouble(val);
		break;
#ifndef NO_SETS
	case VDM_SET:
		/* Can not use UNWRAP_COLLECTION here because it includes a declaration.  */
		col = (struct Collection*)val->value.ptr;
		strcol = (char**)malloc(col->size * sizeof(char*));
		assert(strcol != NULL);
		totallen = 0;

		/* Get pretty printed representations of contents recursively.  */
		for(i = 0; i < col->size; i++)
		{
			strcol[i] = toString((col->value)[i]);
			totallen += strlen(strcol[i]);
		}

		/* Compose full string.  */
		str = (char*)malloc((2 + 1 + col->size * 2 + totallen));
		assert(str != NULL);
		strtmp = str;
		sprintf(str, "%c", ldelim);
		str += sizeof(char);
		for(i = 0; i < col->size - 1; i++)
		{
			sprintf(str, "%s, ", strcol[i]);
			str += (strlen(strcol[i]) + 2) * sizeof(char);
		}
		sprintf(str, "%s%c", strcol[i], rdelim);
		str = strtmp;

		/* Clean up.  */
		for(i = 0; i < col->size; i++)
		{
			free(strcol[i]);
		}
		free(strcol);

		break;
#endif
#ifndef NO_SEQS
	case VDM_SEQ:
		/* Can not use UNWRAP_COLLECTION here because it includes a declaration.  */
		col = (struct Collection*)val->value.ptr;
		strcol = (char**)malloc(col->size * sizeof(char*));
		assert(strcol != NULL);
		totallen = 0;

		/* Get pretty printed representations of contents recursively.  */
		for(i = 0; i < col->size; i++)
		{
			strcol[i] = toString((col->value)[i]);
			totallen += strlen(strcol[i]);
		}

		/* Compose full string.  */
		str = (char*)malloc((2 + 1 + col->size * 2 + totallen));
		assert(str != NULL);
		strtmp = str;
		sprintf(str, "%c", ldelim);
		str += sizeof(char);
		for(i = 0; i < col->size - 1; i++)
		{
			sprintf(str, "%s, ", strcol[i]);
			str += (strlen(strcol[i]) + 2) * sizeof(char);
		}
		sprintf(str, "%s%c", strcol[i], rdelim);
		str = strtmp;

		/* Clean up.  */
		for(i = 0; i < col->size; i++)
		{
			free(strcol[i]);
		}
		free(strcol);

		break;
#endif
#ifndef NO_PRODUCTS
	case VDM_PRODUCT:
		/* Can not use UNWRAP_COLLECTION here because it includes a declaration.  */
		col = (struct Collection*)val->value.ptr;
		strcol = (char**)malloc(col->size * sizeof(char*));
		assert(strcol != NULL);
		totallen = 0;

		/* Get pretty printed representations of contents recursively.  */
		for(i = 0; i < col->size; i++)
		{
			strcol[i] = toString((col->value)[i]);
			totallen += strlen(strcol[i]);
		}

		/* Compose full string.  */
		str = (char*)malloc(3 * sizeof(char) + 2 + 1 + col->size * 2 + totallen);
		assert(str != NULL);
		strtmp = str;
		sprintf(str, "mk_%c", ldelim);
		str += 3 * sizeof(char) + sizeof(char);
		for(i = 0; i < col->size - 1; i++)
		{
			sprintf(str, "%s, ", strcol[i]);
			str += (strlen(strcol[i]) + 2) * sizeof(char);
		}
		sprintf(str, "%s%c", strcol[i], rdelim);
		str = strtmp;

		/* Clean up.  */
		for(i = 0; i < col->size; i++)
		{
			free(strcol[i]);
		}
		free(strcol);

		break;
#endif
	default:
		/* Must return a valid pointer.  */
		str = (char*)malloc(1);
		assert(str != NULL);
		str[0] = 0;
		break;
	}

	return str;
}

#endif /* NO_IO */


