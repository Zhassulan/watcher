package kz.ugs.callisto.system.files.watcher.system;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MyLogger {
	
	public final static Logger logger = LogManager.getLogger(MyLogger.class);

	public static Logger getLogger() {
		return logger;
	}

}
