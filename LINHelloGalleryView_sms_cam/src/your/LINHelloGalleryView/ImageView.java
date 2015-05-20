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
		
		mImageView.setImageBitmap(		// �̹��� ���� 
				Bitmap.createScaledBitmap(image, 450, 450, true));
		// ���̾ƿ� ũ�⿡ ����
		mImageView.setScaleType(android.widget.ImageView.ScaleType.CENTER_INSIDE);
		mImageView.setPadding(3, 3, 3, 3); // �̹��� ����
		mImageView.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();		// ��Ƽ��Ƽ ����
			}
		});
	}
}
