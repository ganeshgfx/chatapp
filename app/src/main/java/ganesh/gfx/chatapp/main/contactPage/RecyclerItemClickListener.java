package ganesh.gfx.chatapp.main.contactPage;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener{
    public static interface OnItemClickListener
    {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener)
    {
        mListener = listener;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                // Important: x and y are translated coordinates here
                final ViewGroup childViewGroup = (ViewGroup) recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (childViewGroup != null && mListener != null) {
                    final List<View> viewHierarchy = new ArrayList<View>();
                    // Important: x and y are raw screen coordinates here
                    getViewHierarchyUnderChild(childViewGroup, e.getRawX(), e.getRawY(), viewHierarchy);

                    View touchedView = childViewGroup;
                    if (viewHierarchy.size() > 0) {
                        touchedView = viewHierarchy.get(0);
                    }
                    mListener.onItemClick(touchedView, recyclerView.getChildPosition(childViewGroup));
                    return true;
                }

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if(childView != null && mListener != null)
                {
                    mListener.onItemLongClick(childView, recyclerView.getChildPosition(childView));
                }
            }
        });
    }

    public void getViewHierarchyUnderChild(ViewGroup root, float x, float y, List<View> viewHierarchy) {
        int[] location = new int[2];
        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            final View child = root.getChildAt(i);
            child.getLocationOnScreen(location);
            final int childLeft = location[0], childRight = childLeft + child.getWidth();
            final int childTop = location[1], childBottom = childTop + child.getHeight();

            if (child.isShown() && x >= childLeft && x <= childRight && y >= childTop && y <= childBottom) {
                viewHierarchy.add(0, child);
            }
            if (child instanceof ViewGroup) {
                getViewHierarchyUnderChild((ViewGroup) child, x, y, viewHierarchy);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e)
    {
        mGestureDetector.onTouchEvent(e);

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent){}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
