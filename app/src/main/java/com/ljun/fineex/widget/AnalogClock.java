package com.ljun.fineex.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;

import com.ljun.fineex.R;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class AnalogClock extends View {
    private final LocalBroadcastManager broadcastManager;
    private Calendar mCalendar;    //用来记录当前时间
    private Timer timer;
    //用来存放三张图片资源
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;
    private Drawable mDial;

    //用来记录表盘图片的宽和高，
    //以便帮助我们在onMeasure中确定View的大
    //小，毕竟，我们的View中最大的一个Drawable就是它了。
    private int mDialWidth;
    private int mDialHeight;

    /**
     * 用来记录View是否被加入到了Window中，我们在View attached到
     * Window时注册监听器，监听时间的变更，并根据时间的变更，改变自己
     * 的绘制，在View从Window中剥离时，解除注册，因为我们不需要再监听
     * 时间变更了，没人能看得到我们的View了。
     */
    private boolean mAttached;

    private float mMinutes;
    private float mHour;
    private float mSecond;
    /**
     * 用来跟踪我们的View 的尺寸的变化，
     * 当发生尺寸变化时，我们在绘制自己
     * 时要进行适当的缩放。
     */
    private boolean mChanged;

    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        //final Resources r = context.getResources();
        timer = new Timer();
        broadcastManager = LocalBroadcastManager.getInstance(context);
        if (mDial == null) {
            mDial = context.getDrawable(R.mipmap.clock_dial);
        }
        if (mHourHand == null) {
            mHourHand = context.getDrawable(R.mipmap.clock_hand_hour);
        }
        if (mMinuteHand == null) {
            mMinuteHand = context.getDrawable(R.mipmap.clock_hand_minute);
        }
        if (mSecondHand == null) {
            mSecondHand = context.getDrawable(R.mipmap.ic_second);
        }
        mCalendar = Calendar.getInstance();
        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }

    private Handler handler = new Handler();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            //这里确定我们要监听的三种系统广播
            filter.addAction("updateTime");
            //filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            broadcastManager.registerReceiver(mIntentReceiver, filter);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(runnable);
                }
            }, 0, 1000);
        }
        mCalendar = Calendar.getInstance();
        onTimeChanged();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent("updateTime"));
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            broadcastManager.unregisterReceiver(mIntentReceiver);
            mAttached = false;
            timer.cancel();
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //View尺寸变化后，我们用changed变量记录下来，
        //同时，恢复mChanged为false，以便继续监听View的尺寸变化。
        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }
        /* 请注意，这里的availableWidth和availableHeight，
           每次绘制时是可能变化的，
           我们可以从mChanged变量的值判断它是否发生了变化，
           如果变化了，说明View的尺寸发生了变化，
           那么就需要重新为时针、分针设置Bounds，
           因为我们需要时针，分针始终在View的中心。*/
        int availableWidth = super.getRight() - super.getLeft();
        int availableHeight = super.getBottom() - super.getTop();

        /* 这里的x和y就是View的中心点的坐标，
          注意这个坐标是以View的左上角为0点，向右x，向下y的坐标系来计算的。
          这个坐标系主要是用来为View中的每一个Drawable确定位置。
          就像View的坐标是用parent的左上角为0点的坐标系计算得来的一样。
          简单来讲，就是ViewGroup用自己左上角为0点的坐标系为
          各个子View安排位置，
          View同样用自己左上角为0点的坐标系
          为它里面的Drawable安排位置。
          注意不要搞混了。*/

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();
        boolean scaled = false;

        /*如果可用的宽高小于表盘图片的宽高，
           就要进行缩放，不过这里，我们是通过坐标系的缩放来实现的。
          而且，这个缩放效果影响是全局的，
          也就是下面绘制的表盘、时针、分针都会受到缩放的影响。*/
        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                    (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

         /*如果尺寸发生变化，我们要重新为表盘设置Bounds。
           这里的Bounds就相当于是为Drawable在View中确定位置，
           只是确定的方式更直接，直接在View中框出一个与Drawable大小
           相同的矩形，
           Drawable就在这个矩形里绘制自己。
           这里框出的矩形，是以(x,y)为中心的，宽高等于表盘图片的宽高的一个矩形，
           不用担心表盘图片太大绘制不完整，
            因为我们已经提前进行了缩放了。*/
        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

        canvas.save();
          /*根据小时数，以点(x,y)为中心旋转坐标系。
            如果你对来回旋转的坐标系感到头晕，摸不着头脑，
            建议你看一下**徐宜生**《安卓群英传》中讲解2D绘图部分中的Canvas一节。*/
        canvas.rotate(mHour * 360.0f / 12.0f, x, y);
        final Drawable hourHand = mHourHand;
        //同样，根据变化重新设置时针的Bounds
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();

        canvas.save();
        //根据分针旋转坐标系
        canvas.rotate(mMinutes * 360.0f / 60.0f, x, y);
        final Drawable minuteHand = mMinuteHand;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();

        canvas.save();
        //根据分针旋转坐标系
        canvas.rotate(mSecond * 360.0f / 60.0f, x, y);
        final Drawable secondHand = mSecondHand;
        if (changed) {
            w = secondHand.getIntrinsicWidth();
            h = secondHand.getIntrinsicHeight();
            secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        secondHand.draw(canvas);
        canvas.restore();
        //最后，我们把缩放的坐标系复原。
        if (scaled) {
            canvas.restore();
        }
    }

    private void onTimeChanged() {
        mCalendar.setTime(new Date());
        int hour = mCalendar.get(Calendar.HOUR);
        int minute = mCalendar.get(Calendar.MINUTE);
        /*这里我们为什么不直接把minute设置给mMinutes，而是要加上
         second /60.0f呢，这个值不是应该一直为0吗？
         这里又涉及到Calendar的 一个知识点，
         也就是它可以是Linient模式，
         此模式下，second和minute是可能超过60和24的，具体这里就不展开了，
         如果不是很清楚，建议看看Google的官方文档中讲Calendar的部分 */

        mSecond = mCalendar.get(Calendar.SECOND);
        mMinutes = minute + mSecond / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        //Log.e("时间", mHour + ":" + mMinutes + ":" + mSecond);
        mChanged = true;
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //这个if判断主要是用来在时区发生变化时，更新mCalendar的时区的，这
            //样，我们的自定义View在全球都可以使用了。
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = Calendar.getInstance(TimeZone.getTimeZone(tz));
            }
            //进行时间的更新
            onTimeChanged();
            //invalidate当然是用来引发重绘了。
            invalidate();
        }
    };
}
