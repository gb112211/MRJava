package com.xuxu.mrjava;

import java.io.IOException;
import java.lang.reflect.Field;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.chimpchat.adb.CommandOutputCapture;
import com.android.chimpchat.hierarchyviewer.HierarchyViewer;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;

/**
 * 
 * @author xuxu
 *
 */
public class AndroidDevice {

	private AdbBackend adb;
	private AdbChimpDevice device;
	private IDevice idevice;
	private HierarchyViewer mhViewer;

	public AndroidDevice() {
		adb = new AdbBackend();
		selectModel();
	}

	private void selectModel() {
		idevice = getIDevice(adb);
		if (new Integer(idevice.getProperty(IDevice.PROP_BUILD_API_LEVEL)) < 16) {
			device = (AdbChimpDevice) adb.waitForConnection();
			init(adb, device);
		}
		
	}
	
	private void selectModel(String deviceId) {
		idevice = getIDevice(adb);
		if (new Integer(idevice.getProperty(IDevice.PROP_BUILD_API_LEVEL)) < 16) {
			device = (AdbChimpDevice) adb.waitForConnection(5000, deviceId);
			init(adb, device);
		}
		
	}

	public AndroidDevice(String deviceId) {
		adb = new AdbBackend();
		selectModel(deviceId);

	}

	public AndroidDevice(String adbPath, String deviceId) {
		adb = new AdbBackend(adbPath, false);
		selectModel(deviceId);
	}

	public AdbBackend getAdbBackend() {
		return this.adb;
	}

	public AdbChimpDevice getAdbChimpDevice() {
		return this.device;
	}

	public IDevice getIDevice() {
		return this.idevice;
	}

	public HierarchyViewer getHierarchyViewer() {
		return this.mhViewer;
	}

	private void init(AdbBackend adb, AdbChimpDevice device) {
		checkDevice(device);
		shell("service call window 1 i32 4939");
		if (device.shell("service call window 3").trim()
				.equals("Result: Parcel(00000000 00000001   '........')")) {
			mhViewer = device.getHierarchyViewer();
		}
	}

	private void checkDevice(AdbChimpDevice device) {
		if (device == null) {
			adb.shutdown();
			throw new MRJavaException("Device not exist or deviceID error...");
		}
	}

	// AdbBackend 类里面已经实例化 AndroidBridge，需要使用 ddmlib 时将会产生冲突
	// 利用反射的方法获取 AdbBackend 类里面的私有成员变量 bridge
	// 通过 bridge 实例化 IDevice
	private IDevice getIDevice(AdbBackend adb) {
		Class<AdbBackend> mAdbBackendClass = AdbBackend.class;
		Field field = null;
		AndroidDebugBridge bridge = null;

		try {
			field = mAdbBackendClass.getDeclaredField("bridge");
			field.setAccessible(true);
			bridge = (AndroidDebugBridge) field.get(adb);

		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		while (bridge.hasInitialDeviceList() == false) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return bridge.getDevices()[0];
	}
	
	public String shell(String cmd) {
		CommandOutputCapture capture = new CommandOutputCapture();
        try {
            idevice.executeShellCommand(cmd, capture);
        } catch (TimeoutException e) {
            return null;
        } catch (ShellCommandUnresponsiveException e) {
            return null;
        } catch (AdbCommandRejectedException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return capture.toString();
	}
	
	/**
	 * 关闭 adb 连接
	 */
	public void closeDevice() {
		adb.shutdown();
	}

}
