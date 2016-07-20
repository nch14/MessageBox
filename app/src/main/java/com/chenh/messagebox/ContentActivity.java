package com.chenh.messagebox;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.chenh.messagebox.sina.WBGetAPI;
import com.chenh.messagebox.util.ListView.DemoLoadMoreView;
import com.chenh.messagebox.util.ListView.DividerItemDecoration;
import com.chenh.messagebox.util.ListView.PtrrvBaseAdapter;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.lhh.ptrrv.library.footer.loadmore.BaseLoadMoreView;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity {

    public static final int JUMP_TO_WEIBO=6;
    public static final int REFRESH_ITEMS=7;
    public static final int SEND_FAILED=9;

    public static final int ADD_MESSAGE=8;

    private static final int DEFAULT_ITEM_SIZE = 20;
    private static final int ITEM_SIZE_OFFSET = 20;

    private static final int MSG_CODE_REFRESH = 0;
    private static final int MSG_CODE_LOADMORE = 1;

    private static final int TIME = 1000;


    private PullToRefreshRecyclerView mMessage;
    private PtrrvAdapter mAdpater;
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

       /* mAdpater=new MessageAdapter(data);
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
        });*/

        data=LocalItem.getLocalItem().items;

        WBGetAPI.createInstance(ContentActivity.this,mHandler);


        mMessage= (PullToRefreshRecyclerView) findViewById(R.id.listView);
        mMessage.setSwipeEnable(true);//open swipe
        DemoLoadMoreView loadMoreView = new DemoLoadMoreView(this, mMessage.getRecyclerView());
        loadMoreView.setLoadmoreString("加载更多");
        loadMoreView.setLoadMorePadding(100);
        mMessage.setLayoutManager(new LinearLayoutManager(this));

        mMessage.setPagingableListener(new PullToRefreshRecyclerView.PagingableListener() {
            @Override
            public void onLoadMoreItems() {
                //WBGetAPI.getWbGetAPI().getNextWB();
                //mHandler.sendEmptyMessageDelayed(MSG_CODE_LOADMORE, TIME);
            }
        });
        mMessage.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                WBGetAPI.getWbGetAPI().getNewWB();
                //mHandler.sendEmptyMessageDelayed(MSG_CODE_REFRESH, TIME);
            }
        });
        mMessage.getRecyclerView().addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mMessage.addHeaderView(View.inflate(this, R.layout.header, null));
        mMessage.setEmptyView(View.inflate(this,R.layout.empty_view,null));
//        mPtrrv.removeHeader();
        mMessage.setLoadMoreFooter(loadMoreView);
        mMessage.getLoadMoreFooter().setOnDrawListener(new BaseLoadMoreView.OnDrawListener() {
            @Override
            public boolean onDrawLoadMore(Canvas c, RecyclerView parent) {
                Log.i("onDrawLoadMore","draw load more");
                WBGetAPI.getWbGetAPI().getNextWB();
                return false;
            }
        });
        mAdpater = new PtrrvAdapter(this,data);
//        mAdapter.setCount(0);
        mMessage.setAdapter(mAdpater);
        mMessage.onFinishLoading(true, false);
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
                        ImageView imageView= (ImageView) convertView.findViewById(R.id.pic);
                        imageView.setImageBitmap(c.allPics.get(0));
                        if(c.allPics.size()>1){
                            ImageView imageView2= (ImageView) convertView.findViewById(R.id.pic2);
                            imageView2.setImageBitmap(c.allPics.get(1));
                        }
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


    private class PtrrvAdapter extends PtrrvBaseAdapter {
        public static final int PURE_MESSAGE=1;
        public static final int REMARK_MESSAGE=2;

        public PtrrvAdapter(Context context, List<Item> data) {
            super(context, data);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType){
                case REMARK_MESSAGE:
                    view = mInflater.inflate(R.layout.list_item_mark_message, null);
                    return new RemarkHolder(view);
                case PURE_MESSAGE:
                    view = mInflater.inflate(R.layout.list_item_raw_message, null);
                    return new PureHolder(view);
                default:
                    view = mInflater.inflate(R.layout.list_item_mark_message, null);
                    return new PureHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Item c=mData.get(position);
            switch (holder.getItemViewType()){
                case REMARK_MESSAGE:
                    RemarkHolder myViewHolder=(RemarkHolder)holder;
                    myViewHolder.user.setText(c.user.screen_name);
                    myViewHolder. postTime.setText(c.created_at);
                    myViewHolder.content.setText(c.text);
                    myViewHolder.partform.setImageResource(R.drawable.ic_com_sina_weibo_sdk_logo);
                    myViewHolder.head.setImageBitmap(c.userHead);
                    myViewHolder.repostContent.setText("@"+c.sourceItem.user.screen_name+":"+c.sourceItem.text);
                    if (c.allPics!=null){
                        if (c.allPics.size()!=0){
                            myViewHolder.pic.setImageBitmap(c.allPics.get(0));
                            if(c.allPics.size()>1){
                                myViewHolder.pic.setImageBitmap(c.allPics.get(1));
                            }
                        }
                    }
                    break;
                case PURE_MESSAGE:
                    PureHolder myViewHolder2=(PureHolder)holder;
                    myViewHolder2.user.setText(c.user.screen_name);
                    myViewHolder2. postTime.setText(c.created_at);
                    myViewHolder2.content.setText(c.text);
                    myViewHolder2.partform.setImageResource(R.drawable.ic_com_sina_weibo_sdk_logo);
                    myViewHolder2.head.setImageBitmap(c.userHead);
                    if (c.allPics!=null){
                        if (c.allPics.size()!=0){
                            myViewHolder2.pic.setImageBitmap(c.allPics.get(0));
                            if(c.allPics.size()>1){
                                myViewHolder2.pic.setImageBitmap(c.allPics.get(1));
                            }
                        }
                    }
                    break;
            }

            super.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemViewType(int position) {
            if (mData.get(position).sourceItem!=null){
                return REMARK_MESSAGE;
            }else {
                return PURE_MESSAGE;
            }
        }

        class PureHolder extends RecyclerView.ViewHolder{
            public TextView user;
            public TextView postTime;
            public TextView content;
            public ImageView head;
            public ImageView partform;
            public ImageView pic;
            public ImageView pic2;
            public PureHolder(View itemView) {
                super(itemView);
                user=(TextView) itemView.findViewById(R.id.user_name);
                postTime=(TextView) itemView.findViewById(R.id.post_time);
                content=(TextView) itemView.findViewById(R.id.content);
                head=(ImageView)itemView.findViewById(R.id.message_head);
                partform= (ImageView) itemView.findViewById(R.id.where);
                pic= (ImageView)itemView.findViewById(R.id.pic);
                pic2= (ImageView)itemView.findViewById(R.id.pic2);
            }
        }
        class RemarkHolder extends RecyclerView.ViewHolder{
            public TextView user;
            public TextView postTime;
            public TextView content;
            public ImageView head;
            public ImageView partform;
            public ImageView pic;
            public ImageView pic2;
            public TextView repostContent;
            public RemarkHolder(View itemView) {
                super(itemView);
                user=(TextView) itemView.findViewById(R.id.user_name);
                postTime=(TextView) itemView.findViewById(R.id.post_time);
                content=(TextView) itemView.findViewById(R.id.content);
                head=(ImageView)itemView.findViewById(R.id.message_head);
                partform= (ImageView) itemView.findViewById(R.id.where);
                pic= (ImageView)itemView.findViewById(R.id.pic);
                pic2= (ImageView)itemView.findViewById(R.id.pic2);
                repostContent = (TextView) itemView.findViewById(R.id.repost_text);

            }
        }

    }

    public float getRawSize(int unit, float value) {
        Resources res = this.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }

}
