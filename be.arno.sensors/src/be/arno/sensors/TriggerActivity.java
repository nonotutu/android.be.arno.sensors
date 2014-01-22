package be.arno.sensors;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class TriggerActivity extends Activity {
	
	private static final String LOG_TAG = "TriggerActivity";

	private SensorManager mSensorManager;
    private Sensor sensor;
	private TriggerEventListener mListener = new TriggerListener();
	
	private boolean state;
	
	private RelativeLayout rela;
	
	private TextView txvwName;
	private TextView txvwValues;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Enlève la barre de titre
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_trigger);		
		Log.i(LOG_TAG, "init()");
		
		Intent intent = getIntent();
		int sensor_type = intent.getExtras().getInt("trigger_type");

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = mSensorManager.getDefaultSensor(sensor_type);

		rela = (RelativeLayout)findViewById(R.id.trigger_rela);
		txvwValues = (TextView)findViewById(R.id.trigger_txvwValues);
		txvwName = (TextView)findViewById(R.id.step_txvwName);
		
		txvwName.setText(sensor.getName());
		txvwValues.setText("");
		
		state = false;
		switchColors();
	}


	@Override
	protected void onResume() {
	    super.onResume();

        mSensorManager.requestTriggerSensor(mListener, sensor);
	}

	
	@Override
	protected void onDestroy() {
	    super.onPause();
	    mSensorManager.cancelTriggerSensor(mListener, sensor);
	}
	
	
	private void switchColors() {
		
		if ( state ) {
			rela.setBackgroundColor(0xFF000000);
			txvwName.setTextColor(0xFFFFFFFF);
			txvwValues.setTextColor(0xFFFFFFFF);
			state = false;
		} else {
			rela.setBackgroundColor(0xFFFFFFFF);
			txvwName.setTextColor(0xFF000000);
			txvwValues.setTextColor(0xFF000000);
			state = true;
		}
	}

	class TriggerListener extends TriggerEventListener {
	
		@Override
		public void onTrigger(TriggerEvent event) {
			
			switchColors();
			
			txvwValues.setText(
					"· triggered at : "
					+ millis2HR(new GregorianCalendar().getTimeInMillis())
					+ txvwValues.getText().toString()
					);
			
	        mSensorManager.requestTriggerSensor(mListener, sensor);
		}
	}
	
	private String millis2HR(long time){
			
	    SimpleDateFormat magic_rabbit = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSS");
	    return magic_rabbit.format(time);
		}
}

