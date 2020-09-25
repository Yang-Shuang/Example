package com.yang.base.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    public static final String END = "@end@";

    public static String input2String(InputStream in) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
            if (new String(buffer, 0, length).contains("@end@")) {
                return toResponse(result.toString("UTF-8"));
            }
        }
        return toResponse(result.toString("UTF-8"));
    }

    private static String toResponse(String s) {
        if (s == null || s.length() == 0) return null;
        if (s.contains(END))
            return s.substring(0, s.indexOf(END));
        else return s;
    }

    public static void copyAssetsFile(Context context, String assetsPath, String outPath) {
        try {
            OutputStream myOutput = new FileOutputStream(outPath);
            InputStream myInput = context.getAssets().open(assetsPath);
            byte[] buffer = new byte[1024 * 8];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }
            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
