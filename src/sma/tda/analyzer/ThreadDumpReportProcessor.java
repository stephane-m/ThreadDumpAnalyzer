package sma.tda.analyzer;

import java.util.Map;
import sma.tda.entity.ThreadDump;
import sma.tda.entity.report.ThreadDumpReport;

/**
 * Report processors which scope is only one thread dump should implement this interface.
 */
public interface ThreadDumpReportProcessor {
	void updateReport(ThreadDumpReport paramThreadDumpReport, ThreadDump paramThreadDump, Map<String, Object> paramMap);
}
