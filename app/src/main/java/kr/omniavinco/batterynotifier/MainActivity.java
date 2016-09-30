package kr.omniavinco.batterynotifier;

import android.os.BatteryManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.util.Range;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import org.florescu.android.rangeseekbar.RangeSeekBar;

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
		seekBar = (RangeSeekBar<Integer>)findViewById(R.id.rangeSeekBar);
		
		Button button= (Button) findViewById(R.id.send_button);
		button.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Context context = getApplicationContext();
				Utils.BatteryState state = Utils.getBatteryLevel(context);
		    	Utils.sendBatteryLevel(context, state, Utils.LevelState.Test);
		    }
		});

		SharedPreferences pref = getSharedPreferences("kr.omniavinco.batterynotifier.pref", MODE_PRIVATE);
		seekBar.setSelectedMinValue(pref.getInt("lower", 20));
		seekBar.setSelectedMaxValue(pref.getInt("upper", 80));
		updateLevelTextView();
		seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
		        @Override
		        public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
		        	updateLevelTextView();
		        	Editor prefEditor = getSharedPreferences("kr.omniavinco.batterynotifier.pref", MODE_PRIVATE).edit();
		        	prefEditor.putInt("lower", minValue);
		        	prefEditor.putInt("upper", maxValue);
		        	prefEditor.commit();
		        }
		});
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
