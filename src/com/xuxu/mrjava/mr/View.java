package com.xuxu.mrjava.mr;

import java.util.List;

import org.eclipse.swt.graphics.Point;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.hierarchyviewer.HierarchyViewer;
import com.android.hierarchyviewerlib.models.ViewNode;
import com.xuxu.mrjava.AndroidDevice;
import com.xuxu.mrjava.MRJavaException;

/**
 * 
 * @author xuxu
 *
 */
public class View {

	private HierarchyViewer mhViewer;
	private AdbBackend adb;

	public View(AndroidDevice aDevice) {
		this.adb = aDevice.getAdbBackend();
		if (aDevice.getHierarchyViewer() == null) {
			adb.shutdown();
			throw new MRJavaException("View server can't start or sdk version more than 15...");
		}
		this.mhViewer = aDevice.getHierarchyViewer();
		
	}

	/**
	 * 获取元素的 ViewNode 对象
	 * 
	 * @param id
	 *            元素的 id, 例如 resource-id 为 android:id/text1，则 id 为 "id/text1"
	 * @return 返回 ViewNode 对象
	 */
	public ViewNode findViewNodeById(String id) {
		ViewNode viewNode = mhViewer.findViewById(id);
		if (viewNode == null) {
			notFoundViewID();
		}
		return viewNode;
	}

	/**
	 * 获取 ViewNode 集合
	 * 
	 * @param id
	 *            元素的 id, 例如 resource-id 为 android:id/text1，则 id 为 "id/text1"
	 * @return 返回 ViewNode 集合
	 */
	public List<ViewNode> findViewNodesById(String id) {
		ViewNode viewNode = mhViewer.findViewById(id);
		if (viewNode == null) {
			notFoundViewID();
		}
		return viewNode.children;
	}

	/**
	 * 通过元素的 id 获取元素的位置
	 * 
	 * @param id
	 *            元素的id，例如 resource-id 为 android:id/text1，则 id 为 "id/text1"
	 * @return 返回元素的位置坐标
	 */
	public Point findViewById(String id) {
		ViewNode viewNode = findViewNodeById(id);
		Point point = HierarchyViewer.getAbsoluteCenterOfView(viewNode);
		return point;
	}

	/**
	 * 通过元素自己的 index 与 父视图的 id 获取元素的位置
	 * 
	 * @param fatherId
	 *            父视图的 id
	 * @param index
	 *            目标元素的 index
	 * @return 返回元素的位置坐标
	 */
	public Point findViewById(String fatherId, int index) {
		List<ViewNode> childViewNodes = findViewNodesById(fatherId);
		if (childViewNodes.size() - 1 < index) {
			notFoundViewIndex();
		}
		Point point = HierarchyViewer.getAbsoluteCenterOfView(childViewNodes
				.get(index));
		return point;
	}

	/**
	 * 通过父视图 id、多个 index 获取目标元素的位置
	 * 
	 * @param fatherId
	 *            父视图 id
	 * @param index
	 *            多个 index，如(0, 0, 0, 3),目标元素的 index 为 3
	 * @return 返回元素的位置坐标
	 */
	public Point findViewById(String fatherId, int... index) {
		ViewNode viewNode = findViewNodeById(fatherId);
		ViewNode mViewNode = getViewNode(viewNode, 0, index);
		List<ViewNode> childViewNodes = mViewNode.children;
		if (childViewNodes.size() - 1 < index[index.length - 1]) {
			notFoundViewIndex();
		}
		Point point = HierarchyViewer.getAbsoluteCenterOfView(childViewNodes
				.get(index[index.length - 1]));
		return point;
	}

	/**
	 * 通过元素 id 获取指定元素的 text 属性值
	 * 
	 * @param id
	 *            元素的id，例如 resource-id 为 android:id/text1，则 id 为 "id/text1"
	 * @return 返回 text
	 */
	public String getViewText(String id) {
		ViewNode viewNode = findViewNodeById(id);
		return mhViewer.getText(viewNode);
	}

	/**
	 * 通过元素自己的 index 与 父视图的 id 获取 text 属性值
	 * 
	 * @param fatherId
	 *            父视图的 id
	 * @param index
	 *            目标元素的索引
	 * @return 返回 text, text 属性值为空时返回""
	 */
	public String getViewText(String fatherId, int index) {
		String text = "";
		List<ViewNode> childViewNodes = findViewNodesById(fatherId);
		// 元素没有 text 属性时，返回 text 值为 ""
		try {
			text = mhViewer.getText(childViewNodes.get(index));
		} catch (RuntimeException e) {
			return "";
		}

		return text;
	}

	/**
	 * 通过父视图 id、多个 index 获取目标元素的 text
	 * 
	 * @param fatherId
	 *            父视图 id
	 * @param index
	 *            多个 index，如(0, 0, 0, 3),目标元素的 index 为 3
	 * @return 返回 text, text 属性值为空时返回""
	 */
	public String getViewText(String fatherId, int... index) {
		String text = "";
		ViewNode viewNode = findViewNodeById(fatherId);
		ViewNode mViewNode = getViewNode(viewNode, 0, index);
		List<ViewNode> childViewNodes = mViewNode.children;
		if (childViewNodes.size() - 1 < index[index.length - 1]) {
			notFoundViewIndex();
		}

		try {
			text = mhViewer
					.getText(childViewNodes.get(index[index.length - 1]));
		} catch (RuntimeException e) {
			return "";
		}

		return text;
	}

	// 递归获取ViewNode
	private ViewNode getViewNode(ViewNode viewNode, int start, int[] index) {
		int i = start;
		if (i < index.length - 1) {
			return getViewNode(viewNode.children.get(index[i]), ++i, index);
		}
		return viewNode;

	}

	private void notFoundViewID() {
		adb.shutdown();
		throw new MRJavaException("Not Found this view, maybe the id error...");
	}

	private void notFoundViewIndex() {
		adb.shutdown();
		throw new MRJavaException(
				"Not Found this view, maybe the index out of rang...");
	}
}
