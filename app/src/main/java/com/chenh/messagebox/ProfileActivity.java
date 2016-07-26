package com.chenh.messagebox;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;


public class ProfileActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE=1;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("修改头像");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        imageView= (ImageView) findViewById(R.id.imageView4);
        imageView.setImageResource(R.drawable.head_59);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //仅返回可以打开流的文件
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //设置正确的MIME类型并启动
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK){
            //返回的Intent中包含用户所选的文件
            ContentResolver resolver = getContentResolver();
            Uri selectedContent = data.getData();
            if (requestCode==REQUEST_IMAGE){
                //向BitmapFactory传递InputStream
                imageView.setImageURI(selectedContent);
            }
        }
    }

}
