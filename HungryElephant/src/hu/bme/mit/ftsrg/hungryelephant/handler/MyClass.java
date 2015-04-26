package hu.bme.mit.ftsrg.hungryelephant.handler;

public class MyClass implements MyClassMBean {

	private int state = 0;
	private String hidden = null;
	
	public void warble(){}
	
	public String getHidden(){return hidden;}
	public void setHidden(String h){hidden= h;}
	
	public void setState(int s) {state = s;}
	public int getState(){return state;}
	
	public void reset(){}


}
