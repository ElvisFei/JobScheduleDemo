package idv.ron.jobscheduledemo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class MainFragment extends Fragment {
    private static final String TAG = "TAG_MainFragment";
    private static final int JOB_ID = 101;
    private Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 點擊Schedule Job按鈕
        view.findViewById(R.id.btScheduleJob).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName componentName = new ComponentName(activity, MyJobService.class);
                JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                        // 充電時才啟動
                        .setRequiresCharging(true)
                        // 非計費網路連線時才啟動
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                        // 設定持續執行的間隔時間大約為15分鐘，無法精準控制何時啟動
                        .setPeriodic(AlarmManager.INTERVAL_FIFTEEN_MINUTES)
                        // 設備重開機後是否啟動，如果是要在manifest檔案加上permission RECEIVE_BOOT_COMPLETED
                        .setPersisted(true)
                        // 設備是否處於休眠狀態才啟動
                        .setRequiresDeviceIdle(false)
                        .build();
                JobScheduler jobScheduler = (JobScheduler) activity.getSystemService(JOB_SCHEDULER_SERVICE);
                if (jobScheduler != null) {
                    int resultCode = jobScheduler.schedule(jobInfo);
                    if (resultCode == JobScheduler.RESULT_SUCCESS) {
                        Log.d(TAG, "Job scheduled");
                    } else {
                        Log.d(TAG, "Job scheduling failed");
                    }
                }
            }
        });

        // 點擊Cancel Job按鈕
        view.findViewById(R.id.btCancelJob).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobScheduler jobScheduler = (JobScheduler) activity.getSystemService(JOB_SCHEDULER_SERVICE);
                if (jobScheduler != null) {
                    jobScheduler.cancel(JOB_ID);
                    Log.d(TAG, "Job cancelled");
                }
            }
        });

    }
}
