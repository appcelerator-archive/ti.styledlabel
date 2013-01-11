/**
 * Ti.StyledLabel Module
 * Copyright (c) 2010-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.styledlabel.parsing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiUIHelper;

import ti.styledlabel.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.webkit.URLUtil;

public class CustomImageGetter implements Html.ImageGetter {

	private final TiViewProxy _proxy;

	public CustomImageGetter(TiViewProxy proxy) {
		_proxy = proxy;
	}

	@Override
	public Drawable getDrawable(String url) {
		Drawable d;
		if (URLUtil.isNetworkUrl(url)) {
			d = retrieveNetworkDrawable(url);
		} else {
			d = retrieveLocalDrawable(url);
		}
		if (d != null)
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		return d;
	}

	private Drawable retrieveLocalDrawable(String image) {
		String url = _proxy.resolveUrl(null, image);
		TiBaseFile file = TiFileFactory.createTitaniumFile(
				new String[] { url }, false);
		try {
			Drawable d = new BitmapDrawable(TiUIHelper.createBitmap(file
					.getInputStream()));
			return d;
		} catch (IOException e) {
			Util.e("Error creating drawable from path: " + image.toString(), e);
		}
		return null;
	}

	public Drawable retrieveNetworkDrawable(String url) {
		try {
			String name = url.hashCode() + ".png";
			File f = new File(TiApplication.getInstance().getCacheDir()
					.getAbsolutePath()
					+ name);
			if (!f.exists())
				downloadImage(_proxy.resolveUrl(null, url), f);
			return Drawable.createFromPath(f.getAbsolutePath());
		} catch (Exception e) {
			Util.e("Hit exception while downloading image " + url, e);
		}
		return null;
	}

	private void downloadImage(String url, File f) throws IOException {
		URL myFileUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
		conn.setDoInput(true);
		conn.connect();
		InputStream is = conn.getInputStream();

		Bitmap bm = BitmapFactory.decodeStream(is);
		FileOutputStream out = new FileOutputStream(f);
		bm.compress(Bitmap.CompressFormat.PNG, 90, out);
	}
}