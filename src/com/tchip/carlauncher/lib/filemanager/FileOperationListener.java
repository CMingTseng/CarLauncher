package com.tchip.carlauncher.lib.filemanager;

public interface FileOperationListener {
	void onFileProcessed(String filename);

	boolean isOperationCancelled();
}
