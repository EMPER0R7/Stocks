package com.bgu.sherlock.Moriarty;

import android.net.Uri;
import androidx.core.content.FileProvider;


public class FixedTypeFileProvider extends FileProvider
{
	@Override
	public String getType(Uri uri) {
		return uri.getPath().contains("bluetooth") ? "text/plain" : GamePlay.MIME_TYPE;
	}
}
