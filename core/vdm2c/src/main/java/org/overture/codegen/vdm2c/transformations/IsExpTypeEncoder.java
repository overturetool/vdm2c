package org.overture.codegen.vdm2c.transformations;

import org.apache.commons.lang.BooleanUtils;
import org.overture.ast.types.ASet1SetType;
import org.overture.cgc.extast.analysis.AnswerCAdaptor;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.vdm2c.analysis.ClassAssocAnalysis;

public class IsExpTypeEncoder extends AnswerCAdaptor<String> {


    public static final String INT = "i";
    public static final String NAT = "j";
    public static final String REAL = "d";
    public static final String BOOL = "b";
    public static final String CHAR = "c";
    public static final String NAT1 = "k";
    public static final String RAT = "e";
    public static final String TOKEN = "t";
    public static final String SEQ_OF = "Q";
    public static final String SEQ1_OF = "Z";
    public static final String SET_OF = "T";
    public static final String SET1_OF = "Y";
    public static final String PRODUCT = "P";
    public static final String RECORD = "R";
    public static final String MAP = "M";
    public static final String CLASS = "W";

    public static final String DEL = ",";

    public static String encodeType(STypeIR typeToEncode) throws AnalysisException {
        IsExpTypeEncoder encoder = new IsExpTypeEncoder();

        String strEnc = typeToEncode.apply(encoder);

        StringBuilder cEnc = new StringBuilder();
        cEnc.append('{');
        cEnc.append(strEnc);
        cEnc.append('}');
        return cEnc.toString();
    }

    public String quotes(String s)
    {
        return "\'" + s + "\'";
    }

    @Override
    public String caseAIntNumericBasicTypeIR(AIntNumericBasicTypeIR node) throws AnalysisException {
        return optional(node) + quotes(INT);
    }

    @Override
    public String caseANatNumericBasicTypeIR(ANatNumericBasicTypeIR node) throws AnalysisException {
        return optional(node) + quotes(NAT);
    }

    @Override
    public String caseARealNumericBasicTypeIR(ARealNumericBasicTypeIR node) throws AnalysisException {
        return optional(node) + quotes(REAL);
    }

    @Override
    public String caseABoolBasicTypeIR(ABoolBasicTypeIR node) throws AnalysisException {
        return optional(node) + quotes(BOOL);
    }

    @Override
    public String caseACharBasicTypeIR(ACharBasicTypeIR node) throws AnalysisException {
        return optional(node) + quotes(CHAR);
    }

    @Override
    public String caseANat1NumericBasicTypeIR(ANat1NumericBasicTypeIR node) throws AnalysisException {

        return optional(node) + quotes(NAT1);
    }

    @Override
    public String caseARatNumericBasicTypeIR(ARatNumericBasicTypeIR node) throws AnalysisException {
        return optional(node) + quotes(RAT);
    }

    @Override
    public String caseATokenBasicTypeIR(ATokenBasicTypeIR node) throws AnalysisException {
        return optional(node) + quotes(TOKEN);
    }

    @Override
    public String caseASeqSeqTypeIR(ASeqSeqTypeIR node) throws AnalysisException {

        return optional(node) + quotes(BooleanUtils.isTrue(node.getSeq1()) ? SEQ1_OF : SEQ_OF )+ DEL  + node.getSeqOf().apply(THIS);
    }

    @Override
    public String caseASetSetTypeIR(ASetSetTypeIR node) throws AnalysisException {

        // The IR does not yet support set1
        boolean isSet1 = AssistantBase.getVdmNode(node) instanceof ASet1SetType;

        return optional(node) + quotes(isSet1 ? SET1_OF : SET_OF)+ DEL  + node.getSetOf().apply(THIS);
    }

    @Override
    public String caseATupleTypeIR(ATupleTypeIR node) throws AnalysisException {

        String product = quotes(PRODUCT) + DEL;
        product += node.getTypes().size();

        final String PRODUCT_DEL = quotes("*");
        String sep = PRODUCT_DEL;
        for(STypeIR t : node.getTypes())
        {
            product += DEL + sep  + DEL + t.apply(THIS);
        }

        product += DEL + PRODUCT_DEL;

        return optional(node) + product;
    }

    @Override
    public String caseARecordTypeIR(ARecordTypeIR node) throws AnalysisException {
        return optional(node) + quotes(RECORD )+ DEL  + ClassAssocAnalysis.toClassId(node.getName().getName());
    }

    @Override
    public String caseAMapMapTypeIR(AMapMapTypeIR node) throws AnalysisException {

        final String MAP_DEL = quotes("*");

        return optional(node) + quotes(MAP) +  DEL + MAP_DEL + DEL + node.getFrom().apply(THIS) + DEL + MAP_DEL + DEL + node.getTo().apply(THIS) + DEL + MAP_DEL;
    }

    public String optional(STypeIR type)
    {
        return (BooleanUtils.isTrue(type.getOptional()) ? "'0'" : "'1'") + DEL;
    }

    @Override
    public String caseAClassTypeIR(AClassTypeIR node) throws AnalysisException {
        return optional(node) + quotes(CLASS)+ DEL + ClassAssocAnalysis.toClassId(node.getName());
    }

    @Override
    public String createNewReturnValue(INode iNode) throws AnalysisException {
        return null;
    }

    @Override
    public String createNewReturnValue(Object o) throws AnalysisException {
        return null;
    }
}
