package com.ljun.fineex;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

public class MainActivity extends AppCompatActivity {
    SwitchCompat switchCompat;
    private NotificationManagerCompat manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = NotificationManagerCompat.from(this);
        /*Button floatingButton = new Button(this);
        floatingButton.setText("button");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                0, 0,
                PixelFormat.TRANSPARENT
        );
        // flag 设置 Window 属性
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        // type 设置 Window 类别（层级）
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        layoutParams.gravity = Gravity.CENTER;
        WindowManager windowManager = getWindowManager();
        windowManager.addView(floatingButton, layoutParams);*/
        switchCompat = (SwitchCompat) findViewById(R.id.switch2);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        switchCompat.setTrackResource(R.mipmap.switch_green);
                    } else {
                        switchCompat.setTrackResource(R.mipmap.switch_gray);
                    }
                }
            }
        });
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification("标题", "副标题",
                        Uri.parse("http://i.gtimg.cn/music/photo/mid_album_300/m/h/003cpvED39hxmh.jpg"));
            }
        });
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.cancel(10);
            }
        });
    }

    private void notification(final String title, final String sub, Uri uri) {
        if (!MainActivity.this.isDestroyed()) {
            Glide.with(this).load(uri).asBitmap()
                    .centerCrop()
                    .error(R.mipmap.ic_launcher)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            setNotify(title, sub, resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            setNotify(title, sub, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                        }
                    });
        } else {
            setNotify(title, sub, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        }

    }

    private void setNotify(String title, String sub, Bitmap largeIcon) {
        RemoteViews remoteViews = new RemoteViews(MainActivity.this.getPackageName(), R.layout.widget_standard);
        remoteViews.setTextViewText(R.id.title1, title);
        remoteViews.setTextViewText(R.id.sub_title, sub);
        remoteViews.setImageViewBitmap(R.id.iv_cover, largeIcon);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews);

        manager.notify(10, builder.build());
    }

    private void setNotify1(String title, String sub, Bitmap largeIcon) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(sub);
        manager.notify(10, builder.build());
    }

    private PendingIntent pIntent(String action) {
        Intent intent = new Intent(action);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static final String PREVIOUS = "previous";
    public static final String TOGGLE_PLAY = "togglePlay";
    public static final String NEXT = "next";
}
