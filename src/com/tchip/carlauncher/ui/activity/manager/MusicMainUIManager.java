package com.tchip.carlauncher.ui.activity.manager;

import android.view.View;

public abstract class MusicMainUIManager {

	protected abstract void setBgByPath(String path);

	public abstract View getView();

	public abstract View getView(int from);

	public abstract View getView(int from, Object obj);
}
