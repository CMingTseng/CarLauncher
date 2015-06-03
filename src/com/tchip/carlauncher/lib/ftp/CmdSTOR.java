package com.tchip.carlauncher.lib.ftp;

public class CmdSTOR extends CmdAbstractStore implements Runnable {
	protected String input;

	public CmdSTOR(SessionThread sessionThread, String input) {
		super(sessionThread, CmdSTOR.class.toString());
		this.input = input;
	}

	public void run() {
		doStorOrAppe(getParameter(input), false);
	}
}
