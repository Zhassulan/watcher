package kz.ugs.callisto.system.files.watcher;

import kz.ugs.callisto.system.files.watcher.system.MyLogger;

public class WhatchApp {
	
	public static void main(String[] args) {
		WatcherService ws = new WatcherService();
		ws.start();
	}
}
