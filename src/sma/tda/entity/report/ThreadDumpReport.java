package sma.tda.entity.report;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import lombok.Getter;
import lombok.Setter;
import sma.tda.entity.JVMThread;
import sma.tda.entity.RequestThread;
import sma.tda.entity.ThreadDump;
import sma.tda.entity.Top;

@Getter @Setter
public class ThreadDumpReport {

	private ThreadDump mThreadDump = null;

	private Top top = null;

	private float mCpuUsageTreshold = 0.0F;

	private SortedMap<Float, JVMThread> mThreadByCpu = null;

	private List<RequestThread> ajpThread = new ArrayList<>();

	private List<RequestThread> ajpThreadWithPath = new ArrayList<>();

	private List<RequestThread> ajpThreadSocketRead = new ArrayList<>();

	public ThreadDumpReport(ThreadDump pThreadDump) {
		this.mThreadDump = pThreadDump;
	}

	public void addAjpThread(RequestThread pThread) {
		this.ajpThread.add(pThread);
	}

	public void addAjpThreadWithPath(RequestThread pThread) {
		this.ajpThreadWithPath.add(pThread);
	}

	public void addAjpThreadSocketRead(RequestThread pThread) {
		this.ajpThreadSocketRead.add(pThread);
	}
}
