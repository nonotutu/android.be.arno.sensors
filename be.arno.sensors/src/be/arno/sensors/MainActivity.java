package be.arno.sensors;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private static final String LOG_TAG = "MainActivity";
		
	@Override
	public final void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);

	    /*
	    Button bttnSensors = (Button)findViewById(R.id.main_bttnSensors);
	    bttnSensors.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent sensorsIntent = new Intent(getApplicationContext(), SensorsActivity.class);
						startActivity(sensorsIntent);
					}
				}
	    );*/

	    /*
	    Button bttnAaaah = (Button)findViewById(R.id.main_bttnAaaah);
	    bttnAaaah.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						//if ( ! isMyServiceRunning() )
						//{
							Intent aaaahIntent = new Intent(getApplicationContext(), AaaahService.class);
							startService(aaaahIntent);
						//}
					}
				}
	    );*/

	    
	}
}