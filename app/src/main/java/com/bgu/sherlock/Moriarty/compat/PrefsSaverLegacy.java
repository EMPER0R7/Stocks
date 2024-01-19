package com.bgu.sherlock.Moriarty.compat;

import android.content.SharedPreferences.Editor;

public class PrefsSaverLegacy extends PrefsSaver
{
	@Override
	public void save(Editor ed) {
		ed.commit();
	}

	@Override
	public void backup() {}
}
