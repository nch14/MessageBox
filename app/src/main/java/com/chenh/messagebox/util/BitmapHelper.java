package com.chenh.messagebox.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by chenh on 2016/7/19.
 */
public class BitmapHelper {
    static int IO_BUFFER_SIZE=2048;

//    public static Bitmap decodeFile(File file){
//        try {
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),bmOptions);
//            bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
//
//        } catch (FileNotFoundException e) {}
//        return null;
//    }




     public static Bitmap decodeFile(File file){
         try {
             BitmapFactory.Options opt = new BitmapFactory.Options();
             opt.inJustDecodeBounds = true;
             BitmapFactory.decodeStream(new FileInputStream(file),null,opt);
             final int REQUIRED_SIZE=70;
             int width_tmp=opt.outWidth, height_tmp=opt.outHeight;
             int scale=1;
             while(true){
             if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
             break;
             width_tmp/=2;
             height_tmp/=2;
             scale*=2;
             }
             BitmapFactory.Options opte = new BitmapFactory.Options();
             opte.inSampleSize=scale;
             return BitmapFactory.decodeStream(new FileInputStream(file), null, opte);
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         }
         return null;
     }

}
