package kz.ugs.callisto.system.files.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kz.ugs.callisto.system.files.watcher.model.FileModel;
import kz.ugs.callisto.system.files.watcher.service.WebServiceClient;
import kz.ugs.callisto.system.files.watcher.system.Base64Decoder;
import kz.ugs.callisto.system.files.watcher.system.FileLockTest;
import kz.ugs.callisto.system.files.watcher.system.FileManager;
import kz.ugs.callisto.system.files.watcher.system.MyLogger;
import kz.ugs.callisto.system.files.watcher.system.WatcherSystem;

public class DirectoryWatcher {

	public void startWatch(String folder, String extension) {
	
		Path path = Paths.get(folder);
		
		WatchService watchService = null;
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e1) {
			MyLogger.getLogger().error(e1.getMessage(), e1);
		}
		try {
			path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);
			MyLogger.getLogger().info("Watching folder " + folder);
		} catch (IOException e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		}

		WatchKey key;
		try {
			while ((key = watchService.take()) != null) {
				for (WatchEvent<?> event : key.pollEvents()) {
					
					String fullPath = null;
					String fileName = null;
					
					MyLogger.getLogger().info("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
					
					if (event.kind().toString().equals("ENTRY_CREATE")) {
						Path filePath = (Path) event.context();
						fullPath = filePath.toAbsolutePath().toString();
						fileName = filePath.toString();
						//MyLogger.getLogger().info("Full path: " + filePath.toAbsolutePath() + ", " + filePath.toFile().getAbsolutePath());
						
						FileLockTest flt = new FileLockTest();
						
						if (fullPath != null)	{
							if (fullPath.contains(extension))	{
								if (fullPath != null) {
									while (flt.isLocked(fullPath))	{
										MyLogger.getLogger().info(fullPath + " still locked..");
										TimeUnit.SECONDS.sleep(2);
									}	
									MyLogger.getLogger().info(fullPath + " is not locked");
									WatcherSystem wsys = new WatcherSystem();
									if (wsys.sendToPrinter(folder + "/" + fileName))	{
										MyLogger.getLogger().info("File " + fileName + " successfully sent to print application");
										File file = new File(fullPath);
										FileManager.deleteFile(file);
									}
									/*								
									if (sendToService(folder, fileName, fullPath))	{
										MyLogger.getLogger().info("File " + fileName + " successfully sent to web service and deleted");
									}
									*/
								}
							}	else	{
								File file = new File(fullPath);
								FileManager.deleteFile(file);
								
								file = new File(folder + "/" + fileName);
								FileManager.deleteFile(file);
							}	
						}
					}
					
					
				}
				key.reset();
			}
		} catch (InterruptedException e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		}

	}
	
}
