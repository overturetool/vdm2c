package org.overture.codegen.vdm2c;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.types.ASet1SetType;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.vdm2c.transformations.IsExpTypeEncoder;

public class IsExpTypeEncoderTest {

    @Test
    public void integer() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new AIntNumericBasicTypeIR());
        String expected = "{'i'}";

        check(actual, expected);
    }

    @Test
    public void nat() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ANatNumericBasicTypeIR());
        String expected = "{'j'}";

        check(actual, expected);
    }

    @Test
    public void real() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ARealNumericBasicTypeIR());
        String expected = "{'d'}";

        check(actual, expected);
    }

    @Test
    public void bool() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ABoolBasicTypeIR());
        String expected = "{'b'}";

        check(actual, expected);
    }

    @Test
    public void character() throws  AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ACharBasicTypeIR());
        String expected = "{'c'}";

        check(actual, expected);
    }

    @Test
    public void nat1() throws  AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ANat1NumericBasicTypeIR());
        String expected = "{'k'}";

        check(actual, expected);
    }

    @Test
    public void rat() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ARatNumericBasicTypeIR());
        String expected = "{'e'}";

        check(actual, expected);
    }

    @Test
    public void token() throws AnalysisException {

        String actual = IsExpTypeEncoder.encodeType(new ATokenBasicTypeIR());
        String expected = "{'t'}";

        check(actual, expected);
    }

    @Test
    public void seqOf() throws AnalysisException {

        ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
        seqType.setSeqOf(new AIntNumericBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(seqType);
        String expected = "{'Q','i'}";

        check(actual, expected);
    }

    @Test
    public void seq1Of() throws AnalysisException {

        ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
        seqType.setSeq1(true);
        seqType.setSeqOf(new ACharBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(seqType);
        String expected = "{'Z','c'}";

        check(actual, expected);
    }

    @Test
    public void setOf() throws AnalysisException {

        ASetSetTypeIR setType = new ASetSetTypeIR();
        setType.setSetOf(new ABoolBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(setType);
        String expected = "{'T','b'}";

        check(actual, expected);
    }

    @Test
    public void set1Of() throws AnalysisException {

        ASetSetTypeIR setType = new ASetSetTypeIR();
        setType.setSourceNode(new SourceNode(new ASet1SetType()));
        setType.setSetOf(new ANatNumericBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(setType);
        String expected = "{'Y','j'}";

        check(actual, expected);
    }

    @Test
    public void product() throws AnalysisException {

        ATupleTypeIR tup = new ATupleTypeIR();
        tup.getTypes().add(new ANat1NumericBasicTypeIR());
        tup.getTypes().add(new ARatNumericBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(tup);
        String expected = "{'P',2,'*','k','*','e','*'}";

        check(actual, expected);
    }

    @Test
    public void record() throws AnalysisException {

        ATypeNameIR name = new ATypeNameIR();
        name.setDefiningClass("A");
        name.setName("Rec");

        ARecordTypeIR recType = new ARecordTypeIR();
        recType.setName(name);

        String actual = IsExpTypeEncoder.encodeType(recType);
        String expected = "{'R',CLASS_ID_Rec_ID}";

        check(actual, expected);
    }

    @Test
    public void map() throws AnalysisException {

        AMapMapTypeIR mapType = new AMapMapTypeIR();
        mapType.setFrom(new AIntNumericBasicTypeIR());
        mapType.setTo(new ACharBasicTypeIR());

        String actual = IsExpTypeEncoder.encodeType(mapType);
        String expected = "{'M','*','i','*','c','*'}";

        check(actual, expected);
    }

    @Test
    public void clazz() throws AnalysisException {

        AClassTypeIR classType = new AClassTypeIR();
        classType.setName("A");

        String actual = IsExpTypeEncoder.encodeType(classType);
        String expected = "{'W',CLASS_ID_A_ID}";

        check(actual, expected);
    }

    private void check(String actual, String expected) {
        Assert.assertEquals("Got unexpected C encoding", expected, actual);
    }
}
