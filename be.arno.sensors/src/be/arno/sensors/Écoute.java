package be.arno.sensors;

public class Écoute {
	
	private long timestamp;
	public float[] values;
	
	public Écoute() {}
	
	public Écoute(long timestamp, float[] values) {
		this.timestamp = timestamp;
		this.values = values.clone();
	}
	
	public long getTS() {
		return this.timestamp;
	}
	/*
	public float[] getValues() {
		return this.values;
	}*/
	
}
