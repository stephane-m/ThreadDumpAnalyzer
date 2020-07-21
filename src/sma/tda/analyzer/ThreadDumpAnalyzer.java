package sma.tda.analyzer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sma.tda.entity.ThreadDump;
import sma.tda.entity.Top;
import sma.tda.entity.report.GlobalReport;
import sma.tda.entity.report.ThreadDumpReport;
import sma.tda.parser.td.ThreadDumpParser;
import sma.tda.parser.top.TopFileParser;


/**
 * The main class in charge of analyzing the thread dump.
 * Two files will be parsed, the top file containing the list of processes and their cpu usage
 * and the thread dump file.
 */
public class ThreadDumpAnalyzer {
	
	// --- instance variables --------------------------------------------------
	
	/** The parser used to parse the top file. */
	private TopFileParser mTopFileParser = null;

	/** The parser used to parse the thread dump. */
	private ThreadDumpParser mThreadDumpParser = null;

	/**
	 * List of processors used to generate the global report, as a single thread dump file contains multiple thread dump.
	 */
	private List<GlobalReportProcessor> globalReportprocessors = null;

	/** List of processors used to generate the report for each thread dump. */
	private List<ThreadDumpReportProcessor> threadDumpReportprocessors = null;

	
	// --- constructors  -------------------------------------------------------
	
	public ThreadDumpAnalyzer(TopFileParser pTopFileParser, ThreadDumpParser pThreadDumpParser,
			List<GlobalReportProcessor> pGlobalReportprocessors, List<ThreadDumpReportProcessor> pThreadDumpReportprocessors) {
		this.mTopFileParser = pTopFileParser;
		this.mThreadDumpParser = pThreadDumpParser;
		this.globalReportprocessors = pGlobalReportprocessors;
		this.threadDumpReportprocessors = pThreadDumpReportprocessors;
	}
	
	
	// --- methods -------------------------------------------------------------

	public GlobalReport analyze(File pTopFile, File pThreadDumpFile) {
		List<Top> tops = this.mTopFileParser.parse(pTopFile);
		List<ThreadDump> threadDumps = this.mThreadDumpParser.parse(pThreadDumpFile);
		
		Map<String, Object> additionalProcessorInfo = new HashMap<>();
		additionalProcessorInfo.put("topProcessList", tops);
		
		GlobalReport report = new GlobalReport();
		
		for (ThreadDump td : threadDumps) {
			ThreadDumpReport tdReport = analyzeThreadDump(td, additionalProcessorInfo);
			report.addThreadDumpReport(tdReport);
		}
		
		updateglobalReport(report);
		
		return report;
	}

	public ThreadDumpReport analyzeThreadDump(ThreadDump pThreadDump, Map<String, Object> pAdditionProcessorInfo) {
		ThreadDumpReport report = new ThreadDumpReport(pThreadDump);
		
		if (this.threadDumpReportprocessors != null) {
			for (ThreadDumpReportProcessor proc : this.threadDumpReportprocessors) {
				proc.updateReport(report, pThreadDump, pAdditionProcessorInfo);
			}
		}
		return report;
	}

	public void updateglobalReport(GlobalReport pGlobalReport) {
		if (this.globalReportprocessors != null) {
			for (GlobalReportProcessor proc : this.globalReportprocessors) {
				proc.updateReport(pGlobalReport);
			}
		}
	}
}
