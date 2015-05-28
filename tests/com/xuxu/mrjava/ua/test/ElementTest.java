package com.xuxu.mrjava.ua.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xuxu.mrjava.AndroidDevice;
import com.xuxu.mrjava.MRJava;
import com.xuxu.mrjava.ua.Element;

/**
 * 
 * @author xuxu
 *
 *         测试环境：Android 4.4，小米4机器，Mac环境。请自行修改对应设置
 *
 */
public class ElementTest {

	static AndroidDevice aDevice;
	static MRJava mrj;
	static Element element;

	// mac下需要配置adb绝对路径
	static String adbPath = "/Users/xuxu/utils/android/android-sdk-macosx/platform-tools/adb";
	// 测试机为小米4
	static String XIAOMI = "44c826a0";

	// ApiDemos路径
	static String apkPath = System.getProperty("user.dir")
			+ "/apk/api-demos.apk";
	static String apiDemosPackegeName = "com.example.android.apis";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 非Mac，使用注释掉的构造方法
		// aDevice = new AndroidDevice(你的Android设备id);

		aDevice = new AndroidDevice(adbPath, XIAOMI);
		element = new Element(aDevice);
		mrj = new MRJava(aDevice);

		if (!mrj.isInstalled(apiDemosPackegeName)) {
			mrj.installLocalApp(apkPath);
		}

		// 启动ApiDemos
		mrj.sleep(2000);
		mrj.startActivity("com.example.android.apis/.ApiDemos");
		mrj.sleep(2000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		aDevice.closeDevice();
	}

	@Test
	public void testFindElementByText() {
		assertNotNull(element.findElementByText("App"));
	}

	@Test
	public void testFindElementsByText() {
		assertEquals(1, element.findElementsByText("App").size());
	}

	@Test
	public void testFindElementById() {
		assertNotNull(element.findElementById("android:id/text1"));
	}

	@Test
	public void testFindElementsById() {
		assertTrue(element.findElementsById("android:id/text1").size() > 1);
	}

	@Test
	public void testFindElementByClass() {
		assertNotNull(element.findElementByClass("android.widget.TextView"));
	}

	@Test
	public void testFindElementsByClass() {
		assertTrue(element.findElementsByClass("android.widget.TextView")
				.size() > 1);
	}

	@Test
	public void testFindElementByChecked() {
		assertNotNull(element.findElementByChecked("false"));
	}

	@Test
	public void testFindElementsByChecked() {
		assertEquals(0, element.findElementsByChecked("true").size());
	}

	@Test
	public void testFindElementByCheckable() {
		assertNotNull(element.findElementByCheckable("false"));
	}

	@Test
	public void testFindElementsByCheckable() {
		assertTrue(element.findElementsByCheckable("false").size() > 1);
	}

	@Test
	public void testFindElementsByContentdesc() {
		assertTrue(element.findElementsByContentdesc("test").size() == 0);
	}

	@Test
	public void testFindElementByClickable() {
		assertNotNull(element.findElementByClickable("true"));
	}

	@Test
	public void testFindElementsByClickable() {
		assertTrue(element.findElementsByClickable("true").size() > 1);
	}

	@Test
	public void testGetTextById() {
		assertEquals("App", element.getTextById("android:id/text1").get(2));
	}

	@Test
	public void testGetTextByClass() {
		assertEquals("App", element.getTextByClass("android.widget.TextView")
				.get(3));
	}

}
