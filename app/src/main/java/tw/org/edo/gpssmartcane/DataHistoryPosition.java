package tw.org.edo.gpssmartcane;

/**
 * Created by CLIFF on 2018/4/14.
 */

public class DataHistoryPosition {
    String latitudeDMM;
    String position_N_S;
    String longitudeDMM;
    String position_E_W;
    String time;

    public DataHistoryPosition(String latitude_dmm, String position_n_s, String longitude_dmm, String position_e_w, String time){
        this.latitudeDMM = latitude_dmm;
        this.position_N_S = position_n_s;
        this.longitudeDMM = longitude_dmm;
        this.position_E_W = position_e_w;
        this.time = time;
    }

    public String toString(){
        return "latitudeDMM = " + this.latitudeDMM + ", " +
            "position_N_S = " + this.position_N_S + ", " +
            "longitudeDMM = " + this.longitudeDMM + ", " +
            "position_E_W = " + this.position_E_W +
            "time = " + this.time;
    }
}
