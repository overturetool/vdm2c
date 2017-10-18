package org.overture.codegen.vdm2c;

import java.io.File;
import java.util.List;

public class ContractsGarbageCollectionTests extends ContractsTests {

    @Override
    protected List<String> buildArgs(String... paths)
    {
        List<String> args = super.buildArgs(paths);
        args.add("-gc");

        return args;
    }

    @Override
    protected File getFixtureFile()
    {
        return new File("src/test/resources/garbage-collection/TestFlowFunctions.h");
    }
}
