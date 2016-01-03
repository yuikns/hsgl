package tutorial;


import com.nativelibs4java.opencl.*;
import com.nativelibs4java.util.IOUtils;
import org.bridj.Pointer;

import java.io.IOException;
import java.nio.ByteOrder;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.bridj.Pointer.allocateFloats;

public class JavaCLTutorial1 {
    //https://code.google.com/p/javacl/wiki/GettingStarted
    public static void test(String[] args) throws IOException {
        CLContext context = JavaCL.createBestContext();
        CLQueue queue = context.createDefaultQueue();
        ByteOrder byteOrder = context.getByteOrder();

        int n = 1024;

        Pointer<Float> aPtr = allocateFloats(n).order(byteOrder);
        Pointer<Float> bPtr = allocateFloats(n).order(byteOrder);

        Pointer<Float> nPtr = allocateFloats(n).order(byteOrder);

        for (int i = 0; i < n; i++) {
            aPtr.set(i, (float) cos(i));
            bPtr.set(i, (float) sin(i));
        }

        // Create OpenCL input buffers (using the native memory pointers aPtr and bPtr) :
        CLBuffer<Float>
                a = context.createBuffer(CLMem.Usage.Input, aPtr),
                b = context.createBuffer(CLMem.Usage.Input, bPtr);

        // Create an OpenCL output buffer :
        CLBuffer<Float> out = context.createBuffer(CLMem.Usage.Output, nPtr);

        // Read the program sources and compile them :
        String src = IOUtils.readText(JavaCLTutorial1.class.getResource("/TutorialKernels.cl"));
        CLProgram program = context.createProgram(src);

        // Get and call the kernel :
        CLKernel addFloatsKernel = program.createKernel("add_floats");
        addFloatsKernel.setArgs(a, b, out, n);
        CLEvent addEvt = addFloatsKernel.enqueueNDRange(queue, new int[]{n});

        Pointer<Float> outPtr = out.read(queue, addEvt); // blocks until add_floats finished

        // Print the first 10 output values :
        for (int i = 0; i < 10 && i < n; i++)
            System.out.println("out[" + i + "] = " + outPtr.get(i));

    }
}