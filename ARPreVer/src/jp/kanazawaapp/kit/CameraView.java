package jp.kanazawaapp.kit;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

public class CameraView extends SurfaceView  implements SurfaceHolder.Callback{

	 private SurfaceHolder holder;
	 private Camera camera;

	public CameraView(Context context) {
		super(context);
		 //サーフェイスホルダーの取得とコールバック通知先の指定
        holder = getHolder();
        holder.addCallback(this);
//      SDK3.1以降不要ためコメントアウト
//   	  // SurfaceViewの種別をプッシュバッファーに変更します 
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	//カメラ起動時
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//カメラが開けないなら何もしない
		try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
        }
		
	}

	//変更時
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		//surfaceの解像度を設定
		ViewGroup.LayoutParams layoutParams = getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = height;
		setLayoutParams(layoutParams);
		
		Camera.Parameters parameters = camera.getParameters();
		List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
		
		for (Camera.Size size : sizes) {
			//縦横比を調べる
			if (size.width / width == size.height / height) {
				width = size.width;
				height = size.height;
				break;
			}
		}
		parameters.setPreviewSize(width, height);
		camera.setParameters(parameters);

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
