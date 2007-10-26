package com.versionone.common.sdk;

/**
 * Helper class to convert between the many ways we need to use Status Codes
 * @author Jerry D. Odenwelder Jr.
 *
 */
public interface IStatusCodes {
	
	/**
	 * Return a list of values to display
	 */
	String[] getDisplayValues();
	
	/**
	 * Given a name return it's index
	 * Returns 0 if the value is invalid
	 */
	int getIndex(String value);
	
	/**
	 * Given an index, return the value
	 */
	String getDisplayValue(int index);
	
}
