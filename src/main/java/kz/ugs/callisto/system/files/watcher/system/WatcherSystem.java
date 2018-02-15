package kz.ugs.callisto.system.files.watcher.system;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import org.apache.commons.logging.impl.AvalonLogger;

public class WatcherSystem {
	
	public static String getHostName()	{
		InetAddress myHost = null;
		try {
			myHost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		}
        return myHost.getHostName();
	}
	
	public boolean sendToPrinter(String filePath)	{
		ProcessBuilder builder = new ProcessBuilder();
		String splViewer = PropsManager.getInstance().getProperty("VIEWER");
		builder.command(splViewer, "-p", filePath);
		
		Process process = null;
		try {
			MyLogger.getLogger().info("Trying to execute command "  + builder.command());
			process = builder.start();
		} catch (IOException e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		}
		StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
		Executors.newSingleThreadExecutor().submit(streamGobbler);
		int exitCode = -1;
		try {
			exitCode = process.waitFor();
			MyLogger.logger.info("Exit code is " + exitCode);
		} catch (InterruptedException e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		}
		return true;
		/*
		if (exitCode == 0)
			return true;
		else 
			return false;
		*/
	}
}
