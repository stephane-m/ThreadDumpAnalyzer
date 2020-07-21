package sma.tda.analyzer;

import sma.tda.entity.report.GlobalReport;

/**
 * Report processors which scope covers the whole thread dump file (and hence multiple thread dump)
 * should implement this interface.
 */
public interface GlobalReportProcessor {
	void updateReport(GlobalReport paramGlobalReport);
}
