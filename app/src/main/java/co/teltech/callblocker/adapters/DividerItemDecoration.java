package co.teltech.callblocker.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import co.teltech.callblocker.R;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    public static final int TYPE_GREY = 0;

    private Drawable mDivider;
    private float px120;

    public DividerItemDecoration(Context context, int type) {
        px120 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, context.getResources().getDisplayMetrics());
        switch (type) {
            case TYPE_GREY:
                mDivider = ContextCompat.getDrawable(context, R.drawable.divider_gray);
                break;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = (int) px120;
        int right = parent.getWidth();

        int childCount = parent.getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = parent.getChildAt(index);

            if (index < childCount - 1) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}
