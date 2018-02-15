package kz.ugs.callisto.system.files.watcher.model;

import java.io.File;
import java.util.Map;

//import javax.xml.bind.annotation.*;

//@XmlRootElement
public class FileModel {

	private String hostName;
	private String fileName;
	private File bFile;
	private String fullPath;

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getbFile() {
		return bFile;
	}

	public void setbFile(File bFile) {
		this.bFile = bFile;
	}

}
