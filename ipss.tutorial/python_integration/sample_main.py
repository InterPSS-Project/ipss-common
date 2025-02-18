import jpype


jvm_path = jpype.getDefaultJVMPath()


jar_path = "lib/ipss_runnable.jar"


jpype.startJVM(jvm_path, "-ea", f"-Djava.class.path={jar_path}")


SampleLoadflow = jpype.JClass("org.interpss.tutorial.ch2_intro.SampleLoadflow")


SampleLoadflow.main([])


jpype.shutdownJVM()
