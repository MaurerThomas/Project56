package com.resist.pcbuilder;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * An implementation of a log handling class that writes to the system's standard error and output streams based on the level of the error.
 */
public class LogHandler extends Handler {
	@Override
	public void close() throws SecurityException {}

	@Override
	public void flush() {
		System.out.flush();
		System.err.flush();
	}

	@Override
	public void publish(LogRecord record) {
		if(getFormatter() == null) {
			setFormatter(new SimpleFormatter());
		}
		String msg = getFormatter().format(record);
		if(record.getLevel().intValue() >= Level.WARNING.intValue()) {
			System.err.print(msg);
		} else {
			System.out.print(msg);
		}
	}
}
