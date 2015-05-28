package com.xuxu.mrjava.util;

import com.xuxu.mrjava.AndroidDevice;

/**
 * 
 * @author xuxu
 *
 * 在logcat里面打印log
 */
public class Log {
	
	private AndroidDevice aDevice;
	
	public Log(AndroidDevice aDevice) {
		this.aDevice = aDevice;
	}
	
	public void v(String TAG, String message) {
		aDevice.shell("log -p v -t " + TAG + " " + message);
	}
	
	public void d(String TAG, String message) {
		aDevice.shell("log -p d -t " + TAG + " " + message);
	}
	
	public void i(String TAG, String message) {
		aDevice.shell("log -p i -t " + TAG + " " + message);
	}
	
	public void w(String TAG, String message) {
		aDevice.shell("log -p w -t " + TAG + " " + message);
	}
	
	public void e(String TAG, String message) {
		aDevice.shell("log -p e -t " + TAG + " " + message);
	}

}
