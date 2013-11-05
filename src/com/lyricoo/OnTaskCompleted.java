package com.lyricoo;

/** Use this interface to add a callback method
 * to async tasks on completion.
 * @author Eli
 *
 */
public interface OnTaskCompleted {
	void onTaskCompleted(Object result);
}
