package be.arno.sensors;

import be.arno.sensors.SensorInfos;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


public class SensorsActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
    //private final TriggerEventListener mListener = new TriggerListener();

	// private List<TextView> txvws;
	private List<Sensor> deviceSensors;
	private int[] backColors;
	private int backColor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensors);
		
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
	    
	    RelativeLayout rela = (RelativeLayout)findViewById(R.id.sensors_rela);
	    LinearLayout lila = (LinearLayout)findViewById(R.id.sensors_lila);
	    
	    // rela.setBackgroundColor(0xFFFFFFFF/*0xFF669900*/);

	    backColors = new int[]{
			0x7F000000 + getResources().getColor(R.color.a_light_blue),
			0x7F000000 + getResources().getColor(R.color.a_light_purple),
			0x7F000000 + getResources().getColor(R.color.a_light_green),
			0x7F000000 + getResources().getColor(R.color.a_light_orange),
			0x7F000000 + getResources().getColor(R.color.a_light_red)
		    };
	    
	    Random rand = new Random();
	    backColor = backColors[rand.nextInt(backColors.length)];
	    
	    Iterator<Sensor> sensors = deviceSensors.iterator();
	    while (sensors.hasNext())
	        lila.addView(caseSensor(sensors.next()));
	    
	    
	    
	    
	}
	
	
	

	private String enTete(Sensor s) {
		
		String text = "";

		text += (SensorInfos.isTrigger(s.getType()) ? "[Trigger] " : "" );
		text += "<b>" + s.getName() + "</b>";
		text += "<br/>Type : " + SensorInfos.getName(s.getType()) + " [" + s.getType() + "]";
		text += "<br/>Vendor : " + s.getVendor();
		text += "<br/>Power : " + s.getPower();
		text += "<br/>Resolution : " + s.getResolution();
		text += "<br/>Maximum range : " + s.getMaximumRange();
		text += "<br/>Minimum delay : " + s.getMinDelay();
		text += "<br/>Unit : " + SensorInfos.getUnit(s.getType());
		text += "<br/>Version : " + s.getVersion();
		
		return text;
	}
	
	
	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	
	@Override
	public final void onSensorChanged(SensorEvent event) {
		
		/*
		
		Sensor currentSensor = event.sensor;
		String text = enTete(currentSensor);
		
		for ( int i = 0 ; i < event.values.length ; i += 1 ) {
			text += "<b>" + i + "</b> : " + event.values[i] + "<br/>";
		}
		
		for ( int i = 0 ; i < deviceSensors.size() ; i+= 1 ) {
			if ( currentSensor.equals(deviceSensors.get(i)) )
				txvws.get(i).setText(Html.fromHtml(text));
		}
		
		*/

	}

	//@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	protected void onResume() {
	    super.onResume();

	    /*
	    Iterator<Sensor> itr = deviceSensors.iterator();
	    while (itr.hasNext()) {
		    mSensorManager.registerListener(this, itr.next(), SensorManager.SENSOR_DELAY_UI);
	    }*/

	    // TODO : NOT WITH 16
	    /*Iterator<Sensor> itr2 = deviceSensors.iterator();
	    while (itr2.hasNext()) {
	    	Sensor s = itr2.next();
		    mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
	        mSensorManager.requestTriggerSensor(mListener, s);
	        Toast.makeText(this, s.getName(), Toast.LENGTH_SHORT).show();
	    }*/

	}

	// @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	protected void onPause() {
	    super.onPause();
	    /*
	    mSensorManager.unregisterListener(this);
	    */
	    // TODO : NOT WITH 16
	    /* Iterator<Sensor> itr2 = deviceSensors.iterator();
	    while (itr2.hasNext()) {
	    	mSensorManager.cancelTriggerSensor(mListener, itr2.next());
	    }*/
	}
	
	private TextView caseSensor(Sensor s) {
		final Sensor sensor = s;
		
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    llp.setMargins(0, 0, 0, 24);
	    
        TextView txvw = new TextView(getApplicationContext());
        
        txvw.setActivated(true);
        txvw.setTextColor(Color.BLACK);
        txvw.setText(Html.fromHtml(enTete(sensor)));
	    txvw.setCompoundDrawablesWithIntrinsicBounds(0, 0, SensorInfos.getIcon(sensor.getType()), 0);
	    txvw.setPadding(16, 8, 16, 8);
	    txvw.setLayoutParams(llp);
	    txvw.setBackgroundColor(backColor);
	    txvw.setOnClickListener(
        		new OnClickListener() {
					@Override
					public void onClick(View v) {
						if ( sensor.getType() == Sensor.TYPE_SIGNIFICANT_MOTION ) {
							Intent intent = new Intent(getApplicationContext(), TriggerActivity.class);
							intent.putExtra("trigger_type", sensor.getType());
							startActivity(intent);
						} else {
							Intent intent = new Intent(getApplicationContext(), SensorActivity.class);
							intent.putExtra("sensor_type", sensor.getType());
							startActivity(intent);
						}
					}});

	    return txvw;
	}
	
}



