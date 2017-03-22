package ca.nismit.util.weather.marker;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import ca.nismit.util.weather.R;

public class CustomMarkerView extends com.github.mikephil.charting.components.MarkerView {

    private TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (highlight.getAxis().toString().equals("LEFT")) {
            tvContent.setText("" + e.getY() + "°");
        } else if(highlight.getAxis().toString().equals("RIGHT")) {
            tvContent.setText("" + e.getY() + "mm");
        } else {

        }

        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {
        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / (float) 2.3), -( getHeight() + 6));
        }

        return mOffset;
    }
}
