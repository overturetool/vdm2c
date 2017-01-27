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



//For now only handles CSV files containing floating point numbers.
TVP vdm_CSV_freadval(TVP f, TVP index)
{
	char* fileName = unpackString(f);
	FILE *file = fopen(fileName, "r");
	unsigned char c = 'a';
	int lineSought = index->value.intVal;
	int currLine = 1;
	int i;
	char *lineBuf, *tempLineBuf, *tailPtr;
	size_t bytesToRead = 1000;
	TVP *values;
	TVP resultProd[2];
	TVP result;

	//If only one column there will be no comma.
	int numCols = 1;


	//Determine number of columns.
	while(c != '\n')
	{
		c = fgetc(file);
		if(c == ',')
			numCols++;
	}
	//For the list into which they will be stored.
	values = malloc(numCols * sizeof(TVP));


	//Seek to the specified line.
	rewind(file);
	while(currLine < lineSought)
	{
		c = fgetc(file);
		if(c == '\n')
			currLine++;
	}


	//Read line.
	lineBuf = malloc(bytesToRead);
	tempLineBuf = lineBuf;

	c = 'a';
	i = 0;
	while(c != '\n')
	{
		c = fgetc(file);
		lineBuf[i] = c;
		i++;
	}


	//Parse line.
	for(i = 0; i < numCols; i++)
	{
		values[i] = newReal(strtod(lineBuf, &tailPtr));
		tailPtr++;
		lineBuf = tailPtr;
	}


	//Create result product of success value and list of values
	//on line sought.
	resultProd[0] = newBool(true);
	resultProd[1] = newSeqWithValues(numCols, values);
	result = newProductWithValues(2, resultProd);


	//Clean up
	free(tempLineBuf);
	free(fileName);
	for(i = 0; i < numCols; i++)
	{
		vdmFree(values[i]);
	}
	free(values);
	fclose(file);

	return result;
}


TVP vdm_CSV_fwriteval(TVP filename, TVP val, TVP fdir)
{}



TVP vdm_CSV_ferror()
{}
