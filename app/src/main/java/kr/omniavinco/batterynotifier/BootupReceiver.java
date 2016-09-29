package kr.omniavinco.batterynotifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		intent.setAction("kr.omniavinco.batterynotifier.service");
        context.startService(intent);
	}

}
