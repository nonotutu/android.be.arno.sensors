
package be.arno.sensors;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public class MultiStatesToggle extends Button {
	
		private static final String LOG_TAG = "FourStatesToggle";

	    int _state = 0;
	    int maxState = 1;
	    
	    int[] valeurs = {0,1};
	    String[] textes = {"OFF", "ON"};
	    
	    public MultiStatesToggle(Context context) {
	        super(context);
	        //_state = 0;
	        //maxState = 2;
	        setButtonText();
	    }

	    public void setContent(int[] valeurs, String[] textes) {
	        _state = 0;
	        maxState = Math.min(valeurs.length, textes.length)-1;
	        this.valeurs = valeurs;
	        this.textes = textes;
	        setButtonText();
	    }

	    public MultiStatesToggle(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        //_state = 0;
	        setButtonText();
	    }

	    public MultiStatesToggle(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	        //_state = 0;
	        setButtonText();
	    }

	    @Override
	    public boolean performClick() {
	        nextState();
	        return super.performClick();
	    }

	    private void nextState() {
	        _state++;
	        if (_state > maxState ) {
	            _state = 0;
	        }
	        setButtonText();
	    }

	    private void setButtonText() {
	    	this.setText(textes[_state]);
	    }

	    public int getState() {
	        return _state;
	    }

	    public int getValue() {
	        return valeurs[_state];
	    }
	    
	    public void setStateFromValue(int value) {
	    	for ( int i = 0 ; i < maxState + 1 ; i += 1 )
	    		if ( valeurs[i] == value ) {
	    			this._state = i;
	    			break; }
	    	setButtonText();
	    }
	    
	}
	

