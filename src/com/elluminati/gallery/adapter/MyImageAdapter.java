package com.elluminati.gallery.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elluminati.gallery.MultiPhotoSelectActivity;
import com.elluminati.gallery.R;
import com.elluminati.gallery.constants.Helper;
import com.elluminati.gallery.model.GalleryModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class MyImageAdapter extends BaseAdapter {
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ArrayList<String> mList;
	private ArrayList<GalleryModel> galleryModels;
	private LayoutInflater mInflater;
	private Activity activity;
	private DisplayImageOptions options;
	private int mode;

	public MyImageAdapter(Activity activity, ArrayList<String> imageList,
			ArrayList<GalleryModel> galleryModels) {
		imageLoader = ImageLoader.getInstance();
		this.activity = activity;
		mInflater = LayoutInflater.from(activity);
		mList = new ArrayList<String>();
		this.mList = imageList;
		this.galleryModels = galleryModels;

		if (galleryModels == null) {
			mode = Helper.TAG_MODE_FILES;
		} else {
			mode = Helper.TAG_MODE_DIRECTORY;
		}

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.stub_image)
				.showImageForEmptyUri(R.drawable.image_for_empty_url)
				.cacheInMemory().cacheOnDisc().build();
	}

	@Override
	public int getCount() {
		if (mode == Helper.TAG_MODE_DIRECTORY) {
			return galleryModels.size();
		} else {
			return mList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		ImageView imgShow, imageView;
		TextView txtFolderName;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.inflater_show_image, null);
			holder.imgShow = (ImageView) convertView
					.findViewById(R.id.imgShowing);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imgSelector);
			holder.txtFolderName = (TextView) convertView
					.findViewById(R.id.txtFolderName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (mode == Helper.TAG_MODE_FILES) {
			imageLoader.displayImage("file://" + mList.get(position),
					holder.imgShow, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(
									activity, R.anim.fade_in);
							holder.imgShow.setAnimation(anim);
							anim.start();
						}
					});
			holder.imageView.setBackgroundResource(R.drawable.deselect_image);
		} else {
			GalleryModel gm = galleryModels.get(position);
			imageLoader.displayImage("file://" + gm.folderImagePath,
					holder.imgShow, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(
									activity, R.anim.fade_in);
							holder.imgShow.setAnimation(anim);
							anim.start();
						}
					});
			String strName = gm.folderName;
			if (strName.length() > 15) {
				strName = strName.substring(0, 15);
			}
			strName = strName + " [" + gm.folderImages.size() + "]";
			holder.txtFolderName.setVisibility(View.VISIBLE);
			holder.txtFolderName.setText(strName);
		}
		holder.imageView.setTag(position);
		holder.imageView.setOnClickListener(clickListener);

		return convertView;
	}

	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			if (mode == Helper.TAG_MODE_DIRECTORY) {
				((MultiPhotoSelectActivity) activity)
						.setFilesFromFolder(position);
			}
		}
	};

	public void stopImageLoader() {
		imageLoader.stop();
	}

}