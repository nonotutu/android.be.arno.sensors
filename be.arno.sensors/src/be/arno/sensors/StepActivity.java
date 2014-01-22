package be.arno.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

@TargetApi(19)
public class StepActivity extends Activity  implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor sensorStepCounter;
	private Sensor sensorStepDetector;
	
	private int[] backColors;
	private int backColor = 0;
	
	private TextView txvwName;
	private TextView txvwSteps;
	private RelativeLayout rela;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Enlève la barre de titre
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_step);
		
		txvwName = (TextView)findViewById(R.id.step_txvwName);
		txvwSteps = (TextView)findViewById(R.id.step_txvwSteps);
		rela = (RelativeLayout)findViewById(R.id.step_rela);
		
		backColors = new int[]{
				getResources().getColor(R.color.a_light_blue),
				getResources().getColor(R.color.a_light_purple),
				getResources().getColor(R.color.a_light_green),
				getResources().getColor(R.color.a_light_orange),
				getResources().getColor(R.color.a_light_red)
			    };

		rela.setBackgroundColor(backColors[backColor]);

	    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		sensorStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

		sensorManager.registerListener( this , sensorStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener( this , sensorStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
		
		txvwName.setText(sensorStepCounter.getName());
		txvwName.append("\n" + sensorStepDetector.getName());
		
	}

	
	@Override
	public void onDestroy() {
		super.onDestroy();
		sensorManager.unregisterListener( this );
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.step, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if ( event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR ) {
			backColorPlusOne();
			rela.setBackgroundColor(backColors[backColor]);
		}
		
		if ( event.sensor.getType() == Sensor.TYPE_STEP_COUNTER ) {
			txvwSteps.setText("" + (int) event.values[0]);
		}
	}
	
	private void backColorPlusOne() {
		backColor += 1;
		if ( backColor >= backColors.length )
			backColor = 0;
	}

}
