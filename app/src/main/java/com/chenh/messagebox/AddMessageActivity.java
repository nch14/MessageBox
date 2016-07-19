package com.chenh.messagebox;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chenh.messagebox.sina.WBGetAPI;

public class AddMessageActivity extends AppCompatActivity {
    private ImageView mSinaLogo;
    private ImageView mQzoneLogo;
    private ImageView mTwitterLogo;
    private ImageView mFacebookLogo;

    private boolean mSinaChoosed;
    private boolean mQzoneChoosed;
    private boolean mTwitterChoosed;
    private boolean mFacebookChoosed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("写说说");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSinaLogo= (ImageView) findViewById(R.id.imageView_sina);
        mSinaLogo.setOnClickListener(new InnerOnclickListener());

        mQzoneLogo= (ImageView) findViewById(R.id.imageView_qzone);
        mQzoneLogo.setOnClickListener(new InnerOnclickListener());

        mTwitterLogo= (ImageView) findViewById(R.id.imageView_twitter);
        mTwitterLogo.setOnClickListener(new InnerOnclickListener());

        mFacebookLogo= (ImageView) findViewById(R.id.imageView_facebook);
        mFacebookLogo.setOnClickListener(new InnerOnclickListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_push) {
            if (mQzoneChoosed||mSinaChoosed||mFacebookChoosed||mTwitterChoosed){
                Toast.makeText(AddMessageActivity.this,"已经发布了",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(AddMessageActivity.this,"至少选择一个要分享的平台",Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class InnerOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int id=v.getId();

            switch (id){
                case R.id.imageView_sina:
                    if (!mSinaChoosed) {
                        ((ImageView) v).setImageResource(R.drawable.ic_com_sina_weibo_sdk_logo);
                        mSinaChoosed=true;
                    }else {
                        ((ImageView) v).setImageResource(R.drawable.ic_com_sina_weibo_sdk_logo_grey);
                        mSinaChoosed=false;
                    }
                    break;
                case R.id.imageView_qzone:
                    if (!mQzoneChoosed) {
                        ((ImageView)v).setImageResource(R.drawable.qq_logo_color);
                        mQzoneChoosed=true;
                    }else {
                        ((ImageView)v).setImageResource(R.drawable.qq_logo_grey);
                        mQzoneChoosed=false;
                    }




                    break;
                case R.id.imageView_twitter:
                    break;
                case R.id.imageView_facebook:
                    break;

            }


        }
    }


}
