package com.xuxu.mrjava.mr.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.xuxu.mrjava.AndroidDevice;
import com.xuxu.mrjava.MRJava;
import com.xuxu.mrjava.mr.View;

/**
 * 
 * @author xuxu
 *
 * 测试环境：模拟器、Android2.3版本、mac
 *
 */
public class ViewTest {

	static String emulator = "emulator-5554";
	// mac下需要配置adb绝对路径
	static String adbPath = "/Users/xuxu/utils/android/android-sdk-macosx/platform-tools/adb";

	static AndroidDevice aDevice;
	static View view;
	static MRJava mrj;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 非Mac，使用注释掉的构造方法
		// aDevice = new AndroidDevice(你的模拟器id);

		aDevice = new AndroidDevice(adbPath, emulator);
		view = new View(aDevice);
		mrj = new MRJava(aDevice);

		//启动ApiDemos
		mrj.sleep(2000);
		mrj.startActivity("com.example.android.apis/.ApiDemos");
		mrj.sleep(2000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		aDevice.closeDevice();
	}

	@Test
	public void testFindViewNodeById() {
		assertNotNull(view.findViewById("id/list"));
	}

	@Test
	public void testFindViewNodesById() {
		assertTrue(view.findViewNodesById("id/list").size() > 1);
	}

	@Test
	public void testFindViewByIdString() {
		assertNotNull(view.findViewById("id/text1"));
	}

	@Test
	public void testFindViewByIdStringInt() {
		assertNotNull(view.findViewById("id/list", 2));
	}

	@Test
	public void testFindViewByIdStringIntArray() {
		assertNotNull(view.findViewById("id/content", 0, 2));
	}

	@Test
	public void testGetViewTextString() {
		assertEquals("App", view.getViewText("id/text1"));
	}

	@Test
	public void testGetViewTextStringInt() {
		assertEquals("Content", view.getViewText("id/list", 1));
	}

	@Test
	public void testGetViewTextStringIntArray() {
		assertEquals("Content", view.getViewText("id/content", 0, 1));
	}

}
