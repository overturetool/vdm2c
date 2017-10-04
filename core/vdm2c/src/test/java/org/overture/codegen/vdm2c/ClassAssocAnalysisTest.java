package org.overture.codegen.vdm2c;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.vdm2c.analysis.ClassAssocAnalysis;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;

import java.util.List;

public class ClassAssocAnalysisTest {

    @Test
    public void noInheritance() throws AnalysisException {

        String model = "class A end A";
        String expected = "int assoc[] = {};";
        checkResult(model, expected);
    }

    @Test
    public void simpleInheritance() throws AnalysisException {

        // A inherits from B
        String model = "class A is subclass of B end A class B end B";
        String expected = "#include \"A.h\"\n#include \"B.h\"\n\nint assoc[] = {CLASS_ID_A_ID, CLASS_ID_B_ID};";
        checkResult(model, expected);
    }

    @Test
    public void mixed() throws AnalysisException {

        // A inherits from B. C has no parent.
        String model = "class A is subclass of B end A class B end B class C end C";
        String expected = "#include \"A.h\"\n#include \"B.h\"\n\nint assoc[] = {CLASS_ID_A_ID, CLASS_ID_B_ID};";
        checkResult(model, expected);
    }

    @Test
    public void multipleInheritance() throws AnalysisException {

        // A inherits from both B and C
        String model = "class A is subclass of B,C end A class B end B class C end C";
        String expected = "#include \"A.h\"\n#include \"B.h\"\n#include \"C.h\"\n\nint assoc[] = {CLASS_ID_A_ID, CLASS_ID_B_ID, CLASS_ID_A_ID, CLASS_ID_C_ID};";
        checkResult(model, expected);
    }

    @Test
    public void singleInheritanceComplex() throws AnalysisException {

        // Inheritance hirarchy (child -> parent) B -> A, C -> A, D -> C.
        String model = "class A end A class B is subclass of A end B class C is subclass of A end C class D is subclass of C end D";
        String expected = "#include \"A.h\"\n#include \"B.h\"\n#include \"C.h\"\n#include \"D.h\"\n\nint assoc[] = {CLASS_ID_B_ID, CLASS_ID_A_ID, CLASS_ID_C_ID, CLASS_ID_A_ID, CLASS_ID_D_ID, CLASS_ID_C_ID};";
        checkResult(model, expected);
    }

    private void checkResult(String model, String expected) throws AnalysisException {
        Assert.assertEquals("Got unexpected class association array", CFormat.getGeneratedFileComment() + expected, buildAssocArray(model));
    }

    private String buildAssocArray(String model) throws AnalysisException {
        Settings.dialect = Dialect.VDM_PP;
        Settings.release = Release.VDM_10;

        TypeCheckerUtil.TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckPp(model);
        Assert.assertTrue("Expected no parse errors. Got: " + result.parserResult.errors.toString(), result.parserResult.errors.isEmpty());
        Assert.assertTrue("Expected no type errors. Got: " + result.errors, result.errors.isEmpty());

        List<SClassDefinition> ast = result.result;

        return ClassAssocAnalysis.runAnalysis(ast);
    }
}
