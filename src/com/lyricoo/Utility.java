package com.lyricoo;

import android.content.Context;
import android.widget.Toast;

public class Utility {

	/**
	 * Compares two phone numbers and checks if they are equal or not
	 * 
	 * @param number1
	 * @param number2
	 * @return
	 */
	public static boolean isPhoneNumberEqual(String number1, String number2) {
		// make sure both number are non null
		if (number1 == null || number2 == null)
			return false;

		// TODO: Take into account hyphens, and possibly area code and country
		// code
		return number1.equals(number2);
	}

	/**
	 * Display a toast with default settings
	 * 
	 * @param context
	 * @param msg
	 */
	public static void makeBasicToast(Context context, String msg) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, msg, duration);
		toast.show();
	}

	/**
	 * Print a message to log
	 * 
	 * @param msg
	 */
	public static void log(String msg) {
		System.out.println(msg);
	}

	/**
	 * Check if a string is null or has only white space
	 * 
	 */
	public static boolean isStringBlank(String str) {
		if (str == null || str.trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Validates an email address by making sure it is non null, not blank, and
	 * matches the proper email format
	 * 
	 * @param target
	 * @return
	 */
	public static boolean isValidEmail(CharSequence target) {
		if (isStringBlank(target.toString())) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

}
