package com.tchip.carlauncher.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.util.Log;

import com.tchip.carlauncher.Constant;

public class BTCommand {
	/**
	 * 节点路径
	 */
	public static String NODE_PATH = "/dev/goc_serial";

	/**
	 * 命令前缀
	 */
	public static String PREFIX = "AT#";

	/**
	 * 命令后缀
	 */
	public static String SUFFIX = "\r\n";

	/**
	 * 拨号，命令后追加号码
	 */
	public static String CM_DIALER = "CW";

	/**
	 * 挂断电话
	 */
	public static String CM_HANG = "CG";

	/**
	 * 音量加
	 */
	public static String CM_VOL_PLUS = "CK";

	/**
	 * 音量减
	 */
	public static String CM_VOL_MINUS = "CL";

	/**
	 * 查询配对列表
	 */
	public static String CM_BAND_LIST = "MX";

	/**
	 * 读取电话本
	 */
	public static String CM_CONTACT = "PA";

	/**
	 * 发送命令
	 * 
	 * @param command
	 */
	public static void SendCommand(String command) {
		File file = new File(NODE_PATH);
		command = PREFIX + command + SUFFIX;
		if (file.exists()) {
			try {
				StringBuffer strbuf = new StringBuffer("");
				strbuf.append(command);
				Log.d(Constant.TAG, "11111111::::::	" + strbuf);

				OutputStream output = null;
				OutputStreamWriter outputWrite = null;
				PrintWriter print = null;

				try {
					output = new FileOutputStream(file);
					outputWrite = new OutputStreamWriter(output);

					print = new PrintWriter(outputWrite);
					print.print(strbuf.toString());
					print.flush();
					output.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.e(Constant.TAG, "output error");
				}
			} catch (IOException e) {
				Log.e(Constant.TAG, "IO Exception");
			}
		} else {
			Log.e(Constant.TAG, "File:" + file + "not exists");
		}
	}

	public static String getNodeContent() {
		String content = "kong"; // 文件内容字符串
		// 打开文件
		File file = new File(NODE_PATH);
		if (file.exists()) {
			try {
				FileReader fileReader = new FileReader(file);
				try {
					//content = fileReader.read();
					BufferedReader reader = new BufferedReader(fileReader);
					content = reader.readLine();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

}
