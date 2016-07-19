package com.lingyang.basedemo.config;


public class Const {
	
	public static final String USERTOKEN_FIRST = "2147550101_3356753920_1685865782_5e66341ab86fa3becec154f71dd4095f";
    public static final String USERTOKEN_SECOND = "2147550102_3356753920_1685865787_03373899bf20c3d64b1b34a656792b70";
    public static final String CONFIG  =  "[Config]\r\nIsDebug=1\r\nIsCaptureDev=1\r\nIsPlayDev=1\r\nIsSendBroadcast=0\r\nUdpSendInterval=2" +
	 		"\r\nSendPacketBufferLength=1408\r\nRecvPacketBufferLength=1408\r\n[Tracker]\r\nCount=3\r\nIP1=121.42.156.148\r\nPort1=80" +
	 		"\r\nIP2=182.254.149.39\r\nPort2=80\r\nIP3=203.195.157.248\r\nPort3=80\r\n[RealtimeModeConfig]\r\nLongConncettionServerIP=223.202.103.146" +
	 		"\r\nLongConncettionServerPort=8088\r\nDebugServerIP=120.24.56.51\r\nDebugServerPort=41234\r\nRealtimeModeConfigString=00000000000000\r\nPlayerDataBufferDelayLength=6" +
	 		"[LogServer]\r\nCount=1\r\nIP1=120.26.74.53\r\nPort1=80\r\n";


    public static final String KEY_OF_PLAYER_TYPE="PLAYER_TYPE";
    
//   protocolType：   1:私有； 2:rtmp； 3:录像
//   connectType（1：是推流端，上传媒体流； 2：是拉流端，取媒体流播放）
//   MODE: 1．使用QSUP协议推流，私有模式。
//         2.使用QSTP协议推流，公众模式
//         3.使用QSTP协议推流，私有模式，开启云存储
//         4.使用QSTP协议推流，公众模式，开启云存储
  
    //互联地址
    public static final String FACETIME_CALLING_USERTOKEN="2147550102_3356753920_1685865787_03373899bf20c3d64b1b34a656792b70";
    public static final String FACETIME_LISTENER_USERTOKEN="2147550101_3356753920_1685865782_5e66341ab86fa3becec154f71dd4095f";
    public static final String FACETIME_URL="topvdn://203.195.157.248:80?token=2147550101_3356753920_1685865782_5e66341ab86fa3becec154f71dd4095f&" +
    		"protocolType=1";
    
    //直播推流地址
    public static final String BROADCAST_URL = "topvdn://0.0.0.0:0?protocolType=2&connectType=1&mode=2&" +
    		"token=2147550101_3356753920_1685865782_5e66341ab86fa3becec154f71dd4095f";
    
    /**
     * 播放器
     */
    //公众摄像机直播观看地址（私有带云存储也是该格式） 格式暂定  rtmp6.public.topvdn.cn:1935是转发服务器地址
    public static final String CAMERA_PLAYER_URL = "rtmp://rtmp6.public.topvdn.cn/live/1003176_3356491776_1465286522_b31e8a48097732dc528053e7280ad711";
//    public static final String CAMERA_PLAYER_URL = "topvdn://rtmp6.public.topvdn.cn:1935?token=1003176&protocolType=2&connectType=2";
    
    //私有不带云存储摄像机直播观看地址 token是摄像头token  203.195.157.248:80调度服务器地址
//    public static final String PRIVATE_CAMERA_PLAYER_URL = "topvdn://203.195.157.248:80?token=1003182_3222536192_1467302400_b862e6a09c7c12022794a18aa61e71bb&"+
//    		"protocolType=1" ;
    //私有不带云存储摄像机直播观看地址 token是摄像头token  203.195.157.248:80调度服务器地址 亿播
    public static final String PRIVATE_CAMERA_PLAYER_URL = "topvdn://121.42.156.148:80?protocolType=1&"
    		+ "token=1073873030_3222274048_1468055289_d37af8d98e5cd53b7a7a2a90c7ec7bf8" ;
    
    //手机直播观看地址
    public static final String BROADCAST_LIVE_URL = "topvdn://rtmp1.public.topvdn.cn:1935?token=2147550101_3356753920_1685865782_5e66341ab86fa3becec154f71dd4095f&" +
    		"protocolType=2&connectType=2";
    
     //云存储观看地址 ，，client_token就是设备token，所有时间单位都是秒
//    public static final String RECORD_PLAYER_URL = "topvdn://public.topvdn.cn?protocolType=3&" +
//    		"token=1003639_3356753920_1466920474_d0063cb1050b05adb0fe69767ba95138&cid=1003639&" +
//    		"begin=1464317700&end=1464319828&play=1464317700";  
	
    public static final String RECORD_PLAYER_URL = "topvdn://183.57.151.177:1935?protocolType=3&connectType=2&token=1003155_3222536192_1493481600_1e1fbd38c4d26902a796d063b6b8f787&"
    		+ "cid=1003155&begin=1467388800&end=1467475200&play=1467388800";  
	
	

}
