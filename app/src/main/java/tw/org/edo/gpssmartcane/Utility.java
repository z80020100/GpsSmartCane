package tw.org.edo.gpssmartcane;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by JOHN on 2018/4/13.
 */

public class Utility {
    public static void makeTextAndShow(final Context context, final String text, final int duration) {
        Toast toast = android.widget.Toast.makeText(context, text, duration);
        toast.setText(text);
        toast.show();
    }
}
