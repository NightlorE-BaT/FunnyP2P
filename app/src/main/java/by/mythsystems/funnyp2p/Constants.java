package by.mythsystems.funnyp2p;

import static android.net.wifi.p2p.WifiP2pManager.*;

public class Constants {
	
	/**
	 * Date format pattern to use with Date instances that should be parsed in
	 * the String
	 */
	public static final String DATE_STRING_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final int REQ_CODE_PICK_IMAGE = 60003;
	public static String ANDROID_RESOURCE_PREFIX = "android.resource://";
	public static String ANDROID_RESOURCE_RAW_TYPE = "raw";

	public static class WiFiStatusCode {
		public static final byte DISABLED = WIFI_P2P_STATE_DISABLED;
		public static final byte INIT = WIFI_P2P_STATE_ENABLED;
		public static final byte SCAN = 3;
		public static final byte FOUND = 4;
		public static final byte CONNECTING = 5;
		public static final byte CONNECTED = 6;
	}

	public enum LoginSystems {
		Default, GooglePlus, Facebook
	}

}
