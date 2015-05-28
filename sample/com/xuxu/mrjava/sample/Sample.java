package com.xuxu.mrjava.sample;

import com.android.chimpchat.core.TouchPressType;
import com.xuxu.mrjava.AndroidDevice;
import com.xuxu.mrjava.MRJava;
import com.xuxu.mrjava.mr.View;

/**
 * 
 * @author xuxu
 *
 * 环境：模拟器，Android 2.3
 *
 */
public class Sample {

	public static void main(String[] args) {
		String deviceId = "emulator-5554";
		
		AndroidDevice aDevice = new AndroidDevice(deviceId);
		MRJava mrj = new MRJava(aDevice);
		View view = new View(aDevice);
		
		System.out.println("Android Version: " + mrj.getAndroidVersion());
		System.out.println("SDK Version: " + mrj.getSdkVersion());
		System.out.println("DeviceID: " + mrj.getSerialNumber());
		System.out.println("Screen size: " + mrj.getScreenWidth() + "x" + mrj.getScreenHeight());
		System.out.println("Device state: " + mrj.getState());
		System.out.println("Battery level: " + mrj.getBatteryLevel());
		
		//启动ApiDemos
		mrj.startActivity("com.example.android.apis/.ApiDemos");
		mrj.sleep(2000);
		
		//点击Content
		mrj.touch(view.findViewById("id/list", 1), TouchPressType.DOWN_AND_UP);
		mrj.sleep(2000);
		
		//点击Content进入新界面后打印列表中索引为2的元素text，预期应该为Resources
		System.out.println(view.getViewText("id/list", 2));
		
		//关闭连接
		aDevice.closeDevice();
	}

}
