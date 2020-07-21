package sma.tda.analyzer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import sma.tda.conf.Configuration;
import sma.tda.entity.JVMThread;
import sma.tda.entity.ThreadDump;
import sma.tda.entity.Top;
import sma.tda.entity.report.ThreadDumpReport;


/**
 * Processor comparing thread from thread dump to top processes from top file, in order
 * to identify the most cpu consuming threads.
 */
public class CpuConsumingThreadReportProcessor implements ThreadDumpReportProcessor {
	public static final String TOP_PROCESS_LIST_KEY = "topProcessList";

	public static final String TOP_FILE_DATE_KEY = "topFileDate";

	public static final String CPU_TRESHOLD_KEY = "cpuThreshold";

	@Override
	public void updateReport(ThreadDumpReport pThreadDumpReport, ThreadDump pThreadDump,
			Map<String, Object> pAdditionalInfo) {
		@SuppressWarnings("unchecked")
		List<Top> tops = (List<Top>) pAdditionalInfo.get("topProcessList");
		Top top = getClosestTopByTime(tops, pThreadDump);
		pThreadDumpReport.setTop(top);
		identifyCpuConsumingThreads(pThreadDumpReport, top, pThreadDump,
				Configuration.getInstance().getCpuUsageTresholdForReport());
	}

	public Top getClosestTopByTime(List<Top> pTopList, ThreadDump pThreadDump) {
		long threadDumpTime = pThreadDump.getDate().getTime();
		long timeDistance = Long.MAX_VALUE;
		Top selectedTop = null;
		for (Top top : pTopList) {
			long topTime = top.getDate().getTime();
			long currentDistance = Math.abs(topTime - threadDumpTime);
			if (timeDistance > currentDistance) {
				timeDistance = currentDistance;
				selectedTop = top;
			}
		}
		return selectedTop;
	}

	public void identifyCpuConsumingThreads(ThreadDumpReport pThreadDumpReport, Top pTop, ThreadDump pThreadDump,
			float pCpuThreshold) {
		SortedMap<Float, JVMThread> threadByCpu = new TreeMap<>(Collections.reverseOrder());
		pThreadDumpReport.setMCpuUsageTreshold(pCpuThreshold);
		for (Map.Entry<Integer, Float> entry : (Iterable<Map.Entry<Integer, Float>>) pTop.getProcesses().entrySet()) {
			if (((Float) entry.getValue()).floatValue() > pCpuThreshold) {
				String processIdHex = Integer.toHexString(((Integer) entry.getKey()).intValue());
				threadByCpu.put(entry.getValue(), pThreadDump.getThreadByNid("0x" + processIdHex));
			}
		}
		pThreadDumpReport.setMThreadByCpu(threadByCpu);
	}
}
