package sma.tda.parser.td;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import sma.tda.conf.Configuration;
import sma.tda.entity.JVMThread;
import sma.tda.entity.RequestThread;
import sma.tda.entity.ThreadDump;
import sma.tda.utils.Logger;

/**
 * Parser used to parse thread dump file. A single thread dump file may contain
 * multiple thread dumps.
 */
public class ThreadDumpParser {

	// --- instance variables --------------------------------------------------

	/** Pattern used to identify he date line of a thread dump. */
	private Pattern threadDumpDateLinePattern = null;

	/**
	 * Pattern used to identify line which is the beginning of a new thread dump.
	 */
	private Pattern threadStartLinePattern = null;

	/** Used to parse date. */
	private SimpleDateFormat dateFormatter = null;

	// --- constructors --------------------------------------------------------

	public ThreadDumpParser() {
		this.threadDumpDateLinePattern = Pattern.compile(Configuration.getInstance().getThreadDumpDateLineRegex());
		this.threadStartLinePattern = Pattern.compile(Configuration.getInstance().getThreadStartLineRegex());
		this.dateFormatter = new SimpleDateFormat(Configuration.getInstance().getThreadDumpTimeFormatPattern());
		this.dateFormatter
				.setTimeZone(TimeZone.getTimeZone(Configuration.getInstance().getThreadDumpDateFormatTimeZone()));
	}

	// --- methods -------------------------------------------------------------

	public List<ThreadDump> parse(final File pThreadDumpFile) {
		final List<ThreadDump> result = new ArrayList<ThreadDump>();
		int threadDumpIndex = 0;
		ThreadDump threadDump = null;
		JVMThread thread = null;
		String line = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(pThreadDumpFile))) {
			while ((line = br.readLine()) != null) {
				if (this.ignoreLine(line)) {
					continue;
				}
				final Matcher tdDateMatcher = this.threadDumpDateLinePattern.matcher(line);
				if (tdDateMatcher.find()) {
					threadDump = new ThreadDump(threadDumpIndex++);
					try {
						threadDump.setDate(this.dateFormatter.parse(tdDateMatcher.group(1)));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					result.add(threadDump);
				} else {
					final Matcher startTDMatcher = this.threadStartLinePattern.matcher(line);
					if (startTDMatcher.find()) {
						final String name = startTDMatcher.group(1);
						thread = this.newJVMThread(name);
						thread.setName(name);
						thread.setTid(startTDMatcher.group(2));
						thread.setNid(startTDMatcher.group(3));
						thread.setState(startTDMatcher.group(4));
						threadDump.addThread(thread);
					} else {
						if (!StringUtils.isNotBlank((CharSequence) line) || thread == null) {
							continue;
						}
						final String trimmedLine = line.trim();
						thread.addLine(trimmedLine);
						this.processThreadLine(trimmedLine, thread);
					}
				}
			}
		} catch (IOException e) {
			Logger.getInstance().logError("Error while parsing thread dump file " + pThreadDumpFile.getAbsolutePath(),
					(Throwable) e);
		}
		return result;
	}

	private JVMThread newJVMThread(final String pThreadName) {
		if (pThreadName.startsWith("ajp-") || pThreadName.startsWith("http-")) {
			return (JVMThread) new RequestThread();
		}
		return new JVMThread();
	}

	private boolean ignoreLine(final String pLine) {
		return pLine.startsWith("Full thread dump Java HotSpot") || pLine.startsWith("JNI global references");
	}

	private void processThreadLine(final String pLine, final JVMThread pThread) {
		if (pLine.startsWith("java.lang.Thread.State:")) {
			String s = pLine.substring(24);
			if (s.indexOf(32) > 0) {
				s = s.substring(0, s.indexOf(32));
			}
			pThread.setThreadState(s);
		} else if (pLine.startsWith("- locked <")) {
			String s = pLine.substring(10);
			if (s.indexOf(62) > 0) {
				s = s.substring(0, s.indexOf(62));
			}
			pThread.addLockOwned(s);
		} else if (pLine.startsWith("- waiting on <")) {
			String s = pLine.substring(14);
			if (s.indexOf(62) > 0) {
				s = s.substring(0, s.indexOf(62));
			}
			pThread.addLockWaiting(s);
		}
	}
}