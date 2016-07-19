package com.lingyang.basedemo.activity;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lingyang.base.utils.MD5Util;
import com.lingyang.basedemo.R;
import com.lingyang.basedemo.config.Const;
import com.lingyang.basedemo.config.Utils;
import com.lingyang.sdk.cloud.LYService;


public class MainActivity extends AppBaseActivity {

	private final int FACETIME_OF_CALLING_SIDE_OF_VALUE=01; //互联主叫方
	
	private final int FACETIME_OF_LISTENER_SIDE_OF_VALUE=02; //互联被叫方
	
	private final int LIVE_BROADCAST_OF_VALUE=03; //设备广播
	
	public static final int PLAYER_OF_PUBLIC_CAMERA_OF_VALUE=04; //公众摄像机播放
	
	public static final int PLAYER_OF_PRIVATE_CAMERA_OF_VALUE=05; //私有摄像机播放
	
	public static final int PLAYER_OF_BROADCAST_OF_VALUE=06; //设备广播播放
	
	private final int PLAYER_OF_RECORD_CAMERA_OF_VALUE=07; //云存储播放
	
	private int mType;
	private LinearLayout mOptionsLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Utils.setContext(this);
		initView();
	}

	View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_facetime:
				// 互联
				showSelectDialog();
				break;
			case R.id.tv_broadcast:
				// 设备直播
				mType=LIVE_BROADCAST_OF_VALUE;
				startCloudServiceWithFacetime(Const.USERTOKEN_SECOND);
				break;
			case R.id.tv_public_live_player:
				// 观看公众摄像机直播
				mType=PLAYER_OF_PUBLIC_CAMERA_OF_VALUE;
				startCloudServiceWithFacetime(Const.USERTOKEN_FIRST);
				break;
			case R.id.tv_private_live_player:
				// 观看私有摄像机直播
				mType=PLAYER_OF_PRIVATE_CAMERA_OF_VALUE;
				startCloudServiceWithFacetime(Const.USERTOKEN_FIRST);
				break;
			case R.id.tv_broadcast_player:
				// 观看设备广播
				mType=PLAYER_OF_BROADCAST_OF_VALUE;
				startCloudServiceWithFacetime(Const.USERTOKEN_FIRST);
				break;
			case R.id.tv_record_player:
				// 观看云存储
				mType=PLAYER_OF_RECORD_CAMERA_OF_VALUE;
				startCloudServiceWithFacetime(Const.USERTOKEN_FIRST);
				break;
			}

		}
	};

	private void showSelectDialog() {
		new AlertDialog.Builder(this)
				.setTitle("选择登录类型")
				.setItems(new CharSequence[] { "主叫方", "被叫方" },
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									mType=FACETIME_OF_CALLING_SIDE_OF_VALUE;
									 startCloudServiceWithFacetime(Const.FACETIME_LISTENER_USERTOKEN);
								} else {
									mType=FACETIME_OF_LISTENER_SIDE_OF_VALUE;
									startCloudServiceWithFacetime(Const.FACETIME_CALLING_USERTOKEN);
								}
								dialog.cancel();
							}
						}).create().show();
	}

	private void initView() {
		Log.v("md5String-", "md5--" + MD5Util.getMD5String("666666"));
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText("功能展示Demo");
		TextView facetime = (TextView) findViewById(R.id.tv_facetime);
		TextView broadcast = (TextView) findViewById(R.id.tv_broadcast);
		TextView publicCameraPlayer = (TextView) findViewById(R.id.tv_public_live_player);
		TextView privateCameraPlayer = (TextView) findViewById(R.id.tv_private_live_player);
		TextView recordPlayer = (TextView) findViewById(R.id.tv_record_player);
		TextView broadcastPlayer = (TextView) findViewById(R.id.tv_broadcast_player);
		findViewById(R.id.back).setVisibility(View.GONE);
		
		mOptionsLayout=(LinearLayout) findViewById(R.id.layout_player_options);

		findViewById(R.id.back).setOnClickListener(mClickListener);
		facetime.setOnClickListener(mClickListener);
		broadcast.setOnClickListener(mClickListener);
		publicCameraPlayer.setOnClickListener(mClickListener);
		privateCameraPlayer.setOnClickListener(mClickListener);
		broadcastPlayer.setOnClickListener(mClickListener);
		recordPlayer.setOnClickListener(mClickListener);
	}
	
	@Override
	void startCloudServiceAfter(boolean isStartSuccess) {
		super.startCloudServiceAfter(isStartSuccess);
		if (isStartSuccess) {
			Intent intent=null;
			switch (mType) {
			case FACETIME_OF_CALLING_SIDE_OF_VALUE:
				// 主叫方
				 intent = new Intent(MainActivity.this,
						FaceTimeCallActivity.class);
				startActivity(intent);
				break;
			case FACETIME_OF_LISTENER_SIDE_OF_VALUE:
				// 被叫方
				 intent = new Intent(MainActivity.this,
						 FaceTimeCalledActivity.class);
				startActivity(intent);
				break;
			case LIVE_BROADCAST_OF_VALUE:
				// 设备直播
				intent = new Intent(MainActivity.this,
						BroadcastLiveActivity.class);
				startActivity(intent);
				break;
			case PLAYER_OF_PUBLIC_CAMERA_OF_VALUE:
				// 公众摄像机播放
				intent = new Intent(MainActivity.this,
						LivePlayerActivity.class);
				intent.putExtra(Const.KEY_OF_PLAYER_TYPE, PLAYER_OF_PUBLIC_CAMERA_OF_VALUE);
				startActivity(intent);
				break;
			case PLAYER_OF_PRIVATE_CAMERA_OF_VALUE:
				// 私有摄像机播放
				intent = new Intent(MainActivity.this,
						LivePlayerActivity.class);
				intent.putExtra(Const.KEY_OF_PLAYER_TYPE, PLAYER_OF_PRIVATE_CAMERA_OF_VALUE);
				startActivity(intent);
				break;
			case PLAYER_OF_BROADCAST_OF_VALUE:
				// 设备直播播放
				intent = new Intent(MainActivity.this,
						LivePlayerActivity.class);
				intent.putExtra(Const.KEY_OF_PLAYER_TYPE, PLAYER_OF_BROADCAST_OF_VALUE);
				startActivity(intent);
				break;
			case PLAYER_OF_RECORD_CAMERA_OF_VALUE:
				// 云存储播放
				intent = new Intent(MainActivity.this,
						RecordPlayerActivity.class);
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	}
}
