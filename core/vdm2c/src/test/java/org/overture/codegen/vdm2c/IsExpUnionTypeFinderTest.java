package org.overture.codegen.vdm2c;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2c.transformations.IsExpUnionTypeFinder;

import java.util.List;

public class IsExpUnionTypeFinderTest {

    @Test
    public void passNull() throws AnalysisException {
        List<STypeIR> res = findTypes(null);
        Assert.assertTrue("Expected no types", res.isEmpty());
    }

    @Test
    public void nat() throws AnalysisException {

        List<STypeIR> res = findTypes(new ANatNumericBasicTypeIR());

        Assert.assertEquals("Expected a single type", 1, res.size());
        Assert.assertEquals("Expected type to be nat", new ANatNumericBasicTypeIR(), res.get(0));
    }

    @Test
    public void natCharTuple() throws AnalysisException {

        ATupleTypeIR tup = new ATupleTypeIR();
        tup.getTypes().add(new ANatNumericBasicTypeIR());
        tup.getTypes().add(new ACharBasicTypeIR());

        List<STypeIR> res = findTypes(tup);

        Assert.assertEquals("Expected only a single type",1,  res.size());
        Assert.assertTrue("Expected nat *char type", res.contains(tup));
    }

    @Test
    public void natOrChar() throws AnalysisException {

        AUnionTypeIR un = new AUnionTypeIR();
        un.getTypes().add(new ANatNumericBasicTypeIR());
        un.getTypes().add(new ACharBasicTypeIR());

        List<STypeIR> res = findTypes(un);
        Assert.assertEquals("Expected two types", 2, res.size());

        Assert.assertEquals("Expected nat type", new ANatNumericBasicTypeIR(), res.get(0));
        Assert.assertEquals("Expected char type", new ACharBasicTypeIR(), res.get(1));
    }

    @Test
    public void productWithSingleUnionTypeField() throws AnalysisException {
        // nat * (char | bool)

        AUnionTypeIR secFieldType = new AUnionTypeIR();
        secFieldType.getTypes().add(new ACharBasicTypeIR());
        secFieldType.getTypes().add(new ABoolBasicTypeIR());

        ATupleTypeIR tup = new ATupleTypeIR();
        tup.getTypes().add(new ANatNumericBasicTypeIR());
        tup.getTypes().add(secFieldType);

        List<STypeIR> res = findTypes(tup);
        Assert.assertEquals("Expected two types", 2, res.size());


        // Expect nat * char
        ATupleTypeIR expectedFirst = new ATupleTypeIR();
        expectedFirst.getTypes().add(new ANatNumericBasicTypeIR());
        expectedFirst.getTypes().add(new ACharBasicTypeIR());

        // Expect nat * bool
        ATupleTypeIR expectedSecond = new ATupleTypeIR();
        expectedSecond.getTypes().add(new ANatNumericBasicTypeIR());
        expectedSecond.getTypes().add(new ABoolBasicTypeIR());

        Assert.assertEquals("Expected first type to be: nat * char", expectedFirst, res.get(0));
        Assert.assertEquals("Expected second type to be: nat * bool", expectedSecond, res.get(1));
    }

    @Test
    public void productWithTwoUnionTypeFields() throws AnalysisException {

        // (nat | token) * (char | bool)

        AUnionTypeIR firstFieldType = new AUnionTypeIR();
        firstFieldType.getTypes().add(new ANatNumericBasicTypeIR());
        firstFieldType.getTypes().add(new ATokenBasicTypeIR());

        AUnionTypeIR secFieldType = new AUnionTypeIR();
        secFieldType.getTypes().add(new ACharBasicTypeIR());
        secFieldType.getTypes().add(new ABoolBasicTypeIR());

        ATupleTypeIR tup = new ATupleTypeIR();
        tup.getTypes().add(firstFieldType);
        tup.getTypes().add(secFieldType);

        List<STypeIR> res = findTypes(tup);
        Assert.assertEquals("Expected four types", 4, res.size());

        // Expect nat * char
        ATupleTypeIR natChar = new ATupleTypeIR();
        natChar.getTypes().add(new ANatNumericBasicTypeIR());
        natChar.getTypes().add(new ACharBasicTypeIR());

        // Expect nat * bool
        ATupleTypeIR natBool = new ATupleTypeIR();
        natBool.getTypes().add(new ANatNumericBasicTypeIR());
        natBool.getTypes().add(new ABoolBasicTypeIR());

        // Expect token * char
        ATupleTypeIR tokenChar = new ATupleTypeIR();
        tokenChar.getTypes().add(new ATokenBasicTypeIR());
        tokenChar.getTypes().add(new ACharBasicTypeIR());

        // Expect token * bool
        ATupleTypeIR tokenBool = new ATupleTypeIR();
        tokenBool.getTypes().add(new ATokenBasicTypeIR());
        tokenBool.getTypes().add(new ABoolBasicTypeIR());

        Assert.assertTrue("Expected nat * char", res.contains(natChar));
        Assert.assertTrue("Expected nat * bool", res.contains(natBool));
        Assert.assertTrue("Expected token * char", res.contains(tokenChar));
        Assert.assertTrue("Expected token * bool", res.contains(tokenBool));
    }
    
    private List<STypeIR> findTypes(STypeIR type) throws AnalysisException {
        return IsExpUnionTypeFinder.findTypes(new TransAssistantIR(null), type);
    }
}
