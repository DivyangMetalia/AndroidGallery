package com.elluminati.gallery.model;

import java.util.TreeSet;

public class GalleryModel {
	public TreeSet<String> folderImages;
	public String folderName, folderImagePath;

	public GalleryModel() {
		folderImages = new TreeSet<String>();
	}
}
