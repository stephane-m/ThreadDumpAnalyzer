package sma.tda.entity;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class RequestThread extends JVMThread {
	private String type = null;

	private String path = null;

	private String jSessionId = null;

	private String userId = null;
}
