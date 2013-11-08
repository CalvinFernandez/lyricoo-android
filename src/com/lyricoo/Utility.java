package com.lyricoo;


public class Utility {
	
	
	/** Compares two phone numbers and checks if they are equal or not
	 * 
	 * @param number1
	 * @param number2
	 * @return
	 */
	public static boolean isPhoneNumberEqual(String number1, String number2){
		// make sure both number are non null
		if(number1 == null || number2 == null) return false;
		
		// TODO: Take into account hyphens, and possibly area code and country code
		return number1.equals(number2);		
	}
}
