/* Copyright (C) 2012 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package be.arno.sensors.graphing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class Graphe extends View {
	
	private static final String LOG_TAG = "Graphe extends ViewGroup";

	public static final int MODE_MERGED = 0;
	public static final int MODE_SPLIT = 1;
	
	public static final int MARKER_TYPE_LINE = 0;
	public static final int MARKER_TYPE_POINT = 1;
	public static final int MARKER_TYPE_LINEnPOINT = 2;

	public static final int DRAW_AXIS = 0;
	public static final int DRAW_SECONDS = 1;
	public static final int NO_AXIS = 2;
	
	// Calculés automatiquement
	private int w, h;
	private float yFactor;
	private int numberOfDatas = 0;

	// Paramétrables : aucune opération en aval
	private float maxValue = 0;
	private float xTimeFactor = 1;
	private float yZoomFactor = 1;
	private int mode = 0;        // MODE_MERGED
    private int marker_type = 0; // MARKER_TYPE_LINE
	private int max_markers = 100;
	private int marker_colors[];
	private int draw_axis = 0;
	
	// Paramétrables : opérations en aval
	private int back_color = 0xFF000000;
    private int axis_color = 0xFFFFFFFF;
    private int axis_width = 5;
    private int marker_line_width = 5;
    private int marker_point_size = 10;
    
    // Statiques
	private final int default_data_line_color = 0xFFFFFFFF;

	// Données et graphiques
	private long[] lastTimes;
	private float[] actualValues;
	private List<List<Point>> pointss;
	private List<Paint> linePaints;
	private List<Paint> pointPaints;
    private Paint xAxisPaint;
	

    private void init() {
    	Log.d(LOG_TAG, "void init()");
        setLayerToHW(this);
        
        lastTimes = new long[numberOfDatas];
        actualValues = new float[numberOfDatas];
        pointss = new ArrayList<List<Point>>();
        linePaints = new ArrayList<Paint>();
        pointPaints = new ArrayList<Paint>();
        
        xAxisPaint = new Paint();
        changeAxisColor(axis_color);
        changeAxisWidth(axis_width);

        setBackgroundColor(back_color);

    }
    
	
    public void setMaxValue(float maxValue) { this.maxValue = maxValue; }

	public void setXTimeFactor(float xTimeFactor) { this.xTimeFactor = xTimeFactor; }
	public float getXTimeFactor() { return this.xTimeFactor; }

	public void setYZoomFactor(float yZoomFactor) { this.yZoomFactor = yZoomFactor; }
	public float getYZoomFactor() { return this.yZoomFactor; }

	public int getMode() { return this.mode; }
	public void setMode(int mode) { this.mode = mode; }
	
	public void setMaxMarkers(int maxMarkers) { this.max_markers = Math.max(0,maxMarkers); }
	public int getMaxMarkers() { return max_markers; }
	
	public void setColors(int[] colors) { marker_colors = colors; }
	
	public void setMarkerType(int marker_type) { this.marker_type = marker_type; }
	
	public void setDrawAxis(int draw_axis) { this.draw_axis = draw_axis; }
	
	

	public void changeColors(int[] colors) {
	
		marker_colors = colors;
				
		for ( int i = 0 ; i < Math.min(colors.length, linePaints.size()) ; i += 1 ) {
	   		linePaints.get(i).setColor(colors[i]);
	    }
    	
		for ( int i = 0 ; i < Math.min(colors.length, linePaints.size()) ; i += 1 ) {
	   		pointPaints.get(i).setColor(colors[i]);
	    }
	}
	
	
	public int getNumberPoints() {
		int count = 0;
		for ( int i = 0 ; i < pointss.size() ; i += 1 ) {
			for ( int j = 0 ; j < pointss.get(i).size() ; j += 1 ) {
				count += 1;
			}
		}
		return count;
	}
	
	
	
    public Graphe(Context context) {
        super(context);
    }


    public Graphe(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	Log.d(LOG_TAG, "void onSizeChanged(...)");
    	
    	// Recalcul de la position des points si changement de taille
    	if ( oldw != 0 && oldh != 0 ) {
	    	float xDiff = w / (float) oldw;
	    	float yDiff = h / (float) oldh;
	    	for ( int i = 0 ; i < numberOfDatas ; i += 1 )
	    		for ( int j = 0 ; j < pointss.get(i).size() ; j += 1 ) {
						pointss.get(i).get(j).x *= xDiff;
						pointss.get(i).get(j).y *= yDiff;
	    		}
	    }

    	// Assignement des nouvelles valeurs relatives à la taille du canvas
    	this.w = w;
    	this.h = h;
    	this.yFactor = (h/2) / maxValue;

		super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	// Log.d(LOG_TAG, "void onLayout(...)");
    
        // Do nothing. Do not call the superclass method--that would start a layout pass
        // on this view's children. This lays out its children in onSizeChanged().
    }
    

    @Override
    protected void onDraw(Canvas canvas) {
    	//Log.d(LOG_TAG, "void onDraw(Canvas canvas)");
        super.onDraw(canvas);
        
        // Dessine un repère à 1, 4 et 10 secondes sur l'axe des x
        if ( draw_axis == DRAW_SECONDS )
	        for ( int i = 0 ; i < w * xTimeFactor ; i += 1000 )
	        	if ( i%10000 == 0 )
	        		canvas.drawLine(
	        				w - ( i / xTimeFactor ),
	        				h/2 - 20,
	        				w - ( i / xTimeFactor ),
	        				h/2 + 20,
	        				xAxisPaint);        
	        	else
					canvas.drawLine(
							w - ( i / xTimeFactor ),
							h/2 - 10,
							w - ( i / xTimeFactor ),
							h/2 + 10,
							xAxisPaint);

		// Dessine l'axe des x
        if ( draw_axis == DRAW_AXIS || draw_axis == DRAW_SECONDS )
			switch (mode) {
			case MODE_MERGED:
	        	canvas.drawLine(
					w,
					h/2,
					0,
					h/2,
					xAxisPaint);
	        	break;
			case MODE_SPLIT:
	        	for ( int i = 0 ; i < numberOfDatas ; i += 1 )
	        		canvas.drawLine(
	        				w,
	        				h/2/numberOfDatas + i * h/numberOfDatas,
	        				0,
	        				h/2/numberOfDatas + i * h/numberOfDatas,
	        				xAxisPaint);
	        	break;
			}

        for ( int i = 0 ; i < numberOfDatas ; i += 1 )
        	for ( int j = 1 ; j < pointss.get(i).size() ; j += 1 ) {
        		if ( marker_type == MARKER_TYPE_POINT || marker_type == MARKER_TYPE_LINEnPOINT )
        			canvas.drawPoint(pointss.get(i).get(j).x, pointss.get(i).get(j).y, pointPaints.get(i));
        		if ( marker_type == MARKER_TYPE_LINE || marker_type == MARKER_TYPE_LINEnPOINT )
	        		canvas.drawLine(
	        				pointss.get(i).get(j-1).x,
	        				pointss.get(i).get(j-1).y,
	        				pointss.get(i).get(j).x,
	        				pointss.get(i).get(j).y,
	        				linePaints.get(i));
        }}

    
    public void changeAxisColor(int color) {
        xAxisPaint.setColor(color);
    }
    
    /*
    public void changeBackColor(int color) {
        setBackgroundColor(color);
    }*/
    
    public void changeAxisWidth(int width) {
    	xAxisPaint.setStrokeWidth(width);
        xAxisPaint.setStyle(Paint.Style.STROKE);
    }
    
    public void changeMarkerLineWidth(int width) {
    	// Change la valeur par défaut, pour les potentielles nouvelles lignes de données
    	marker_line_width = width;
    	// Change la valeur des lignes de données déjà existantes
    	for ( Paint p : linePaints ) {
    		p.setStrokeWidth(width);
            p.setStyle(Paint.Style.STROKE);
    	}    	
    }

    
    public void changeMarkerPointSize(int size) {
    	// Change la valeur par défaut, pour les potentielles nouvelles lignes de données
    	marker_point_size = size;
    	// Change la valeur des lignes de données déjà existantes
    	for ( Paint p : pointPaints ) {
    		p.setStrokeWidth(size);
    	}    	
    }

    private void setLayerToSW(View v) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void setLayerToHW(View v) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }
    
    
    
    private void increaseNumberOfDatas(int newNumberOfDatas) {
    	Log.d(LOG_TAG, "void increaseNumberOfDatas(int newNumberOfDatas)");

    	// Agrandissement de _lastTimes_
    	long[] newLastTimes = new long[newNumberOfDatas];
	    System.arraycopy(lastTimes, 0, newLastTimes, 0, lastTimes.length);
    	lastTimes = newLastTimes.clone();
    	
    	// Agrandissement de _actualValues_
    	float[] newActualValues = new float[newNumberOfDatas];
	    System.arraycopy(actualValues, 0, newActualValues, 0, actualValues.length);
    	actualValues = newActualValues.clone();
    	
    	// Agrandissement de _pointss_
    	for ( int i = 0 ; i < ( newNumberOfDatas - numberOfDatas ) ; i += 1 )
    		pointss.add(new ArrayList<Point>());
    	
    	/* S'il y a plus de _data_ que de _pointPaints,
    	 * création de _linePaints_ blancs */
    	while ( newNumberOfDatas > linePaints.size() ) {
    		int c;
    		if ( marker_colors.length >= linePaints.size() )
    			c = marker_colors[linePaints.size()];
    		else
    			c = default_data_line_color;
    		linePaints.add(createLinePaint(c));
        }
    	
    	/* S'il y a plus de _data_ que de _pointPaints,
    	 * création de _pointPaints_ blancs */
    	while ( newNumberOfDatas > pointPaints.size() ) {
    		int c;
    		if ( marker_colors.length >= pointPaints.size() )
    			c = marker_colors[pointPaints.size()];
    		else
    			c = default_data_line_color;
            pointPaints.add(createPointPaint(c));
        }
    }
    
    
    private Paint createPointPaint(int color) {
    	Paint pointPaint = new Paint();
   		pointPaint.setColor(color);
   		pointPaint.setStrokeWidth(marker_point_size);
        return pointPaint;
    }
    
    
    private Paint createLinePaint(int color) {
    	Paint linePaint = new Paint();
   		linePaint.setColor(color);
   		linePaint.setStrokeWidth(marker_line_width);
        linePaint.setStyle(Paint.Style.STROKE);
        return linePaint;
    }
    
    
    
    // TODO
    private void decreaseNumberOfDatas(int newNumberOfDatas) {
    }
    
    
    public void addPoint(float[] values, long timeValue) {
    	//Log.d(LOG_TAG, "void addPoint(float[] values, long timeValue)");

    	// S'il y a une donnée en plus
    	if ( values.length > numberOfDatas ) {
    		increaseNumberOfDatas(values.length);
    		numberOfDatas = values.length;
    	}
    	
    	// S'il y a une donnée en moins
    	if ( values.length < numberOfDatas ) {
    		decreaseNumberOfDatas(values.length);
    		numberOfDatas = values.length;
    	}

    	actualValues = values;

    	for ( int idx = 0 ; idx < numberOfDatas ; idx += 1 ) {
    	
    		// Calcule la différence de distance avec le dernier point
	    	float diff = ( timeValue - lastTimes[idx] ) / xTimeFactor ;
	    	
	    	// Déplace vers la gauche tous les points
	    	for ( int i = 0 ; i < pointss.get(idx).size() ; i+=1 ) {
				pointss.get(idx).get(i).x -= diff;
			}
	
	    	// Définit la hauteur du nouveau point
	    	float y = mode==MODE_SPLIT?
	    			h/2/numberOfDatas + idx * h/numberOfDatas - (values[idx]*yFactor/numberOfDatas*yZoomFactor)
	    			:(h/2) - (values[idx]*yFactor*yZoomFactor);

	    	// Ajoute le dernier point en dernière position
	    	pointss.get(idx).add( new Point( w , y ) );
	    	
	    	
	    	/* Supprime les points en trop */
	    	// S'il y a au moins un point ...
	    	if ( pointss.get(idx).size() > 0 ) {
	    		// .... supprime les points dépassant la limite autorisée _maxPoints_
	    		if ( pointss.get(idx).size() > max_markers )
	    			for ( int i = pointss.get(idx).size() ; i > max_markers ; i -= 1 )
	    				pointss.get(idx).remove(0);
	    	}
	    	
	    	/* Supprime les points en trop */
	    	// S'il y a au moins deux points ...
	    	if ( pointss.get(idx).size() > 1 ) {
	    		/*  .... supprime tous les points plus à que le graphe,
	    		*        sauf le plus proche de 0 (pour le tracer de ligne) */
    			while ( pointss.get(idx).get(1).x < 0 )
	    			pointss.get(idx).remove(0);
	    		
	    	}
	    	
	    	// Enregistre la valeur temporelle du nouveau point pour le calcul de la distance
	    	lastTimes[idx] = timeValue;
	    	
    	}
   	
        setLayerToSW(this);

    }
   
    
    
    private class Point {
    	
    	public float x;
    	public float y;
    	
    	Point(float x, float y) {
    		this.x = x;
    		this.y = y;
    	}
    }

   
}
