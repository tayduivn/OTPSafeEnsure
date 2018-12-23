package com.itrus.ikey.safecenter.TOPMFA.widget.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itrus.ikey.safecenter.TOPMFA.R;


public class ImageNumber extends LinearLayout {
    private Context context;
    private ImageView topView, bottomView;

    public ImageNumber(Context context) {
        super(context);
        init(context);
    }

    public ImageNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageNumber(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        View layout = mInflater.inflate(R.layout.img_number, this);
        topView = (ImageView) layout.findViewById(R.id.img_top);
        bottomView = (ImageView) layout.findViewById(R.id.img_bottom);
        setNumber(0);
    }

    public void setChangeNumber(int x, int y) {
        setAnime(AnimUtils.getTopDrawable(context, x, y), AnimUtils.getBottomDrawable(context, x, y));
    }


    public void setAnime(AnimationDrawable top, AnimationDrawable bottom) {
        topView.setBackgroundDrawable(top);
        bottomView.setBackgroundDrawable(bottom);

        topView.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation =
                        (AnimationDrawable) topView.getBackground();
                frameAnimation.start();
            }
        });

        bottomView.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable frameAnimation2 =
                        (AnimationDrawable) bottomView.getBackground();
                frameAnimation2.start();
            }
        });

    }


    public void setNumber(int x) {
        topView.setBackgroundResource(AnimUtils.getTopDrawable(context, x));
        bottomView.setBackgroundResource(AnimUtils.getBottomDrawable(context, x));
    }

}
