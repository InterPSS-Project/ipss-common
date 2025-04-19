import jpype
from pathlib import Path

# Get script directory for reliable path resolution
script_dir = Path(__file__).resolve().parent

# Let jpype find the JVM automatically
jvm_path = jpype.getDefaultJVMPath()

# Use platform-independent path joining
jar_path = str(script_dir.parent / "lib" / "ipss_runnable.jar")
print(f"JAR path: {jar_path}")

# Start JVM with proper path separators
jpype.startJVM(jvm_path, "-ea", f"-Djava.class.path={jar_path}")

SampleLoadflow = jpype.JClass("org.interpss.tutorial.ch2_intro.SampleLoadflow")

SampleLoadflow.main([])

jpype.shutdownJVM()
