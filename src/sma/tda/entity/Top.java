package sma.tda.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Top {

	private Date date = null;

	private Map<Integer, Float> processes = new HashMap<>();

	public String toString() {
		return "Top(date=" + date + ", processes=" + processes + ")";
	}

	public void addProcess(Integer pPid, Float pCpu) {
		this.processes.put(pPid, pCpu);
	}
}
