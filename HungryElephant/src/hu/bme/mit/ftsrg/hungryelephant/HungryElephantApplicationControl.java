package hu.bme.mit.ftsrg.hungryelephant;

public class HungryElephantApplicationControl implements HungryElephantApplicationControlMBean {
	
	HungryElephantApplication hea=null;
	
	public HungryElephantApplicationControl(HungryElephantApplication hea){
		this.hea=hea;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return this.hea.port;
	}
	
	public void exit() {
		System.exit(0);
	}

	@Override
	public int getUserNumber() {
		return this.hea.getModel().users().size();
	}

	@Override
	public int getRestaurantNumber() {
		return this.hea.getModel().restaurants().size();
	}
	
}
