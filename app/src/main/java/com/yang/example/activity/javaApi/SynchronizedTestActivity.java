package com.yang.example.activity.javaApi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.yang.base.utils.LogUtil;
import com.yang.example.R;
import com.yang.base.activity.SimpleBarActivity;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class SynchronizedTestActivity extends SimpleBarActivity {


    static class Company {
        public ProgressBar progressBar1, progressBar2, progressBar3;

        public Company() {
        }

        public void cons(ProgressBar progressBar1, ProgressBar progressBar2, ProgressBar progressBar3) {
            this.progressBar1 = progressBar1;
            this.progressBar2 = progressBar2;
            this.progressBar3 = progressBar3;
        }
    }

    @BindView(R.id.progress1)
    ProgressBar progressBar1;

    @BindView(R.id.progress2)
    ProgressBar progressBar2;

    @BindView(R.id.progress3)
    ProgressBar progressBar3;

    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.button4)
    Button button4;

    private int threadFlag1 = 0x00000000;
    private int threadFlag2 = 0x00000000;

    private final int THREAD1_COMPLETE = 0x00000001;
    private final int THREAD2_COMPLETE = 0x00000002;
    private final int THREAD3_COMPLETE = 0x00000004;
    private final int THREAD_ALL_COM = 0x00000007;

    private Random mRandom;

    private final Company mCompany = new Company();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronized_test);
        mRandom = new Random();
        mCompany.cons(progressBar1, progressBar2, progressBar3);
    }

    @OnClick({R.id.button1, R.id.button2, R.id.button3, R.id.button4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                showToast("单线程");
                reset();
                button1.setEnabled(false);
                startSingleThread();
                break;
            case R.id.button2:
                showToast("多线程");
                reset();
                button2.setEnabled(false);
                startMultiThread();
                break;
            case R.id.button3:
                showToast("线程同步");
                reset();
                button3.setEnabled(false);
                startSyncThread();
                break;
            case R.id.button4:
                showToast("线程死锁");
                showToast("" + (button1 == null));
                break;
        }
    }

    private void startSingleThread() {
        new Thread(() -> {
            if (progressBar1.getProgress() < progressBar1.getMax()) {
                increase(progressBar1);
            }
            if (progressBar2.getProgress() < progressBar2.getMax()) {
                increase(progressBar2);
            }
            if (progressBar3.getProgress() < progressBar3.getMax()) {
                increase(progressBar3);
            }
            runOnUiThread(() -> button1.setEnabled(true));
        }).start();
    }

    private void startMultiThread() {
        threadFlag1 = 0;
        new Thread(() -> {
            if (progressBar1.getProgress() < progressBar1.getMax()) {
                increase(progressBar1);
            }
            runOnUiThread(() -> {
                threadFlag1 |= THREAD1_COMPLETE;
                enableButton2();
            });
        }).start();
        new Thread(() -> {
            if (progressBar2.getProgress() < progressBar2.getMax()) {
                increase(progressBar2);
            }
            runOnUiThread(() -> {
                threadFlag1 |= THREAD2_COMPLETE;
                enableButton2();
            });
        }).start();
        new Thread(() -> {
            if (progressBar3.getProgress() < progressBar3.getMax()) {
                increase(progressBar3);
            }
            runOnUiThread(() -> {
                threadFlag1 |= THREAD3_COMPLETE;
                enableButton2();
            });
        }).start();
    }

    private void enableButton2() {
        LogUtil.e("threadFlag1 : " + threadFlag1);
        if ((threadFlag1 ^ THREAD_ALL_COM) == 0) {
            button2.setEnabled(true);
        }
    }

    private void enableButton3() {
        LogUtil.e("threadFlag2 : " + threadFlag2);
        if ((threadFlag2 ^ THREAD_ALL_COM) == 0) {
            button3.setEnabled(true);
        }
    }

    public void startSyncThread() {
        threadFlag2 = 0;
        new Thread(() -> {
            while ((threadFlag2 & THREAD1_COMPLETE) != THREAD1_COMPLETE) {
                synchronized (mCompany) {
                    if (mCompany.progressBar1.getProgress() < mCompany.progressBar1.getMax()) {
                        mCompany.progressBar1.setProgress(mCompany.progressBar1.getProgress() + 1);
                    } else {
                        threadFlag2 |= THREAD1_COMPLETE;
                    }
                }
                try {
                    Thread.sleep(mRandom.nextInt(50) + 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(this::enableButton3);
        }).start();
        new Thread(() -> {
            while ((threadFlag2 & THREAD2_COMPLETE) != THREAD2_COMPLETE) {
                synchronized (mCompany) {
                    if (mCompany.progressBar2.getProgress() < mCompany.progressBar2.getMax()) {
                        mCompany.progressBar2.setProgress(mCompany.progressBar2.getProgress() + 1);
                    } else {
                        threadFlag2 |= THREAD2_COMPLETE;
                    }
                }
                try {
                    Thread.sleep(mRandom.nextInt(50) + 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(this::enableButton3);
        }).start();
        new Thread(() -> {
            while ((threadFlag2 & THREAD3_COMPLETE) != THREAD3_COMPLETE) {
                synchronized (mCompany) {
                    if (mCompany.progressBar3.getProgress() < mCompany.progressBar3.getMax()) {
                        mCompany.progressBar3.setProgress(mCompany.progressBar3.getProgress() + 1);
                    } else {
                        threadFlag2 |= THREAD3_COMPLETE;
                    }
                }
                try {
                    Thread.sleep(mRandom.nextInt(50) + 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(this::enableButton3);
        }).start();
    }

    private void increase(ProgressBar bar) {
        while (bar.getProgress() < bar.getMax()) {
            bar.setProgress(bar.getProgress() + 1);
            try {
                Thread.sleep(mRandom.nextInt(50) + 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void reset() {
        progressBar1.setProgress(0);
        progressBar2.setProgress(0);
        progressBar3.setProgress(0);
    }

    static class DataManager {
        private static DataManager mDataManager;

        public DataManager() {
        }

        public static DataManager getInstance() {
            return mDataManager == null ? mDataManager = new DataManager() : mDataManager;
        }

        public static void increase() {

        }

        public synchronized static void syncIncrease() {

        }

        public void add() {

        }

        public synchronized void syncAdd() {

        }
    }
}
