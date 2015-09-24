package com.juttec.goldmetal.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.Log;

public class GetContentUrl {
	public static Uri geturi(Intent intent,Context context) {

		Uri uri = intent.getData();

		String type = intent.getType();

		if (uri.getScheme().equals("file") && (type.contains("image/"))) {

			String path = uri.getEncodedPath();

			if (path != null) {

				path = Uri.decode(path);

				ContentResolver cr = context.getContentResolver();

				StringBuffer buff = new StringBuffer();

				buff.append("(")

				.append(Images.ImageColumns.DATA)

				.append("=")

				.append("'" + path + "'")

				.append(")");

				Cursor cur = cr.query(

				Images.Media.EXTERNAL_CONTENT_URI,

				new String[] { Images.ImageColumns._ID },

				buff.toString(), null, null);

				int index = 0;

				for (cur.moveToFirst(); !cur.isAfterLast(); cur

				.moveToNext()) {

					index = cur.getColumnIndex(Images.ImageColumns._ID);

					// set _id value

					index = cur.getInt(index);

				}

				if (index == 0) {

					// do nothing

				} else {

					Uri uri_temp = Uri

					.parse("content://media/external/images/media/"

					+ index);

					if (uri_temp != null) {

						uri = uri_temp;

						Log.i("urishi", uri.toString());

					}

				}

			}

		}

		return uri;

	}
}
