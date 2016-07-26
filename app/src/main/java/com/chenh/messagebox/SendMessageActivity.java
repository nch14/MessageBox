package com.chenh.messagebox;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chenh.messagebox.fb.FBGetApi;
import com.chenh.messagebox.sina.WBSendAPI;
import com.chenh.messagebox.twiiter.TwitterSendAPI;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SendMessageActivity extends AppCompatActivity {


    public static final int REQUEST_IMAGE=1;

    private ImageView mSinaLogo;
    private ImageView mQzoneLogo;
    private ImageView mTwitterLogo;
    private ImageView mFacebookLogo;

    private boolean mSinaChoosed;
    private boolean mQzoneChoosed;
    private boolean mTwitterChoosed;
    private boolean mFacebookChoosed;

    private boolean mAddPhoto;

    private EditText mEditText;
    private ImageView mImage;
    private Bitmap bitmap;

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

        mEditText= (EditText) findViewById(R.id.editText);
        mImage= (ImageView) findViewById(R.id.imageView);
        mImage.setOnClickListener(new InnerOnclickListener());
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
                if (mAddPhoto){
                    Toast.makeText(SendMessageActivity.this,"已经发布!",Toast.LENGTH_SHORT).show();
                    if (mSinaChoosed)
                        new WBSendAPI(SendMessageActivity.this,null).sendWeiboWithPic(mEditText.getText().toString(),bitmap);
                    if (mTwitterChoosed)
                        TwitterSendAPI.send(new String[]{mEditText.getText().toString(),bitmap.toString()});
                    if (mFacebookChoosed){
                        Bitmap image = bitmap;
                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(image)
                                .build();
                        SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .build();
                        ShareApi.share(content, null);
                    }
                }else {
                    Toast.makeText(SendMessageActivity.this,"已经发布!",Toast.LENGTH_SHORT).show();
                    if (mSinaChoosed)
                        new WBSendAPI(SendMessageActivity.this,null).sendWeibo(mEditText.getText().toString());
                    if (mTwitterChoosed)
                        TwitterSendAPI.send(mEditText.getText().toString());
                    if (mFacebookChoosed)
                        FBGetApi.sendFB(mEditText.getText().toString());
                }

                this.finish();
            }else {
                Toast.makeText(SendMessageActivity.this,"至少选择一个要分享的平台",Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class InnerOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            int id=v.getId();
            Intent intent;

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
                    if (!mTwitterChoosed) {
                        ((ImageView) v).setImageResource(R.drawable.twitter_logo);
                        mTwitterChoosed=true;
                    }else {
                        ((ImageView) v).setImageResource(R.drawable.twitter_logo_grey);
                        mTwitterChoosed=false;
                    }
                    break;
                case R.id.imageView_facebook:
                    if (!mFacebookChoosed) {
                        ((ImageView) v).setImageResource(R.drawable.facebook_logo);
                        mFacebookChoosed=true;
                    }else {
                        ((ImageView) v).setImageResource(R.drawable.facebook_logo_grey);
                        mFacebookChoosed=false;
                    }
                    break;
                case R.id.imageView:
                    intent=new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    //仅返回可以打开流的文件
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    //设置正确的MIME类型并启动
                    intent.setType("image/*");
                    startActivityForResult(intent,REQUEST_IMAGE);
                    break;

            }


        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK){
            //返回的Intent中包含用户所选的文件
            ContentResolver resolver = getContentResolver();
            Uri selectedContent = data.getData();
            if (requestCode==REQUEST_IMAGE){
                //向BitmapFactory传递InputStream

                try {
                    //使用ContentProvider通过URI获取原始图片
                    bitmap = MediaStore.Images.Media.getBitmap(resolver, selectedContent);
/*                    if (bitmap != null) {
                        //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                        Bitmap smallBitmap = zoomBitmap(photo, photo.getWidth() / 5, photo.getHeight() / 5);
                        //释放原始图片占用的内存，防止out of memory异常发生
                        bitmap.recycle();

                        //iv_image.setImageBitmap(smallBitmap);
                    }*/
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    mImage.setImageURI(selectedContent);

                    mAddPhoto=true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

}
