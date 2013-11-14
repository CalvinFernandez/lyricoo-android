package com.lyricoo;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
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

		// make sure numbers are in the same format
		number1 = formatPhoneNumberForServer(number1);
		number2 = formatPhoneNumberForServer(number2);

		// remove the country code if there is one. We probably won't have a
		// country code for user entered numbers, but numbers from the contact
		// list probably have them. Assume all numbers are US for now and just
		// take the last 10 digits
		if (number1.length() > 10) {
			number1 = number1.substring(number1.length() - 10);
		}

		if (number2.length() > 10) {
			number2 = number2.substring(number2.length() - 10);
		}

		// the strings should now be in the same format so we can do a simple
		// string compare
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
	 * Print a message to log. Shortcut for calling to System.out.println()
	 * 
	 * @param msg
	 *            The message to log
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

	/**
	 * Prepare a phone number to be stored on the server as an integer
	 * 
	 * @param number
	 *            The phone number to format
	 * @return A string with only numeric characters ready to stored as an
	 *         integer
	 */
	public static String formatPhoneNumberForServer(String number) {
		// start with number in standard format
		String formatted = PhoneNumberUtils.formatNumber(number);
		// remove all non numeric characters
		String result = formatted.replaceAll("[^0-9]", "");

		return result;
	}

}
