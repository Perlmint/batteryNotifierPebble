package kr.omniavinco.batterynotifier;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class BatteryMonitorService extends Service {
	protected Utils.BatteryState prevState;
	protected int lowerLevel, upperLevel;
	
	@Override
	public void onCreate() {
		prevState = Utils.getBatteryLevel(getApplicationContext());
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		Log.i("BatteryNotifier", "Bind");
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Log.i("BatteryNotifier", "Schedule Run");
				Context context = getApplicationContext();
				updateSetting();
				Utils.BatteryState currentState = Utils.getBatteryLevel(context);
				Log.i("BatteryNotifier", String.format("current Level : %d%%", currentState.level));
				final boolean over = prevState.level < currentState.level && currentState.level >= upperLevel;
				final boolean under = prevState.level > currentState.level && currentState.level <= lowerLevel;
		    	if (over || under)
		    	{
		    		Utils.sendBatteryLevel(context, currentState, over ? Utils.LevelState.Over : Utils.LevelState.Under);
		    	}
		    	prevState = currentState;
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
