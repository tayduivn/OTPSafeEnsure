package com.leo.gesturelibray.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.leo.gesturelibray.crypto.Base64;
import com.leo.gesturelibray.entity.Point;
import com.leo.gesturelibray.enums.LockMode;
import com.leo.gesturelibray.util.ConfigUtil;
import com.leo.gesturelibray.util.LockUtil;
import com.leo.gesturelibray.util.MathUtil;
import com.leo.gesturelibray.util.OnCompleteListener;
import com.leo.gesturelibray.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 手势解锁
 */
public class CustomLockView extends View {
    //保存密码key
    private String saveLockKey = "saveLockKey";
    //是否保存保存PIN
    private boolean isSavePin = false;
    //控件宽度
    private float width = 0;
    //控件高度
    private float height = 0;
    //是否已缓存
    private boolean isCache = false;
    //画笔
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //九宫格的圆
    private Point[][] mPoints = new Point[3][3];
    //选中圆的集合
    private List<Point> sPoints = new ArrayList<Point>();
    //判断是否正在绘制并且未到达下一个点
    private boolean movingNoPoint = false;
    //正在移动的x,y坐标
    float moveingX, moveingY;
    //密码最小长度
    private int passwordMinLength = 3;
    //判断是否触摸屏幕
    private boolean checking = false;
    //刷新
    private TimerTask task = null;
    //计时器
    private Timer timer = new Timer();
    //监听
    private OnCompleteListener mCompleteListener;
    //清除痕迹的时间
    private long CLEAR_TIME = 100;
    //错误限制 默认为4次
    private int errorNumber = 4;
    //记录上一次滑动的密码
    private String oldPassword = null;
    //记录当前第几次触发 默认为0次
    private int showTimes = 0;
    //是否显示滑动方向 默认为显示
    private boolean isShow = false;
    //当前密码是否正确 默认为正确
    private boolean isCorrect = true;
    //验证或者设置 0:设置 1:验证
    private LockMode mode = LockMode.SETTING_PASSWORD;
    //用于执行清除界面
    private Handler handler = new Handler();
    //按下时连线的颜色
    private int mColorOnLine = Color.parseColor("#1f8cf1");
    //错误的连线颜色
    private int mColorErroLine = Color.RED;

    //普通状态下外圈的颜色
    private int mColorUpRing = Color.parseColor("#6cb8ff");//蓝色
    //按下时外圈的颜色
    private int mColorOnRing = Color.parseColor("#6cb8ff");//蓝色
    //错误时外圈的颜色
    private int mColorErrorRing = Color.parseColor("#6cb8ff");//蓝色

    //普通状态下内圈的颜色
    private int mInnerColorUpRing = Color.TRANSPARENT;
    //按下时内圈的颜色
    private int mInnerColorOnRing = Color.parseColor("#1f8cf1");//深蓝色
    //松开手时 错误内圈的颜色
    private int mInnerColorErrorRing = Color.RED;

    //内圆大小
    private float mInnerRingWidth = 0;
    //外圈大小
    private float mOuterRingWidth = 0;
    //左右的间距
    private float horizontalMargin = 0;
    //内圆间距
    private float mCircleHorizontalSpacing = 0;
    //内圆竖向的间距
    private float mCircleVerticalSpacing = 0;
    //小圆半径
    private float mInnerRingRadius = 0;
    //大圆半径
    private float mOuterRingRadios = 0;
    //按下时圆圈的边宽
    private int mOnStrokeWidth = 4;
    //编辑密码前是否验证
    private boolean isEditVerify = false;
    //是否立即清除密码
    private boolean isClearPasssword = true;

    //用于定时执行清除界面
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(run);
            reset();
            postInvalidate();
        }
    };

    public CustomLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MathUtil.measure(widthMeasureSpec);
        height = MathUtil.measure(heightMeasureSpec);
        Log.d("TEST11", "width:" + width);
        Log.d("TEST11", "height:" + height);
//        width = Math.min(width, height);
//        height = width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isCache) {
            initCache();
        }
        //绘制圆以及显示当前状态
        drawToCanvas(canvas);
    }

    /**
     * 计算圆以及连接线的尺寸
     */
    private void initGestureLockViewWidth() {
//        0.2*2+0.06*3+0.22*2=1
//        18    8     20    8  20    8  18
        //16 10
        horizontalMargin = width * 0.15f;//左右间距
        mOuterRingWidth = width * 0.1f;//外圈大小
        mInnerRingWidth = width * 0.04f;//内园大小
        mCircleHorizontalSpacing = width * 0.2f;//内圆间距
        mCircleVerticalSpacing = width * 0.17f;//大圆半径
        mOuterRingRadios = mOuterRingWidth / 2;//大圆半径
        mInnerRingRadius = mInnerRingWidth / 2;//小圆半径
        Log.d("TEST11", "horizontalMargin:" + horizontalMargin);
        Log.d("TEST11", "mOuterRingWidth:" + mOuterRingWidth);
        Log.d("TEST11", "mInnerRingWidth:" + mInnerRingWidth);
        Log.d("TEST11", "mCircleHorizontalSpacing:" + mCircleHorizontalSpacing);
        Log.d("TEST11", "mCircleVerticalSpacing:" + mCircleVerticalSpacing);
        Log.d("TEST11", "mOuterRingRadios:" + mOuterRingRadios);
        Log.d("TEST11", "mInnerRingRadius:" + mInnerRingRadius);
    }


    /**
     * 初始化Cache信息
     */
    private void initCache() {
//        float y = 0;
        initGestureLockViewWidth();
        float x1 = horizontalMargin + mOuterRingRadios;
        float x2 = horizontalMargin + (mCircleHorizontalSpacing + mOuterRingWidth) + mOuterRingRadios;
        float x3 = horizontalMargin + 2 * (mCircleHorizontalSpacing + mOuterRingWidth) + mOuterRingRadios;

        float y1 = mOuterRingRadios;
        float y2 = mCircleVerticalSpacing + mOuterRingWidth + mOuterRingRadios;
        float y3 = mCircleVerticalSpacing * 2 + mOuterRingWidth * 2 + mOuterRingRadios;


        // 计算圆圈的大小及位置
        mPoints[0][0] = new Point(x1, y1);
        mPoints[0][1] = new Point(x2, y1);
        mPoints[0][2] = new Point(x3, y1);
        mPoints[1][0] = new Point(x1, y2);
        mPoints[1][1] = new Point(x2, y2);
        mPoints[1][2] = new Point(x3, y2);
        mPoints[2][0] = new Point(x1, y3);
        mPoints[2][1] = new Point(x2, y3);
        mPoints[2][2] = new Point(x3, y3);
        int k = 0;
        for (Point[] ps : mPoints) {
            for (Point p : ps) {
                p.index = k;
                k++;
            }
        }
        isCache = true;
    }


    /**
     * 图像绘制
     *
     * @param canvas
     */
    private void drawToCanvas(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        // 画所有点
        drawAllPoint(canvas);
        //画连接线
        drawAllLine(canvas);
    }

    /**
     * 绘制解锁连接线
     *
     * @param canvas
     */
    private void drawAllLine(Canvas canvas) {
        if (sPoints.size() > 0) {
            Point tp = sPoints.get(0);
            for (int i = 1; i < sPoints.size(); i++) {
                //根据移动的方向绘制线
                Point p = sPoints.get(i);
                if (p.state == Point.STATE_CHECK_ERROR) {
                    drawErrorLine(canvas, tp, p);
                } else {
                    drawLine(canvas, tp, p);
                }
                tp = p;
            }
            if (this.movingNoPoint) {
                //到达下一个点停止移动绘制固定的方向
                drawLine(canvas, tp, new Point((int) moveingX + 20, (int) moveingY));
            }
        }
    }


    // 绘制内圆
    private void drawInnerCircle(Canvas canvas, Point pCicle, boolean error) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(error ? mInnerColorErrorRing : mInnerColorOnRing);
        canvas.drawCircle(pCicle.x, pCicle.y, mInnerRingRadius, mPaint);
    }


    /**
     * 绘制解锁图案所有的点
     *
     * @param canvas
     */
    private void drawAllPoint(Canvas canvas) {
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                Point p = mPoints[i][j];
                if (p != null) {
                    if (p.state == Point.STATE_CHECK) {
                        onDrawOn(canvas, p);
                    } else if (p.state == Point.STATE_CHECK_ERROR) {
                        onDrawError(canvas, p);
                    } else {
                        onDrawNoFinger(canvas, p);
                    }
                }
            }
        }
    }


    /**
     * 绘制按下时状态
     *
     * @param canvas
     */
    private void onDrawOn(Canvas canvas, Point p) {
        // 绘制外圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorOnRing);
        mPaint.setStrokeWidth(mOnStrokeWidth);
        canvas.drawCircle(p.x, p.y, mOuterRingRadios, mPaint);
        drawInnerCircle(canvas, p, false);
    }


    /**
     * 绘制松开手时状态
     *
     * @param canvas
     */
    private void onDrawError(Canvas canvas, Point p) {
        // 绘制圆圈
        mPaint.setColor(mColorErrorRing);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mOnStrokeWidth);
        canvas.drawCircle(p.x, p.y, mOuterRingRadios, mPaint);
        drawInnerCircle(canvas, p, true);
    }


    /**
     * 绘制普通状态
     *
     * @param canvas
     */
    private void onDrawNoFinger(Canvas canvas, Point p) {
        // 绘制外圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColorUpRing);
        mPaint.setStrokeWidth(mOnStrokeWidth);
        canvas.drawCircle(p.x, p.y, mOuterRingRadios, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 不可操作
        if (errorNumber <= 0) {
            return false;
        }
        isCorrect = true;
        handler.removeCallbacks(run);
        movingNoPoint = false;
        float ex = event.getX();
        float ey = event.getY();
        boolean isFinish = false;
        Point p = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 点下
                // 如果正在清除密码,则取消
                p = actionDown(ex, ey);
                break;
            case MotionEvent.ACTION_MOVE: // 移动
                p = actionMove(ex, ey);
                break;
            case MotionEvent.ACTION_UP: // 提起
                p = checkSelectPoint(ex, ey);
                checking = false;
                isFinish = true;
                break;
            default:
                movingNoPoint = true;
                break;
        }
        if (!isFinish && checking && p != null) {
            int rk = crossPoint(p);
            if (rk == 2) {
                //与非最后一重叠
                movingNoPoint = true;
                moveingX = ex;
                moveingY = ey;
            } else if (rk == 0) {
                //一个新点
                p.state = Point.STATE_CHECK;
                addPoint(p);
            }
        }
        if (isFinish) {
            actionFinish();
        }
        postInvalidate();
        return true;
    }

    /**
     * 解锁图案绘制完成
     */
    private void actionFinish() {
        handler.postDelayed(run, 500);
        if (this.sPoints.size() == 1) {
            this.reset();
            return;
        }
        if (this.sPoints.size() < passwordMinLength
                && this.sPoints.size() > 0) {
            // clearPassword(CLEAR_TIME);
            error();
            if (mCompleteListener != null) {
                mCompleteListener.onPasswordIsShort(passwordMinLength);  //密码太短
            }
            return;
        }
        if (this.sPoints.size() >= passwordMinLength) {
            int[] indexs = new int[sPoints.size()];
            for (int i = 0; i < sPoints.size(); i++) {
                indexs[i] = sPoints.get(i).index;
            }
            if (mode == LockMode.SETTING_PASSWORD || isEditVerify) {
                invalidSettingPass(Base64.encryptionString(indexs), indexs);
            } else {
                onVerifyPassword(Base64.encryptionString(indexs), indexs);
            }
        }
    }

    /**
     * 按下
     *
     * @param ex
     * @param ey
     */
    private Point actionDown(float ex, float ey) {
        // 如果正在清除密码,则取消
        if (task != null) {
            task.cancel();
            task = null;
        }
        // 删除之前的点
        reset();
        Point p = checkSelectPoint(ex, ey);
        if (p != null) {
            checking = true;
        }
        return p;
    }

    /**
     * 移动
     *
     * @param ex
     * @param ey
     */
    private Point actionMove(float ex, float ey) {
        Point p = null;
        if (checking) {
            p = checkSelectPoint(ex, ey);
            if (p == null) {
                movingNoPoint = true;
                moveingX = ex;
                moveingY = ey;
            }
        }
        return p;
    }


    /**
     * 向选中点集合中添加一个点
     *
     * @param point
     */
    private void addPoint(Point point) {
        this.sPoints.add(point);
    }

    /**
     * 检查点是否被选择
     *
     * @param x
     * @param y
     * @return
     */
    private Point checkSelectPoint(float x, float y) {
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                Point p = mPoints[i][j];
                if (LockUtil.checkInRound(p.x, p.y, mOuterRingRadios, (int) x, (int) y)) {
                    return p;
                }
            }
        }
        return null;
    }

    /**
     * 判断点是否有交叉 返回 0,新点 ,1 与上一点重叠 2,与非最后一点重叠
     *
     * @param p
     * @return
     */
    private int crossPoint(Point p) {
        // 重叠的不最后一个则 reset
        if (sPoints.contains(p)) {
            if (sPoints.size() > 2) {
                // 与非最后一点重叠
                if (sPoints.get(sPoints.size() - 1).index != p.index) {
                    return 2;
                }
            }
            return 1; // 与最后一点重叠
        } else {
            return 0; // 新点
        }
    }

    /**
     * 重置点状态
     */
    public void reset() {
        for (Point p : sPoints) {
            p.state = Point.STATE_NORMAL;
        }
        sPoints.clear();
    }

    /**
     * 清空当前信息
     */
    public void clearCurrent() {
        showTimes = 0;
        errorNumber = 4;
        isCorrect = true;
        reset();
        postInvalidate();
    }

    /**
     * 画两点的连接
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawLine(Canvas canvas, Point a, Point b) {
        mPaint.setColor(mColorOnLine);
        mPaint.setStrokeWidth(3);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }

    /**
     * 错误线
     *
     * @param canvas
     * @param a
     * @param b
     */
    private void drawErrorLine(Canvas canvas, Point a, Point b) {
        mPaint.setColor(mColorErroLine);
        mPaint.setStrokeWidth(3);
        canvas.drawLine(a.x, a.y, b.x, b.y, mPaint);
    }


    /**
     * 设置已经选中的为错误
     */
    private void error() {
        for (Point p : sPoints) {
            p.state = Point.STATE_CHECK_ERROR;
        }
    }

    /**
     * 验证设置密码，滑动两次密码是否相同
     *
     * @param password
     */
    private void invalidSettingPass(String password, int[] indexs) {
        if (showTimes == 0) {
            oldPassword = password;
            if (mCompleteListener != null) {
                mCompleteListener.onAginInputPassword(mode, password, indexs);
            }
            showTimes++;
            reset();
        } else if (showTimes == 1) {
            onVerifyPassword(password, indexs);
        }
    }

    /**
     * 验证本地密码与当前滑动密码是否相同
     *
     * @param indexs
     */
    private void onVerifyPassword(String password, int[] indexs) {
        if (oldPassword != null && oldPassword.length() == password.length()) {
            if (!StringUtils.isEquals(oldPassword, password)) {
                isCorrect = false;
            } else {
                isCorrect = true;
            }
        } else {
            isCorrect = false;
        }
        if (!isCorrect) {
            drawPassWordError();
        } else {
            drawPassWordRight(password, indexs);
        }
    }

    /**
     * 密码输入错误回调
     */
    private void drawPassWordError() {
        if (mCompleteListener == null) {
            return;
        }
        if (mode == LockMode.SETTING_PASSWORD) {
            mCompleteListener.onEnteredPasswordsDiffer();
        } else if (mode == LockMode.EDIT_PASSWORD && isEditVerify) {
            mCompleteListener.onEnteredPasswordsDiffer();
        } else {
            errorNumber--;
            if (errorNumber <= 0) {
                mCompleteListener.onErrorNumberMany();
            } else {
                mCompleteListener.onError(errorNumber + "");
            }
        }
        error();
        postInvalidate();
    }


    /**
     * 输入密码正确相关回调
     *
     * @param indexs
     * @param password
     */
    private void drawPassWordRight(String password, int[] indexs) {
        if (mCompleteListener == null) {
            return;
        }
        if (mode == LockMode.EDIT_PASSWORD && !isEditVerify) {//修改密码，旧密码正确，进行新密码设置
            mCompleteListener.onInputNewPassword();
            isEditVerify = true;
            showTimes = 0;
            return;
        }
        if (mode == LockMode.EDIT_PASSWORD && isEditVerify) {
            savePassWord(password);
        } else if (mode == LockMode.CLEAR_PASSWORD) {//清除密码
            if (isClearPasssword) {
                ConfigUtil.getInstance(getContext()).remove(saveLockKey);
            }
        } else if (mode == LockMode.SETTING_PASSWORD) {//完成密码设置，存储到本地
            savePassWord(password);
        } else {
            isEditVerify = false;
        }
        mCompleteListener.onComplete(password, indexs);
    }

    /**
     * 存储密码到本地
     *
     * @param password
     */
    private void savePassWord(String password) {
        if (isSavePin) {
            ConfigUtil.getInstance(getContext()).putString(saveLockKey, password);
        }
    }


    /**
     * 设置监听
     *
     * @param mCompleteListener
     */
    public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }


    public String getSaveLockKey() {
        return saveLockKey;
    }

    //设置保存密码的key
    public void setSaveLockKey(String saveLockKey) {
        this.saveLockKey = saveLockKey;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    //设置允许最大输入错误次数
    public void setErrorNumber(int errorNumber) {
        this.errorNumber = errorNumber;
    }


    public String getOldPassword() {
        return oldPassword;
    }

    //设置已经设置过的密码，验证密码时需要用到
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public int getPasswordMinLength() {
        return passwordMinLength;
    }

    //设置密码最少输入长度
    public void setPasswordMinLength(int passwordMinLength) {
        this.passwordMinLength = passwordMinLength;
    }

    public LockMode getMode() {
        return mode;
    }

    //设置解锁模式
    public void setMode(LockMode mode) {
        this.mode = mode;
    }

    public boolean isSavePin() {
        return isSavePin;
    }

    //设置密码后是否保存到本地
    public void setSavePin(boolean savePin) {
        isSavePin = savePin;
    }

    public boolean isClearPasssword() {
        return isClearPasssword;
    }

    //是否立即清除密码
    public void setClearPasssword(boolean clearPasssword) {
        isClearPasssword = clearPasssword;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
