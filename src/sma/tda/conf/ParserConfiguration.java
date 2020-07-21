package sma.tda.conf;


/**
 * Configuration component used to configure parsers must implements this interface.
 */
public interface ParserConfiguration {
	
	String getTopStartLineRegex();

	String getJbossProcessLineRegex();

	String getThreadDumpDateLineRegex();

	String getThreadDumpStartLineRegex();

	String getThreadStartLineRegex();

	String getThreadDumpTimeFormatPattern();

	String getThreadDumpDateFormatTimeZone();
}
