package com.lingyang.basedemo.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lingyang.basedemo.R;
import com.lingyang.basedemo.config.Const;
import com.lingyang.basedemo.config.Constants;
import com.lingyang.sdk.av.SessionConfig;
import com.lingyang.sdk.broadcast.BroadcastListener;
import com.lingyang.sdk.broadcast.ILiveBroadcast;
import com.lingyang.sdk.broadcast.LYLiveBroadcast;
import com.lingyang.sdk.cloud.LYService;
import com.lingyang.sdk.exception.LYException;
import com.lingyang.sdk.view.LYGLCameraEncoderView;
import com.lingyang.sdk.view.LYGLCameraView;
import com.lingyang.sdk.view.SurfaceFrameShape;

public class BroadcastLiveActivity extends AppBaseActivity {

	private LYLiveBroadcast mLiveBroadcast;
	private SessionConfig mSessionConfig;
	private Button mStartBtn;
	 private LYGLCameraEncoderView mPreview;

	View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_start:
				mHandler.obtainMessage(Constants.TaskState.ISRUNING).sendToTarget();
				//开始直播
				mLiveBroadcast.startBroadcasting(Const.BROADCAST_URL);
				break;
			case R.id.btn_end:
				mHandler.obtainMessage(Constants.TaskState.ISRUNING).sendToTarget();
				// 结束直播
				mLiveBroadcast.stopBroadcasting();//结束直播
				break;
			case R.id.back:
				finish();
				break;
			case R.id.btn_togglecamera:
				// 切换摄像头
				mLiveBroadcast.switchCamera();
				break;
			case R.id.btn_toggleflash:
				// 切换闪光灯
				mLiveBroadcast.toggleFlash();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broadcast_live);
		//音视频和摄像机的初始化配置，用户可根据实际需要进行配置。
		mSessionConfig = new SessionConfig.Builder()
		.withVideoBitrate(512000)
		.withAudioSampleRateInHz(16000)// 音频采样率
		.withVideoResolution(640, 480)
		.withDesireadCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)
		.withCameraDisplayOrientation(90)
		.withAudioChannels(1)		// 通道 1单通道 2双通道
		.useHardAudioEncode(false).useHardVideoEncode(false)
		.useAudio(true)
		.useVideo(true)
		.withAudioBitrate(16000)// 音频码率
		.build();
		mLiveBroadcast = new LYLiveBroadcast(this, mSessionConfig);
		init();
	}

	private void init() {
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText("直播");
		mPreview = (LYGLCameraEncoderView) findViewById(R.id.ly_preview);
		mStartBtn = (Button) findViewById(R.id.btn_start);
		Button end = (Button) findViewById(R.id.btn_end);
		Button toggleCamera = (Button) findViewById(R.id.btn_togglecamera);
		Button toggleFlash = (Button) findViewById(R.id.btn_toggleflash);

		findViewById(R.id.back).setOnClickListener(mClickListener);
		mStartBtn.setOnClickListener(mClickListener);
		end.setOnClickListener(mClickListener);
		toggleCamera.setOnClickListener(mClickListener);
		toggleFlash.setOnClickListener(mClickListener);
		
		//设置圆形预览框，默认四边形
		mPreview.setShape(SurfaceFrameShape.CIRCLE);
		//将预览view提到视图最上层，在圆形预览和三角形预览时必须调用此方法才能看到预览下层的视图
		mPreview.setZOrderOnTop(true);
		
		//设置本地预览
		mLiveBroadcast.setLocalPreview(mPreview);
		
		mLiveBroadcast.setBroadcastListener(new BroadcastListener() {
					@Override
					public void onBroadcastStart() {
						showToast("马上开始直播");
					}

					@Override
					public void onBroadcastLive() {
						mHandler.obtainMessage(Constants.TaskState.SUCCESS).sendToTarget();
						showToast("正在直播");
						mStartBtn.setText("正在直播");
						mStartBtn.setBackgroundResource(R.color.colorLightOrange);
					}

					@Override
					public void onBroadcastStop() {
						mHandler.obtainMessage(Constants.TaskState.SUCCESS).sendToTarget();
						showToast("停止直播");
						mStartBtn.setText("开始直播");
						mStartBtn.setBackgroundResource(R.color.colorOrange);
					}

					@Override
					public void onBroadcastError(LYException exception) {
						mHandler.obtainMessage(Constants.TaskState.EXCEPITON).sendToTarget();
						showToast("直播出错" + exception.getCode() + "--"
								+ exception.getMessage());
					}
				});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mLiveBroadcast.onHostActivityPaused();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mLiveBroadcast.onHostActivityResumed();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//结束直播
		mLiveBroadcast.stopBroadcasting();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//资源释放
		mLiveBroadcast.release();
		
	}
}
