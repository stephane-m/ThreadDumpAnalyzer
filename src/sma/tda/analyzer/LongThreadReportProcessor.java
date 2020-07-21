package sma.tda.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sma.tda.entity.JVMThread;
import sma.tda.entity.report.GlobalReport;
import sma.tda.entity.report.ThreadDumpReport;

/**
 * Processor in charge of identifying ajp thread that lasts over multiple thread dump.
 * A ajp thread is expected to last for few milliseconds or few seconds.
 * Each thread dump have dozen of seconds separating them, hence ajp thread lasting over
 * multiple thread dump are supposed to be long.
 */
public class LongThreadReportProcessor implements GlobalReportProcessor {
	
	@Override
	public void updateReport(GlobalReport pGlobalReport) {
		Map<String, Integer> result = new HashMap<>();
		for (ThreadDumpReport r : pGlobalReport.getThreadDumpReports()) {
			for (JVMThread thread : r.getAjpThreadWithPath()) {
				String key = String.valueOf(thread.getTid()) + " - " + thread.getName();
				if (!pGlobalReport.getLongRunningThreads().containsKey(key)) {
					pGlobalReport.getLongRunningThreads().put(key, new ArrayList<ThreadDumpReport>());
				}
				((List<ThreadDumpReport>) pGlobalReport.getLongRunningThreads().get(key)).add(r);
				if (result.containsKey(key)) {
					int newResult = ((Integer) result.get(key)).intValue() + 1;
					result.put(key, Integer.valueOf(newResult));
					continue;
				}
				result.put(key, Integer.valueOf(0));
			}
		}
	}
}
