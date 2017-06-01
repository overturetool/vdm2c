#include "Vdm.h"
#include "VdmUnpackString.h"

char* unpackString(TVP charseq)
{
	int i;
	char* str;



	UNWRAP_COLLECTION(col, charseq);

	str = (char*)malloc(col->size * sizeof(char) + 1);
	assert(str != NULL);
	str[col->size] = 0;

	for(i = 0; i < col->size; i++)
	{
		ASSERT_CHECK_CHAR((col->value[i]));
		str[i] = ((col->value[i])->value).charVal;
	}

	return str;
}
