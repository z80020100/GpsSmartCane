package tw.org.edo.gpssmartcane;

/**
 * Created by JOHN on 2018/4/13.
 */

public class Constant {
    public static final int ACTIVITY_LOGIN = 0;
    public static final int ACTIVITY_SETTING = 1;
    public static final int ACTIVITY_BINDING = 2;
    public static final int ACTIVITY_ADD_CANE = 3;
    public static final int ACTIVITY_REGISTER = 4;

    public static final int RESULT_LOGIN_SUCCESS = 0;
    public static final int RESULT_LOGIN_FAIL = -1;
    public static final int RESULT_REGISTER_FAIL_UNKNOWN = -1;
    public static final int RESULT_REGISTER_FAIL_EXISTS = -2;
    public static final int RESULT_REGISTER_SUCCESS = 1;
    public static final String RESULT_LOGIN_SUCCESS_NO_GPS_SIGNAL = "0000000000000000000000000000";
    public static final int RESULT_SEARCH_FAIL = -1;
    public static final int RESULT_QUERY_STATUS_FAIL_NOT_SUPPORT_MULTI = -3;
    public static final int RESULT_QUERY_STATUS_FAIL_NO_BOUND_CANE = -2;
    public static final int RESULT_QUERY_STATUS_FAIL = -1;
    public static final int RESULT_QUERY_STATUS_SUCCESS = 0;
    public static final int RESULT_BINDING_WAIT_CONFIRM_CODE = 2;
    public static final int RESULT_BINDING_OK = 1;
    public static final int RESULT_BINDING_FAIL = -1;
    public static final int RESULT_BINDING_WAIT_CONFIRM_CODE_ERROR = -3;

    public static final int RESULT_SETTING_SUCCESS = 1;
    public static final int RESULT_SETTING_FAIL = -1;

    public static final int RESULT_CLEAR_EMERGENCY_FAIL = -1;
    public static final int RESULT_CLEAR_EMERGENCY_SUCCESS = 1;

    public static final String RETURN_VALUE_LOGIN = "return_value_login";
    public static final String RETURN_VALUE_REGISTER_EMAIL = "return_value_register_email";
    public static final String RETURN_VALUE_REGISTER_PASSWORD = "return_value_register_password";

    public static final String URL_LOGIN = "http://edo.org.tw/cane/gmap.asp";
    public static final String URL_REGISTER = "http://edo.org.tw/cane/reg_add.asp";
    public static final String URL_SEARCH_HISTORY = "http://edo.org.tw/cane/gmapqry.asp";
    public static final String URL_QUERY_STATUS = "http://edo.org.tw/cane/cane_qry.asp";
    public static final String URL_EDIT_PARAMETERS = "http://edo.org.tw/cane/cane_edit.asp";
    public static final String URL_CLEAR_EMERGENCY = "http://edo.org.tw/cane/cane_emerg_off.asp";
    public static final String URL_BINDING_CANE = "http://edo.org.tw/cane/cane_add.asp";
    public static final String URL_BINDING_CANE_CHECK = "http://edo.org.tw/cane/confirm_chk.asp";

    public static final String NAME_LOGIN_EMAIL = "email";
    public static final String NAME_LOGIN_PASSWORD = "pwd";
    public static final String NAME_REGISTER_EMAIL = "email";
    public static final String NAME_REGISTER_PASSWORD = "pwd";
    public static final String NAME_SEARCH_HISTORY_CANE_UID = "a";
    public static final String NAME_SEARCH_HISTORY_START_RANGE = "b";
    public static final String NAME_SEARCH_HISTORY_END_RANGE = "c";
    public static final String NAME_QUERY_STATUS_USER_ID = "id";

    public static final String NAME_EDIT_PARAMETERS_USER_ID = "id";
    public static final String NAME_EDIT_PARAMETERS_CANE_UID = "uid";
    public static final String NAME_EDIT_PARAMETERS_CANE_NAME = "cane_name";
    public static final String NAME_EDIT_PARAMETERS_LOW_BATTERY_ALERT = "battery_alert";
    public static final String NAME_EDIT_PARAMETERS_SET_FREQ = "set_freq";
    public static final String NAME_EDIT_PARAMETERS_SET_STEP = "set_count";
    public static final String NAME_EDIT_PARAMETERS_EMERGY_CALL = "emergy_call";
    public static final String NAME_EDIT_PARAMETERS_EMERGY_MAIL = "emergy_mail";

    public static final String NAME_BIND_CANE_UID = "uid";
    public static final String NAME_BIND_CANE_EMERGY_CALL = "emerg_call";
    public static final String NAME_BIND_CANE_EMERGY_MAIL = "emerg_mail";

    public static final String NAME_BIND_CANE_CONFIRM_UID = "uid";
    public static final String NAME_BIND_CANE_CONFIRM_USER_ID = "id";
    public static final String NAME_BIND_CANE_CONFIRM_CODE = "rnd_code";

    public static final String NAME_CLEAR_EMERGENCY_UID = "uid";

    public static final String COOKIE_ASP_SESSION_ID_NAME_PREFIX = "ASPSESSIONID";

    public static final String SHAREPREFERENCES_FILE_NAME = "data";
    public static final String SHAREPREFERENCES_FIELD_LOGIN_EMAIL = "email";
    public static final String SHAREPREFERENCES_FIELD_LOGIN_PASSWORD = "pwd";
    public static final String SHAREPREFERENCES_FIELD_USER_ID = "user_id";
    public static final String SHAREPREFERENCES_FIELD_CANE_UID = "cane_uid";
    public static final String SHAREPREFERENCES_FIELD_FREQ_INDEX = "freq_index";
    public static final String SHAREPREFERENCES_FIELD_STEP_INDEX = "step_index";
    public static final String SHAREPREFERENCES_FIELD_LOW_BATTERY_INDEX = "low_battery_index";

    public static final int SHAREPREFERENCES_CHECK_FAIL = -1;
    public static final int SHAREPREFERENCES_WRITE_FAIL = -1;
    public static final int SHAREPREFERENCES_CHECK_OK = 0;
    public static final int SHAREPREFERENCES_WRITE_OK = 0;
}
