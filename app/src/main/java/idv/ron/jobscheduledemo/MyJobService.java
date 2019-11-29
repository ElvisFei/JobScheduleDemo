package idv.ron.jobscheduledemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class MyJobService extends JobService {
    private static final String TAG = "TAG_MyJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob");
        startJob(params);
        return true; // 回傳false就不再呼叫onStopJob()，參看API文件
    }

    private void startJob(final JobParameters params) {
        new Thread() {
            @Override
            public void run() {
                for (int i = 1; i <= 10; i++) {
                    /* 即使呼叫JobScheduler.cancel()，已經啟動的任務仍舊執行，
                        但是可利用狀態變數強制停止現行任務 */
                    if (jobCancelled) {
                        return;
                    }
                    Log.d(TAG, "number " + i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.toString());
                    }
                }
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }.start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob");
        jobCancelled = true;
        return false;
    }

}
