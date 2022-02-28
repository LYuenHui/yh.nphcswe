package com.nphcswe.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DateUtil {

	private DateUtil() {
	}

	public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

	public static final String FORMAT_DD_MMM_YY = "dd-MMM-yy";

	public static final List<String> DATE_PATTERNS = Arrays.asList(FORMAT_YYYY_MM_DD, FORMAT_DD_MMM_YY);

	public static boolean isParseable(String date, List<String> datePatterns) {

		if (parseDate(date, datePatterns) != null) {
			return true;
		}

		return false;
	}

	public static Date parseDate(String date, List<String> datePatterns) {

		SimpleDateFormat sf = new SimpleDateFormat();
		sf.setLenient(false);
		for (String pattern : datePatterns) {
			sf.applyPattern(pattern);
			try {
				return sf.parse(date);
			} catch (ParseException e) {
			}
		}

		return null;
	}

}
