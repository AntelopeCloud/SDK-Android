package com.lingyang.basedemo.config;

/**
 * 系统常量
 */
public final class Constants {


	/*
	 * HTTP常量
	 */
	public static final class Http {
		/**
		 * 访问正确
		 */
		public static final int STATUS_CODE_SUCESS = 200;
		/**
		 * 访问不存在的页面
		 */
		public static final int STATUS_CODE_URI_NOT_EXSITS = 404;

		/**
		 * 系统错误
		 */
		public static final int ERROR_CODE_SYSTEM_ERROR = 10001;

		/**
		 * 参数错误
		 */
		public static final int ERROR_CODE_NOT_PARAM_ERROR = 20001;

		/**
		 * no permission access_token错误
		 */
		public static final int ERROR_CODE_NO_PERMISSION = 20010;

		/**
		 * 密码错误
		 */
		public static final int ERROR_CODE_LOGIN_PWD_ERROR = 20011;

		/**
		 * 用户名不存在
		 */
		public static final int ERROR_CODE_USER_NOT_EXSIT = 20012;

		/**
		 * 用户名已存在
		 */
		public static final int ERROR_CODE_USER_EXSIT = 20013;

		/**
		 * 密码格式错误
		 */
		public static final int ERROR_CODE_PWD_FORMAT_ERROR = 20014;
		/**
		 * 账户已经激活
		 */
		public static final int ERROR_CODE_ACTIVATE_ACCOUNT = 20015;

		/**
		 * 账户激活已过期
		 */
		public static final int ERROR_CODE_ACTIVATE_EXPIRED = 20016;

		/**
		 * activate_token错误
		 */
		public static final int ERROR_CODE_TOKEN_ERROR = 20017;
		/**
		 * 账户未激活
		 */
		public static final int ERROR_CODE_LOGIN_ACCOUNT_NOT_ACTIVATED = 20018;
		/**
		 * 账户未激活
		 */
		public static final int ERROR_CODE_DEVICE_ALREADY_ADDED = 20021;

		/**
		 * URI 错误
		 */
		public static final int ERROR_CODE_NOT_EXSITS_URI = 30001;


	}

	/*
	 * 任务运行状态
	 */
    public static final class TaskState {
        public static final int SUCCESS = 0x1111;// 任务成功
        public static final int FAILURE = 0x1112;// 任务失败
        public static final int ISRUNING = 0x1113;// 任务正在运行
        public static final int PAUSE = 0x1114;// 任务暂停
        public static final int EXCEPITON = 0x1115;// 任务异常
    }
}
