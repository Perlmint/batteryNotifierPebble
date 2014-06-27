package kr.omniavinco.batterynotifier;

import kr.omniavinco.batterynotifier.RangeSeekBar.OnRangeSeekBarChangeListener;
import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {
	protected EditText lowerLevelLabel;
	protected EditText upperLevelLabel;
	protected RangeSeekBar<Integer> seekBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.i("BatteryNotifier", "StartUp");
		
		Context context = getApplicationContext();
		
		Intent intent = new Intent();
		intent.setAction("kr.omniavinco.batterynotifier.service");
        context.startService(intent);
		
		lowerLevelLabel = (EditText)findViewById(R.id.lowerLevel);
		upperLevelLabel = (EditText)findViewById(R.id.upperLevel);
		
		Button button= (Button) findViewById(R.id.send_button);
		button.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Context context = getApplicationContext();
		    	IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
				Intent batteryStatus = context.registerReceiver(null, ifilter);
				
		    	int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		    	int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		    	int batteryPct = (int)(level / (float)scale * 100);
		    	Utils.sendBatteryLevel(context, batteryPct);
		    }
		});
		
		seekBar = new RangeSeekBar<Integer>(0, getResources().getInteger(R.integer.MaxBatteryLevel), context);
		SharedPreferences pref = getSharedPreferences("kr.omniavinco.batterynotifier.pref", MODE_PRIVATE);
		seekBar.setSelectedMinValue(pref.getInt("lower", 20));
		seekBar.setSelectedMaxValue(pref.getInt("upper", 80));
		updateLevelTextView();
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
		        @Override
		        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
		        	updateLevelTextView();
		        	Editor prefEditor = getSharedPreferences("kr.omniavinco.batterynotifier.pref", MODE_PRIVATE).edit();
		        	prefEditor.putInt("lower", minValue);
		        	prefEditor.putInt("upper", maxValue);
		        	prefEditor.commit();
		        }
		});

		// add RangeSeekBar to pre-defined layout
		ViewGroup layout = (ViewGroup) findViewById(R.id.setting_layout);
		layout.addView(seekBar);
	}
	
	public void updateLevelTextView()
	{
		lowerLevelLabel.setText(String.format("%d", seekBar.getSelectedMinValue()));
    	upperLevelLabel.setText(String.format("%d", seekBar.getSelectedMaxValue()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
