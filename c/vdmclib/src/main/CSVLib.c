/*
 * CSVLib.c
 *
 *  Created on: Jan 2017
 *      Author: Victor Bandur
 */

#include <stdio.h>

#include "TypedValue.h"
#include "Vdm.h"
#include "CSVLib.h"
#include "IOLib.h"



TVP vdm_CSV_flinecount(TVP f)
{
	char* fileName = unpackString(f);
	FILE *file = fopen(fileName, "r");
	unsigned char c;
	int lines = 0;

	while(!feof(file))
	{
		c = fgetc(file);
		if(c == '\n')
			lines++;
	}

	fclose(fileName);
	free(fileName);
}



TVP vdm_CSV_freadval(TVP f, TVP index)
{
	//this one
}


TVP vdm_CSV_fwriteval(TVP filename, TVP val, TVP fdir)
{}



TVP vdm_CSV_ferror()
{}
