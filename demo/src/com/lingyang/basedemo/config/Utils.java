package com.lingyang.basedemo.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.lingyang.base.utils.CLog;

public class Utils {

	private static Object lock = new Object();

	private static File mSDCacheDir;// 临时数据存放地
	private static File mCacheDir;// 数据存放地
	private static File tempZipFile;// 临时数据
	private File localZipFile;// 数据
	private File eventFile;//
	private File devicesFile;//
	private File snapshotFile;
	private File recordFile, eventThumbFile;
	private File logFile;
	private File userFile;
	private static Utils Utils;

	private static Context mContext;

	public static void setContext(Context context){
		mContext=context;
	}

	private Utils() {
	}

	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(float dpValue) {  
        final float scale = mContext.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
    
	public static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	public static Utils getInstance() {
		if (Utils == null) {
			Utils = new Utils();
		}
		return Utils;
	}

	public static void writeFile(File file, byte[] content, boolean append)
			throws IOException {
		FileOutputStream psuhOutputStream = new FileOutputStream(file, append);
		psuhOutputStream.write(content);
		psuhOutputStream.close();
	}

	public static String readStringFile(File file) throws IOException {
		return new String(readFile(file));
	}

	public static byte[] readFile(File file) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = fis.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		byte[] result = baos.toByteArray();
		fis.close();
		baos.close();
		return result;
	}

	public static File createFile(File dir, String name) throws IOException {
		return createFile(dir.getPath() + "/" + name);
	}

	public static File createFile(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	public static void deleteFile(File file) {
		if (file.exists())
			file.delete();
	}

	public static void EventCacheManagement(File eventFile) {
		if (eventFile.exists()) {
			List<File> events = Arrays.asList(eventFile.listFiles());
			if (events.size() > 100) {
				Collections.sort(events, new FileComparator());
				for (int i = 50; i < events.size(); i++) {
					File event = events.get(i);
					if (event != null && event.exists() && event.isFile()) {
						event.delete();
					}
				}
			}
		}
	}

	public File getDevicesFile() {
		if (devicesFile == null || !devicesFile.exists()) {
			initCacheDir();
		}
		return devicesFile;
	}

	public static class FileComparator implements Comparator<File> {

		public int compare(File file1, File file2) {
			if (file1.lastModified() < file2.lastModified()) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	private final String videoPath = "/Topvdn/我的录像";
	private final String picturePath = "/Topvdn/我的相册";
	private final String eventPath = "/Topvdn/安全防护";
	private final String cachePath = "/Topvdn/cache";

	private void initCacheDir() {

		mCacheDir = mContext.getCacheDir();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				|| !Environment.isExternalStorageRemovable()) {
			mSDCacheDir = mContext.getExternalCacheDir();
			if (mSDCacheDir == null) {
				mSDCacheDir = new File(Environment
						.getExternalStorageDirectory().getPath(), cachePath);
			}
			CLog.v("mSDCacheDir0:" + mSDCacheDir);

			snapshotFile = new File(Environment.getExternalStorageDirectory()
					.getPath() + picturePath);
			recordFile = new File(Environment.getExternalStorageDirectory()
					.getPath() + videoPath);
			eventFile = new File(Environment.getExternalStorageDirectory()
					.getPath() + eventPath);
		} else {
			CLog.v("mSDCacheDir1:" + mSDCacheDir);
			mSDCacheDir = mCacheDir;
			snapshotFile = new File(Environment.getRootDirectory().getPath()
					+ picturePath);
			recordFile = new File(Environment.getRootDirectory().getPath()
					+ videoPath);
			eventFile = new File(Environment.getRootDirectory().getPath()
					+ eventPath);
		}

		CLog.v("mSDCacheDir2:" + mSDCacheDir);
		if (!mSDCacheDir.exists()) {
			boolean flg = mSDCacheDir.mkdirs();
			CLog.v("mSDCacheDir flg:" + flg);
		}
		if (!snapshotFile.exists()) {
			snapshotFile.mkdirs();
		}
		if (!recordFile.exists()) {
			recordFile.mkdirs();
		}

		if (!eventFile.exists()) {
			eventFile.mkdirs();
		}

		userFile = new File(mSDCacheDir, "/user");
		devicesFile = new File(mSDCacheDir, "/devices");
		logFile = new File(mSDCacheDir.getAbsolutePath()
				.replace("cache", "log"));
		eventThumbFile = new File(mSDCacheDir.getAbsolutePath().replace(
				"cache", "eventThumb"));

		if (!userFile.exists()) {
			userFile.mkdir();
		}

		if (!devicesFile.exists()) {
			devicesFile.mkdirs();
		}

		if (!logFile.exists()) {
			logFile.mkdirs();
		}
		if (!eventThumbFile.exists()) {
			eventThumbFile.mkdirs();
		}
	}

	public File getEventFile() {
		if (eventFile == null || !eventFile.exists()) {
			Utils.initCacheDir();
		}
		return eventFile;
	}

	public File getmSDCacheDir() {
		if (mSDCacheDir == null || !mSDCacheDir.exists()) {
			Utils.initCacheDir();
		}
		CLog.v("mSDCacheDir-" + mSDCacheDir);
		return mSDCacheDir;
	}

	public File getmCacheDir() {
		if (mCacheDir == null || !mCacheDir.exists()) {
			Utils.initCacheDir();
		}
		return mCacheDir;
	}

	public File getTempZipFile() {
		if (tempZipFile == null || !tempZipFile.exists()) {
			Utils.initCacheDir();
		}
		return tempZipFile;
	}

	public File getLocalZipFile() {
		if (localZipFile == null || !localZipFile.exists()) {
			Utils.initCacheDir();
		}
		return localZipFile;
	}

	public File getSnapshotFile() {
		if (snapshotFile == null || !snapshotFile.exists()) {
			Utils.initCacheDir();
		}
		return snapshotFile;
	}

	public File getRecordFile() {
		if (recordFile == null || !recordFile.exists()) {
			Utils.initCacheDir();
		}
		return recordFile;
	}

	public File getLogFile() {
		if (logFile == null || !logFile.exists()) {
			Utils.initCacheDir();
		}
		return logFile;
	}

	public File getEventThumbFile() {
		if (eventThumbFile == null || !eventThumbFile.exists()) {
			Utils.initCacheDir();
		}
		return eventThumbFile;
	}

	public File getUserFile() {
		if (userFile == null || !userFile.exists()) {
			Utils.initCacheDir();
		}
		return userFile;
	}

	public long getAvailableSpare() {
		return calcAvailableSpare();
	}

	public void updateGallery(String filename)// filename是我们的文件全名，包括后缀哦
	{
		MediaScannerConnection.scanFile(mContext,
				new String[] { filename }, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					public void onScanCompleted(String path, Uri uri) {
						// CLog.i("Scanned " + path + ":");
						// CLog.i("-> uri=" + uri);
					}
				});
	}

	/**
	 * 
	 * @return 单位 : M
	 */
	@SuppressWarnings("deprecation")
	public long calcAvailableSpare() {
		String sdCard = "";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			sdCard = Environment.getExternalStorageDirectory().getPath();
		} else {
			sdCard = Environment.getDataDirectory().getPath();
		}
		StatFs statFs = new StatFs(sdCard);
		long blockSize = statFs.getBlockSize();
		long blocks = statFs.getAvailableBlocks();
		long spare = (blocks * blockSize) / (1024 * 1024);
		// CLog.d("calcAvailableSpare:" + spare);
		return spare;
	}

	// Android获取一个用于打开图片文件的intent
	public static Intent getImageFileIntent(String param) {

		File file=new File(param);
		if(!file.exists()){
			return null;
		}
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// Android获取一个用于打开VIDEO文件的intent
	public static Intent getVideoFileIntent(String param) {
		File file=new File(param);
		if(!file.exists()){
			return null;
		}
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "video/*");
		return intent;
	}
}
