package de.studiocode.invui.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class DataUtils {
    
    public static byte[] toByteArray(List<Integer> intList) {
        byte[] array = new byte[intList.size()];
        for (int i = 0; i < intList.size(); i++)
            array[i] = intList.get(i).byteValue();
        
        return array;
    }
    
    public static byte[] toByteArray(int[] intArray) {
        byte[] array = new byte[intArray.length];
        for (int i = 0; i < intArray.length; i++)
            array[i] = (byte) intArray[i];
        
        return array;
    }
    
    public static int[] toIntArray(byte[] byteArray) {
        int[] array = new int[byteArray.length];
        for (int i = 0; i < byteArray.length; i++)
            array[i] = byteArray[i];
        
        return array;
    }
    
    public static void writeIntArray(DataOutputStream dos, int[] array) throws IOException {
        dos.writeInt(array.length);
        for (int i : array) dos.writeInt(i);
    }
    
    public static int[] readIntArray(DataInputStream din) throws IOException {
        int size = din.readInt();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = din.readInt();
        }
        
        return array;
    }
    
    public static void writeByteArray(DataOutputStream dos, byte[] array) throws IOException {
        dos.writeInt(array.length);
        dos.write(array);
    }
    
    public static byte[] readByteArray(DataInputStream din) throws IOException {
        int size = din.readInt();
        byte[] array = new byte[size];
        din.readFully(array);
        return array;
    }
    
    public static void write2DByteArray(DataOutputStream dos, byte[][] array2d) throws IOException {
        dos.writeInt(array2d.length);
        for (byte[] array : array2d) {
            writeByteArray(dos, array);
        }
    }
    
    public static byte[][] read2DByteArray(DataInputStream din) throws IOException {
        int size2d = din.readInt();
        byte[][] array2d = new byte[size2d][];
        for (int i = 0; i < size2d; i++) {
            array2d[i] = readByteArray(din);
        }
        return array2d;
    }
    
}
