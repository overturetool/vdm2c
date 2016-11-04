/*
 * IOLib.c
 *
 *  Created on: Feb 25, 2016
 *      Author: Victor Bandur
 */
#ifndef CUSTOM_IO

#include "TypedValue.h"
#include "Vdm.h"
#include "IOLib.h"



char* unpackString(TVP charseq)
{
	int i;
	char* str;



	UNWRAP_COLLECTION(col, charseq);

	str = (char*)malloc(col->size * sizeof(char) + 1);
	str[col->size] = 0;

	for(i = 0; i < col->size; i++)
	{
		ASSERT_CHECK_CHAR((col->value[i]));
		str[i] = ((col->value[i])->value).charVal;
	}

	return str;
}



TVP vdm_IO_freadval(TVP filename)
{
	//TODO.

	return NULL;
}



TVP vdm_IO_fwriteval(TVP filename, TVP val, TVP fdir)
{
	//TODO.

	return NULL;
}



TVP vdm_IO_writeval(TVP val)
{
	//TODO.

	return NULL;
}



void vdm_IO_printf(TVP format, TVP args)
{
	//TODO.

	return;
}



void vdm_IO_println(TVP arg)
{
	char* str;
	int i;
	int isstring;

	//TODO:  A quick hack to print a sequence of chars as a legible string.
	if(arg->type == VDM_SEQ)
	{
		isstring = 1;
		UNWRAP_COLLECTION(col, arg);
		for(i = 0; i < col->size; i++)
		{
			if(col->value[i]->type != VDM_CHAR)
			{
				isstring = 0;
				break;
			}
		}

		if(isstring)
		{
			str = unpackString(arg);
		}
		else
		{
			str = printVdmBasicValue(arg);
		}
	}
	else
	{
		str = printVdmBasicValue(arg);
	}

	printf("%s\n", str);
	free(str);

	return;
}



void vdm_IO_print(TVP arg)
{
	char* str;

	int i;
	int isstring;

	//TODO:  A quick hack to print a sequence of chars as a legible string.
	if(arg->type == VDM_SEQ)
	{
		isstring = 1;
		UNWRAP_COLLECTION(col, arg);
		for(i = 0; i < col->size; i++)
		{
			if(col->value[i]->type != VDM_CHAR)
			{
				isstring = 0;
				break;
			}
		}

		if(isstring)
		{
			str = unpackString(arg);
		}
		else
		{
			str = printVdmBasicValue(arg);
		}
	}
	else
	{
		str = printVdmBasicValue(arg);
	}

	printf("%s\n", str);
	free(str);

	return;
}



TVP vdm_IO_ferror()
{
	//TODO.

	return NULL;
}



TVP vdm_IO_fecho(TVP filename, TVP text, TVP fdir)
{
	//TODO.

	return NULL;
}
TVP vdm_IO_echo(TVP text)
{
	//TODO:  Ensure a string is passed.
	char *str = unpackString(text);

	printf("%s", str);

	free(str);

	return NULL;
}
#endif
