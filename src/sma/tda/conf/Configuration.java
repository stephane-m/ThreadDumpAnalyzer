package sma.tda.conf;

import java.io.IOException;
import java.util.Properties;
import sma.tda.utils.Logger;


public class Configuration implements ParserConfiguration {
	private static final Object INSTANCE_LOCK = new Object();

	private static final String CONFIG_FILE_PATH = "sma/tda/conf/Configuration.properties";

	private static Configuration instance = null;

	private Properties mProperties = null;

	private Configuration() {
		initProperties();
	}

	public void overrideProperties(Properties pOverrideProperties) {
		pOverrideProperties.entrySet().stream().forEach(entry -> {
			if (this.mProperties.stringPropertyNames().contains(entry.getKey()))
				this.mProperties.setProperty((String) entry.getKey(), (String) entry.getValue());
		});
	}

	private void initProperties() {
		this.mProperties = new Properties();
		try {
			this.mProperties.load(Configuration.class.getClassLoader().getResourceAsStream(CONFIG_FILE_PATH));
			Logger.getInstance().logDebug(this.mProperties);
		} catch (IOException e) {
			System.err.println("ERROR loading properties");
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Configuration getInstance() {
		if (instance == null) {
			synchronized (INSTANCE_LOCK) {
				instance = new Configuration();
			}
		}
		return instance;
	}

	public String getStringProperty(String mPropertyName) {
		return this.mProperties.getProperty(mPropertyName);
	}

	public String getTopStartLineRegex() {
		return getStringProperty("topStartLineRegex");
	}

	public String getJbossProcessLineRegex() {
		return getStringProperty("jbossProcessLineRegex");
	}

	public String getThreadDumpStartLineRegex() {
		return getStringProperty("threadDumpStartLineRegex");
	}

	public String getThreadStartLineRegex() {
		return getStringProperty("threadStartLineRegex");
	}

	public String getThreadDumpDateLineRegex() {
		return getStringProperty("threadDumpDateLineRegex");
	}

	public String getThreadDumpTimeFormatPattern() {
		return getStringProperty("threadDumpTimeFormatPattern");
	}

	public String getThreadDumpDateFormatTimeZone() {
		return getStringProperty("threadDumpDateFormatTimeZone");
	}

	public float getCpuUsageTresholdForReport() {
		return Float.parseFloat(getStringProperty("cpuUsageTresholdForReport"));
	}

	public String getRequestThreadNameRegex() {
		return getStringProperty("requestThreadNameRegex");
	}

	public String getMultiCriteriaEndecaRequestRegex() {
		return getStringProperty("multiCriteriaEndecaRequestRegex");
	}

	public String getSessionAndUserRegex() {
		return getStringProperty("sessionAndUserRegex");
	}
}
