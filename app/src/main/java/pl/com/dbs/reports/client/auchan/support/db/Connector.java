/**
 * 
 */
package pl.com.dbs.reports.client.auchan.support.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * TODO
 * 
 * http://127.0.0.1/apex/f?p=4550:1:1494491542417640
 * 
 * http://daust.blogspot.com/2006/01/xe-changing-default-http-port.html
 * 
 * http://infotechinspiration.blogspot.com/2012/07/how-to-unlock-hr-schema-in-oracle.html
 * 
 * mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0 -Dpackaging=jar
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Service
public class Connector {
	private static final Logger logger = Logger.getLogger(Connector.class);
	private static final String SCHEMA = "HR";
	private Connection connection = null;
	
	public Connection connect() throws ClassNotFoundException, SQLException {
		if (connection!=null) return connection;
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
 
		logger.info("Oracle JDBC Driver Registered!");
 
		connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "hr", "hr");
 
		if (connection != null) logger.info("You made it, take control your database now!");
		else logger.error("Failed to make connection!");

		logger.error("Setting current schema:"+SCHEMA);
		connection.createStatement().execute("alter session set current_schema="+SCHEMA);
		
		return connection;	
	}
}
