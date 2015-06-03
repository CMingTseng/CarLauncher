package com.tchip.carlauncher.lib.ftp;

public class CmdAPPE extends CmdAbstractStore implements Runnable {
	protected String input;

	public CmdAPPE(SessionThread sessionThread, String input) {
		super(sessionThread, CmdAPPE.class.toString());
		this.input = input;
	}

	public void run() {
		doStorOrAppe(getParameter(input), true);
	}
}
