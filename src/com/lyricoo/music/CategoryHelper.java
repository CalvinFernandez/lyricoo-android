package com.lyricoo.music;

import com.lyricoo.R;

public class CategoryHelper {
	public static Integer mapToImage(Integer mId) {
		switch (mId) {
		case 1:
			return R.drawable.flirty;
		case 2:
			return R.drawable.loveyou;
		case 3:
			return R.drawable.missyou;
		case 4:
			return R.drawable.getiton;
		case 5:
			return R.drawable.outtatown;
		case 6:
			return R.drawable.raunchy;
		case 7:
			return R.drawable.suck;
		case 8:
			return R.drawable.rock;
		case 9:
			return R.drawable.birthday;
		case 10:
			return R.drawable.fuckedup;
		case 11:
			return R.drawable.apology;
		case 12:
			return R.drawable.friday;
		case 13:
			return R.drawable.jock;
		case 14:
			return R.drawable.booze;
		case 15:
			return R.drawable.its420;
		case 16:
			return R.drawable.lastnight;
		case 17:
			return R.drawable.selfie;
		case 18:
			return R.drawable.bro;
		case 19:
			return R.drawable.help;
		case 20:
			return R.drawable.hangin;
		default:
			return R.drawable.missyou;
		}

	}
}
