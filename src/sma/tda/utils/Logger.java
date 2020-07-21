package sma.tda.utils;

public class Logger {
	private static final Object INSTANCE_LOCK = new Object();

	private static Logger instance = null;

	private boolean mLoggingDebug = false;

	public static Logger getInstance() {
		if (instance == null) {
			synchronized (INSTANCE_LOCK) {
				instance = new Logger();
			}
		}
		return instance;
	}

	public void logDebug(Object message) {
		if (isLoggingDebug()) {
			System.out.println("DEBUG - " + message);
		}
	}

	public void logError(String message) {
		System.err.println(message);
	}

	public void logError(String message, Throwable t) {
		logError(message);
		t.printStackTrace();
	}

	public boolean isLoggingDebug() {
		return this.mLoggingDebug;
	}

	public void setLoggingDebug(boolean pLoggingDebug) {
		this.mLoggingDebug = pLoggingDebug;
	}
}
