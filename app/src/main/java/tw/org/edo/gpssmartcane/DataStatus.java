package tw.org.edo.gpssmartcane;

/**
 * Created by CLIFF on 2018/4/24.
 */

public class DataStatus {
    public String uid;
    public String caneName;
    public int batteryCapacity;
    public int batteryAlertThreshold;
    public boolean caneFall;
    public boolean emergencyNotice;
    public boolean lightStatus;
    public String sendFreq;
    public String sendStep;
    public String stepCount;
    public String emergCallNumber;
    public String emergMail;
    public boolean confirm;


    public DataStatus(){

    }

    public void setData(String uid, String caneName, int batteryCapacity, int batteryAlertThreshold,
                      boolean caneFall, boolean emergencyNotice, boolean lightStatus,
                      String sendFreq, String sendStep, String stepCount,
                      String emergCallNumber, String emergMail, boolean confirm){
        this.uid = uid;
        this.caneName = caneName;
        this.batteryCapacity = batteryCapacity;
        this.batteryAlertThreshold = batteryAlertThreshold;
        this.caneFall = caneFall;
        this.emergencyNotice = emergencyNotice;
        this.lightStatus = lightStatus;
        this.sendFreq = sendFreq;
        this.sendStep = sendStep;
        this.stepCount = stepCount;
        this.emergCallNumber = emergCallNumber;
        this.emergMail = emergMail;
        this.confirm = confirm;
    }

    public String toString(){
        return String.format("uid = %s\r\n" +
                "caneName = %s\r\n" +
                "batteryCapacity = %d\r\n" +
                "batteryAlertThreshold = %d\r\n" +
                "caneFall = %b\r\n" +
                "emergencyNotice = %b\r\n" +
                "lightStatus = %b\r\n" +
                "sendFreq = %s\r\n" +
                "sendStep = %s\r\n" +
                "stepCount = %s\r\n" +
                "emergCallNumber = %s\r\n" +
                "emergMail = %s\r\n" +
                "confirm = %b", uid, caneName, batteryCapacity, batteryAlertThreshold,
                caneFall, emergencyNotice, lightStatus,
                sendFreq, sendStep, stepCount,
                emergCallNumber, emergMail, confirm);
    }

    public void syncToServer(){

    }
}
