package kz.ugs.callisto.system.files.watcher.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

public class Base64Decoder {
	
	public synchronized String encodeFile(File file)	{
		String base64String = "";
		try (FileInputStream imageInFile = new FileInputStream(file)) {
			// Reading a Image file from file system
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);
			base64String = Base64.getEncoder().encodeToString(imageData);
		} catch (FileNotFoundException e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		} catch (IOException ioe) {
			MyLogger.getLogger().error(ioe.getMessage(), ioe);
		}
		return base64String;
	}

}
