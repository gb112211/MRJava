package com.xuxu.mrjava;

public enum AndroidKeycode {
	//在PhysicalButton的基础上新增几个物理按键 keycode
	
	HOME("KEYCODE_HOME"),
    SEARCH("KEYCODE_SEARCH"),
    MENU("KEYCODE_MENU"),
    BACK("KEYCODE_BACK"),
    DPAD_UP("DPAD_UP"),
    DPAD_DOWN("DPAD_DOWN"),
    DPAD_LEFT("DPAD_LEFT"),
    DPAD_RIGHT("DPAD_RIGHT"),
    DPAD_CENTER("DPAD_CENTER"),
    ENTER("enter"),
	//新增
	POWER("26"),
	VOLUME_UP("24"),
	VOLUME_DOWN("25"),
	SPACE("62"),
	BACKSPACE("67"), //退格键，删除文本输入框的内容
//	MOVE_HOME("122"),
//	MOVE_END("123");
	KEYCODE_DPAD_LEFT("21");
	
	private String keyName;

    private AndroidKeycode(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
}
