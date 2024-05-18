package org.purbarun.instrumentation;

import java.lang.instrument.Instrumentation;

public class MyJavaAgent {
	public static void premain(String agentArgs, Instrumentation instrumentation) {
		ClassFileTransformerImpl classTransformer = new ClassFileTransformerImpl();
		classTransformer.init();
		instrumentation.addTransformer(classTransformer);
	}
}
