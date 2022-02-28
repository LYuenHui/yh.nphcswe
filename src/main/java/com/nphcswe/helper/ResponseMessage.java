package com.nphcswe.helper;

public class ResponseMessage {

	private ResponseMessage() {
	}

	public static final String MSG_SUC_CREATED = "Successfully created";
	public static final String MSG_SUC_NO_CREATE = "Success but no data updated";
	public static final String MSG_SUC_CREATE_UPDATE = "Data created or uploaded";
	public static final String MSG_SUC_UPDATED = "Successfully updated";
	public static final String MSG_SUC_DELETED = "Successfully deleted";

	public static final String MSG_ERR_NO_SUCH_EMPLOYEE = "No such employee";
	// public static final String MSG_ERR_NO_SUCH_EMPLOYEE_BAD_INPUT = "Bad input -
	// no such employee";
	public static final String MSG_ERR_EMPLOYEE_EXIST = "Employee ID already exists";
	public static final String MSG_ERR_NOT_UNIQUE_EMPLOYEE_LOGIN = "Employee login not unique";
	public static final String MSG_ERR_NOT_UNIQUE = "not unique";
	// public static final String MSG_ERR_INVALID = "Invalid salary";
	public static final String MSG_ERR_UNKNOWN = "Unknown Error";
	public static final String MSG_ERR_FILE_SIZE_LIMIT_10MB = "File size exceeed limit of 10MB";

	public static final String MSG_ERR_NOT_UNIQUE_FIELD = "%s exists and not unique";
	public static final String MSG_ERR_NOT_UNIQUE_IDS = "ID [%s] not unique";
	public static final String MSG_ERR_NOT_UNIQUE_LOGINS = "Login [%s] not unique";
	public static final String MSG_ERR_NOT_UNIQUE_ROW_FIELD = "row %s : [%s] exists and not unique";

	public static final String MSG_ERR_MANDATORY_FIELD = "[%s] is mandatory";
	public static final String MSG_ERR_MANDATORY_ROW_FIELD = "row [%s] with empty value [%s]";

	public static final String MSG_ERR_INVALID_FIELD = "Invalid %s";
	// public static final String MSG_ERR_INVALID_DATE_FIELD = "date [%s] is in
	// invalid format. Supported formats: %s";
	public static final String MSG_ERR_INVALID_FORMAT_FIELD = "row %s with invalid format [%s]";
	// public static final String MSG_ERR_INVALID_PARAM_SEARCH = "Invalid parameter
	// [%s]";
	// public static final String MSG_ERR_INVALID_PARAM_FORMAT_FIELD = "parameter
	// [%s] is in invalid format. Supported formats: %s";

	public static final String MSG_ERR_MISSING_REQUEST_BODY = "Required request body is missing";
	public static final String MSG_ERR_UNSUPPORT_CONTENT_TYPE = "Content type not supported";
	public static final String MSG_ERR_UNSUPPORT_REQUEST_METHOD = "Request method not supported";
	public static final String MSG_ERR_CSV_COLUMN_FORMAT = "CSV has more than expected columns of 5";
	public static final String MSG_ERR_FILE_READING = "Error reading file, please try again";
	public static final String MSG_ERR_REQUEST_INVALID = "Request body is invalid";
}