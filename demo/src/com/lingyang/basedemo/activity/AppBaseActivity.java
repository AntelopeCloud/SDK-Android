package com.lingyang.basedemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.lingyang.basedemo.config.Const;
import com.lingyang.basedemo.config.Constants;
import com.lingyang.sdk.CallBackListener;
import com.lingyang.sdk.cloud.LYService;
import com.lingyang.sdk.exception.LYException;
import com.lingyang.sdk.util.CLog;


public class AppBaseActivity extends FragmentActivity {

    protected Handler mHandler;
    protected ProgressDialog mProgressDialog;
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppBaseActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        LYService.getInstance().setDebuggable(true);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("请稍候");
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.TaskState.ISRUNING:
                        if (!mProgressDialog.isShowing() && !isFinishing())
                            mProgressDialog.show();
                        break;
                    case Constants.TaskState.PAUSE:
                        if (mProgressDialog.isShowing() && !isFinishing())
                            mProgressDialog.dismiss();
                        break;
                    case Constants.TaskState.SUCCESS:
                        if (mProgressDialog.isShowing() && !isFinishing())
                            mProgressDialog.dismiss();
                        break;
                    case Constants.TaskState.FAILURE:
                        if (mProgressDialog.isShowing() && !isFinishing())
                            mProgressDialog.dismiss();
                        break;
                    case Constants.TaskState.EXCEPITON:
                        if (mProgressDialog.isShowing() && !isFinishing())
                            mProgressDialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
    }
    /**
     * 开启云服务
     */
    public void startCloudServiceWithFacetime(String userToken){
        LYService.getInstance().startCloudService(
        		userToken
                , Const.CONFIG
                , new CallBackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        CLog.v("long-" + aLong);
                        showToast("登录成功"+LYService.getInstance().isOnline());
                        
                        startCloudServiceAfter(true);
                    }

                    @Override
                    public void onError(LYException exception) {
                        CLog.v("exception-" + exception.toString());
                        showToast("登录失败"+exception.getMessage());
                        startCloudServiceAfter(false);
                    }
                });
    }
    
    /**
     * 开启云服务后的操作
     * @param isStartSuccess 是否成功开启云服务
     */
    void startCloudServiceAfter(boolean isStartSuccess){};

    @Override
    protected void onDestroy() {
    	super.onDestroy();
      
        //退出是要关闭云服务，节省资源
        LYService.getInstance().stopCloudService();
        
    }

}
