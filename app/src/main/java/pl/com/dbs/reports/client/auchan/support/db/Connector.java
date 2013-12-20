/**
 * 
 */
package pl.com.dbs.reports.client.auchan.support.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.support.db.ConnectionContext;

/**
 * http://127.0.0.1/apex/f?p=4550:1:1494491542417640
 * http://daust.blogspot.com/2006/01/xe-changing-default-http-port.html
 * http://infotechinspiration.blogspot.com/2012/07/how-to-unlock-hr-schema-in-oracle.html
 * mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0 -Dpackaging=jar
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Service
public class Connector {
	private static final Logger logger = Logger.getLogger(Connector.class);
	
	private Connection connection = null;

	public Connection connect(ConnectionContext context) throws ClassNotFoundException, SQLException {
		if (connection!=null) return connection;
		
		//Class.forName("oracle.jdbc.driver.OracleDriver");
		Class.forName(context.getDriver());
 
		logger.info("Oracle JDBC Driver Registered!");
 
		//DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "hr", "hr");
		connection = DriverManager.getConnection(context.getUrl(), context.getUser(), context.getPassword());
		
		if (connection != null) logger.info("You made it, take in control your database now!");
		else logger.info("Failed to make connection!");

		logger.info("Setting current schema:"+context.getName());
		connection.createStatement().execute("alter session set current_schema="+context.getSchema());
		
		return connection;	
	}
}
