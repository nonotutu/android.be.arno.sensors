package be.arno.sensors;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AaaahService extends IntentService implements SensorEventListener {
	
	public AaaahService() {
		super("AaaahService");
		
	}
	
	public AaaahService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	private static final String LOG_TAG = "AaaahService";

	private SensorManager mSensorManager;
	private Sensor sensor;

	// private TextView txvw;
	
	private MediaPlayer mp;
	
	private List<Écoute> écoutes;
	private long écouteEnPause = 0;
	

	public void init() {
		Log.i(LOG_TAG, "init()");
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		écoutes = new ArrayList<Écoute>();
		
		mp = MediaPlayer.create(this, R.raw.aaaah);
	    
	}

	
	
	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	
	@Override
	public final void onSensorChanged(SensorEvent event) {

		// Sensor currentSensor = event.sensor;
		
		if ( écouteEnPause > 0 && écouteEnPause + ( 4 * 1000 ) < new GregorianCalendar().getTimeInMillis()) {
			écouteEnPause = 0;
			Log.d("écoute", "plus en pause");
		}
		
		if ( écouteEnPause == 0 ) {
			Écoute écoute = new Écoute(new GregorianCalendar().getTimeInMillis(), event.values);
			écoutes.add( écoute );
		}
		
		if ( écoutes.size() > 48 ) {
			écoutes.remove(0);
		}
		
		if ( écoutes.size() == 48 ) {
			
			int[] nbPics = {0,0};
			int nbHors = 0;
			
			if ( écoutes.get(23).values[2] > ( 9.5 + 5 ) || écoutes.get(23).values[2] < ( 9.5 - 5 ) ) {
				
				Log.d(LOG_TAG, "tilt");
					
				for ( int i = 0 ; i < 48 ; i += 1 ) {

					if ( écoutes.get(i).values[2] > ( 9.5 + 5 ) )
						nbPics[0] += 1;
					if ( écoutes.get(i).values[2] < ( 9.5 - 5 ) )
						nbPics[1] += 1;
					
					if ( 	   écoutes.get(i).values[0] > ( 0.5 + 8 )
							|| écoutes.get(i).values[0] < ( 0.5 - 8 )
							|| écoutes.get(i).values[1] > ( 0.5 + 8 )
							|| écoutes.get(i).values[1] < ( 0.5 - 8 ) ) {
						nbHors += 1;
					}
					
				}
				
				Log.d(LOG_TAG, "nbPics : " + nbPics[0] + ", " + nbPics[1]);
				Log.d(LOG_TAG, "nbHors : " + nbHors);
				
			}
			
			if ( nbPics[0] > 2 && nbPics[1] > 2 && nbHors == 0 ) {
				écoutes.clear();
				écouteEnPause = new GregorianCalendar().getTimeInMillis();
				// Toast.makeText(getApplicationContext(), "TILT", Toast.LENGTH_SHORT).show();
				Log.d("écoute", "en pause");
		        mp.start();
				
			}
		}
	}



	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(LOG_TAG, "void onHandleIntent(Intent)");
		
	    init();
		mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
	}

	/*
	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "void onDestroy()");
	    mSensorManager.unregisterListener(this);
	}*/

}
