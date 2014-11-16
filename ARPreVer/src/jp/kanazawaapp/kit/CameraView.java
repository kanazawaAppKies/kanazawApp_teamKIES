package jp.kanazawaapp.kit;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView  implements SurfaceHolder.Callback{

	 private SurfaceHolder surfaceHolder;
	 private Camera camera;

	public CameraView(Context context) {
		super(context);
		 //サーフェイスホルダーの取得とコールバック通知先の指定
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
//        SDK3.1以降不要ためコメントアウト
//     	  // SurfaceViewの種別をプッシュバッファーに変更します 
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	//カメラ起動時
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//カメラが開けないなら何もしない
		 try {
	            camera = Camera.open();
	            camera.setPreviewDisplay(surfaceHolder);
	        } catch (Exception e) {
	        }
		
	}

	//変更時
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		// プレビューの開始
        camera.startPreview();
		
	}

	//終了時
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
		
	}

}
