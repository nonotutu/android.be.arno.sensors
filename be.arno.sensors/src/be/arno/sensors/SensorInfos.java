package be.arno.sensors;

import android.hardware.Sensor;


public final class SensorInfos {

	private static String unknown = "#_unknown_#";
	private static String notapplicable = "n/a";
	
	public static String getUnit(int type) {
		
		switch(type) {
		case Sensor.TYPE_ACCELEROMETER:
		case Sensor.TYPE_GRAVITY:
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return "m/s²";
		case Sensor.TYPE_MAGNETIC_FIELD:
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return "µT";
		case Sensor.TYPE_GYROSCOPE:
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return "rad/s";
		case Sensor.TYPE_LIGHT:
			return "lx";
		case Sensor.TYPE_PRESSURE:
			return "hPa";
		case Sensor.TYPE_TEMPERATURE:
			return "°C";
		case Sensor.TYPE_PROXIMITY:
			return "cm";
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return "%";
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			return "°C";
		case Sensor.TYPE_ORIENTATION:
			return "deg";
		case Sensor.TYPE_ROTATION_VECTOR:
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
			return "angle × sin(θ/2)";
		case Sensor.TYPE_STEP_COUNTER:
			return "step";
		case Sensor.TYPE_STEP_DETECTOR:
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return notapplicable;
		default:
			return unknown;
		}
			
		
	}

	public static String getName(int type) {
		
		switch(type) {
		case Sensor.TYPE_ACCELEROMETER:
			return "accelerometer"; 
		case Sensor.TYPE_MAGNETIC_FIELD:
			return "magnetic field";
		case Sensor.TYPE_ORIENTATION:
			return "orientation (deprecated)";
		case Sensor.TYPE_GYROSCOPE:
			return "gyroscope"; 
		case Sensor.TYPE_LIGHT:
			return "light";
		case Sensor.TYPE_PRESSURE:
			return "pressure";
		case Sensor.TYPE_TEMPERATURE:
			return "temperature (deprecated)";
		case Sensor.TYPE_PROXIMITY:
			return "proximity";
		case Sensor.TYPE_GRAVITY:
			return "gravity";
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return "linear acceleration";
		case Sensor.TYPE_ROTATION_VECTOR:
			return "rotation vector";
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return "relative humidity";
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			return "ambient temperature";
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return "magnetic field uncalibrated";
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
			return "game rotation vector";
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return "gyroscope uncalibrated";
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return "significant motion";
		case Sensor.TYPE_STEP_DETECTOR:
			return "step detector";
		case Sensor.TYPE_STEP_COUNTER:
			return "step counter";
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
			return "geomagnetic rotation vector";
		default:
			return unknown;
		}
	}
	
	public static int getIcon(int type){
		
		switch(type) {
		case Sensor.TYPE_ACCELEROMETER:
			return R.drawable.ic_sensor_accelerometer;
		case Sensor.TYPE_MAGNETIC_FIELD:
			return R.drawable.ic_sensor_magnetic;
		case Sensor.TYPE_ORIENTATION:
			return R.drawable.ic_sensor_orientation_thick;
		case Sensor.TYPE_GYROSCOPE:
			return R.drawable.ic_sensor_gyroscope;
		case Sensor.TYPE_LIGHT:
			return R.drawable.ic_sensor_light;
		case Sensor.TYPE_PRESSURE:
			return R.drawable.ic_sensor_pressure;
		case Sensor.TYPE_TEMPERATURE:
			return R.drawable.ic_sensor_temperature;
		case Sensor.TYPE_PROXIMITY:
			return R.drawable.ic_sensor_proximity;
		case Sensor.TYPE_GRAVITY:
			return R.drawable.ic_sensor_gravity;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return R.drawable.ic_sensor_linear_acceleration_2;
		case Sensor.TYPE_ROTATION_VECTOR:
			return R.drawable.ic_sensor_rotation;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return R.drawable.ic_sensor_humidity;
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			return R.drawable.ic_sensor_temperature;
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return R.drawable.ic_sensor_magnetic;
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
			return R.drawable.ic_sensor_rotation;
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return R.drawable.ic_sensor_gyroscope;
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return R.drawable.ic_sensor_significant_motion;
		case Sensor.TYPE_STEP_DETECTOR:
			return R.drawable.ic_sensor_step_detector;
		case Sensor.TYPE_STEP_COUNTER:
			return R.drawable.ic_sensor_step_counter;
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
			return R.drawable.ic_sensor_rotation;
		default:
			return -1;
		}
	}
	
	public static boolean isTrigger(int type) {
		
		switch(type) {
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return true;
		default:
			return false;
		}

	}
	
}
