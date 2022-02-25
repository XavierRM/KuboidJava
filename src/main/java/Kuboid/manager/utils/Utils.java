package Kuboid.manager.utils;

import java.nio.FloatBuffer;

import static org.lwjgl.system.MemoryUtil.memAllocFloat;

public class Utils {

    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }
}
