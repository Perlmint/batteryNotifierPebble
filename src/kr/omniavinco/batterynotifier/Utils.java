package kr.omniavinco.batterynotifier;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class Utils {
	static public void sendBatteryLevel(Context context, int level) {
		final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

	    final Map<String, String> data = new HashMap<String, String>();
	    data.put("title", "BatteryLevel");
	    data.put("body", String.format("%d%%", level));
	    final JSONObject jsonData = new JSONObject(data);
	    final String notificationData = new JSONArray().put(jsonData).toString();

	    i.putExtra("messageType", "PEBBLE_ALERT");
	    i.putExtra("sender", "BatteryNotifier");
	    i.putExtra("notificationData", notificationData);

	    context.sendBroadcast(i);
	}
	
	static public final int getBatteryLevel(Context context) {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		
    	int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    	int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

    	int batteryPct = (int)(level / (float)scale * 100);
    	return batteryPct;
	}
}
