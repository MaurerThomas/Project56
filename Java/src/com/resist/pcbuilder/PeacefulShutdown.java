package com.resist.pcbuilder;

import java.util.logging.Level;

/**
 * This class tries to stop the program when it receives a shutdown signal.
 */
public class PeacefulShutdown implements Runnable {
	private PcBuilder pcbuilder;

	public PeacefulShutdown(PcBuilder pcbuilder) {
		this.pcbuilder = pcbuilder;
	}

	@Override
	public void run() {
		PcBuilder.LOG.log(Level.INFO, "Shutting down...\n       |\n      /'\\\n     | 0 |\n      \\_/\n     / | \\");
		pcbuilder.stop();
	}
}