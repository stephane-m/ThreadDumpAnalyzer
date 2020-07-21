package sma.tda.entity.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class GlobalReport {

	private List<ThreadDumpReport> threadDumpReports = new ArrayList<>();

	private Map<String, List<ThreadDumpReport>> longRunningThreads = new HashMap<>();

	public void addThreadDumpReport(ThreadDumpReport pThreadDumpReport) {
		this.threadDumpReports.add(pThreadDumpReport);
	}
}
