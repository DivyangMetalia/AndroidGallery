package com.elluminati.gallery;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.elluminati.gallery.adapter.MyImageAdapter;
import com.elluminati.gallery.constants.Helper;
import com.elluminati.gallery.constants.Utils;
import com.elluminati.gallery.model.GalleryModel;

public class MultiPhotoSelectActivity extends Activity {

	private ProgressDialog pd;
	private GridView gridView;
	private ArrayList<GalleryModel> galleryModels;
	private MyImageAdapter imageAdapter;
	private LinearLayout llShowButtons;
	private int mode = Helper.TAG_MODE_DIRECTORY;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (pd != null) {
				pd.dismiss();
				pd = null;
			}
			switch (msg.what) {
			case 0:
				setImagesOnAdapater();
				break;
			case 1:
				gridView.setAdapter(imageAdapter);
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_grid);
		llShowButtons = (LinearLayout) findViewById(R.id.llShowButtons);
		llShowButtons.setVisibility(View.GONE);
	}

	@SuppressWarnings("deprecation")
	private void setImagesOnAdapater() {
		pd = ProgressDialog.show(MultiPhotoSelectActivity.this, "Show Images",
				"Get Images ... ...");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				final String[] columns = { MediaStore.Images.Media.DATA,
						MediaStore.Images.Media._ID };
				final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
				Cursor imagecursor = managedQuery(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
						null, null, orderBy + " DESC");
				galleryModels = Utils.getAllDirectoriesWithImages(imagecursor);
				imageAdapter = new MyImageAdapter(
						MultiPhotoSelectActivity.this, null, galleryModels);
				handler.sendEmptyMessage(1);
			}
		});
		thread.start();

	}

	@Override
	protected void onStop() {
		try {
			if (imageAdapter != null) {
				imageAdapter.stopImageLoader();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStop();
	}

	public void btnCancelSelectedImages(View v) {
		setGallery();
	}

	public void setGallery() {
		if (mode == Helper.TAG_MODE_FILES) {
			llShowButtons.setVisibility(View.GONE);
			mode = Helper.TAG_MODE_DIRECTORY;
			GridView gridView = (GridView) findViewById(R.id.gridview);
			imageAdapter = new MyImageAdapter(this, null, galleryModels);
			gridView.setAdapter(imageAdapter);
		}
	}

	public void setFilesFromFolder(int position) {
		llShowButtons.setVisibility(View.VISIBLE);
		mode = Helper.TAG_MODE_FILES;
		GridView gridView = (GridView) findViewById(R.id.gridview);
		Object[] abc = galleryModels.get(position).folderImages.toArray();
		ArrayList<String> paths = new ArrayList<String>();
		int size = abc.length;
		for (int i = 0; i < size; i++) {
			paths.add((String) abc[i]);
		}
		imageAdapter = new MyImageAdapter(this, paths, null);
		gridView.setAdapter(imageAdapter);
	}

	@Override
	public void onBackPressed() {
		if (mode == Helper.TAG_MODE_FILES) {
			setGallery();
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onStart() {
		if (gridView == null)
			gridView = (GridView) findViewById(R.id.gridview);
		setImagesOnAdapater();
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
		super.onDestroy();
	}

}