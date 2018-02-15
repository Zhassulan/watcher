package kz.ugs.callisto.system.files.watcher.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLockTest {

	public synchronized boolean isLocked(String fileName) {

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(fileName, "rw");
		} catch (FileNotFoundException e1) {
			MyLogger.getLogger().error(e1.getMessage(), e1);
			return true;
		}
		FileChannel fileChannel = raf.getChannel();
		FileLock lock = null;
		try {
			lock = fileChannel.lock(0, 10, true);
		} catch (IOException e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		} finally {
			if (lock != null) {
				try {
					lock.release();
					fileChannel.close();
					raf.close();
					return false;
				} catch (IOException e) {
					MyLogger.getLogger().error(e.getMessage(), e);
				}
			} else
				return true;
		}
		return true;

		/*
		 * 
		 * RandomAccessFile raf = null; try { raf = new RandomAccessFile(fileName,
		 * "rw"); FileChannel fileChannel = raf.getChannel(); try { FileLock lock =
		 * fileChannel.tryLock(); if (lock != null) { lock.close(); return false; } else
		 * return true; } catch (IOException e) {
		 * MyLogger.getLogger().error(e.getMessage(), e); } } catch
		 * (FileNotFoundException e) { MyLogger.getLogger().error(e.getMessage(), e); }
		 * return true;
		 */
	}

}
