/*
 * IO.c
 *
 *  Created on: Feb 25, 2016
 *      Author: mot
 */

#include "TypedValue.h"
#include "Vdm.h"
#include "IO.h"

#ifndef CUSTOM_IO

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
	//TODO.

	return;
}



void vdm_IO_print(TVP arg)
{
	char* str;

	str = printVdmBasicValue(arg);
	printf("%s", str);
	free(str);
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
	int a;
	//TODO.
	char *str = unpackString(text);

	printf("%s", str);

	free(str);

	return NULL;
}
#endif
