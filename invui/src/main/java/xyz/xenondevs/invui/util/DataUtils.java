package xyz.xenondevs.invui.util;

import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DataUtils {
    
    public static void writeByteArray(@NotNull DataOutputStream dos, byte @NotNull [] array) throws IOException {
        dos.writeInt(array.length);
        dos.write(array);
    }
    
    public static byte @NotNull [] readByteArray(@NotNull DataInputStream din) throws IOException {
        int size = din.readInt();
        byte[] array = new byte[size];
        din.readFully(array);
        return array;
    }
    
    public static void write2DByteArray(@NotNull DataOutputStream dos, byte @NotNull [] @NotNull [] array2d) throws IOException {
        dos.writeInt(array2d.length);
        for (byte[] array : array2d) {
            writeByteArray(dos, array);
        }
    }
    
    public static byte @NotNull [] @NotNull [] read2DByteArray(@NotNull DataInputStream din) throws IOException {
        int size2d = din.readInt();
        byte[][] array2d = new byte[size2d][];
        for (int i = 0; i < size2d; i++) {
            array2d[i] = readByteArray(din);
        }
        return array2d;
    }
    
}
