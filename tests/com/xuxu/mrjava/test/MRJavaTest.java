package com.xuxu.mrjava.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.android.chimpchat.core.TouchPressType;
import com.xuxu.mrjava.AndroidDevice;
import com.xuxu.mrjava.AndroidKeycode;
import com.xuxu.mrjava.MRJava;

/**
 * 
 * @author xuxu
 *
 *         测试环境：Android 4.4，小米4机器，Mac环境。请自行修改对应设置
 *
 */

public class MRJavaTest {

	// mac下需要配置adb绝对路径
	static String adbPath = "/Users/xuxu/utils/android/android-sdk-macosx/platform-tools/adb";
	// 测试机为小米4
	static String XIAOMI = "44c826a0";

	// ApiDemos路径
	String apkPath = System.getProperty("user.dir") + "/apk/api-demos.apk";
	String apiDemosPackegeName = "com.example.android.apis";

	static AndroidDevice aDevice;
	static MRJava mrj;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 非Mac，使用注释掉的构造方法
		// aDevice = new AndroidDevice(你的Android设备id);
		aDevice = new AndroidDevice(adbPath, XIAOMI);
		mrj = new MRJava(aDevice);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		aDevice.closeDevice();
	}

	@Test
	public void testGetSerialNumber() {
		assertEquals(XIAOMI, mrj.getSerialNumber());
	}

	@Test
	public void testGetDeviceName() {
		assertEquals("xiaomi-mi_4lte-44c826a0", mrj.getDeviceName());
	}

	@Test
	public void testGetState() {
		assertEquals("ONLINE", mrj.getState());
	}

	@Test
	public void testIsOnline() {
		assertTrue(mrj.isOnline());
	}

	@Test
	public void testIsBootLoader() {
		assertFalse(mrj.isBootLoader());
	}

	@Test
	public void testGetAndroidVersion() {
		assertEquals("4.4.4", mrj.getAndroidVersion());
	}

	@Test
	public void testGetSdkVersion() {
		assertEquals("19", mrj.getSdkVersion());
	}

	@Test
	public void testGetBatteryLevel() {
		assertTrue(mrj.getBatteryLevel() > 0);
	}

	@Test
	public void testGetActivity() {
		if (!mrj.isInstalled(apiDemosPackegeName)) {
			mrj.installLocalApp(apkPath);
		}

		mrj.startActivity("com.example.android.apis/.ApiDemos");
		mrj.sleep(2000);
		assertEquals(
				"com.example.android.apis/com.example.android.apis.ApiDemos",
				mrj.getActivity());

	}

	@Test
	public void testGetPackageName() {
		if (!mrj.isInstalled(apiDemosPackegeName)) {
			mrj.installLocalApp(apkPath);
		}

		mrj.startActivity("com.example.android.apis/.ApiDemos");
		mrj.sleep(2000);
		assertEquals(apiDemosPackegeName, mrj.getPackageName());
	}

	@Test
	public void testInstallLocalApp() {
		assertTrue(mrj.installLocalApp(apkPath));
	}

	@Test
	public void testInstallLocalApps() {
		mrj.unstallApp(apiDemosPackegeName);
		ArrayList<String> appList = new ArrayList<String>();
		appList.add(apkPath);

		mrj.installLocalApps(appList);
		assertTrue(mrj.isInstalled(apiDemosPackegeName));
	}

	@Test
	public void testInstallRemoteApp() {
		if (mrj.isInstalled(apiDemosPackegeName)) {
			mrj.unstallApp(apiDemosPackegeName);
		}

		mrj.pushFile(apkPath, "/data/local/tmp");
		assertTrue(mrj.installRemoteApp("/data/local/tmp/api-demos.apk"));
	}

	@Test
	public void testInstallRemoteApps() {
		if (mrj.isInstalled(apiDemosPackegeName)) {
			mrj.unstallApp(apiDemosPackegeName);
		}

		mrj.pushFile(apkPath, "/data/local/tmp");
		ArrayList<String> apkList = new ArrayList<String>();
		apkList.add("/data/local/tmp/api-demos.apk");
		mrj.installRemoteApps(apkList);
	}

	@Test
	public void testPullFile() {
		mrj.pushFile(apkPath, "/data/local/tmp");
		mrj.pullFile("data/local/tmp/api-demos.apk",
				System.getProperty("user.dir"));
		File file = new File(System.getProperty("user.dir") + "/api-demos.apk");
		assertTrue(file.isFile());
		file.delete();

	}

	@Test
	public void testStartActivity() {
		if (!mrj.isInstalled(apiDemosPackegeName)) {
			mrj.installLocalApp(apkPath);
		}

		mrj.startActivity("com.example.android.apis/.ApiDemos");
		mrj.sleep(2000);
		assertEquals(
				"com.example.android.apis/com.example.android.apis.ApiDemos",
				mrj.getActivity());
	}

	@Test
	public void testClearApp() {
		mrj.clearApp(apiDemosPackegeName);
	}

	@Test
	public void testGetScreenShot() {
		mrj.getScreenShot().writeToFile(
				System.getProperty("user.dir") + "/apk/image.png", "png");
		File file = new File(System.getProperty("user.dir") + "/apk/image.png");
		assertTrue(file.isFile());
		file.delete();
	}

	@Test
	public void testPressStringTouchPressType() {
		mrj.press("KEYCODE_HOME", TouchPressType.DOWN_AND_UP);
	}

	@Test
	public void testPressAndroidKeycodeTouchPressType() {
		mrj.press(AndroidKeycode.BACK, TouchPressType.DOWN_AND_UP);
	}

	@Test
	public void testLongPressAndroidKeycode() {
		mrj.longPress(AndroidKeycode.HOME);
	}

	@Test
	public void testLongPressIntInt() {
		mrj.longPress(500, 500);
	}

	@Test
	public void testTouchIntIntTouchPressType() {
		mrj.touch(500, 500, TouchPressType.DOWN_AND_UP);
	}

	@Test
	public void testSwipeIntIntIntInt() {
		mrj.swipe(200, 500, 800, 500);
	}

	@Test
	public void testSwipeDoubleDoubleDoubleDouble() {
		mrj.swipe(0.2, 0.5, 0.8, 0.5);
	}

	@Test
	public void testSendText() {
		mrj.sendText("hello, world!");
	}

	@Test
	public void testShellCommand() {
		assertNotNull(mrj.shellCommand("date"));
	}

	@Test
	public void testGetScreenWidth() {
		assertEquals(1080, mrj.getScreenWidth());
	}

	@Test
	public void testGetScreenHeight() {
		assertEquals(1920, mrj.getScreenHeight());
	}

}
