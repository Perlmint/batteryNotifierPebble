package kr.omniavinco.batterynotifier;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class BatteryMonitorService extends Service {
	protected int prevLevel;
	protected int lowerLevel, upperLevel;
	
	@Override
	public void onCreate() {
		prevLevel = Utils.getBatteryLevel(getApplicationContext());
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		Log.i("BatteryNotifier", "Bind");
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Log.i("BatteryNotifier", "Schedule Run");
				Context context = getApplicationContext();
				updateSetting();
				int currentLevel = Utils.getBatteryLevel(context);
				Log.i("BatteryNotifier", String.format("current Level : %d%%", currentLevel));
		    	if (prevLevel < currentLevel && currentLevel >= upperLevel ||
		    		prevLevel > currentLevel && currentLevel <= lowerLevel)
		    	{
		    		Utils.sendBatteryLevel(context, currentLevel);		    		
		    	}
		    	prevLevel = currentLevel;
			}
			
		}, 0, 30, TimeUnit.SECONDS);
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		
		
		return null;
	}
	
	protected void updateSetting() {
		SharedPreferences pref = getSharedPreferences("kr.omniavinco.batterynotifier.pref", MODE_PRIVATE);
		lowerLevel = pref.getInt("lower", 20);
		upperLevel = pref.getInt("upper", 80);
		Log.i("BatteryNotifier", String.format("Lower Level : %d%%", lowerLevel));
		Log.i("BatteryNotifier", String.format("Upper Level : %d%%", upperLevel));
	}

}
