package sma.tda.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class JVMThread {

	private String tid = null;

	private String nid = null;

	private String name = null;

	private String state = null;

	private String threadState = null;

	private List<String> lines = new ArrayList<>();

	private List<String> locksOwned = new ArrayList<>();

	private List<String> locksWaiting = new ArrayList<>();

	public void addLine(String pLine) {
		this.lines.add(pLine);
	}

	public void addLockOwned(String pLock) {
		this.locksOwned.add(pLock);
	}

	public void addLockWaiting(String pLock) {
		this.locksWaiting.add(pLock);
	}

	public boolean getOwnsLockSize() {
		return (this.locksOwned.size() > 0);
	}

	public boolean getWaitsLockSize() {
		return (this.locksWaiting.size() > 0);
	}

	public String toString() {
		return "JVMThread(tid=" + getTid() + ", nid=" + getNid() + ", name=" + getName() + ", state=" + getState()
				+ ", threadState=" + getThreadState() + ")";
	}
}
