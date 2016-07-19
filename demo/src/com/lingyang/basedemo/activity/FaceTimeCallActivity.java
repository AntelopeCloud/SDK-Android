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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lingyang.basedemo.R;
import com.lingyang.basedemo.config.Const;
import com.lingyang.basedemo.config.Utils;
import com.lingyang.sdk.CallBackListener;
import com.lingyang.sdk.av.SessionConfig;
import com.lingyang.sdk.cloud.AcceptMessageListener;
import com.lingyang.sdk.cloud.LYService;
import com.lingyang.sdk.exception.LYException;
import com.lingyang.sdk.facetime.IFaceTime;
import com.lingyang.sdk.facetime.LYFaceTime;
import com.lingyang.sdk.player.widget.LYPlayer;
import com.lingyang.sdk.player.widget.OnSnapshotListener;
import com.lingyang.sdk.util.CLog;
import com.lingyang.sdk.view.LYGLCameraEncoderView;


public class FaceTimeCallActivity extends AppBaseActivity {

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
    	title.setText("主叫方");
        camera_preview = (LYGLCameraEncoderView) findViewById(R.id.ly_preview);
        playerview = (LYPlayer) findViewById(R.id.ly_player);
        Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setBackgroundResource(R.color.colorLightOrange);
        btn_start.setEnabled(false);
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
        btn_end.setOnClickListener(mClickListener);
        btn_toogle_camera.setOnClickListener(mClickListener);
        btn_toogle_flash.setOnClickListener(mClickListener);
        btn_snapshot.setOnClickListener(mClickListener);

        toggle_audio.setOnCheckedChangeListener(mChangeListener);
        toggle_local_audio.setOnCheckedChangeListener(mChangeListener);
        toggle_input_stream.setOnCheckedChangeListener(mChangeListener);
        
    }

    private void initData() {
    	
    	 mSessionConfig = new SessionConfig.Builder()
         .withVideoBitrate(512000)//码率
         .withAudioSampleRateInHz(16000)//音频采样率
         .withVideoResolution(640, 480)//分辨率
         .withDesireadCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)
         .withCameraDisplayOrientation(90)//旋转角度
         .withAudioChannels(1)//通道 1单通道 2双通道
         .useHardAudioEncode(false)//是否音频硬编
         .useHardVideoEncode(false)
         .useAudio(true)//是否开启音频
         .useVideo(true)
         .build();
        mLYFaceTime = new LYFaceTime(this, mSessionConfig);
        //设置预览
        mLYFaceTime.setLocalPreview(camera_preview);
      //设置播放器
        mLYFaceTime.setRemoteView(null, playerview);
        
        /**
         * 监听是否开始互联成功
         */
        mLYFaceTime.setCallBackListener(new CallBackListener<Integer>() {

			@Override
			public void onSuccess(Integer t) {
				showToast("开始互联");
			        btn_snapshot.setEnabled(true);
			        btn_end.setEnabled(true);
			        toggle_audio.setEnabled(true);
			        toggle_local_audio.setEnabled(true);
			        toggle_input_stream.setEnabled(true);
			}

			@Override
			public void onError(LYException exception) {
				showToast("互联失败");
			}
		});
        
        //主叫方：发送连接串被连接的那一方，因为demo中没有连接后台，所以没有演示发送链接串功能
        //此功能实现是在默认已经发送连接串的情况下实现的 
        // 收到消息透传通道ConnectionAcceptted消息，表示对方已建立连接,可以进行推流和观看对方视频等操作;
        //                ConnectionClosed消息表示对方已挂断，连接已断开，可进行关闭退出或重置重连等操作;
        LYService.getInstance().setCloudMessageListener(
                        new AcceptMessageListener() {
                            @Override
                            public void accept(
                            		LYService.CloudMessage message) {
                                CLog.v(message.toString());
                                // 监听到对方连接成功的消息
                                if (message.Name.equals("ConnectionAcceptted")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                        	 showToast("接受对方连接成功");
                                        }
                                    });
                                }
                                // 监听到对方连接断开的消息
                                else if (message.Name.equals("ConnectionClosed")) {
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

    CompoundButton.OnCheckedChangeListener mChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            switch (buttonView.getId()) {
                case R.id.toggle_audio:
                    //控制远程音频
                    if (isChecked) {
                        mLYFaceTime.unmute(null);
                        showToast("开启音频播放");
                    } else {
                        mLYFaceTime.mute(null);
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
            	case R.id.back:
            		finish();
            		break;
            	case R.id.btn_end:
                    mLYFaceTime.closeRemote(null);
                    break;
                case R.id.btn_toogle_camera:
                    mLYFaceTime.switchCamera();
                    break;
                case R.id.btn_toogle_flash:
                    mLYFaceTime.toggleFlash();
                    break;
                case R.id.btn_snapshot:
                	//截图,snapPath必须是已经被创建的文件夹的路径
                	final String snapPath = Utils.getInstance()
    						.getSnapshotFile().getAbsolutePath();
        			playerview.snapshot(snapPath, System.currentTimeMillis()+"",new OnSnapshotListener() {
        				
        				@Override
        				public void onSnapshotSuccess(String arg0) {
        					// TODO Auto-generated method stub
        					 showToast("截图成功");
        				}
        				
        				@Override
        				public void onSnapshotFail(LYException arg0) {
        					// TODO Auto-generated method stub
        					showToast("截图失败");
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
        setContentView(R.layout.activity_face_time_calling);
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