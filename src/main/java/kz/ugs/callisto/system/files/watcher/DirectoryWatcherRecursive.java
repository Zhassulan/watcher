package kz.ugs.callisto.system.files.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kz.ugs.callisto.system.files.watcher.model.FileModel;
import kz.ugs.callisto.system.files.watcher.service.WebServiceClient;
import kz.ugs.callisto.system.files.watcher.system.Base64Decoder;
import kz.ugs.callisto.system.files.watcher.system.FileLockTest;
import kz.ugs.callisto.system.files.watcher.system.FileManager;
import kz.ugs.callisto.system.files.watcher.system.MyLogger;
import kz.ugs.callisto.system.files.watcher.system.WatcherSystem;
import kz.ugs.callisto.system.files.watcher.system.PropsManager;

public class DirectoryWatcherRecursive {

	private static Map<WatchKey, Path> keyPathMap = new HashMap<>();

	public void startWatch(String folder) throws Exception {
		try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
			registerDir(Paths.get(folder), watchService);
			startListening(watchService);
		} catch (Exception e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		}
	}

	private static void registerDir(Path path, WatchService watchService) throws IOException {
		if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			return;
		}
		MyLogger.getLogger().info("registering: " + path);
		
		WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
		keyPathMap.put(key, path);
		
		for (File f : path.toFile().listFiles()) {
			registerDir(f.toPath(), watchService);
		}
	}

	private static void startListening(WatchService watchService) throws Exception {
		while (true) {
			WatchKey queuedKey = watchService.take();
			for (WatchEvent<?> watchEvent : queuedKey.pollEvents()) {
				System.out.printf("Event... kind=%s, count=%d, context=%s Context type=%s%n", watchEvent.kind(), 
					watchEvent.count(), watchEvent.context(), ((Path) watchEvent.context()).getClass());

				// do something useful here

				String fullPath = null;
				
				if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
					// this is not a complete path
					Path path = (Path) watchEvent.context();
					String fileName = path.toString();
					MyLogger.logger.info("Path: " + path.toString());
					// need to get parent path
					Path parentPath = keyPathMap.get(queuedKey);
					//MyLogger.logger.info("Parent path: " + parentPath.toString());
					//MyLogger.logger.info("Subpath: " + parentPath.subpath(3, 4));
					String hostDir =  parentPath.subpath(4, 5).toString();
					// get complete path
					path = parentPath.resolve(path);
					fullPath = path.toString();
					//MyLogger.getLogger().info(path);
					
					registerDir(path, watchService);
					
					FileLockTest flt = new FileLockTest();
					if (fullPath != null) {
						while (flt.isLocked(fullPath))	{
							MyLogger.getLogger().info(fullPath + " still locked..");
							TimeUnit.SECONDS.sleep(2);
						}	
						MyLogger.getLogger().info(fullPath + " is not locked");
						
						//copying for new file name
						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
						LocalDateTime now = LocalDateTime.now();
						String tmpDir = PropsManager.getInstance().getProperty("TMPFOLDER");
						//String destPath = tmpDir + fromHost + "/" + dtf.format(now) + "/";
						
						Path targetPath = Paths.get(tmpDir + "/" + hostDir + "_" + fileName);
						try {
							Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException ex) {
							MyLogger.logger.error(ex.getMessage(), ex);
						}
						
						// send to printer
						fullPath = targetPath.toString();
						WatcherSystem wsys = new WatcherSystem();
						if (wsys.sendToPrinter(fullPath))	{
							MyLogger.getLogger().info("File " + fullPath + " successfully sent to print application");
							File file = new File(fullPath);
							FileManager.deleteFile(file);
							file = new File(path.toString());
							FileManager.deleteFile(file);
						}
		
						/*
						 * sent to web service
						WebServiceClient wsc = new WebServiceClient();
						
						String hostName = parentPath.toString();
						String fileName = watchEvent.context().toString();
						Base64Decoder base64Decoder = new Base64Decoder();
						File file = new File(fullPath);
						String data =  base64Decoder.encodeFile(file);
						String url = PropertiesManager.props.getProperty("URL");
						String params =  "?hostName=" + hostName + "&fileName=" + fileName + "&data=" + data;
						if (wsc.createPostRequestParams(url, hostName, fileName, data))	{
							MyLogger.getLogger().info("File " + fileName + " successfully sent to web service");
							MyLogger.getLogger().info("Status is ok, gonna delete file " + fullPath);
							FileManager.deleteFile(file);
						}
						*/
					}
				}
				
				
				
			}
			if (!queuedKey.reset()) {
				keyPathMap.remove(queuedKey);
			}
			if (keyPathMap.isEmpty()) {
				break;
			}
		}
	}
	
}
