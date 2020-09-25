package com.yang.example.activity.androidApi;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;

import com.yang.example.R;
import com.yang.base.activity.SimpleBarActivity;
import com.yang.base.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InternalStorageWriteActivity extends SimpleBarActivity {

    Thread mThread;
    boolean isWriting = false;
    private StringBuilder str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_storage_write);

        mThread = new Thread(new WriteRunable());
        str = new StringBuilder();
        for (int i = 0; i < 512; i++) {
            str.append("字");
        }
//        mThread.start();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.file_write_start:
                isWriting = true;
                if (!mThread.isAlive()) {
                    mThread.start();
                }
                break;
            case R.id.file_write_stop:
                isWriting = false;
                break;
        }
    }

    /**
     * 获取手机内部空间总大小
     *
     * @return 大小，字节为单位
     */
    static public long getTotalInternalMemorySize() {
        //获取内部存储根目录
        File path = Environment.getDataDirectory();
        //系统的空间描述类
        StatFs stat = new StatFs(path.getPath());
        //每个区块占字节数
        long blockSize = stat.getBlockSize();
        //区块总数
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return 大小，字节为单位
     */
    static public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    class WriteRunable implements Runnable {

        private int count;
        private String path;

        WriteRunable() {
            path = getFilesDir().getPath() + "/test/bigfile.abc";
            if (new File(path).exists()) {
                new File(path).delete();
            }
            String total = getTotalInternalMemorySize() / 1024 / 1024 + "MB";
            String available = getAvailableInternalMemorySize() / 1024 / 1024 + "MB";
            LogUtil.e("总空间：" + total + "    剩余可用空间：" + available);
        }

        @Override
        public void run() {
            while (isWriting) {
                writeFile(path, str.toString(), true);
                count++;
                if (count % 1024 == 0) {
                    LogUtil.e("已写入大小：" + count + "KB," + (count / 1024) + "MB");
                }
            }
            count = 0;
        }
    }

    public static File getFile(String filePath) {
        File file = new File(filePath);
        return getFile(file);
    }

    public static File getFile(File file) {
        if (file != null && !file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try {
                file.createNewFile();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

        return file;
    }

    public static boolean writeFile(String filePath, InputStream is, boolean isappend) {
        FileOutputStream fos = null;

        try {
            File saveFile = getFile(filePath);
            fos = new FileOutputStream(saveFile, isappend);
            return inputStreamToOutputStream(is, fos);
        } catch (Exception var5) {
            var5.printStackTrace();
            return false;
        }
    }

    public static boolean writeFile(String filePath, String str, boolean isappend) {
        return writeFile(filePath, str.getBytes(), isappend);
    }

    public static boolean writeFile(String filePath, byte[] bytes, boolean isappend) {
        FileOutputStream fos = null;

        try {
            File saveFile = getFile(filePath);
            fos = new FileOutputStream(saveFile, isappend);
            fos.write(bytes);
            fos.flush();
            boolean var5 = true;
            return var5;
        } catch (Exception var15) {
            var15.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception var14) {
                ;
            }

        }

        return false;
    }


    public static boolean inputStreamToOutputStream(InputStream is, OutputStream os) {
        try {
            if (is == null || os == null) {
                return false;
            } else {
                byte[] b = new byte[102400];
                boolean var3 = false;

                int i;
                while ((i = is.read(b)) != -1) {
                    os.write(b, 0, i);
                }

                boolean var4 = true;
                return var4;
            }
        } catch (IOException var15) {
            var15.printStackTrace();
            return false;
        } finally {
            try {
                os.flush();
                is.close();
                os.close();
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
