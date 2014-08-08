package com.elluminati.gallery.constants;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import android.database.Cursor;
import android.graphics.Bitmap;

import com.elluminati.gallery.model.GalleryModel;

public class Utils {

	public static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();

	public static Bitmap getBitmapScale(Bitmap bitmap) {
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();
		try {
			return Bitmap.createScaledBitmap(bitmap, (int) width, (int) height,
					true);
		} finally {
			bitmap.recycle();
		}
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static ArrayList<GalleryModel> getAllDirectoriesWithImages(
			Cursor cursor) {
		if (cursor == null) {
			return null;
		}
		cursor.moveToFirst();
		int size = cursor.getCount();

		TreeSet<String> folderPathList = new TreeSet<String>();
		ArrayList<GalleryModel> galleryModels = new ArrayList<GalleryModel>();
		HashMap<String, GalleryModel> map = new HashMap<String, GalleryModel>();

		String imgPath, folderPath;
		GalleryModel tempGalleryModel;
		for (int i = 0; i < size; i++) {
			imgPath = cursor.getString(0).trim();
			folderPath = imgPath.substring(0, imgPath.lastIndexOf("/"));
			if (folderPathList.add(folderPath)) {
				GalleryModel gm = new GalleryModel();
				gm.folderName = folderPath.substring(
						folderPath.lastIndexOf("/") + 1, folderPath.length());
				gm.folderImages.add(imgPath);
				gm.folderImagePath = imgPath;
				galleryModels.add(gm);
				map.put(folderPath, gm);
			} else if (folderPathList.contains(folderPath)) {
				tempGalleryModel = map.get(folderPath);
				tempGalleryModel.folderImages.add(imgPath);
			}
			cursor.moveToNext();
		}
		return galleryModels;
	}

	public static ArrayList<String> getAllImagesOfFolder(String directoryPath) {
		File directory = new File(directoryPath);
		ArrayList<String> pathList = new ArrayList<String>();
		try {
			if (directory.exists() && directory.isDirectory()) {
				String[] fileList = directory.list();
				for (String file : fileList) {
					if (isImage(file)
							&& new File(directoryPath + file).length() > 0
							&& !file.startsWith(".")) {
						pathList.add(directoryPath + file);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pathList;
	}

	public static boolean isImage(String file) {
		try {
			String extension = file.substring(file.lastIndexOf("."),
					file.length());
			if (extension != null) {
				if (extension.equalsIgnoreCase(".jpeg")
						|| extension.equalsIgnoreCase(".jpg")
						|| extension.equalsIgnoreCase(".png")
						|| extension.equalsIgnoreCase(".gif")
						|| extension.equalsIgnoreCase(".tiff")
						|| extension.equalsIgnoreCase(".bmp")) {
					return true;
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return false;
	}

}
