package hu.bme.mit.ftsrg.hungryelephant.handler;



public class BeeperController implements BeeperControllerMBean {
	
	private JMXBeeper beep = null;

	public BeeperController(JMXBeeper beep) {
		this.beep = beep;
	}

	public boolean getMuted() {
		return beep.muted;
	}

	public void setMuted(boolean muted) {
		beep.muted = muted;
		System.out.println(String.format(
				"Mute updated to %b, so currently it is %s", beep.muted,
				(beep.muted ? "muted" : "not muted")));
	}

	public int getCycleTimeInMsec() {
		return beep.cycleTimeInMsec;
	}

	public void setCycleTimeInMsec(int cycleTimeInMsec) {
		if (cycleTimeInMsec < 500)
			beep.cycleTimeInMsec = 500;
		else if (cycleTimeInMsec > 100000)
			beep.cycleTimeInMsec = 100000;
		else
			beep.cycleTimeInMsec = cycleTimeInMsec;

		System.out.println(String.format(
				"Cycle time updated to %d milliseconds", beep.cycleTimeInMsec));
	}

	public void exit() {
		beep.shouldExit = true;
	}

}
