/*
 * CSVLib.c
 *
 *  Created on: Jan 2017
 *      Author: Victor Bandur
 */

#include "CSVLib.h"




TVP vdm_CSV_flinecount(TVP f)
{
	char* fileName = unpackString(f);
	FILE *file = fopen(fileName, "r");
	unsigned char c;
	int lines = 0;
	TVP resultProd[2];
	TVP result;

	while(!feof(file))
	{
		c = fgetc(file);
		if(c == '\n')
			lines++;
	}

	resultProd[0] = newBool(true);
	resultProd[1] = newInt(lines);

	fclose(file);
	free(fileName);

	result = newProductWithValues(2, resultProd);
	return result;
}



TVP vdm_CSV_freadval(TVP f, TVP index)
{
	//this one
}


TVP vdm_CSV_fwriteval(TVP filename, TVP val, TVP fdir)
{}



TVP vdm_CSV_ferror()
{}
