package kz.ugs.callisto.system.files.watcher.system;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * Класс для получения данных из файла конфигурации приложения Spring application.properties
 * Паттерн синглтон с ленивой инициализацией (т.е. по запросу)  
 * @author Zhassulan Tokbaev
 * @version 1.0
 * @see PasswordValidator
 **/

public class PropsManager {
	
	private static Logger logger = LogManager.getLogger(PropsManager.class);
	
	/** ссылка на будущий экземпляр класса **/
	private static volatile PropsManager _instance = null;
	/** класс для обработки свойств **/
	private Properties appProps;
	
	/** конструктор 
	 * @see PropsManager()
	 * **/
	private PropsManager()	{
		appProps = new Properties();
		try {
			appProps.load(PropsManager.class.getResourceAsStream("/application.properties"));
			}
		catch (FileNotFoundException ex) {
			logger.info(ex.getMessage(), ex);
		}
		catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	/** метод получения ссылки экземпляра класса
	 * @see getInstance()
	 */
	public static synchronized PropsManager getInstance() {
        if (_instance == null)
        	 synchronized (PropsManager.class) {
                 if (_instance == null)
                     _instance = new PropsManager();
             }
        return _instance;
    }
	
	/** метод получения значения по имени свойства в конфигурации приложения
	 * @see getProperty()
	 * @param String param название параметра
	 */
	public String getProperty(String param)	{
		return appProps.getProperty(param);
	}
	
	public void setValueProperty(String key, String value)	{
		appProps.setProperty(key, value);
		try {
			appProps.store(new FileOutputStream("application.properties"), "new value " + value + " for key " + key);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
