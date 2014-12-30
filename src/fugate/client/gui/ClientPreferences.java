package fugate.client.gui;

import java.util.HashMap;
import java.util.prefs.Preferences;

public class ClientPreferences {

	public static Preferences prefs;
	
	private static String userName				= "User Name";
	private static String userNameList			= "User Name List";
	private static String userAvailList 		= "User Availability List";
	private static String hostName				= "Host Name";
	private static String portNumber			= "Port Number";
	private static String password				= "Password";
	private static String logMessages			= "Log Messages";
	private static String minimizeSystemTray	= "Minimize to System Tray";
	private static String pttNotification		= "Push to Talk Notification";
	private static String pttHotKey				= "Push to Talk Hotkey";
	private static String hotkey				= "Hotkey";
	private static String hotkeyName			= "Hotkey Name";
	private static String muteSound				= "Mute Sound";
	private static String muteMicrophone		= "Mute Microphone";
	
	public ClientPreferences(){
		prefs = Preferences.userNodeForPackage(this.getClass());
		
		prefs.get(userName, "");
		//prefs.getByteArray(userNameList, convertObject(new HashMap<String, String>()));
		//prefs.getByteArray(userAvailList, );
		prefs.get(hostName, "");
		prefs.get(portNumber, "");
		prefs.get(password, "");
		prefs.getBoolean(logMessages, false);
		prefs.getBoolean(minimizeSystemTray, false);
		prefs.getBoolean(pttNotification, false);
		prefs.getBoolean(pttHotKey, false);
		prefs.getInt(hotkey, 0);
	}

	public static Preferences getPrefs() {
		return prefs;
	}

	public static void setPrefs(Preferences prefs) {
		ClientPreferences.prefs = prefs;
	}

	public static String getUserName() {
		return userName;
	}

	public static String getUserNameList() {
		return userNameList;
	}

	public static String getUserAvailList() {
		return userAvailList;
	}

	public static String getHostName() {
		return hostName;
	}

	public static String getPortNumber() {
		return portNumber;
	}

	public static String getPassword() {
		return password;
	}

	public static String getLogMessages() {
		return logMessages;
	}

	public static String getMinimizeSystemTray() {
		return minimizeSystemTray;
	}

	public static String getPttNotification() {
		return pttNotification;
	}

	public static String getPttHotKey() {
		return pttHotKey;
	}

	public static String getHotkey() {
		return hotkey;
	}

	public static String getMuteSound() {
		return muteSound;
	}

	public static void setMuteSound(String muteSound) {
		ClientPreferences.muteSound = muteSound;
	}

	public static String getMuteMicrophone() {
		return muteMicrophone;
	}

	public static void setMuteMicrophone(String muteMicrophone) {
		ClientPreferences.muteMicrophone = muteMicrophone;
	}

	public static String getHotkeyName() {
		return hotkeyName;
	}

	public static void setHotkeyName(String hotkeyName) {
		ClientPreferences.hotkeyName = hotkeyName;
	}
}
