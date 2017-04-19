package com.galaxyinternet.rili.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.galaxyinternet.framework.core.utils.DateUtil;

public class UtilOper {

	
	
	/**
		1 ：5分钟前         7 ：1天前
		2 ：15分钟前      8 ：2天前
		3 ：30分钟前      9 ：3天前
		4 ：1小时前      10 ：1周前
		5 ：1天前           11 ：不提醒
		6 ：不提醒
	 * 
	 * @param 
	 * 			  startTime: 开始时间
	 *            isAllday：   是否全天 0:否 1:是 
	 *            dictId：         schedule_dict字典表id
	 * @throws ParseException 
	 */
	public static Long getSendTimeBy(String startTime, byte isAllday, Long dictId) throws ParseException {
		Long sendTime = null;

		if (startTime == null || dictId == null)
			return null;

		String format = null;
		if (isAllday == 0) { // 非全天 2017-04-12 17:58:00
			format = "yyyy-MM-dd HH:mm";
			Long slong = DateUtil.stringToLong(startTime, format);

			switch (dictId.intValue()) {
			case 1:
				sendTime = slong - (long) 5 * 60 * 1000;
				break;
			case 2:
				sendTime = slong - (long) 15 * 60 * 1000;
				break;
			case 3:
				sendTime = slong - (long) 30 * 60 * 1000;
				break;
			case 4:
				sendTime = slong - (long) 1 * 60 * 60 * 1000;
				break;
			case 5:
				sendTime = slong - (long) 1 * 24 * 60 * 60 * 1000;
				break;
			case 6:
				break;
			default:
				break;
			}
		} else { // 全天  2017-04-12
			format = "yyyy-MM-dd";
			Date sdate = DateUtil.convertStringToDate(startTime, format);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdate);

			switch (dictId.intValue()) {
			case 7:
				// calendar.roll(Calendar.DATE, -1); 仅 date域发送变化
				calendar.add(Calendar.DATE, -1);
				calendar.set(Calendar.HOUR_OF_DAY, 9);

				sendTime = calendar.getTimeInMillis();
				break;
			case 8:
				calendar.add(Calendar.DATE, -2);
				calendar.set(Calendar.HOUR_OF_DAY, 9);

				sendTime = calendar.getTimeInMillis();
				break;
			case 9:
				calendar.add(Calendar.DATE, -3);
				calendar.set(Calendar.HOUR_OF_DAY, 9);

				sendTime = calendar.getTimeInMillis();
				break;
			case 10:
				calendar.add(Calendar.DATE, -7);
				calendar.set(Calendar.HOUR_OF_DAY, 9);

				sendTime = calendar.getTimeInMillis();
				break;
			case 11:
				break;
			default:
				break;
			}
		}

	/*	String tim = DateUtil.longString(sendTime);
		System.out.println(tim);*/
		return sendTime;
	}
	
	
	
	
	
	
	
	
}
