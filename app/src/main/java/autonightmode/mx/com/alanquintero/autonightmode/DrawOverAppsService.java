/*****************************************************************
 *
 * Copyright (C) 2018 Alan Quintero <http://alanquintero.com.mx/>
 *
 *****************************************************************/
package autonightmode.mx.com.alanquintero.autonightmode;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;

public class DrawOverAppsService extends Service {

    private int level = 0;
    private int light = 0;
    private boolean isLightOn = false;
    private String colorSelected;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private SharedPreferences sharedpreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedpreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        isLightOn = Boolean.parseBoolean(sharedpreferences.getString(Constants.IS_LIGHT_ON, Constants.VALUE_FALSE));
        level = Integer.parseInt(sharedpreferences.getString(Constants.COLOR_LEVEL, Constants.VALUE_ZERO));
        colorSelected = sharedpreferences.getString(Constants.COLOR, Constants.COLOR_WHITE);
        light = Integer.parseInt(sharedpreferences.getString(Constants.LIGHT_LEVEL, Constants.VALUE_ZERO));
        if(isLightOn) {
            onUpdate();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        if(!isLightOn) {
            mView = new MyLoadView(this);
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);

            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mView, mParams);

            startNotification();
            super.onCreate();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        }
        stopNotification();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onUpdate() {
        if(mView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        }
        mView = new MyLoadView(this);
        mWindowManager.addView(mView, mParams);
    }

    public void startNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new Notification.Builder(this)
                        .setContentTitle(Constants.TEXT_READ_MODE)
                        .setContentText(Constants.TEXT_READ_MODE_MSG)
                        .setSmallIcon(R.drawable.moon)
                        .setContentIntent(pendingIntent)
                        .setTicker(Constants.TEXT_READ_MODE_MSG)
                        .build();

        startForeground(Constants.NOTIFICATION_ID, notification);
    }

    private void stopNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Constants.NOTIFICATION_ID);
    }

    public class MyLoadView extends View {

        public MyLoadView(Context context)
        {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (colorSelected != null) {
                canvas.drawARGB(150 - light, 0, 0, 0);
                switch (colorSelected) {
                    case Constants.COLOR_YELLOW:
                        canvas.drawARGB(120, 255, 212, 120 - level);
                        break;
                    case Constants.COLOR_RED:
                        canvas.drawARGB(120, 255, 20, 150 - level);
                        break;
                    case Constants.COLOR_GREEN:
                        canvas.drawARGB(120, 10, 255, 150 - level);
                        break;
                    case Constants.COLOR_BLUE:
                        canvas.drawARGB(120, 100, 212, 150 - level);
                        break;
                    case Constants.COLOR_GRAY:
                        canvas.drawARGB(120, 50, 50, 150 - level);
                        break;
                    case Constants.COLOR_PINK:
                        canvas.drawARGB(120, 255, 120, 150 - level);
                        break;
                    case Constants.COLOR_WHITE:
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        protected void onAttachedToWindow()
        {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

}