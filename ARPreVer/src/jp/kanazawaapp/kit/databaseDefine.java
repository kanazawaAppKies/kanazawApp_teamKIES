package jp.kanazawaapp.kit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class databaseDefine {
	/**データベースの要素*/
	final static String  TYPES = "(name,latitude,longitude,genre,open,close,rest)";
	
	/**データベース挿入のテンプレート文*/
	final static String NAMELIST ="INSERT INTO namelist"+TYPES+"VALUES";
	
	/** データ名と保存場所を登録*/
    static String dbName = "data/data/" + ARPreviewActivity.packageName + "/myDatabase1.db";
    
    /**データベースオブジェクトを生成*/
    static SQLiteDatabase dbObject = SQLiteDatabase.openOrCreateDatabase(dbName, null);
   
    /**古いテーブルを破棄するSQL文*/
    static String dropTable = "DROP TABLE IF EXISTS namelist";
   
    /**3つのカラムを持つテーブルを作成するSQL文を登録*/
    static String createTable = "CREATE TABLE namelist" + "(id INTEGER PRIMARY KEY, name STRING, latitude NUMERIC, longitude NUMERIC, genre INTEGER, open INTEGER, close INTEGER, rest INTEGER)";
	
    /**クアリを設定 (後に営業中のものに変更する)*/
    static String query = "SELECT * FROM namelist WHERE ((open > close) AND 0 <= "+ARPreviewActivity.nowTime+" AND close >= "+ ARPreviewActivity.nowTime +
            ") OR (open <=" + ARPreviewActivity.nowTime + " AND close >= "+ARPreviewActivity.nowTime+") AND NOT(rest = rest | " + ARPreviewActivity.week +") GROUP BY name";
    
    
    /**データベースにデータを追加*/
	public static void databaseInsert(){
		Log.i("database", "挿入開始");
		String[] insert = {
				NAMELIST + "('海鮮どん屋',36.570923,136.648972,0,1100,2100,10001000)",
				NAMELIST + "('廻る近江町市場寿し',36.571633,136.656835,0,930,2000,10000000)",
				NAMELIST + "('近江町海鮮丼家ひら井本店',36.570985,136.656721,0,1100,1530,11001001)",
				NAMELIST + "('近江町海鮮丼家ひら井本店',36.570985,136.656721,0,1700,2130,11001001)",
				NAMELIST + "('近江町海鮮丼家ひら井本店',36.570985,136.656721,0,1030,1530,10111110)",
				NAMELIST + "('近江町海鮮丼家ひら井本店',36.570985,136.656721,0,1700,2130,10111110)",
				NAMELIST + "('いきいき亭',36.571488,136.655877,0,700,1500,10000100)",
				NAMELIST + "('串揚げひかりや近江町店',36.571682,136.656791,0,1100,1500,10100011)",
				NAMELIST + "('串揚げひかりや近江町店',36.571682,136.656791,0,1800,000,10100011)",
				NAMELIST + "('串揚げひかりや近江町店',36.571682,136.656791,0,1800,300,11111100)",
				NAMELIST + "('大友屋',36.571285,136.657308,0,1100,1430,11000000)",
				NAMELIST + "('大友屋',36.571285,136.657308,0,1730,2200,11000000)",
				NAMELIST + "('もりもり寿し近江町店',36.571488,136.655877,0,1000,2100,10000000)",
				NAMELIST + "('百万石うどん 近江町市場本店',36.571346,136.65676,0,800,1620,11000000)",
				NAMELIST + "('近江町食堂',36.571099,136.655827,0,1030,1430,10000000)",
				NAMELIST + "('近江町食堂',36.571099,136.655827,0,1700,2130,10000000)",
				NAMELIST + "('近江町市場寿し 上近江町店',36.570967,136.656465,0,930,1930,10000000)",
				NAMELIST + "('くずの花',36.570794,136.656588,0,900,1730,10000000)",
				NAMELIST + "('刺身屋',36.571166,136.656002,0,1100,1500,10001000)",
				NAMELIST + "('刺身屋',36.571166,136.656002,0,1700,2100,10001000)",
				NAMELIST + "('山さん寿司本店',36.571729,136.656927,0,730,1900,10000000)",
				NAMELIST + "('鮨処 源平',36.571027,136.656966,0,1100,2100,11000001)",
				NAMELIST + "('鮨処 源平',36.571027,136.656966,0,1100,1900,11111110)",
				NAMELIST + "('大口水産ヤキヤキコーナー',36.571249,136.656533,0,900,1700,11001000)",
				NAMELIST + "('大口水産ヤキヤキコーナー',36.571249,136.656533,0,930,1600,10111111)",
				NAMELIST + "('井ノ弥',36.571047,136.656246,0,1100,2100,10010001)",
				NAMELIST + "('井ノ弥',36.571047,136.656246,0,1000,2000,11111110)",
				NAMELIST + "('海鮮丼こてつ',36.571244,136.655497,0,1130,1500,10001000)",
				NAMELIST + "('近江町市場海鮮丼魚旨',36.57078,136.656063,0,1100,1600,10000000)",
				NAMELIST + "('じもの亭',36.570977,136.656908,0,1100,1430,10000000)",
				NAMELIST + "('じもの亭',36.570977,136.656908,0,1700,1900,10000000)",
				NAMELIST + "('鮨　歴々',36.570769,136.656205,0,1130,1430,10000000)",
				NAMELIST + "('鮨　歴々',36.570769,136.656205,0,1700,2000,10000000)",
				NAMELIST + "('カフェ・アルコメルカート',36.571488,136.655877,0,1100,2300,11000001)",
				NAMELIST + "('カフェ・アルコメルカート',36.571488,136.655877,0,800,2300,10111110)",
				NAMELIST + "('海鮮丼いちば',36.571299,136.656346,0,900,1700,10000000)",
				NAMELIST + "('海鮮どん屋',36.570777,136.656471,0,1100,2100,10001000)",
		};
		Log.i("database", "完了");
		//古いテーブルを破棄
		dbObject.execSQL(dropTable);
		//テーブルを作成
		dbObject.execSQL(createTable);
		
		//データを追加
		for(int i = 0;i < insert.length; i++){
			dbObject.execSQL(insert[i]);
		}
		Log.i("database", "挿入完了"+insert.length);
		readDatabase();
	}
	
	private static void readDatabase(){
		Log.i("database", "読み込み開始");
		Cursor cursor = dbObject.rawQuery(query, null);
		
		while (cursor.moveToNext()) {
			GPSData data =  new GPSData();
			//各カラムのidを取得
			int idId = cursor.getColumnIndex("id");
			int idName = cursor.getColumnIndex("name");
	    	int idLatitude = cursor.getColumnIndex("latitude");
	    	int idLongitude = cursor.getColumnIndex("longitude");
	    	int idOpen = cursor.getColumnIndex("open");
	    	int idClose = cursor.getColumnIndex("close");
	    	int idRest = cursor.getColumnIndex("rest");
	    	int idGenre = cursor.getColumnIndex("genre");
	
	    	int id = cursor.getInt(idId);
	    	String name = cursor.getString(idName);
	    	double latitude = cursor.getDouble(idLatitude);
	    	double longitude = cursor.getDouble(idLongitude);
	    	int rest = cursor.getInt(idRest);
	    	int open = cursor.getInt(idOpen);
	    	int close = cursor.getInt(idClose);
	    	int genre = cursor.getInt(idGenre);
	    	
	    	//データの追加
	    	data.info = name;
	    	data.latitude = latitude;
	    	data.longitude = longitude;
	    	data.genre = genre;
	    	data.open = open;
	    	data.close = close;
	    	data.rest = rest;
	    	Log.i("データ","name:"+data.info);
	    	ArView.listAdd(data);
    	}
    	
    	Log.i("database", "読み込み完了");
    	Log.i("database","データベースサイズ:"+ArView.gpsDataList.size());
	}
	
	/**GPS情報を保持するクラス*/
	static class GPSData {
		/**
		 * String<br>
		 * 施設名<br>
		 * 漢字、英字可<br>
		 * 文字数制限なし<br>
		 */
		public String info;
		/**
		 * double<br>
		 * 緯度<br>
		 * 10進法で実数
		 */
		public double latitude; 
		/**
		 * double<br>
		 * 経度<br>
		 * 10進法で実数
		 */
		public double longitude;
		/**
		 * int<br>
		 * ジャンルの種類<br>
		 * 1 観光<br>
		 * 2 飲食*/
		public int genre;
		/**
		 * int<br>
		 * オープン時間
		 */
		public int open;
		/**
		 * int<br>
		 * 閉店時間
		 */
		public int close;
		/**
		 * int<br>
		 * 定休日
		 * */
		public int rest;
	}

}
