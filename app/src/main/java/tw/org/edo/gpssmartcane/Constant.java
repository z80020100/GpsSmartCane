package tw.org.edo.gpssmartcane;

/**
 * Created by JOHN on 2018/4/13.
 */

public class Constant {
    public static final int ACTIVITY_LOGIN = 0;

    public static final int RESULT_LOGIN_SUCCESS = 0;
    public static final int RESULT_LOGIN_FAIL = -1;
    public static final int RESULT_SEARCH_FAIL = -1;
    public static final String RESULT_LOGIN_SUCCESS_NO_GPS_SIGNAL = "0000000000000000000000000000";

    public static final String RETURN_VALUE_LOGIN = "return_value_login";

    public static final String URL_LOGIN = "http://edo.org.tw/cane/gmap.asp";
    public static final String URL_SEARCH_HISTORY = "http://edo.org.tw/cane/gmapqry.asp";

    public static final String NAME_LOGIN_EMAIL = "email";
    public static final String NAME_LOGIN_PASSWORD = "pwd";
    public static final String NAME_SEARCH_HISTORY_CANE_UID = "a";
    public static final String NAME_SEARCH_HISTORY_START_RANGE = "b";
    public static final String NAME_SEARCH_HISTORY_END_RANGE = "c";
}
