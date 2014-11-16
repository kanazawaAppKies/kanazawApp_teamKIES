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
		 //�T�[�t�F�C�X�z���_�[�̎擾�ƃR�[���o�b�N�ʒm��̎w��
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
//        SDK3.1�ȍ~�s�v���߃R�����g�A�E�g
//     	  // SurfaceView�̎�ʂ��v�b�V���o�b�t�@�[�ɕύX���܂� 
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	//�J�����N����
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//�J�������J���Ȃ��Ȃ牽�����Ȃ�
		 try {
	            camera = Camera.open();
	            camera.setPreviewDisplay(surfaceHolder);
	        } catch (Exception e) {
	        }
		
	}

	//�ύX��
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		// �v���r���[�̊J�n
        camera.startPreview();
		
	}

	//�I����
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
		
	}

}
