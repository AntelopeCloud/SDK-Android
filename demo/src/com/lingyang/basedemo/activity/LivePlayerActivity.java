package com.lingyang.basedemo.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lingyang.basedemo.R;
import com.lingyang.basedemo.config.Const;
import com.lingyang.basedemo.config.Constants;
import com.lingyang.basedemo.config.Utils;
import com.lingyang.sdk.exception.LYException;
import com.lingyang.sdk.player.IMediaParamProtocol;
import com.lingyang.sdk.player.widget.LYPlayer;
import com.lingyang.sdk.player.widget.OnClosedListener;
import com.lingyang.sdk.player.widget.OnErrorListener;
import com.lingyang.sdk.player.widget.OnLocalRecordListener;
import com.lingyang.sdk.player.widget.OnPreparedListener;
import com.lingyang.sdk.player.widget.OnSnapshotListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LivePlayerActivity extends AppBaseActivity {

	private LYPlayer mPlayer;
	private int mType;
	private Button mStartBtn,mTalkBtn;

	View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.btn_start:
				mHandler.obtainMessage(Constants.TaskState.ISRUNING).sendToTarget();
				// 开始,观看直播不能暂停
				if(mPlayer.isPlaying()){
					showToast("正在播放");
					return;
				}
				setDataResource();
				mPlayer.start();
				break;
			case R.id.btn_end:
				mHandler.obtainMessage(Constants.TaskState.SUCCESS).sendToTarget();
				// 结束直播
				mPlayer.stop();
				break;
			case R.id.btn_snapshot:
				// 截图
				final String name = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
						.format(new Date(System.currentTimeMillis()))
						+ "_"
						+ System.currentTimeMillis() % 1000;
				final String snapPath = Utils.getInstance()
						.getSnapshotFile().getAbsolutePath();
				mPlayer.snapshot(snapPath, name, new OnSnapshotListener() {

					@Override
					public void onSnapshotSuccess(String snapPath) {
						showToast("截图成功"+snapPath);
					}

					@Override
					public void onSnapshotFail(LYException e) {
						showToast("截图失败"+e.getMessage());
					}
				});
				break;
			}
		}
	};
	/**
	 * 录像状态监听
	 */
OnLocalRecordListener mLocalRecordListener = new OnLocalRecordListener() {
	
	@Override
	public void onRecordSizeChange(long size, long time) {
		showToast("正在录像"+time);
	}
	
	@Override
	public void onRecordError(LYException arg0) {
		showToast("录像出错"+arg0.getCode()+"--"+arg0.getMessage());
	}

	@Override
	public void onRecordStart() {
		showToast("开始录像");
	}

	@Override
	public void onRecordStop() {
		showToast("结束录像");
		
	}
};
	OnCheckedChangeListener mChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			switch (buttonView.getId()) {
			case R.id.toggle_record:
				if (isChecked && mPlayer.isPlaying()) {
//					// 开始本地录像
					final long timestamp = System.currentTimeMillis();
			        Date date = new Date(timestamp);
			        final String name = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
			                .format(date) + "_" + System.currentTimeMillis() % 1000;
			        final String recordPath = Utils.getInstance().getRecordFile()
			                .getAbsolutePath()
			                + "/" + name + ".mp4";
			        mPlayer.startLocalRecord(recordPath);
					
				} else {
					// 结束本地录像
					mPlayer.stopLocalRecord();
				}
				break;

			default:
				break;
			}
		}
	};
	
    public void showParams(){
    	showToast("缓冲区延时："+mPlayer.getMediaParam(IMediaParamProtocol.STREAM_MEDIA_PARAM_BUFFER_DELAY)
    			+"缓冲时长："+mPlayer.getMediaParam(IMediaParamProtocol.STREAM_MEDIA_PARAM_BUFFER_TIME)
    			+"当前视频下载次数："+mPlayer.getMediaParam(IMediaParamProtocol.STREAM_MEDIA_PARAM_VIDEO_DOWNLOADSPEED));
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_broadcast_player);
		Utils.setContext(this);
		init();   
	}
	
	 /**
     * 长按说话
     */
    View.OnTouchListener mOnTalkListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
            	mTalkBtn.setBackgroundResource(R.color.colorHalfOrange);
            	mPlayer.startTalk();
                return false;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
            	mTalkBtn.setBackgroundResource(R.color.colorOrange);
            	mPlayer.stopTalk();
                return true;
            }
            return false;
        }
    };

	private void init() {
		TextView title = (TextView) findViewById(R.id.tv_title);
		mPlayer = (LYPlayer) findViewById(R.id.ly_player);
		Button snapShot = (Button) findViewById(R.id.btn_snapshot);
		Button end = (Button) findViewById(R.id.btn_end);

	    mStartBtn = (Button) findViewById(R.id.btn_start);
		ToggleButton toggleRecord = (ToggleButton) findViewById(R.id.toggle_record);
		mTalkBtn = (Button) findViewById(R.id.btn_talk);

		mTalkBtn.setOnTouchListener(mOnTalkListener);
		toggleRecord.setOnCheckedChangeListener(mChangeListener);

		findViewById(R.id.back).setOnClickListener(mClickListener);
		snapShot.setOnClickListener(mClickListener);
		end.setOnClickListener(mClickListener);
		mStartBtn.setOnClickListener(mClickListener);
		
		
		mType=getIntent().getIntExtra(Const.KEY_OF_PLAYER_TYPE, MainActivity.PLAYER_OF_PUBLIC_CAMERA_OF_VALUE);
		switch (mType) {
		case MainActivity.PLAYER_OF_PUBLIC_CAMERA_OF_VALUE:
			title.setText("观看公众摄像机直播");
			break;
		case MainActivity.PLAYER_OF_PRIVATE_CAMERA_OF_VALUE:
			title.setText("观看私有摄像机直播");
			mTalkBtn.setVisibility(View.GONE);
			break;
		case MainActivity.PLAYER_OF_BROADCAST_OF_VALUE:
			title.setText("观看设备直播");
			mTalkBtn.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		
		/**
		 * 所有连接完成，开始播放监听
		 */
		mPlayer.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(int time) {
				mHandler.obtainMessage(Constants.TaskState.SUCCESS).sendToTarget();
				//time 耗时
				runOnUiThread(new Runnable() {
					public void run() {
						mStartBtn.setText("正在播放");
						mStartBtn.setBackgroundResource(R.color.colorLightOrange);
					}
				});
			}
		});
		/**
		 * 关闭播放器监听
		 */
		mPlayer.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
				mHandler.obtainMessage(Constants.TaskState.SUCCESS).sendToTarget();
				runOnUiThread(new Runnable() {
					public void run() {
						mStartBtn.setText("播放");
						mStartBtn.setBackgroundResource(R.color.colorOrange);
					}
				});
				
			}
		});
		/**
		 * 播放发生错误时回调
		 */
		mPlayer.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(int code, String msg) {
				mHandler.obtainMessage(Constants.TaskState.FAILURE).sendToTarget();
				showToast(code+"-"+msg);
				return false;
			}
		});
		mPlayer.setLocalRecordListener(mLocalRecordListener);
	} 
	
	private void setDataResource(){
		switch (mType) {
		case MainActivity.PLAYER_OF_PUBLIC_CAMERA_OF_VALUE:
			mPlayer.setDataSource(Const.CAMERA_PLAYER_URL);//公众摄像机直播观看
			break;
		case MainActivity.PLAYER_OF_PRIVATE_CAMERA_OF_VALUE:
			mPlayer.setDataSource(Const.PRIVATE_CAMERA_PLAYER_URL);//私有摄像机直播观看
			break;
		case MainActivity.PLAYER_OF_BROADCAST_OF_VALUE:
			mPlayer.setDataSource(Const.BROADCAST_LIVE_URL);//设备直播观看
			break;
		default:
			break;
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mPlayer.pauseToBackground();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		 super.onResume();
		 mPlayer.resumeFromBackground();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mPlayer .isPlaying() ) {
			mPlayer.stop();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}
}
