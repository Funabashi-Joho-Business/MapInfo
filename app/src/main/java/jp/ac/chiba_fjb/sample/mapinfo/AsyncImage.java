package jp.ac.chiba_fjb.sample.mapinfo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncImage extends AsyncTask<String,Void,Bitmap> {
	private final static int ID = (1 << 26)+1;
	private ImageView mImage;
	private String mUrl;

	public AsyncImage(ImageView _image) {
		mImage = _image;
		mImage.setTag(this);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap image = null;
		try {
			if(isCancelled())
				return null;

			byte[] imageData;
			URL imageUrl = new URL(params[0]);
			imageData = getByteArray(imageUrl);

			if(isCancelled())
				return null;

			if(imageData != null)
				image = BitmapFactory.decodeByteArray(imageData,0,imageData.length);
			return image;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if(isCancelled())
			return;
		// 取得した画像をImageViewに設定します。
		if(result != null) {
			mImage.setTag(ID,mUrl);
			mImage.setImageBitmap(result);
			AlphaAnimation alpha = new AlphaAnimation(0, 1.0f);
			alpha.setDuration(500);
			mImage.startAnimation(alpha);
		}
		else {
			mImage.setTag(ID,null);
			mImage.setImageResource(android.R.color.transparent);
		}
		mImage.setTag(null);
	}
	public byte[] getByteArray(URL url) {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			int size;
			InputStream is = url.openStream();
			while((size=is.read(buf)) != -1 && !isCancelled())
				buffer.write(buf,0,size);
			is.close();
			if(isCancelled())
				return null;
			return buffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;


	}
	public void execute(String url) {
		String tag = (String)mImage.getTag(ID);
		if(url.equals(tag))
			return;
		mImage.setImageResource(android.R.color.transparent);
		super.execute(url);
	}
}
