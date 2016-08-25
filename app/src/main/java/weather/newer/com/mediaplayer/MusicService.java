package weather.newer.com.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider. MediaStore.Audio.Media;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

public class MusicService extends Service {
public  static final String TAG =" MusicService " ;
  public static final String ACTION_MUSIC_LIST ="com.newer.weather.ACTION_MUSIC_LIST " ;//自定义广播的名称
public  static final String EXTRA_MUSIC_LIST ="music_list" ;//广播中数据的key值
    MediaPlayer mediaplayer;//在服务中定义一个媒体播放器
    ArrayList<Music> musiclist=new ArrayList<>();//音乐列表
    class  MusicListLoader extends Thread{
        @Override
        public void run() {
            super.run();
            String []projection={Media._ID,Media.DATA,Media.DISPLAY_NAME,Media.ARTIST,Media.ALBUM,Media.DURATION};
            //加载数据
            Cursor cursor=getContentResolver().query(
                   Media.EXTERNAL_CONTENT_URI,//三个，音频，视频，图片,
                    projection,
                   // "is_music !=？ AND artist=？",//音频播放的是音乐而且歌手是某某某
                  // String.format("%s=?  AND   %S=  ?",Media.IS_MUSIC,Media.ARTIST),
                    String.format("%s!=?  ",Media.IS_MUSIC),//希望获得的是音乐，而不是铃声或者其他的流媒体
                    new   String []{"0"},
                    Media .YEAR+ " desc"//根据专辑的年限进行排序


            );
            while(cursor.moveToNext()){
                long id=cursor.getLong(0);
                String data=cursor.getString(1);
                String name=cursor.getString(2);
                String artist=cursor.getString(3);
                String album=cursor.getString(4);
                long duration=cursor.getLong(5);
                Music music=new Music(id,name,data,artist,album,duration);
                musiclist.add(music);
              Log.d(TAG,music.toString() );
            }
            cursor.close();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(TAG,"音乐"+musiclist.size());
            //发广播
            Intent intent =new Intent();
            intent.setAction(ACTION_MUSIC_LIST);
            intent.putExtra(EXTRA_MUSIC_LIST, musiclist);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


        }
    }
    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new MusicListLoader().start();//服务启动的时候就开启线程加载音乐数据填充音乐列表
        mediaplayer=new MediaPlayer();
        //设置监听器
        mediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //播放下一首
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaplayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
