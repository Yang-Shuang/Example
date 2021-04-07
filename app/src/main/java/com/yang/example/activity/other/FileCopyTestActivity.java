package com.yang.example.activity.other;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EdgeEffect;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;

import com.yang.base.activity.SimpleBarActivity;
import com.yang.base.rx.SimpleTask;
import com.yang.base.utils.LogUtil;
import com.yang.example.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopyTestActivity extends SimpleBarActivity {

    private AppCompatEditText from_file_path_edt;
    private AppCompatEditText to_file_path_edt;
    private AppCompatEditText copy_count_edt;
    private AppCompatCheckBox is_self_copy_cb;
    private AppCompatButton copy_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_copy_test);

        from_file_path_edt = findViewById(R.id.from_file_path_edt);
        to_file_path_edt = findViewById(R.id.to_file_path_edt);
        copy_count_edt = findViewById(R.id.copy_count_edt);
        is_self_copy_cb = findViewById(R.id.is_self_copy_cb);
        copy_btn = findViewById(R.id.copy_btn);

        copy_btn.setOnClickListener(this);

        is_self_copy_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    to_file_path_edt.setEnabled(false);
                    copy_count_edt.setEnabled(true);
                } else {
                    to_file_path_edt.setEnabled(true);
                    copy_count_edt.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.copy_btn:
                boolean isSelfCopy = is_self_copy_cb.isChecked();
                String from = from_file_path_edt.getText().toString();
                if (TextUtils.isEmpty(from)) {
                    showToast("缺少源文件路径");
                    return;
                }

                File file = new File(from);
                if (!file.exists()) {
                    showToast("源文件不存在");
                    return;
                }

                if (isSelfCopy) {
                    String count = copy_count_edt.getText().toString();
                    if (TextUtils.isEmpty(count)) {
                        showToast("缺少复制次数");
                        return;
                    }
                    try {
                        long cou = Long.parseLong(count);
                        String toPathName = "";
                        String toPathSuffix = "";
                        if (from.contains(".")) {
                            int pointIndex = from.lastIndexOf(".");
                            toPathName = from.substring(0, pointIndex);
                            toPathSuffix = from.substring(pointIndex, from.length());
                        } else {
                            toPathName = from;
                        }
                        showLoading();
                        String finalToPathName = toPathName;
                        String finalToPathSuffix = toPathSuffix;
                        asyncExecute(new SimpleTask() {
                            @Override
                            public Bundle run(Bundle params) {
                                for (int i = 0; i < cou; i++) {
                                    String to = finalToPathName + "_" + i + finalToPathSuffix;
                                    CopySdcardFile(from, to);
                                    LogUtil.i("Copy Completed : " + to);
                                }
                                return params;
                            }
                        }).execute(this);
                        LogUtil.i("Copy Start : " + System.currentTimeMillis());
                    } catch (NumberFormatException e) {
                        showToast("复制次数格式非法");
                        return;
                    } catch (Exception e) {
                        showToast(e.getMessage());
                        return;
                    }
                } else {
                    String to = to_file_path_edt.getText().toString();
                    if (TextUtils.isEmpty(to)) {
                        showToast("缺少目标文件路径");
                        return;
                    }
                }
                break;
        }
    }


    public int copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
            {
                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

            } else//如果当前项为文件则进行文件拷贝
            {
                CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return 0;
    }


    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public int CopySdcardFile(String fromFile, String toFile) {

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex) {
            return -1;
        }
    }

    @Override
    public void onLoadCompleted(int taskID, Bundle reault) {
        super.onLoadCompleted(taskID, reault);
        dimisssLoading();
        LogUtil.i("Copy Finish : " + System.currentTimeMillis());
    }
}