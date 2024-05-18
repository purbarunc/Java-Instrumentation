package org.purbarun.instrumentation;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.runtime.Desc;
import javassist.scopedpool.ScopedClassPoolFactoryImpl;
import javassist.scopedpool.ScopedClassPoolRepositoryImpl;

public class ClassFileTransformerImpl implements ClassFileTransformer {
	private static final Logger log = Logger.getLogger(ClassFileTransformerImpl.class.getName());
	private ScopedClassPoolFactoryImpl scopedClassPoolFactory = new ScopedClassPoolFactoryImpl();
	private static final String CLASS_TO_INSTRUMENT = "org/purbarun/instrumentation/MyHelloWorld";
	private static final String METHOD_TO_INSTRUMENT = "main";
	private ClassPool rootPool;

	public void init() {
		Desc.useContextClassLoader = true;
		rootPool = ClassPool.getDefault();
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		byte[] byteCode = classfileBuffer;
		if (className.equals(CLASS_TO_INSTRUMENT)) {
			try {
				ClassPool classPool = scopedClassPoolFactory.create(loader, rootPool,
						ScopedClassPoolRepositoryImpl.getInstance());
				CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
				CtMethod[] methods = ctClass.getDeclaredMethods();

				for (CtMethod method : methods) {
					if (method.getName().equals(METHOD_TO_INSTRUMENT)) {
						method.insertBefore("System.out.println(\"Executing Main method !!\");");
						method.insertAfter("System.out.println(\"Main method executed !!\");");
					}
				}
				byteCode = ctClass.toBytecode();
				ctClass.detach();
			} catch (Exception ex) {
				log.log(Level.SEVERE, "Error in transforming the class: " + className, ex);
			}
		}
		return byteCode;
	}
}
