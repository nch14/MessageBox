package com.chenh.messagebox;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.chenh.messagebox.sina.WBGetAPI;
import com.chenh.messagebox.sina.WBJumpAPI;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;

public class ContentActivity extends AppCompatActivity {

    public static final int JUMP_TO_WEIBO=6;
    public static final int REFRESH_ITEMS=7;
    public static final int SEND_FAILED=9;

    public static final int ADD_MESSAGE=8;


    private ListView mMessage;
    private MessageAdapter mAdpater;
    private ArrayList<Item> data;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        setContentView(R.layout.activity_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent=new Intent(ContentActivity.this,SendMessageActivity.class);
                startActivity(intent);
            }
        });

        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                int what=msg.what;
                String message = msg.obj.toString();
                switch (what){
                    case 0:
                        break;
                    case 1:
                        mAdpater.notifyDataSetChanged();
                        Toast.makeText(ContentActivity.this,"本地存储的Item length"+LocalItem.getLocalItem().items.size(),Toast.LENGTH_SHORT).show();
                        break;
                    case JUMP_TO_WEIBO:
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://m.weibo.cn/"+message));//设置一个URI地址
                        startActivity(intent);//用startActivity打开这个指定的网页。
                        break;
                    case REFRESH_ITEMS:
                        mAdpater.notifyDataSetChanged();
                        break;
                    case SEND_FAILED:
                        Toast.makeText(ContentActivity.this,"发送失败，请检查网络！"+LocalItem.getLocalItem().items.size(),Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        mMessage= (ListView) findViewById(R.id.listView);

        data=LocalItem.getLocalItem().items;

        mAdpater=new MessageAdapter(data);
        mMessage.setAdapter(mAdpater);
        mMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item=data.get(position);
                switch (item.where){
                    case Item.TIP:
                        Toast.makeText(ContentActivity.this,"正在加载",Toast.LENGTH_SHORT).show();
                        WBGetAPI.createInstance(ContentActivity.this,mHandler);
                        WBGetAPI.getWbGetAPI().getNextWB();
                        break;
                    case Item.WEIBO:
                        new WBJumpAPI(ContentActivity.this,mHandler,item.user.id).getBase62(item.mid);
                        break;
                }

            }
        });
        WBGetAPI.createInstance(ContentActivity.this,mHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent=new Intent(ContentActivity.this,SettingActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_refresh){
            WBGetAPI.getWbGetAPI().getNewWB();
        }

        return super.onOptionsItemSelected(item);
    }

    class MessageAdapter extends ArrayAdapter<Item>{

        public MessageAdapter(ArrayList<Item> items) {
            super(ContentActivity.this, 0, items);

        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //如果没有，就inflate一个
            Item c=getItem(position);


            if (c.where==Item.TIP){
                convertView=getLayoutInflater().inflate(R.layout.list_item_tip,null);
                TextView tip= (TextView) convertView.findViewById(R.id.tip);
                tip.setText("点此查看更多");
            }else {



                if (c.sourceItem!=null){
                /*// 获取需要被添加控件的布局
                final LinearLayout lin = (LinearLayout) convertView.findViewById(R.id.item_layout);
                // 获取需要添加的布局
                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.list_item_mark_message, null).findViewById(R.id.repost_layout);
                // 将布局加入到当前布局中
                lin.addView(layout);*/
                    convertView=getLayoutInflater().inflate(R.layout.list_item_mark_message,null);

                    TextView repostContent = (TextView) convertView.findViewById(R.id.repost_text);
                    repostContent.setText("@"+c.sourceItem.user.screen_name+":"+c.sourceItem.text);

                }else {
                    convertView=getLayoutInflater().inflate(R.layout.list_item_raw_message,null);
                }

                ImageView partform= (ImageView) convertView.findViewById(R.id.where);
                partform.setImageResource(R.drawable.ic_com_sina_weibo_sdk_logo);

                if (c.allPics!=null){
                    if (c.allPics.size()!=0){
                        LinearLayout layout= (LinearLayout) convertView.findViewById(R.id.pic_area);
                        ImageView imageView= new ImageView(ContentActivity.this);
                        imageView.setImageBitmap(c.allPics.get(0));
                        LinearLayout.LayoutParams PARA = new LinearLayout.LayoutParams(
                                (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 200)
                                ,(int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 200)
                        );
                        imageView.setLayoutParams(PARA);
                        layout.addView(imageView);
                    }
                }

                TextView user=(TextView) convertView.findViewById(R.id.user_name);
                user.setText(c.user.screen_name);

                TextView postTime=(TextView) convertView.findViewById(R.id.post_time);
                postTime.setText(c.created_at);

                TextView content=(TextView) convertView.findViewById(R.id.content);
                content.setText(c.text);

                ImageView head=(ImageView)convertView.findViewById(R.id.message_head);
                head.setImageBitmap(c.userHead);
            }

            return convertView;
        }

    }

    public float getRawSize(int unit, float value) {
        Resources res = this.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }

}
