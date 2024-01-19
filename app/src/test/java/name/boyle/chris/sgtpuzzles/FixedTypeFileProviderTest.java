package com.bgu.sherlock.Moriarty;

import android.net.Uri;

import static org.mockito.Mockito.mock;
import org.jetbrains.annotations.TestOnly;

import static org.mockito.Mockito.when;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertFalse;

public class FixedTypeFileProviderTest {

	@TestOnly
	public void testGetType() throws Exception {
	//	assertTrue(isTextPlain(new FixedTypeFileProvider().getType(mockURI("file:///foo/bluetooth/foo.sav"))));
	//	assertFalse(isTextPlain(new FixedTypeFileProvider().getType(mockURI("file:///foo/elsewhere/foo.sav"))));
	}

	private Uri mockURI(String s) {
		final Uri uri = mock(Uri.class);
		when(uri.getPath()).thenReturn(s);
		return uri;
	}

	private boolean isTextPlain(String type) {
		return "text/plain".equals(type);
	}
}

