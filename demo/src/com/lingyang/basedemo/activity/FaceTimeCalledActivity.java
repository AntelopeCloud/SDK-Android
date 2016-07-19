package com.lingyang.basedemo.activity;

import java.io.File;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lingyang.basedemo.R;
import com.lingyang.basedemo.config.Const;
import com.lingyang.basedemo.config.Constants;
import com.lingyang.basedemo.config.Utils;
import com.lingyang.sdk.CallBackListener;
import com.lingyang.sdk.av.SessionConfig;
import com.lingyang.sdk.cloud.AcceptMessageListener;
import com.lingyang.sdk.cloud.LYService;
import com.lingyang.sdk.exception.LYException;
import com.lingyang.sdk.facetime.LYFaceTime;
import com.lingyang.sdk.player.widget.LYPlayer;
import com.lingyang.sdk.player.widget.OnSnapshotListener;
import com.lingyang.sdk.util.CLog;
import com.lingyang.sdk.view.LYGLCameraEncoderView;


public class FaceTimeCalledActivity extends AppBaseActivity {


    /**
     * 互联api
     */
    LYFaceTime mLYFaceTime;
    /**
     * 本地预览view
     */
    private LYGLCameraEncoderView camera_preview;
    /**
     * 播放器view,用于观看对方视频画面
     */
    private LYPlayer playerview;
    /**
     * 本地采集预览的摄像机及音频配置文件
     */
    private SessionConfig mSessionConfig;
    
    Button btn_end,btn_snapshot;
    ToggleButton toggle_audio,toggle_local_audio,toggle_input_stream;

    private void initView() {
    	TextView title=(TextView) findViewById(R.id.tv_title);
    	title.setText("被叫方");
        camera_preview = (LYGLCameraEncoderView) findViewById(R.id.ly_preview);
        playerview = (LYPlayer) findViewById(R.id.ly_player);
        Button btn_start  = (Button) findViewById(R.id.btn_start);
        btn_end = (Button) findViewById(R.id.btn_end);
        Button btn_toogle_camera = (Button) findViewById(R.id.btn_toogle_camera);
        Button btn_toogle_flash = (Button) findViewById(R.id.btn_toogle_flash);
        btn_snapshot = (Button) findViewById(R.id.btn_snapshot);
        
        toggle_audio=(ToggleButton) findViewById(R.id.toggle_audio);
        toggle_local_audio=(ToggleButton) findViewById(R.id.toggle_lacal_audio);
        toggle_input_stream=(ToggleButton) findViewById(R.id.toggle_input_stream);
        
        btn_snapshot.setEnabled(false);
        btn_end.setEnabled(false);
        toggle_audio.setEnabled(false);
        toggle_local_audio.setEnabled(false);
        toggle_input_stream.setEnabled(false);

        findViewById(R.id.back).setOnClickListener(mClickListener);
        btn_start.setOnClickListener(mClickListener);
        btn_end.setOnClickListener(mClickListener);
        btn_toogle_camera.setOnClickListener(mClickListener);
        btn_toogle_flash.setOnClickListener(mClickListener);
        btn_snapshot.setOnClickListener(mClickListener);

        toggle_audio.setOnCheckedChangeListener(mChangeListener);
        toggle_local_audio.setOnCheckedChangeListener(mChangeListener);
        toggle_input_stream.setOnCheckedChangeListener(mChangeListener);
    }

    private void initData() {
    	//音视频和摄像机的初始化配置，用户可根据实际需要进行配置。
        mSessionConfig = new SessionConfig.Builder()
        .withVideoBitrate(512000)
        .withAudioSampleRateInHz(16000)//音频采样率
        .withVideoResolution(640, 480)
        .withDesireadCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)
        .withCameraDisplayOrientation(90)
        .withAudioChannels(1)//通道 1单通道 2双通道
        .useHardAudioEncode(false)
        .useHardVideoEncode(false)
        .useAudio(true)
        .useVideo(true)
        .build();
        mLYFaceTime = new LYFaceTime(this, mSessionConfig);
        
        //设置本地预览
        mLYFaceTime.setLocalPreview(camera_preview);
        //设置远程播放器
        mLYFaceTime.setRemoteView(null, playerview);
        
        LYService.getInstance().setCloudMessageListener(
                new AcceptMessageListener() {
                    @Override
                    public void accept(
                    		LYService.CloudMessage message) {
                        CLog.v(message.toString());
                        // 监听到对方连接断开的消息
                         if (message.Name.equals("ConnectionClosed")) {
                        	 runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     showToast("通话已挂断");
                                 }
                             });
                        	 
                        	 //断开连接，停止采集发送数据
                              mLYFaceTime.closeRemote(null);
                        }
                    }
                });
        

    }

    OnCheckedChangeListener mChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.toggle_audio:
                	 //控制远程音频
                    if (isChecked) {
                        mLYFaceTime.unmute(Const.FACETIME_URL);
                        showToast("开启音频播放");
                    } else {
                        mLYFaceTime.mute(Const.FACETIME_URL);
                        showToast("关闭音频播放");
                    }
                    break;
                case R.id.toggle_lacal_audio:
                    //控制本地音频
                    if (isChecked) {
                        mLYFaceTime.startAudioRecording();
                        showToast("开启本地音频采集");
                    } else {
                        mLYFaceTime.stopAudioRecording();
                        showToast("关闭本地音频采集");
                    }
                    break;
                case R.id.toggle_input_stream:
                    //控制视频流推送
                    if (isChecked) {
                        mLYFaceTime.startVideoRecording();
                        showToast("开启视频采集");
                    } else {
                        mLYFaceTime.stopVideoRecording();
                        showToast("关闭视频采集");
                    }
                    break;

                default:
                    break;
            }
        }
    };

  View.OnClickListener mClickListener = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
            	mHandler.obtainMessage(Constants.TaskState.ISRUNING).sendToTarget();
                // 被叫方：收到连接串主动发起连接的那一方，因为demo中没有连接后台，所以没有演示接受链接串功能
                //此功能实现是在默认已经接收到连接串的情况下实现的 
            	//从消息透传通道收到主叫方传过来的连接串，主动发起连接，连接成功自动开始推流
                mLYFaceTime.openRemote(Const.FACETIME_URL, new CallBackListener<Integer>() {
					
					@Override
					public void onSuccess(Integer t) {
						mHandler.obtainMessage(Constants.TaskState.SUCCESS).sendToTarget();
						// 连接成功
						runOnUiThread(new Runnable() {
							public void run() {
								showToast("连接成功,开始发送数据");
							        btn_snapshot.setEnabled(true);
							        btn_end.setEnabled(true);
							        toggle_audio.setEnabled(true);
							        toggle_local_audio.setEnabled(true);
							        toggle_input_stream.setEnabled(true);
							}
						});
						
					}
					@Override
					public void onError(final LYException exception) {
						mHandler.obtainMessage(Constants.TaskState.FAILURE).sendToTarget();
						// 连接失败
						 runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                            	 showToast( "连接失败"+exception.getCode()+exception.getMessage());
                             }
                         });
					}
				});
                
                break;
            case R.id.back:
        		finish();
        		break;
            case R.id.btn_end:
            	mLYFaceTime.closeRemote(null);
                break;
            case R.id.btn_toogle_camera:
            	//切换摄像头
                mLYFaceTime.switchCamera();
                break;
            case R.id.btn_toogle_flash:
            	//切换闪光灯
                mLYFaceTime.toggleFlash();
                break;
            case R.id.btn_snapshot:
            	//截图
            	final String snapPath = Utils.getInstance()
						.getSnapshotFile().getAbsolutePath();
    			playerview.snapshot(snapPath, System.currentTimeMillis()+"",new OnSnapshotListener() {
    				
    				@Override
    				public void onSnapshotSuccess(String arg0) {
    					Toast.makeText(FaceTimeCalledActivity.this, "截图成功", Toast.LENGTH_LONG).show();
    				}
    				
    				@Override
    				public void onSnapshotFail(LYException arg0) {
    					Toast.makeText(FaceTimeCalledActivity.this, "截图失败"+arg0.getMessage(), Toast.LENGTH_LONG).show();
    				}
    			} );
            	break;
        }
    }
};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_face_time_listener);
        initView();
        initData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mLYFaceTime.onHostActivityResumed();
        mLYFaceTime.startVideoRecording();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLYFaceTime.stopVideoRecording();
        mLYFaceTime.onHostActivityPaused();
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	mLYFaceTime.closeRemote(null);
    }

    @Override
    protected void onDestroy() {
    	mLYFaceTime.release();
    	 super.onDestroy();
       

    }
}
