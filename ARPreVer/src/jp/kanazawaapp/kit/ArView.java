package jp.kanazawaapp.kit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class ArView extends View {

	public ArView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		//�A���`�G�C���A�X������L����
		//�A���`�G�C���A�X�����@= �Ȑ���΂߂̃M�U�M�U�����Ȃ����邱��
		paint.setAntiAlias(true);
		
		//�`��J�n �����o����悤�ɂ��邽�ߍ����for���������Ń��[�v�\��@�Љ��ꏊ�́@ArrayList���H
		
	}
	
	
}
