package com.xuxu.mrjava;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Point;
import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.core.IChimpImage;
import com.android.chimpchat.core.TouchPressType;
import com.android.chimpchat.hierarchyviewer.HierarchyViewer;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.xuxu.mrjava.util.ReUtils;

/**
 * 
 * @author xuxu
 * 
 */

public class MRJava {

	private AdbChimpDevice device;
	private IDevice idevice;
	private HierarchyViewer mhViewer;
	private AndroidDevice aDevice;

	// sdk <= 15,false; sdk > 15,true
	private static boolean MODEL;

	public MRJava(AndroidDevice aDevice) {
		this.aDevice = aDevice;
		this.idevice = aDevice.getIDevice();
		this.device = aDevice.getAdbChimpDevice();
		this.mhViewer = aDevice.getHierarchyViewer();
		MODEL = (new Integer(idevice.getProperty(IDevice.PROP_BUILD_API_LEVEL))) > 15 ? true
				: false;

	}

	/**
	 * 获取设备的序列号
	 * 
	 * @return 返回设备序列号
	 */
	public String getSerialNumber() {
		return idevice.getSerialNumber();
	}

	/**
	 * 获取设备名称(一个比较人性化的名称，例如 xiaomi-mi_4lte-44c826a0)
	 * 
	 * @return 返回设备名称
	 */
	public String getDeviceName() {
		return idevice.getName();
	}

	/**
	 * 获取设备的状态
	 * 
	 * @return 返回设备状态（BOOTLOADER、OFFLINE、ONLINE、RECOVERY、UNAUTHORIZED）
	 */
	public String getState() {
		return idevice.getState().name();
	}

	/**
	 * @return 返回设备状态是否为 ONLINE(device)
	 */
	public boolean isOnline() {
		return idevice.isOnline();
	}

	/**
	 * @return 返回设备模式是否处于 bootloader 模式
	 */
	public boolean isBootLoader() {
		return idevice.isBootLoader();
	}

	/**
	 * 获取设备的 Android 版本，如 4.4.4
	 * 
	 * @return Android 版本
	 */
	public String getAndroidVersion() {
		return idevice.getProperty(IDevice.PROP_BUILD_VERSION);
	}

	/**
	 * 获取设备 SDK 版本，如 19
	 * 
	 * @return 返回 SDK 版本
	 */
	public String getSdkVersion() {
		return idevice.getProperty(IDevice.PROP_BUILD_API_LEVEL);
	}

	/**
	 * 获取设备电池电量
	 * 
	 * @return 返回电池电量
	 */
	public int getBatteryLevel() {
		int level = 0;
		try {
			level = idevice.getBattery().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return level;
	}

	/**
	 * 获取当前界面的 Activity, 如
	 * com.example.android.apis/com.example.android.apis.ApiDemos
	 * 
	 * @return 返回 Activity 名
	 */
	public String getActivity() {
		sleep(500); // 获取 Activity 之前先等待时间的响应，保证获取的 Activity 为当前界面 Activity
		if (mhViewer == null) {
			Pattern pattern = Pattern.compile("([a-zA-Z0-9.]+/.[a-zA-Z0-9.]+)");
			String result = shellCommand("dumpsys window w | grep \\/ | grep name=");
			return ReUtils.matchString(pattern, result).get(0);
		}
		return mhViewer.getFocusedWindowName();
	}

	/**
	 * 获取当前应用的包名，如 com.example.android.apis
	 * 
	 * @return 返回应用包名
	 */
	public String getPackageName() {
		return getActivity().split("/")[0];
	}

	public boolean isInstalled(String pkgName) {
		String result = shellCommand("pm list package com.example.android.apis");
		if (result.trim().equals("")) {
			return false;
		}

		return true;
	}

	/**
	 * 安装本地应用
	 * 
	 * @param appPath
	 *            本地 apk 路径
	 * @return 安装成功，返回 true
	 */
	public boolean installLocalApp(String localAppPath) {
		try {
			String result = idevice.installPackage(localAppPath, true);
			if (result != null) {
				return false;
			}
			return true;
		} catch (InstallException e) {
			return false;
		}

	}

	/**
	 * 批量安装本地应用
	 * 
	 * @param apkFilePaths
	 *            本地 apk 路径集合
	 * @return 安装完成，返回 true
	 */
	public boolean installLocalApps(List<String> apkFilePaths) {
		for (String path : apkFilePaths) {
			if (!installLocalApp(path)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 安装存储在 Android 设备上的应用，如存放在 sdcard 上的应用
	 * 
	 * @param remoteAppPath
	 *            设备上的 apk 的路径(example: /sdcard/ApiDemos.apk)
	 * @return 安装成功，返回true
	 */
	public boolean installRemoteApp(String remoteAppPath) {
		try {
			String result = idevice.installRemotePackage(remoteAppPath, true);
			if (result != null) {
				return false;
			}
			return true;
		} catch (InstallException e) {
			return false;
		}
	}

	/**
	 * 批量安装存储在 Android 设备上的apk
	 * 
	 * @param apkFilePaths
	 *            Android 设备上的 apk 集合
	 * @return 卸载完成，返回 true
	 */
	public boolean installRemoteApps(List<String> apkFilePaths) {
		for (String path : apkFilePaths) {
			if (!installRemoteApp(path)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 卸载应用
	 * 
	 * @param pkgName
	 *            应用的包名，非 apk 名
	 * @return 卸载成功，返回 true
	 */
	public boolean unstallApp(String pkgName) {
		try {
			String result = idevice.uninstallPackage(pkgName);
			if (result != null) {
				return false;
			}
			return true;
		} catch (InstallException e) {
			return false;
		}
	}

	/**
	 * 批量卸载应用
	 * 
	 * @param packages
	 *            应用包名集合
	 * @return 卸载完成，返回 true
	 */
	public boolean unstallApps(List<String> packages) {
		for (String pkg : packages) {
			if (!unstallApp(pkg)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 复制 Android 设备上的文件到本地
	 * 
	 * @param remotePath
	 *            Android 设备上的文件路径
	 * @param localPath
	 *            本地路径
	 */
	public void pullFile(String remotePath, String localPath) {
		String[] arr = remotePath.split(File.separator);
		String fileName = arr[arr.length - 1];
		try {
			idevice.pullFile(remotePath, localPath + File.separator + fileName);
		} catch (SyncException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 推送本地文件至 Android 设备
	 * 
	 * @param localPath
	 *            本地文件路径
	 * @param remotePath
	 *            Android 设备上文件存放路径
	 */
	public void pushFile(String localPath, String remotePath) {
		String[] arr = localPath.split(File.separator);
		String fileName = arr[arr.length - 1];
		try {
			idevice.pushFile(localPath, remotePath + "/" + fileName);
		} catch (SyncException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动应用
	 * 
	 * @param component
	 *            包名/主类名，例如启动相机(com.android.camera/.Camera)
	 */
	public void startActivity(String component) {
		if (MODEL) {
			aDevice.shell("am start -n " + component);
		} else {
			String action = "android.intent.action.MAIN";
			Collection<String> categories = new ArrayList<String>();
			categories.add("android.intent.category.LAUNCHER");
			device.startActivity(null, action, null, null, categories,
					new HashMap<String, Object>(), component, 0);
		}

	}

	/**
	 * 清除应用数据
	 * 
	 * @param pkgName
	 *            应用包名
	 */
	public void clearApp(String pkgName) {
		shellCommand("pm clear " + pkgName);
	}

	/**
	 * 截屏
	 * 
	 * @return 返回 IChimpImage 对象，可对截图进行操作
	 */
	public IChimpImage getScreenShot() {
		try {
			return new MRJChimpImage(idevice.getScreenshot());
		} catch (TimeoutException e) {
			e.printStackTrace();
			return null;
		} catch (AdbCommandRejectedException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 按键
	 * 
	 * @param keyName
	 *            http://grepcode.com/file/repository.grepcode.com/java/ext/com.
	 *            google
	 *            .android/android/4.4.4_r1/android/view/KeyEvent.java#KeyEvent
	 * @param type
	 *            按下的类型，TouchPressType.DOWN、UP、DOWN_AND_UP
	 */
	public void press(String keyName, TouchPressType type) {
		if (MODEL) {
			aDevice.shell("input keyevent " + keyName);
		} else {
			device.press(keyName, type);
		}
	}

	/**
	 * 按下设备的物理键
	 * 
	 * @param key
	 *            物理键，如电源键、Home 键、音量键等
	 * @param type
	 *            按下的类型，TouchPressType.DOWN、UP、DOWN_AND_UP
	 */
	public void press(AndroidKeycode key, TouchPressType type) {
		if (MODEL) {
			aDevice.shell("input keyevent " + key.name());
		} else {
			device.press(key.name(), type);
		}
	}

	/**
	 * 长按下物理键
	 * 
	 * @param key
	 *            物理键，如电源键、Home 键、音量键等
	 */
	public void longPress(AndroidKeycode key) {
		if (MODEL) {
			aDevice.shell("input keyevent " + key.name() + " --longpress");
		} else {
			device.press(key.name(), TouchPressType.DOWN);
			sleep(1500);
			device.press(key.name(), TouchPressType.UP);
		}
	}

	/**
	 * 长按屏幕上指定的坐标位置，默认持续时间1.5s
	 * 
	 * @param x
	 *            横坐标
	 * @param y
	 *            纵坐标
	 */
	public void longPress(int x, int y) {
		touch(x, y, TouchPressType.DOWN);
		sleep(1500);
		touch(x, y, TouchPressType.UP);
	}

	/**
	 * 长按屏幕上指定的元素，默认时间 1.5s
	 * 
	 * @param point
	 *            元素的位置，通过 findViewById 获取
	 */
	public void longPress(Point point) {
		touch(point, TouchPressType.DOWN);
		sleep(1500);
		touch(point, TouchPressType.UP);
	}

	/**
	 * 长按屏幕上指定的坐标位置，自定义持续时间
	 * 
	 * @param x
	 *            横坐标
	 * @param y
	 *            纵坐标
	 * @param duration
	 *            持续时间
	 */
	public void longPress(int x, int y, long duration) {
		touch(x, y, TouchPressType.DOWN);
		sleep(duration);
		touch(x, y, TouchPressType.UP);
	}

	/**
	 * 长按屏幕上指定的元素，自定义持续时间
	 * 
	 * @param point
	 *            元素的位置，通过 findViewById 获取
	 * @param duration
	 *            持续时间
	 */
	public void longPress(Point point, long duration) {
		touch(point, TouchPressType.DOWN);
		sleep(duration);
		touch(point, TouchPressType.UP);
	}

	/**
	 * 触摸屏幕上指定的坐标位置
	 * 
	 * @param x
	 *            横坐标
	 * @param y
	 *            纵坐标
	 * @param type
	 *            按下的类型，TouchPressType.DOWN、UP、DOWN_AND_UP
	 */
	public void touch(int x, int y, TouchPressType type) {
		if (MODEL) {
			aDevice.shell("input tap " + x + " " + y);
		} else {
			device.touch(x, y, type);
		}
	}

	/**
	 * 触摸屏幕上指定的元素
	 * 
	 * @param point
	 *            元素的位置，通过 findViewById 获取
	 * @param type
	 *            按下的类型，TouchPressType.DOWN、UP、DOWN_AND_UP
	 */
	public void touch(Point point, TouchPressType type) {
		if (MODEL) {
			aDevice.shell("input tap " + point.x + " " + point.y);
		} else {
			device.touch(point.x, point.y, type);
		}

	}

	/**
	 * 通过比例点击元素，0.5表示屏幕中央
	 * 
	 * @param xRatio
	 *            x比例
	 * @param yRatio
	 *            y比例
	 * @param type
	 *            按下的类型，TouchPressType.DOWN、UP、DOWN_AND_UP
	 */
	public void touch(long xRatio, long yRatio, TouchPressType type) {
		Point point = changeRatio(xRatio, yRatio);
		touch(point, type);
	}

	/**
	 * 滑动界面，如果不会设置 steps、ms, 请省略这两个参数
	 * 
	 * @param startx
	 *            起始点横坐标
	 * @param starty
	 *            起始点纵坐标
	 * @param endx
	 *            结束点横坐标
	 * @param endy
	 *            结束点纵坐标
	 * @param steps
	 *            步数，即完成该段距离所用的步数
	 * @param ms
	 *            滑动操作的持续时间
	 */
	public void swipe(int startx, int starty, int endx, int endy, int steps,
			long ms) {
		if (MODEL) {
			aDevice.shell("input swipe " + startx + " " + starty + " " + endx
					+ " " + endy + " " + ms);
		} else {
			device.drag(startx, starty, endx, endy, steps, ms);
		}
	}

	/**
	 * 滑动界面，使用默认步长(5)和持续时间(500ms)
	 * 
	 * @param startx
	 *            起始点横坐标
	 * @param starty
	 *            起始点纵坐标
	 * @param endx
	 *            结束点横坐标
	 * @param endy
	 *            结束点纵坐标
	 */
	public void swipe(int startx, int starty, int endx, int endy) {
		if (MODEL) {
			aDevice.shell("input swipe " + startx + " " + starty + " " + endx
					+ " " + endy + " " + 500);
		} else {
			device.drag(startx, starty, endx, endy, 5, 500);
		}
	}

	/**
	 * 滑动界面
	 * 
	 * @param point1
	 *            起始点元素的位置坐标
	 * @param point2
	 *            结束点元素的位置坐标
	 */
	public void swipe(Point point1, Point point2) {
		swipe(point1.x, point1.y, point2.x, point2.y);
	}

	/**
	 * 滑动界面
	 * 
	 * @param point
	 *            起始点元素位置坐标
	 * @param endx
	 *            结束点横坐标
	 * @param endy
	 *            结束点纵坐标
	 */
	public void swipe(Point point, int endx, int endy) {
		swipe(point.x, point.y, endx, endy);
	}

	/**
	 * 滑动界面
	 * 
	 * @param startx
	 *            起始点横坐标
	 * @param starty
	 *            起始点纵坐标
	 * @param point
	 *            结束点元素坐标
	 */
	public void swipe(int startx, int starty, Point point) {
		swipe(startx, starty, point.x, point.y);
	}

	/**
	 * 通过比例滑动屏幕
	 * 
	 * @param xRatio1
	 *            起始点x比例
	 * @param yRatio1
	 *            起始点y比例
	 * @param xRatio2
	 *            结束点x比例
	 * @param yRatio2
	 *            结束点y比例
	 */
	public void swipe(double xRatio1, double yRatio1, double xRatio2,
			double yRatio2) {
		Point point1 = changeRatio(xRatio1, yRatio1);
		Point point2 = changeRatio(xRatio2, yRatio2);
		swipe(point1, point2);
	}

	/**
	 * 拖拽指定坐标位置的元素，如果不会设置 steps、ms, 请省略这两个参数
	 * 
	 * @param startx
	 *            起始点横坐标
	 * @param starty
	 *            起始点纵坐标
	 * @param endx
	 *            结束点横坐标
	 * @param endy
	 *            结束点纵坐标
	 * @param steps
	 *            步数，即完成该段距离所用的步数
	 * @param ms
	 *            拖拽操作的持续时间
	 */
	public void drag(int startx, int starty, int endx, int endy, int steps,
			long ms) {
		touch(startx, starty, TouchPressType.DOWN);
		sleep(1000);
		swipe(startx, starty, endx, endy, steps, ms);
		touch(endx, endy, TouchPressType.UP);
	}

	/**
	 * 拖拽指定坐标位置的元素
	 * 
	 * @param startx
	 *            起始点横坐标
	 * @param starty
	 *            起始点纵坐标
	 * @param endx
	 *            结束点横坐标
	 * @param endy
	 *            结束点纵坐标
	 */
	public void drag(int startx, int starty, int endx, int endy) {
		touch(startx, starty, TouchPressType.DOWN);
		sleep(1000);
		swipe(startx, starty, endx, endy, 2, 1000);
		touch(endx, endy, TouchPressType.UP);
	}

	public void sendText(String text) {
		String[] str = text.trim().split(" ");
		if (str.length == 1) {
			// device.type(text);
			aDevice.shell("input text " + text);
		} else {
			sendTextSpace(str);
		}

	}

	private void sendTextSpace(String[] str) {
		ArrayList<String> contentt = new ArrayList<String>();
		for (String string : str) {
			if (!string.equals("")) {
				contentt.add(string);
			}
		}

		int length = contentt.size();
		for (int i = 0; i < length; i++) {
			shellCommand("input text " + contentt.get(i));
			sleep(50);
			if (i != length - 1) {
				press(AndroidKeycode.SPACE, TouchPressType.DOWN_AND_UP);
			}
		}
	}

	/**
	 * 执行 adb shell 命令
	 * 
	 * @param command
	 *            shell 命令
	 * @return
	 */
	public String shellCommand(String command) {
		return aDevice.shell(command);
	}

	/**
	 * 
	 * @return 返回屏幕宽度
	 */
	public int getScreenWidth() {
		if (MODEL) {
			return getScreenResolution()[0];
		} else {
			return new Integer(device.getProperty("display.width"));
		}
	}

	/**
	 * 
	 * @return 返回屏幕高度
	 */
	public int getScreenHeight() {
		if (MODEL) {
			return getScreenResolution()[1];
		} else {
			return new Integer(device.getProperty("display.height"));
		}
	}

	private int[] getScreenResolution() {
		Pattern pattern = Pattern.compile("([0-9]+)");
		String info = shellCommand("dumpsys display | grep PhysicalDisplayInfo");
		ArrayList<Integer> out = ReUtils.matchInteger(pattern, info);
		int[] resolution = new int[] { out.get(0), out.get(1) };

		return resolution;
	}

	private Point changeRatio(double xRatio1, double yRatio1) {
		int width = getScreenWidth();
		int height = getScreenHeight();
		int x = (int) (xRatio1 * width);
		int y = (int) (yRatio1 * height);

		return new Point(x, y);
	}

	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
