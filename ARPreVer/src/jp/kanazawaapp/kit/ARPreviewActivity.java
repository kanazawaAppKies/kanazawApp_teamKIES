package jp.kanazawaapp.kit;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class ARPreviewActivity extends Activity implements SensorEventListener{

	private ArView arView;
	private SensorManager sensorManager;
	private List<Sensor> listMag;
	private List<Sensor> listAcc;
	private float[] accelerometerValues = new float[3];
	//x,y,z軸の磁気密度
	private float[] magneticValues =new float[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// フルスクリーン指定
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //タイトルバーの表示を解除
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
        
     // ARViewの取得
        arView = new ArView(this);
        
        //各種センサーの用意
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //地磁気センサー
        listMag = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        //加速度センサー
        listAcc = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        
        //Viewの重ね合わせ
        setContentView(new CameraView(this));
        addContentView(arView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.arpreview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//センサーの情報が変更された
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
        	case Sensor.TYPE_ACCELEROMETER:
        		accelerometerValues = event.values.clone();
        		break;
        	case Sensor.TYPE_MAGNETIC_FIELD:
        		magneticValues  = event.values.clone();
        		break;
        }

		//magneticValues		x,y,z軸の磁気密度
		//accelerometerValues 	x,y,z軸の加速度　
        if (magneticValues != null && accelerometerValues != null) {
            float[] R = new float[16];
            float[] I = new float[16];

            SensorManager.getRotationMatrix(R, I, accelerometerValues,
                    magneticValues);

            float[] actual_orientation = new float[3];

            SensorManager.getOrientation(R, actual_orientation);
            
            
            // 求まった方位角をラジアンから度に変換する
            float direction = (float) Math.toDegrees(actual_orientation[0]);

            
          //描画をする
            arView.drawScreen(direction);
        }
    }

		

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	//activity実行直前
	@Override
	protected void onResume() {
		super.onResume();
        		
        		
		//センサー処理の登録
		
		/* sensorManager
		 * 第1引数		センサーによって呼び出される関数を持つクラス
		 * 第2引数		対象となる値を
		 *　第3引数		呼び出し頻度   (ゲームにするなら SENSOR_DELAY_GAME)
		 * */
        sensorManager.registerListener(this,
                listMag.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                listAcc.get(0), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//センサーを終了
		sensorManager.unregisterListener(this);

	}

	
}



