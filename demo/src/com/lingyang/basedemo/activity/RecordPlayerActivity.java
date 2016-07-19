 package com.lingyang.basedemo.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lingyang.basedemo.R;
import com.lingyang.basedemo.R.layout;
import com.lingyang.basedemo.config.Const;
import com.lingyang.basedemo.config.Constants;
import com.lingyang.basedemo.config.Utils;
import com.lingyang.sdk.exception.LYException;
import com.lingyang.sdk.player.widget.LYPlayer;
import com.lingyang.sdk.player.widget.OnClosedListener;
import com.lingyang.sdk.player.widget.OnPlayProgressListener;
import com.lingyang.sdk.player.widget.OnPreparedListener;
import com.lingyang.sdk.player.widget.OnSeekCompleteListener;
import com.lingyang.sdk.player.widget.OnSnapshotListener;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RecordPlayerActivity extends AppBaseActivity {
	LYPlayer mPlayer;
	Button mStartBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_player);
		init();
	}
	
	View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_start:
				// 开始播放
				mHandler.obtainMessage(Constants.TaskState.ISRUNING).sendToTarget();
				mPlayer.start();
				break;
			case R.id.btn_end:
				// 结束播放
				mHandler.obtainMessage(Constants.TaskState.ISRUNING).sendToTarget();
				mPlayer.stop();
				break;
			case R.id.back:
				finish();
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
			case R.id.btn_gettimes:
				int time=mPlayer.getDuration();
				SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
				String times=format.format(time);
				showToast("时长："+times);
				break;
			case R.id.btn_getpos:
				showToast("当前位置："+mPlayer.getCurrentPosition());
				break;
			case R.id.btn_seek:
				//调整播放时间 ,传参是相对时间
//				if(!(mPlayer.getCurrentPosition()+30000>mPlayer.getDuration()))
					mPlayer.seekTo(300);
//				mPlayer.seekTo(mPlayer.getCurrentPosition()+30000);
				break;
			}
		}
	};
	
	private void init() {
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText("观看云存储");
		mPlayer = (LYPlayer) findViewById(R.id.ly_player);
		Button snapShot = (Button) findViewById(R.id.btn_snapshot);
		Button end = (Button) findViewById(R.id.btn_end);
		Button getTimes = (Button) findViewById(R.id.btn_gettimes);
		Button getPos = (Button) findViewById(R.id.btn_getpos);
		Button seek = (Button) findViewById(R.id.btn_seek);

		mStartBtn = (Button) findViewById(R.id.btn_start);

		mStartBtn.setOnClickListener(mClickListener);
		findViewById(R.id.back).setOnClickListener(mClickListener);
		snapShot.setOnClickListener(mClickListener);
		end.setOnClickListener(mClickListener);
		getPos.setOnClickListener(mClickListener);
		getTimes.setOnClickListener(mClickListener);
		seek.setOnClickListener(mClickListener);
		
		mPlayer.setDataSource(Const.RECORD_PLAYER_URL);
		//播放进度回调
		mPlayer.setOnPlayProgressListener(new OnPlayProgressListener() {
			
			@Override
			public void onPlayProgress(int playProgress) {
				// playProgress 当前播放位置(相对时间)
				
			}
		});
		//seek结果监听
		mPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
			
			@Override
			public void onSeekSuccess(int time) {
				// TODO Auto-generated method stub
				showToast("耗时：+"+time);
			}

			@Override
			public void onSeekError(LYException exception) {
				// TODO Auto-generated method stub
				
			}
		});
		
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
