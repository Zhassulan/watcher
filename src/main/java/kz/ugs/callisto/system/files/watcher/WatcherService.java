package kz.ugs.callisto.system.files.watcher;

import kz.ugs.callisto.system.files.watcher.system.MyLogger;
import kz.ugs.callisto.system.files.watcher.system.PropsManager;

public class WatcherService {

	
	public void start()	{
		/*TODO Внимание, чтобы убрать ограничение в POST на Tomcat 8
		 * в catalina.bat
		 * set CATALINA_OPTS=-Xms1024m -Xmx2048m;
		 * в Connector server.xml
		 * <Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443" 
		 * maxPostSize="52428800" maxHttpHeaderSize="8192" disableUploadTimeout="true" enableLookups="false" 
		 * maxThreads="150" minSpareThreads="25" maxSpareThreads="75"/> 
		*/
		
		/* вариант одна папка 
		String folder = PropertiesManager.props.getProperty("WATCHFOLDER");
		String extension = PropertiesManager.props.getProperty("EXTENSION");
		
		DirectoryWatcher dw = new DirectoryWatcher();
		dw.startWatch(folder, extension);
		*/
		
		/*
		 * серверный вариант, много папок в конфиге
		 */
		//String folder = PropertiesManager.props.getProperty("WATCHFOLDER");
		String folder = PropsManager.getInstance().getProperty("WATCHFOLDER");
		DirectoryWatcherRecursive dwr = new DirectoryWatcherRecursive();
		try {
			dwr.startWatch(folder);
		} catch (Exception e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		}
		
	}
	
	
}
