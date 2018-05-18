package com.llj.framework.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author lu
 *
 */
public class APILogger {
	private static Logger log = LogManager.getLogger("API");

	private static ThreadLocal<APILogRecord> record = new ThreadLocal<APILogRecord>() {
		@Override
		protected APILogRecord initialValue() {
			return new APILogRecord();
		}
	};

	public static APILogRecord getCurrRecord() {
		return record.get();
	}

	public static void doLog() {
		APILogRecord record = getCurrRecord();
		log.info(record.print());
	}

	public static void clearCurrRecord() {
		record.remove();
	}

}
