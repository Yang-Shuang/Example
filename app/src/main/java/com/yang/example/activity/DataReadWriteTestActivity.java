package com.yang.example.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.yang.example.R;
import com.yang.example.bean.LogDataBean;
import com.yang.example.rx.SimpleTask;
import com.yang.example.utils.LogUtil;
import com.yang.example.utils.SQLiteTestHelper;
import com.yang.example.view.NoteView;

import rx.Subscriber;

import static com.yang.example.view.NoteView.LIGHT_LEVEL;

public class DataReadWriteTestActivity extends SimpleBarActivity {

    private NoteView mNoteView;
    private EditText insertCountEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_read_write_test);
        mNoteView = (NoteView) findViewById(R.id.data_noteview);
        insertCountEdt = (EditText) findViewById(R.id.data_read_write_count_edt);
    }


    @Override
    public void onLoadCompleted(int taskID, Bundle result) {
        super.onLoadCompleted(taskID, result);
        LogUtil.e("************   onCompleted   ************ ID :" + taskID);
        String message = "result is null";
        if (result != null) {
            message = result.toString();
        }
        LogUtil.e(message);
        LogUtil.e("*********************************************************");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.data_read_write_count_btn1:
                int count = getNumber(insertCountEdt);
                long time = System.currentTimeMillis();
                mNoteView.addText("Sqlite插入" + count + "数据开始：" + time);
                SQLiteTestHelper helper = new SQLiteTestHelper(this);
                for (int i = 0; i < count; i++) {
                    LogDataBean bean = new LogDataBean();
                    bean.setContent("Content");
                    bean.setTag("tag");
                    bean.setType("Error");
                    bean.setTime("" + System.currentTimeMillis());
                    helper.insertData(bean);
                }
                long endTime = System.currentTimeMillis();
                helper.closeDB();
                mNoteView.addText("Sqlite插入" + count + "数据结束：" + endTime);
                mNoteView.addText("Sqlite插入" + count + "数据耗时：" + (endTime - time));
                mNoteView.addText(LIGHT_LEVEL, "Sqlite插入数据平均每条：" + ((endTime - time) / 1f / count));
                break;
            case R.id.data_read_write_count_btn2:
                asyncExecute(new SimpleTask(11) {
                    @Override
                    public Bundle run(Bundle params) {
                        params.putString("time1", "" + System.currentTimeMillis());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogUtil.e("********  Task 11 end ********");
                        return params;
                    }
                }).next(new SimpleTask(12) {
                    @Override
                    public Bundle run(Bundle params) {
                        params.putString("time2", "" + System.currentTimeMillis());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogUtil.e("********  Task 12 end ********");
                        return params;
                    }
                }).next(new SimpleTask(13) {
                    @Override
                    public Bundle run(Bundle params) {
                        params.putString("time3", "" + System.currentTimeMillis());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogUtil.e("********  Task 13 end ********");
                        return params;
                    }
                }).execute(this);
                break;
        }
    }

    public int getNumber(EditText editText) {
        if (editText.getText() == null || editText.getText().equals("")) return 0;
        try {
            return Integer.valueOf(editText.getText().toString());
        } catch (NumberFormatException e) {
            editText.setText("0");
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.getMessage());
        }
        return 0;
    }
}
