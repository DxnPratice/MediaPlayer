package weather.newer.com.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView textview;
    ProgressBar progressBar;
    MusicListReceiver  receiver;
    ArrayList<Music> list;//对服务中音乐列表的引用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       startService(new Intent(this, MusicService.class));
        textview= (TextView) findViewById(R.id.textview);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //.注册广播接收器
        IntentFilter  filter=new IntentFilter();
        filter.addAction(MusicService.ACTION_MUSIC_LIST);
        receiver=new MusicListReceiver();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注销广播接收器
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
    }
    class MusicListReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
          list= (ArrayList<Music>) intent.getSerializableExtra(MusicService.EXTRA_MUSIC_LIST);
            //显示数据
            textview.setText(list.toString());
            textview.setVisibility(View.VISIBLE);
           progressBar.setVisibility(View.GONE);

        }
    }

}
