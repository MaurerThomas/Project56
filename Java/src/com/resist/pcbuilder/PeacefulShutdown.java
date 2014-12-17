package com.resist.pcbuilder;

public class PeacefulShutdown implements Runnable {
	private PcBuilder pcbuilder;

	public PeacefulShutdown(PcBuilder pcbuilder) {
		this.pcbuilder = pcbuilder;
	}

	@Override
	public void run() {
		System.out.println("Shutting down...");
		System.out.println("       |");
		System.out.println("      /'\\");
		System.out.println("     | 0 |");
		System.out.println("      \\_/");
		System.out.println("     / | \\");
		pcbuilder.stop();
	}
}