/*
 * CSVLib.c
 *
 *  Created on: Jan 2017
 *      Author: Victor Bandur
 */

#include "CSVLib.h"

/* This must not be protected by NO_CSV for source code FMU export to work properly.  */
char* resourcesLocation = NULL;

#ifndef NO_CSV

char* URIToNativePath(const char* uri)
{
#if defined WIN32 || defined WIN64
	char native_path_separator = '\\';
	char foreign_path_separator = '/';
#else
	char native_path_separator = '/';
	char foreign_path_separator = '\\';
#endif

	unsigned int path_start = 0;
	char* path = NULL;
	unsigned int path_len = 0;
	unsigned int uri_len = 0;
	unsigned int i = 0;
	unsigned int j = 0;
	char buf[3] = "00";

	if (!uri)
	{
		return NULL;
	}
	uri_len = (unsigned int)strlen(uri);
	if (uri_len == 0)
	{
		return NULL;
	}
	/* Check if we got a file:/// uri */
	if (strncmp(uri, "file:///", 8) == 0)
	{
		if (uri[9] == ':')
		{
			/* Windows drive letter in the URI (e.g. file:///c:/ uri */
			/* Remove the file:/// */
			path_start = 8;
		}
		else
		{
			/* Remove the file:// but keep the third / */
			path_start = 7;
		}
	}
#if defined WIN32 || defined WIN64
	/* Check if we got a file://hostname/path uri */
	else if (strncmp(uri, "file://", 7) == 0)
	{
		/* Convert to a network share path: //hostname/path */
		path_start = 5;
	}
#endif
	/* Check if we got a file:/ uri */
	else if (strncmp(uri, "file:/", 6) == 0)
	{
		if (uri[7] == ':')
		{
			/* Windows drive letter in the URI (e.g. file:/c:/ uri */
			/* Remove the  file:/ */
			path_start = 6;
		}
		else
		{
			/* Remove the file: but keep the / */
			path_start = 5;
		}
	}
	/* Assume that it is a native path */
	else
	{
		path_start = 0;
	}
	/* Check the length of the remaining string */
	path_len = (int)strlen(&uri[path_start]);
	if (path_len == 0)
	{
		return NULL;
	}   path = (char*)malloc(path_len + 2);
	assert(path != NULL);
	/* Copy the remainder of the uri and replace all percent encoded character
	 * * by their ASCII character and translate slashes to backslashes on Windows
	 *  * and backslashes to slashes on other OSses   */
	for (i = path_start, j = 0; i < uri_len; i++, j++)
	{
		if (uri[i] == '%')
		{
			/* Replace the precent-encoded hexadecimal digits by its US-ASCII       * representation */
			if (i < uri_len - 2)
			{
				if ((isxdigit(uri[i + 1])) && (isxdigit(uri[i + 2])))
				{
					strncpy(buf, uri + i + 1, 2);
					path[j] = (unsigned char)strtol(buf, NULL, 16);
					i += 2;
					path_len -= 2;
				}
				else
				{
					/* Not percent encoded, keep the % */
					path[j] = uri[i];
				}
			}
			else
			{
				/* Not percent encoded, keep the % */
				path[j] = uri[i];
			}
		}
		else if (uri[i] == foreign_path_separator)
		{
			/* Translate slashes to backslashes on Windows and backslashes to slashes
			 *       * on other OSses */
			path[j] = native_path_separator;
		}
		else
		{
			/* Just copy the character */
			path[j] = uri[i];
		}
	}   /* Check if we need to add a path separator at the end */
	if (path[path_len - 1] == native_path_separator)
	{
		path[path_len] = '\0';
	}
	else
	{
		path[path_len] = native_path_separator;
	}
	/* Make sure that the string is always NULL terminated */
	path[path_len + 1] = '\0';
	return path;
}




TVP vdm_CSV_flinecount(TVP f)
{
	char *resLoc = NULL;
	char *fileName, *filePath;
	FILE *file;

	fileName = unpackString(f);

	/* Generated code is in the context of a co-simulation;  */
	if(resourcesLocation != NULL)
	{
		resLoc = URIToNativePath(resourcesLocation);
		filePath = (char*)calloc(strlen(resLoc) + strlen(fileName) + 1, sizeof(char));
		assert(filePath != NULL);
		strcat(filePath, resLoc);
		strcat(filePath, fileName);
	}
	/* Generated code is being used on its own.  */
	else
	{
		filePath = (char*)calloc(strlen(fileName) + 1, sizeof(char));
		assert(filePath != NULL);
		strcat(filePath, fileName);
	}

	file = fopen(filePath, "r");

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
	free(filePath);
	if(resLoc != NULL)
	{
		free(resLoc);
	}

	result = newProductWithValues(2, resultProd);

	return result;
}



/* For now only handles CSV files containing floating point numbers.  */
TVP vdm_CSV_freadval(TVP f, TVP index)
{
	char *resLoc = NULL;
	char *fileName, *filePath;
	FILE *file;

	unsigned char c = 'a';
	int lineSought = index->value.intVal;
	int currLine = 1;
	int i;
	char *lineBuf, *tempLineBuf, *tailPtr;
	size_t bytesToRead = 1000;
	TVP *values;
	TVP resultProd[2];
	TVP result;

	/* If only one column there will be no comma.  */
	int numCols = 1;

	fileName = unpackString(f);

	/* Generated code is in the context of a co-simulation;  */
	if(resourcesLocation != NULL)
	{
		resLoc = URIToNativePath(resourcesLocation);
		filePath = (char*)calloc(strlen(resLoc) + strlen(fileName) + 1, sizeof(char));
		assert(filePath != NULL);
		strcat(filePath, resLoc);
		strcat(filePath, fileName);
	}
	/* Generated code is being used on its own.  */
	else
	{
		filePath = (char*)calloc(strlen(fileName) + 1, sizeof(char));
		assert(filePath != NULL);
		strcat(filePath, fileName);
	}

	file = fopen(filePath, "r");

	/* Determine number of columns.  */
	while(c != '\n')
	{
		c = fgetc(file);
		if(c == ',')
			numCols++;
	}
	/* For the list into which they will be stored.  */
	values = malloc(numCols * sizeof(TVP));
	assert(values != NULL);

	/* Seek to the specified line.  */
	rewind(file);
	while(currLine < lineSought)
	{
		c = fgetc(file);
		if(c == '\n')
			currLine++;
	}

	/* Read line.  */
	lineBuf = malloc(bytesToRead);
	assert(lineBuf != NULL);
	tempLineBuf = lineBuf;

	c = 'a';
	i = 0;
	while(c != '\n')
	{
		c = fgetc(file);
		lineBuf[i] = c;
		i++;
	}


	/* Parse line.  */
	for(i = 0; i < numCols; i++)
	{
		values[i] = newReal(strtod(lineBuf, &tailPtr));
		tailPtr++;
		lineBuf = tailPtr;
	}


	/* Create result product of success value and list of values  */
	/* on line sought.  */
	resultProd[0] = newBool(true);
	resultProd[1] = newSeqWithValues(numCols, values);
	result = newProductWithValues(2, resultProd);

	fclose(file);

	/* Clean up  */
	free(tempLineBuf);
	for(i = 0; i < numCols; i++)
	{
		vdmFree(values[i]);
	}
	free(values);
	free(fileName);
	free(filePath);
	if(resLoc != NULL)
	{
		free(resLoc);
	}

	return result;
}


TVP vdm_CSV_fwriteval(TVP filename, TVP val, TVP fdir)
{
	return NULL;
}



TVP vdm_CSV_ferror()
{
	return NULL;
}

#endif /* NO_CSV */
