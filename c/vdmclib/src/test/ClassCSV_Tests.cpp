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
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */

#include "gtest/gtest.h"
//#include <unistd.h>

extern "C"
{
#include "Vdm.h"
#include "CSVLib.h"

}

#ifndef NO_CSV

TVP packString(char *charseq)
{
	int len = strlen(charseq);
	int i;
	TVP *theString;

	theString = (TVP *)malloc(len * sizeof(TVP));

	for(i = 0; i < len; i++)
	{
		theString[i] = newChar(charseq[i]);
	}

	return newSeqWithValues(len, theString);
}


TEST(ClassCSV, linecount)
{
	TVP CSVFileAsSeq;
	TVP lineCount;

	char *CSVContents[] =
	{		"1,2,3,4",
			"5,6,7,8",
			"9,10,11,12",
			"12.1,12.2,12.3,12.4",
			"12.5,12.6,12.7,12.8"
	};

	FILE *CSVFile = fopen("CSVTestInput.csv", "w");
	for(int i = 0; i < 5; i++)
	{
		fprintf(CSVFile, "%s\n", CSVContents[i]);
	}
	fclose(CSVFile);

	CSVFileAsSeq = packString("CSVTestInput.csv");

	lineCount = vdm_CSV_flinecount(CSVFileAsSeq);

	UNWRAP_PRODUCT(col, lineCount);

	EXPECT_EQ(5, col->value[1]->value.intVal);

	vdmFree(lineCount);
	vdmFree(CSVFileAsSeq);
}

TEST(ClassCSV, read_value)
{
	TVP CSVFileAsSeq;
	TVP lineInFileAsSeq;

	char *CSVContents[] =
	{		"1,2,3,4",
			"5,6,7,8",
			"9,10,11,12",
			"12.1,12.2,12.3,12.4",
			"12.5,12.6,12.7,12.8"
	};

	FILE *CSVFile = fopen("CSVTestInput.csv", "w");
	for(int i = 0; i < 5; i++)
	{
		fprintf(CSVFile, "%s\n", CSVContents[i]);
	}
	fclose(CSVFile);

	CSVFileAsSeq = packString("CSVTestInput.csv");

	lineInFileAsSeq = vdm_CSV_freadval(CSVFileAsSeq, newInt(4));



	UNWRAP_PRODUCT(col, lineInFileAsSeq);
	//lineAsSeq gets the sequence of chars.
	UNWRAP_PRODUCT(lineAsSeq, col->value[1]);

	EXPECT_EQ(12.1, lineAsSeq->value[0]->value.doubleVal);
	EXPECT_EQ(12.2, lineAsSeq->value[1]->value.doubleVal);
	EXPECT_EQ(12.3, lineAsSeq->value[2]->value.doubleVal);
	EXPECT_EQ(12.4, lineAsSeq->value[3]->value.doubleVal);

	vdmFree(CSVFileAsSeq);
	vdmFree(lineInFileAsSeq);
}

#endif /* NO_CSV */
