package your.LINHelloGalleryView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class ImageView extends Activity {
	android.widget.ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view);
	
		mImageView=(android.widget.ImageView) findViewById(R.id.imageView);
	
		Bitmap image=BitmapFactory.decodeResource(getResources(), 
				ImageAdapter.mImageIds[LINHelloGalleryViewActivity.instance.pos]);
		
		mImageView.setImageBitmap(		// 이미지 설정 
				Bitmap.createScaledBitmap(image, 450, 450, true));
		// 레이아웃 크기에 맞춤
		mImageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_INSIDE);
		mImageView.setPadding(3, 3, 3, 3); // 이미지 간격
		mImageView.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();		// 액티비티 종료
			}
		});
	}
}
