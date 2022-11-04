package com.spring.api.util;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class TimeUtil {
	public String getTimestamp() {
		Date date = new Date();
		return (date.getYear()+1900)+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
	}
}