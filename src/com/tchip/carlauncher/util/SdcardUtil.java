package com.tchip.carlauncher.util;

import android.os.StatFs;


public class SdcardUtil {
	
	/** 
     * 获得SD卡总大小 
     * @return 
     */  
    public static long getSDTotalSize(String  SDCardPath) {  
        StatFs stat = new StatFs(SDCardPath);  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        return blockSize * totalBlocks;
    }  
  
    /** 
     * 获得sd卡剩余容量，即可用大小 
     * @return 
     */  
    public static long getSDAvailableSize(String  SDCardPath) {  
        //StatFs stat = new StatFs("/storage/sdcard1");  
        StatFs stat = new StatFs(SDCardPath);  
        long blockSize = stat.getBlockSize();  
        long availableBlocks = stat.getAvailableBlocks();  
        return blockSize * availableBlocks;
    } 

}
