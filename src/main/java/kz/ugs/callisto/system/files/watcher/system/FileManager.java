package kz.ugs.callisto.system.files.watcher.system;

import java.io.File;

public class FileManager {

	public static boolean deleteFile(String filename) {
		File file = new File(filename);
		try {
			if (file.delete()) {
				MyLogger.getLogger().info(filename + " was successfully deleted");
				return true;
			} else {
				MyLogger.getLogger().error("Error deleting \"" + filename + "\"");
				return false;
			}
		} catch (SecurityException e) {
			MyLogger.getLogger().error(e.getMessage(), e);
			return false;
		}
	}
	
	public static boolean deleteFile(File file) {
		try {
			if (file.delete()) {
				MyLogger.getLogger().info(file.getAbsolutePath() + " was successfully deleted");
				return true;
			} else {
				MyLogger.getLogger().error("Error deleting \"" + file.getAbsolutePath() + "\"");
				return false;
			}
		} catch (SecurityException e) {
			MyLogger.getLogger().error(e.getMessage(), e);
			return false;
		}
	}

}