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

    public static final String COOKIE_ASP_SESSION_ID_NAME_PREFIX = "ASPSESSIONID";

    public static final String SHAREPREFERENCES_FILE_NAME = "data";
    public static final String SHAREPREFERENCES_FIELD_LOGIN_EMAIL = "email";
    public static final String SHAREPREFERENCES_FIELD_LOGIN_PASSWORD = "pwd";
    public static final String SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID = "session_id";
    public static final String SHAREPREFERENCES_FIELD_LOGIN_SESSION_ID_FIELD_NAME = "session_id_field_name";
    public static final String SHAREPREFERENCES_FIELD_USER_ID = "user_id";
    public static final String SHAREPREFERENCES_FIELD_CANE_UID = "cane_uid";

    public static final int SHAREPREFERENCES_CHECK_FAIL = -1;
    public static final int SHAREPREFERENCES_WRITE_FAIL = -1;
    public static final int SHAREPREFERENCES_CHECK_OK = 0;
    public static final int SHAREPREFERENCES_WRITE_OK = 0;
}
