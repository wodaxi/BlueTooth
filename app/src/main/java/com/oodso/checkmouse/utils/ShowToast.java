package com.oodso.checkmouse.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by xulei on 2016/6/14.
 *
 * 为了能够在页面onpause 和ondestroy方法中使用Toast.cancel(结束toast)
 */
public class ShowToast {

    Context mContext;
    Toast mToast;

    public ShowToast(Context context) {
        mContext = context;

        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    public void show(int resId, int duration) {
        show(mContext.getText(resId), duration);
    }

    public void show(CharSequence s, int duration) {
        mToast.setDuration(duration);
        mToast.setText(s);
        mToast.show();
    }

    public void cancel() {
        mToast.cancel();
    }
}
