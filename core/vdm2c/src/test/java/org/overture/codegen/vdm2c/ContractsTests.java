package org.overture.codegen.vdm2c;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.overture.codegen.vdm2c.CMakeUtil.CMakeGenerateException;

public class ContractsTests extends NativeTestBase {

    @Override
    protected List<String> buildArgs(String... paths)
    {
        List<String> args = super.buildArgs(paths);
        args.add("-pre");

        return args;
    }

    @Test
    public void preCond()  throws IOException, InterruptedException, CMakeGenerateException
    {
        generate(getPath("contracts/PreConditions.vdmrt"));
        compileAndTest(new File("src/test/resources/native/contracts/PreConditions_Tests.cpp".replace('/', File.separatorChar)));
    }
}
