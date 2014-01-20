package be.arno.sensors;

import java.util.GregorianCalendar;

import be.arno.sensors.graphing.Graphe;
import be.arno.sensors.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.RelativeLayout.LayoutParams;


public class SensorActivity extends Activity implements SensorEventListener {
	
	private static final String LOG_TAG = "SensorActivity";

	private SensorManager mSensorManager;
	private Sensor sensor;
	private int numberOfEvents;

	private int maxMarkers;
	private int xTimeFactor;
	private int yZoomFactor;
	private int lineWidth;
	private int pointSize;
	private int backColor;
	private MultiStatesToggle mstgSplit;
	private MultiStatesToggle mstgDelay;
	private MultiStatesToggle mstgMarkerType;
	private MultiStatesToggle mstgAxis;

	private int[] colors;

	boolean isFillScreen;
	
	private RelativeLayout rela;
	private RelativeLayout relaLegend;
	private TextView txvwName;
	private TextView txvwValues;
	private TextView txvwColors;
	private TextView txvwMaxMarkers;
	private Button bttnMaxMarkersPlus;
	private Button bttnMaxMarkersMinus;
	private TextView txvwXTimeFactor;
	private Button bttnXTimeFactorPlus;
	private Button bttnXTimeFactorMinus;
	private TextView txvwYZoomFactor;
	private Button bttnYZoomFactorPlus;
	private Button bttnYZoomFactorMinus;
	private TextView txvwLineWidth;
	private Button bttnLineWidthPlus;
	private Button bttnLineWidthMinus;
	private TextView txvwPointSize;
	private Button bttnPointSizePlus;
	private Button bttnPointSizeMinus;
	private Button bttnBackColor;
	
	private float xTimeFactors[] = {1,2,5,10,20};
	private float yZoomFactors[] = {1,2,3,4};
	private int lineWidths[] = {1,2,3,4,5,6,7,8};
	private int pointSizes[] = {1,2,3,4,5,6,7,8};
	private int backColors[][] = {
			new int[]{0xFF000000, 0xFFFFFFFF},
			new int[]{0xFF7F7F7F, 0xFFFFFFFF},
			new int[]{0xFFFFFFFF, 0xFF000000}
			};
	
	private static Graphe graphe;
	
	private RelativeLayout.LayoutParams grapheLayoutParams;
	private RelativeLayout.LayoutParams grapheLayoutParamsFillScreen;
	private int layoutPadding[];
	
	private ViewFlipper viewFlipper;
	private Animation mInFromRight;
    private Animation mOutToLeft;
    private Animation mInFromLeft;
    private Animation mOutToRight;
    
    private TextView[] txvwsColors;
    private LinearLayout lilaColors;
    private LinearLayout.LayoutParams custom_textview_params;
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	
	public void init() {
		Log.i(LOG_TAG, "init()");
		
		Intent intent = getIntent();
		int sensor_type = intent.getExtras().getInt("sensor_type");

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = mSensorManager.getDefaultSensor(sensor_type);
		numberOfEvents = 0;

	    isFillScreen = false;
	    
	    mstgDelay.setContent(
	    		new int[]{
	    				SensorManager.SENSOR_DELAY_NORMAL, 
	    				SensorManager.SENSOR_DELAY_UI,
	    				SensorManager.SENSOR_DELAY_GAME,
	    				SensorManager.SENSOR_DELAY_FASTEST},
	    		new String[]{
	    				getResources().getString(R.string.sensor_delay_normal),
	    				getResources().getString(R.string.sensor_delay_ui),
	    				getResources().getString(R.string.sensor_delay_game),
	    				getResources().getString(R.string.sensor_delay_fastest)});
	    
	    mstgSplit.setContent(
	    		new int[]{
	    				Graphe.MODE_MERGED, 
	    				Graphe.MODE_SPLIT,
	    				2},
	    		new String[]{
	    				getResources().getString(R.string.data_view_merged),
	    				getResources().getString(R.string.data_view_split),
	    				getResources().getString(R.string.data_view_average)});
	    
	    mstgMarkerType.setContent(
	    		new int[]{
	    				Graphe.MARKER_TYPE_LINE,
	    				Graphe.MARKER_TYPE_LINEnPOINT,
	    				Graphe.MARKER_TYPE_POINT},
	    		new String[]{
	    				getResources().getString(R.string.marker_type_line),
	    				getResources().getString(R.string.marker_type_line_point),
	    				getResources().getString(R.string.marker_type_point)});
	    
	    mstgAxis.setContent(
	    		new int[]{
	    				Graphe.DRAW_AXIS,
	    				Graphe.DRAW_SECONDS,
	    				Graphe.NO_AXIS},
	    		new String[]{
	    				getResources().getString(R.string.draw_axis),
	    				getResources().getString(R.string.draw_seconds),
	    				getResources().getString(R.string.no_axis)});


	    SharedPreferences settings = getSharedPreferences("sensor_prefs" + sensor.getType(), 0);
		
  	    maxMarkers = settings.getInt( "max_markers", 100 );
  	    xTimeFactor = settings.getInt( "x_time_factor", 0 );
  	    yZoomFactor = settings.getInt( "y_zoom_factor", 0 );
  	    mstgSplit.setStateFromValue( settings.getInt( "split", 0 ) );
  		mstgDelay.setStateFromValue( settings.getInt( "delay", SensorManager.SENSOR_DELAY_NORMAL ) );
	    lineWidth = settings.getInt( "line_width", 3);
	    pointSize = settings.getInt( "point_size", 5);
	    mstgMarkerType.setStateFromValue( settings.getInt( "marker_type", Graphe.MARKER_TYPE_LINE ) );
	    backColor = settings.getInt( "back_color", 0);
	    mstgAxis.setStateFromValue( settings.getInt( "draw_axis", 0 ) );
  		
	    colors = new int[]{
        		settings.getInt( "colors_0", getResources().getColor(R.color.a_light_blue)),
        		settings.getInt( "colors_1", getResources().getColor(R.color.a_light_purple)),
        		settings.getInt( "colors_2", getResources().getColor(R.color.a_light_green)),
        		settings.getInt( "colors_3", getResources().getColor(R.color.a_light_orange)),
        		settings.getInt( "colors_4", getResources().getColor(R.color.a_light_red)),
        		settings.getInt( "colors_5", getResources().getColor(R.color.a_dark_blue)),
        		settings.getInt( "colors_6", getResources().getColor(R.color.a_dark_purple)),
        		settings.getInt( "colors_7", getResources().getColor(R.color.a_dark_green)),
        		settings.getInt( "colors_8", getResources().getColor(R.color.a_dark_orange)),
        		settings.getInt( "colors_9", getResources().getColor(R.color.a_dark_red))
        		};
	    
		txvwMaxMarkers.setText("" + maxMarkers);
	    txvwXTimeFactor.setText("" + xTimeFactors[xTimeFactor]);
	    txvwYZoomFactor.setText("" + yZoomFactors[yZoomFactor]);
	    txvwLineWidth.setText("" + lineWidths[lineWidth]);
	    txvwPointSize.setText("" + pointSizes[pointSize]);

	    graphe.setMaxValue(sensor.getMaximumRange());
	    graphe.setColors(colors);

	    graphe.setMaxMarkers(maxMarkers);
	    graphe.setXTimeFactor(xTimeFactors[xTimeFactor]);
	    graphe.setYZoomFactor(yZoomFactors[yZoomFactor]);

	    graphe.changeAxisWidth(3);
	    graphe.changeMarkerLineWidth(lineWidths[lineWidth]);
	    graphe.changeMarkerPointSize(pointSizes[pointSize]);
	    graphe.setMarkerType(mstgMarkerType.getValue());
	    graphe.setMode(mstgSplit.getValue());

		graphe.setBackgroundColor(backColors[backColor][0]);
		graphe.changeAxisColor(backColors[backColor][1]);
		graphe.setDrawAxis(mstgAxis.getValue());
	    
	    txvwName.setTextColor(backColors[backColor][1]);
	    txvwValues.setTextColor(backColors[backColor][1]);

	    txvwName.setText(sensor.getName());
	    bttnBackColor.setBackgroundColor(backColors[backColor][0]);
	    bttnBackColor.setTextColor(backColors[backColor][1]);
	    
	    lilaColors = (LinearLayout)findViewById(R.id.sensor_lilaColors);
	    
	    custom_textview_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    custom_textview_params.weight = 1;
	    custom_textview_params .setMargins(
	    		getResources().getDimensionPixelSize(R.dimen.custom_textview_margin),
        		getResources().getDimensionPixelSize(R.dimen.custom_textview_margin),
        		getResources().getDimensionPixelSize(R.dimen.custom_textview_margin),
        		getResources().getDimensionPixelSize(R.dimen.custom_textview_margin));
        generateColors();
	}
	
	
	private void generateColors() {
		
		lilaColors.removeAllViews();
		
	    final TextView txvwColors[] = new TextView[10];
	    for ( int i = 0 ; i < 10 ; i += 1 ) {
	    	TextView txvw = new TextView(getApplicationContext());
	        txvw.setActivated(true);
	        txvw.setLayoutParams(custom_textview_params );
		    txvw.setBackgroundColor(colors[i]);
		    final int ii = i;
		    txvw.setOnClickListener(
	        		new OnClickListener() {
						@Override
						public void onClick(View v) {

							if ( ii != 0 ) {
							
								int temp = colors[ii];
								
								colors[ii] = colors[0];
								colors[0] = temp;

								generateColors();
								graphe.changeColors(colors);
								numberOfEventsOrColorsHasChanged();
							}
							
						}});
		    txvwColors[i] = txvw;
		    lilaColors.addView(txvw);
	    }
	
	}
	
	
	public void switchDisplay() {
		Log.d(LOG_TAG, "void switchDisplay()");
		
		if ( isFillScreen ) {
		
			rela.removeView(viewFlipper);
			rela.setPadding(0, 0, 0, 0);
			graphe.setLayoutParams(grapheLayoutParamsFillScreen);
			
		} else {

			rela.setPadding(layoutPadding[0], layoutPadding[1], layoutPadding[2], layoutPadding[3]);
			graphe.setLayoutParams(grapheLayoutParams);
			rela.addView(viewFlipper);
			relaLegend.setVisibility(View.VISIBLE);
		}
	}
	
	
	public void numberOfEventsOrColorsHasChanged() {
		
		String carrés = "";

		for ( int i = 0 ; i < numberOfEvents ; i += 1 )
	    	carrés += "<font color='#" 
	    			+ Integer.toHexString(colors[i]-0xFF000000)
	    			+ "'>⬛</font><br/>";
	    
		txvwColors.setText(Html.fromHtml(carrés));
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "void onCreate(Bundle)");
		super.onCreate(savedInstanceState);
		// Enlève la barre de titre
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sensor);		
			    
		rela = 				   (RelativeLayout)   findViewById(R.id.sensor_rela);

        graphe =               (Graphe)           findViewById(R.id.sensor_graphe);
		
		relaLegend =           (RelativeLayout)   findViewById(R.id.sensor_relaLegend);
	    txvwName =             (TextView)         findViewById(R.id.sensor_txvwName);
	    txvwValues =           (TextView)         findViewById(R.id.sensor_txvwValues);
	    txvwColors =           (TextView)         findViewById(R.id.sensor_txvwColors);

	    txvwMaxMarkers =       (TextView)         findViewById(R.id.sensor_txvwMaxMarkers);
        bttnMaxMarkersPlus =   (Button)           findViewById(R.id.sensor_bttnMaxMarkersPlus);
        bttnMaxMarkersMinus =  (Button)           findViewById(R.id.sensor_bttnMaxMarkersMinus);
  	    txvwXTimeFactor =      (TextView)         findViewById(R.id.sensor_txvwXTimeFactor);
        bttnXTimeFactorPlus =  (Button)           findViewById(R.id.sensor_bttnXTimeFactorPlus);
        bttnXTimeFactorMinus = (Button)           findViewById(R.id.sensor_bttnXTimeFactorMinus);
	    txvwYZoomFactor =      (TextView)         findViewById(R.id.sensor_txvwYZoomFactor);
        bttnYZoomFactorPlus =  (Button)           findViewById(R.id.sensor_bttnYZoomFactorPlus);
        bttnYZoomFactorMinus = (Button)           findViewById(R.id.sensor_bttnYZoomFactorMinus);
	    txvwLineWidth =        (TextView)         findViewById(R.id.sensor_txvwLineWidth);
        bttnLineWidthPlus =    (Button)           findViewById(R.id.sensor_bttnLineWidthPlus);
        bttnLineWidthMinus =   (Button)           findViewById(R.id.sensor_bttnLineWidthMinus);
	    txvwPointSize =        (TextView)         findViewById(R.id.sensor_txvwPointSize);
        bttnPointSizePlus =    (Button)           findViewById(R.id.sensor_bttnPointSizePlus);
        bttnPointSizeMinus =   (Button)           findViewById(R.id.sensor_bttnPointSizeMinus);
	    mstgDelay =            (MultiStatesToggle)findViewById(R.id.sensor_mstgDelay);
        mstgSplit =            (MultiStatesToggle)findViewById(R.id.sensor_bttnSplit);
        mstgMarkerType =       (MultiStatesToggle)findViewById(R.id.sensor_mstgMarkerType);
        bttnBackColor =        (Button)           findViewById(R.id.sensor_bttnBackColor);
        mstgAxis =             (MultiStatesToggle)findViewById(R.id.sensor_mstgAxis);

        // Retient les paddings du graphe en Pas FillScreen
        layoutPadding = new int[4];
		layoutPadding[0] = ((RelativeLayout) graphe.getParent()).getPaddingLeft();
		layoutPadding[1] = ((RelativeLayout) graphe.getParent()).getPaddingTop();
		layoutPadding[2] = ((RelativeLayout) graphe.getParent()).getPaddingRight();
		layoutPadding[3] = ((RelativeLayout) graphe.getParent()).getPaddingBottom();
		
		// Retient les paramères de layout du Graphe
		grapheLayoutParams = (RelativeLayout.LayoutParams) graphe.getLayoutParams();

		// Prépare les paramètres de layout du Graphe en FillScreen
		grapheLayoutParamsFillScreen = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		grapheLayoutParamsFillScreen.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		grapheLayoutParamsFillScreen.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

	    mstgSplit.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						if ( mstgSplit.getValue() == Graphe.MODE_MERGED 
								|| mstgSplit.getValue() == Graphe.MODE_SPLIT )
							graphe.setMode( mstgSplit.getValue() );
					}
				});
	    
	    mstgMarkerType.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						graphe.setMarkerType( mstgMarkerType.getValue() );
					}
				});

	    bttnMaxMarkersPlus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						maxMarkers += 10;
						graphe.setMaxMarkers(maxMarkers);
						txvwMaxMarkers.setText(""+graphe.getMaxMarkers());
					}
				});

	    bttnMaxMarkersMinus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						maxMarkers -= 10;
						graphe.setMaxMarkers(maxMarkers);
						txvwMaxMarkers.setText(""+graphe.getMaxMarkers());
					}
				});
	    
	    txvwMaxMarkers.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						txvwMaxMarkers.setText(""+graphe.getNumberPoints());
					}
				});
	    
	    
	    graphe.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						isFillScreen = ! isFillScreen;
						switchDisplay();
					}
					
				});
	    
	    mstgDelay.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						onPause();
						onResume();
					}
				});
	    
	    bttnYZoomFactorMinus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						yZoomFactor = Math.max(0, yZoomFactor-1);
						graphe.setYZoomFactor(yZoomFactors[yZoomFactor]);
						txvwYZoomFactor.setText("" + yZoomFactors[yZoomFactor]);
					}
				});
	    
	    bttnYZoomFactorPlus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						yZoomFactor = Math.min(yZoomFactors.length-1, yZoomFactor+1);
						graphe.setYZoomFactor(yZoomFactors[yZoomFactor]);
						txvwYZoomFactor.setText("" + yZoomFactors[yZoomFactor]);
					}
				});

	    bttnXTimeFactorMinus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						xTimeFactor = Math.max(0, xTimeFactor-1);
						graphe.setXTimeFactor(xTimeFactors[xTimeFactor]);
						txvwXTimeFactor.setText("" + xTimeFactors[xTimeFactor]);
					}
				});
	    
	    bttnXTimeFactorPlus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						xTimeFactor = Math.min(xTimeFactors.length-1, xTimeFactor+1);
						graphe.setXTimeFactor(xTimeFactors[xTimeFactor]);
						txvwXTimeFactor.setText("" + xTimeFactors[xTimeFactor]);
					}
				});

	    bttnLineWidthMinus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						lineWidth = Math.max(0, lineWidth-1);
						graphe.changeMarkerLineWidth(lineWidths[lineWidth]);
						txvwLineWidth.setText("" + lineWidths[lineWidth]);
					}
				});
	    
	    bttnLineWidthPlus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						lineWidth = Math.min(lineWidths.length-1, lineWidth+1);
						graphe.changeMarkerLineWidth(lineWidths[lineWidth]);
						txvwLineWidth.setText("" + lineWidths[lineWidth]);
					}
				});

	    bttnPointSizeMinus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						pointSize = Math.max(0, pointSize-1);
						graphe.changeMarkerPointSize(pointSizes[pointSize]);
						txvwPointSize.setText("" + pointSizes[pointSize]);
					}
				});
	    
	    bttnPointSizePlus.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						pointSize = Math.min(pointSizes.length-1, pointSize+1);
						graphe.changeMarkerPointSize(pointSizes[pointSize]);
						txvwPointSize.setText("" + pointSizes[pointSize]);
					}
				});

	    txvwValues.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						relaLegend.setVisibility(View.INVISIBLE);
					}
				});
	    
	    bttnBackColor.setOnClickListener(
	    		new OnClickListener() {
	    			@Override
	    			public void onClick(View v) {
	    				backColor += 1;
	    				if ( backColor >= backColors.length )
	    					backColor = 0;
	    				graphe.setBackgroundColor(backColors[backColor][0]);
	    				graphe.changeAxisColor(backColors[backColor][1]);
	    				bttnBackColor.setBackgroundColor(backColors[backColor][0]);
	    			    bttnBackColor.setTextColor(backColors[backColor][1]);
	    			    txvwName.setTextColor(backColors[backColor][1]);
	    			    txvwValues.setTextColor(backColors[backColor][1]);
	    			}
	    		});
	    
	    mstgAxis.setOnClickListener(
	    		new OnClickListener() {
					@Override
					public void onClick(View v) {
						graphe.setDrawAxis(mstgAxis.getValue());
					}
				});
	    
	    viewFlipper = (ViewFlipper) findViewById(R.id.sensor_vwfp);
        viewFlipper.setDisplayedChild(0);
        
        final GestureDetector gestureDetector;
        gestureDetector = new GestureDetector(getApplicationContext(), new MyGestureDetector());

        initAnimation();
        
	    init();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
	      SharedPreferences settings = getSharedPreferences("sensor_prefs" + sensor.getType(), 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putInt( "max_markers", maxMarkers );
	      editor.putInt( "x_time_factor", xTimeFactor );
	      editor.putInt( "y_zoom_factor", yZoomFactor );
	      editor.putInt( "split", mstgSplit.getValue() );
	      editor.putInt( "delay", mstgDelay.getValue() );
	      editor.putInt( "line_width", lineWidth );
	      editor.putInt( "point_size", pointSize );
	      editor.putInt( "marker_type", mstgMarkerType.getValue() );
	      editor.putInt( "back_color", backColor );
	      editor.putInt( "draw_axis", mstgAxis.getValue() );
	      editor.putInt( "colors_0", colors[0] );
	      editor.putInt( "colors_1", colors[1] );
	      editor.putInt( "colors_2", colors[2] );
	      editor.putInt( "colors_3", colors[3] );
	      editor.putInt( "colors_4", colors[4] );
	      editor.putInt( "colors_5", colors[5] );
	      editor.putInt( "colors_6", colors[6] );
	      editor.putInt( "colors_7", colors[7] );
	      editor.putInt( "colors_8", colors[8] );
	      editor.putInt( "colors_9", colors[9] );
	      editor.commit();
	}
	
	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	
	@Override
	public final void onSensorChanged(SensorEvent event) {
		
		if ( event.values.length != numberOfEvents ) {
			numberOfEvents = event.values.length;
			numberOfEventsOrColorsHasChanged();
		}
		
		if ( txvwValues.getVisibility() == View.VISIBLE ) {
			String text = "";
			for ( int i = 0 ; i < event.values.length ; i += 1 )
				text += event.values[i] + "\n";
				// Log.i(LOG_TAG, "" + event.values[i]); }
			txvwValues.setText(text);
		}
		
		if ( mstgSplit.getValue() == 0 || mstgSplit.getValue() == 1 ) {
			graphe.addPoint(event.values, new GregorianCalendar().getTimeInMillis());
		} else if ( mstgSplit.getValue() == 2 ) {
			int value = 0;
			for ( int i = 0 ; i < event.values.length ; i += 1 )
				value += event.values[i];
			value /= event.values.length;		
			graphe.addPoint(new float[]{value}, new GregorianCalendar().getTimeInMillis());
		}
	}
	

	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	protected void onResume() {
		Log.i(LOG_TAG, "void onResume()");
	    super.onResume();
		mSensorManager.registerListener(this, sensor, mstgDelay.getValue() );
	}

	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	protected void onPause() {
		Log.i(LOG_TAG, "void onPause()");
	    super.onPause();
	    mSensorManager.unregisterListener(this);
	}
	
	private void initAnimation() {
	        mInFromRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
	                +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
	                Animation.RELATIVE_TO_PARENT, 0.0f,
	                Animation.RELATIVE_TO_PARENT, 0.0f);
	        mInFromRight.setDuration(500);
	        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
	        mInFromRight.setInterpolator(accelerateInterpolator);

	        mInFromLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
	                -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
	                Animation.RELATIVE_TO_PARENT, 0.0f,
	                Animation.RELATIVE_TO_PARENT, 0.0f);
	        mInFromLeft.setDuration(500);
	        mInFromLeft.setInterpolator(accelerateInterpolator);

	        mOutToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
	                0.0f, Animation.RELATIVE_TO_PARENT, +1.0f,
	                Animation.RELATIVE_TO_PARENT, 0.0f,
	                Animation.RELATIVE_TO_PARENT, 0.0f);
	        mOutToRight.setDuration(500);
	        mOutToRight.setInterpolator(accelerateInterpolator);

	        mOutToLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
	                Animation.RELATIVE_TO_PARENT, -1.0f,
	                Animation.RELATIVE_TO_PARENT, 0.0f,
	                Animation.RELATIVE_TO_PARENT, 0.0f);
	        mOutToLeft.setDuration(500);
	        mOutToLeft.setInterpolator(accelerateInterpolator);

	        final GestureDetector gestureDetector;
	        gestureDetector = new GestureDetector(getApplicationContext(), new MyGestureDetector());

	        viewFlipper.setOnTouchListener(new OnTouchListener() {

	            public boolean onTouch(View v, MotionEvent event) {
	                if (gestureDetector.onTouchEvent(event)) {
	                    return false;
	                } else {
	                    return true;
	                }
	            }
	        });
	}
	

	private class MyGestureDetector extends SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            System.out.println(" in onFling() :: ");
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper.setInAnimation(mInFromRight);
                viewFlipper.setOutAnimation(mOutToLeft);
                viewFlipper.showNext();
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                viewFlipper.setInAnimation(mInFromLeft);
                viewFlipper.setOutAnimation(mOutToRight);
                viewFlipper.showPrevious();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

}



