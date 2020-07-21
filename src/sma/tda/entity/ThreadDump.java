package sma.tda.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ThreadDump {

	private int index = 0;

	private Date date = null;

	private List<JVMThread> threads = new ArrayList<>();

	private Map<String, JVMThread> threadByNid = new HashMap<>();

	public ThreadDump(int pIndex) {
		this.index = pIndex;
	}

	public void addThread(JVMThread pThread) {
		this.threads.add(pThread);
		this.threadByNid.put(pThread.getNid().toLowerCase(), pThread);
	}

	public JVMThread getThreadByNid(String pNid) {
		return this.threadByNid.get(pNid);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("index=").append(this.index);
		builder.append("date=").append(this.date);
		builder.append(", thread count=").append(this.threads.size());
		return builder.toString();
	}
}
