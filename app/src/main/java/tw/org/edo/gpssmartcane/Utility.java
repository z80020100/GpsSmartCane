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

    public static double latitudeDMMtoDD(String latitude_dmm, String position_n_s){
        double latitude_dd = Double.parseDouble(latitude_dmm.substring(0, 2));
        double m = Double.parseDouble(latitude_dmm.substring(2));
        latitude_dd += m/60d;
        if(position_n_s.equals("S")){
            latitude_dd *= -1;
        }
        return latitude_dd;
    }

    public static double longitudeDMMtoDD(String longitude_dmm, String position_e_w){
        double longitude_dd = Double.parseDouble(longitude_dmm.substring(0, 3));
        double m = Double.parseDouble(longitude_dmm.substring(3));
        longitude_dd += m/60d;
        if(position_e_w.equals("W")){
            longitude_dd *= -1;
        }
        return longitude_dd;
    }
}
