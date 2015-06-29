package com.tchip.carlauncher.model;

import java.sql.Timestamp;
import java.util.Date;



public class VideoTable {
	private Integer id;
	private String name;
	private String path;
	private String path_withoutname;
	private Integer protect;
	private Integer keep_save;
	private String resolution;
	private String file_size;
	private String duration;
	private String path_thumbnail;
	private String btime;
	private Integer btime_unix;
	private String etime;
	private Integer etime_unix;
	
	
	public VideoTable()
	{
	}
	
	
	
	public VideoTable(String name, String path,
			String path_withoutname, Integer protect, Integer keep_save,
			String resolution, String file_size, String duration,
			String path_thumbnail, String btime, Integer btime_unix,
			String etime, Integer etime_unix) {
		this(null,   name,  path,
				 path_withoutname,  protect,  keep_save, resolution,
				 file_size,  duration,  path_thumbnail, btime,
				 btime_unix,  etime,  etime_unix);
	}
	


	public VideoTable(Integer id, String name, String path,
			String path_withoutname, Integer protect, Integer keep_save,
			String resolution, String file_size, String duration,
			String path_thumbnail, String btime, Integer btime_unix,
			String etime, Integer etime_unix) {
		super();
		this.id = id;
		this.name = name;
		this.path = path;
		this.path_withoutname = path_withoutname;
		this.protect = protect;
		this.keep_save = keep_save;
		this.resolution = resolution;
		this.file_size = file_size;
		this.duration = duration;
		this.path_thumbnail = path_thumbnail;
		this.btime = btime;
		this.btime_unix = btime_unix;
		this.etime = etime;
		this.etime_unix = etime_unix;
	}



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getProtect() {
		return protect;
	}

	public void setProtect(Integer protect) {
		this.protect = protect;
	}

	public String getBtime() {
		return btime;
	}

	public void setBtime(String btime) {
		this.btime = btime;
	}

	public Integer getBtime_unix() {
		return btime_unix;
	}

	public void setBtime_unix(Integer btime_unix) {
		this.btime_unix = btime_unix;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}

	public Integer getEtime_unix() {
		return etime_unix;
	}

	public void setEtime_unix(Integer etime_unix) {
		this.etime_unix = etime_unix;
	}
	
	public String getPath_withoutname() {
		return path_withoutname;
	}

	public void setPath_withoutname(String path_withoutname) {
		this.path_withoutname = path_withoutname;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getPath_thumbnail() {
		return path_thumbnail;
	}

	public void setPath_thumbnail(String path_thumbnail) {
		this.path_thumbnail = path_thumbnail;
	}
	
	public Integer getKeep_save() {
		return keep_save;
	}

	public void setKeep_save(Integer keep_save) {
		this.keep_save = keep_save;
	}



	@Override
	public String toString() {
		return "VideoTable [id=" + id + ", name=" + name + ", path=" + path
				+ ", path_withoutname=" + path_withoutname + ", protect="
				+ protect + ", keep_save=" + keep_save + ", resolution="
				+ resolution + ", file_size=" + file_size + ", duration="
				+ duration + ", path_thumbnail=" + path_thumbnail + ", btime="
				+ btime + ", btime_unix=" + btime_unix + ", etime=" + etime
				+ ", etime_unix=" + etime_unix + "]";
	}

}
