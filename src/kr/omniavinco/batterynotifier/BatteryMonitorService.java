package kr.omniavinco.batterynotifier;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class BatteryMonitorService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		PebbleKit.sendAckToPebble(getApplicationContext(), 1);
	}

}
