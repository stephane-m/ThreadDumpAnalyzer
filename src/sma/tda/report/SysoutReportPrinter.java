package sma.tda.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sma.tda.conf.Configuration;
import sma.tda.entity.JVMThread;
import sma.tda.entity.RequestThread;
import sma.tda.entity.report.GlobalReport;
import sma.tda.entity.report.ThreadDumpReport;

public class SysoutReportPrinter implements ReportPrinter {
	private boolean mPrintThreadStack = false;

	public void printReport(GlobalReport report) {
		printSeparator();
		printCpuConsumingThreads(report);
		printSeparator();
		printLongRunningThread(report);
		printSeparator();
		printMultiCriteriaEndecaRequest(report);
	}

	private void printCpuConsumingThreads(GlobalReport report) {
		for (ThreadDumpReport tdReport : report.getThreadDumpReports()) {
			System.out.println("------------------------------");
			System.out.println("Thread dump #" + tdReport.getMThreadDump().getIndex());
			for (Map.Entry<Float, JVMThread> entry : (Iterable<Map.Entry<Float, JVMThread>>) tdReport.getMThreadByCpu()
					.entrySet())
				System.out.println("CPU " + entry.getKey() + "% - " + entry.getValue());
			StringBuilder builder = new StringBuilder();
			builder.append("Ajp total=").append(tdReport.getAjpThread().size());
			builder.append(", with path=").append(tdReport.getAjpThreadWithPath().size());
			builder.append(", socket read=").append(tdReport.getAjpThreadSocketRead().size());
			System.out.println(builder);
			System.out.println();
		}
	}

	private void printLongRunningThread(GlobalReport report) {
		System.out.println("Long running threads:");
		System.out.println("------------------------------");
		System.out.println("Threads lasting over " + report.getThreadDumpReports().size() + " thread dumps:");
		for (Map.Entry<String, List<ThreadDumpReport>> entry : (Iterable<Map.Entry<String, List<ThreadDumpReport>>>) report
				.getLongRunningThreads().entrySet()) {
			if ((entry.getValue().size() == report.getThreadDumpReports().size())) {
				System.out.println(entry.getKey());
				for (JVMThread t : ((ThreadDumpReport) report.getThreadDumpReports().get(0)).getAjpThreadWithPath()) {
					String k = String.valueOf(t.getTid()) + " - " + t.getName();
					if (this.mPrintThreadStack && k.equals(entry.getKey()))
						printThreadStack(t, 10);
				}
			}
		}
		System.out.println("------------------------------");
		System.out.println("Threads lasting over " + (report.getThreadDumpReports().size() - 1) + " thread dumps:");
		for (Map.Entry<String, List<ThreadDumpReport>> entry : (Iterable<Map.Entry<String, List<ThreadDumpReport>>>) report
				.getLongRunningThreads().entrySet()) {
			if ((entry.getValue()).size() == report.getThreadDumpReports().size() - 1) {
				System.out.println(entry.getKey());
			}
		}
	}

	private void printMultiCriteriaEndecaRequest(GlobalReport report) {
		System.out.println("Multi criteria Endeca requets:");
		List<String> alreadyPrinted = new ArrayList<>();
		Pattern p = Pattern.compile(Configuration.getInstance().getMultiCriteriaEndecaRequestRegex());
		for (ThreadDumpReport tdReport : report.getThreadDumpReports()) {
			for (RequestThread r : tdReport.getAjpThreadWithPath()) {
				Matcher m = p.matcher(r.getPath());
				if (m.matches() && !alreadyPrinted.contains(r.getPath())) {
					System.out.println(r.getPath());
					alreadyPrinted.add(r.getPath());
				}
			}
		}
	}

	private void printSeparator() {
		System.out.println();
		System.out.println("==================================================");
	}

	private static void printThreadStack(JVMThread thread, int numberOfLines) {
		int count = 0;
		for (String l : thread.getLines()) {
			System.out.println(l);
			if (count++ > numberOfLines)
				break;
		}
	}
}
