package kr.omniavinco.batterynotifier;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class Utils {
	public enum LevelState {
		Over,
		Under,
		Test
	}

	static final int notificationId = 1;

	static public void sendBatteryLevel(Context context, BatteryState state, LevelState levelState) {
		int titleId = 0;
		switch (levelState) {
			case Over:
				titleId = R.string.notification_title_over;
				break;
			case Under:
				titleId = R.string.notification_title_under;
				break;
			case Test:
				titleId = R.string.notification_title_test;
				break;
		}
		int statusStringId = 0;
		switch (state.status) {
			case Charging:
				statusStringId = R.string.battery_status_charging;
				break;
			case Discharging:
				statusStringId = R.string.battery_status_discharging;
				break;
			case Full:
				statusStringId = R.string.battery_status_full;
				break;
			case NotCharging:
				statusStringId = R.string.battery_status_notcharging;
				break;
			case Unknown:
				statusStringId = R.string.battery_status_unknown;
				break;
		}

		Resources resources = context.getResources();
		final String title = resources.getString(titleId);
		final String bodyTemplate = resources.getString(R.string.notification_body_template);
		String body = String.format(bodyTemplate, state.level, resources.getString(statusStringId));

		Intent notiIntent = new Intent(context, MainActivity.class);
		notiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle(title)
						.setContentText(body)
						.addAction(
								android.R.drawable.ic_menu_manage,
								resources.getString(R.string.notification_open_setting),
								pendingIntent);

		NotificationManagerCompat manager = NotificationManagerCompat.from(context);
		manager.notify(notificationId, builder.build());
	}

	static public class BatteryState {
		int level;
		enum Status {
			Charging,
			Discharging,
			Full,
			NotCharging,
			Unknown
		};
		Status status;
	}
	
	static public final BatteryState getBatteryLevel(Context context) {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		
    	int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    	int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

		BatteryState ret = new BatteryState();
    	ret.level = (int)(level / (float)scale * 100);
		switch (status) {
			case BatteryManager.BATTERY_STATUS_CHARGING:
				ret.status = BatteryState.Status.Charging;
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
				ret.status = BatteryState.Status.Discharging;
				break;
			case BatteryManager.BATTERY_STATUS_FULL:
				ret.status = BatteryState.Status.Full;
				break;
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				ret.status = BatteryState.Status.NotCharging;
				break;
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				ret.status = BatteryState.Status.Unknown;
				break;
		}
    	return ret;
	}
}
