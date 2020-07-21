package sma.tda.analyzer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sma.tda.conf.Configuration;
import sma.tda.entity.JVMThread;
import sma.tda.entity.RequestThread;
import sma.tda.entity.ThreadDump;
import sma.tda.entity.report.ThreadDumpReport;

public class AjpThreadReportProcessor implements ThreadDumpReportProcessor {

	@Override
	public void updateReport(ThreadDumpReport pThreadDumpReport, ThreadDump pThreadDump,
			Map<String, Object> pAdditionalInfo) {
		
		Pattern requestNamePattern = Pattern.compile(Configuration.getInstance().getRequestThreadNameRegex());
		Pattern sessionAndUserPattern = Pattern.compile(Configuration.getInstance().getSessionAndUserRegex());
		
		for (JVMThread thread : pThreadDump.getThreads()) {
			if (thread instanceof RequestThread) {
				RequestThread rThread = (RequestThread) thread;
				String tname = rThread.getName();
				if (tname.startsWith("ajp-")) {
					pThreadDumpReport.addAjpThread(rThread);
					Matcher m = requestNamePattern.matcher(tname);
					if (m.find()) {
						rThread.setType(m.group(1));
						rThread.setPath(m.group(2));
					}
					Matcher m2 = sessionAndUserPattern.matcher(tname);
					if (m2.find()) {
						rThread.setJSessionId(m.group(1));
						rThread.setUserId(m.group(2));
					}
					if (tname.contains(";path=")) {
						pThreadDumpReport.addAjpThreadWithPath(rThread);
					}
					if (((String) thread.getLines().get(1)).contains("at java.net.SocketInputStream.socketRead0")) {
						pThreadDumpReport.addAjpThreadSocketRead(rThread);
					}
				}
			}
		}
	}
	
}
