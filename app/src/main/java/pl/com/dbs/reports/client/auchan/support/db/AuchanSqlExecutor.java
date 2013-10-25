/**
 * 
 */
package pl.com.dbs.reports.client.auchan.support.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.support.db.SqlExecuteException;
import pl.com.dbs.reports.api.support.db.SqlExecutor;

/**
 * TODO
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Service
public class AuchanSqlExecutor implements SqlExecutor {
	private static final Logger logger = Logger.getLogger(AuchanSqlExecutor.class);
	
	@Autowired private Connector connector;
	
	@Override
	public ResultSet execute(final String sql) throws ClassNotFoundException, SQLException, SqlExecuteException {
		Connection connection = connector.connect();
		
		connection.setAutoCommit(false);
	    Statement stmt = connection.createStatement();
	    ResultSet rset = stmt.executeQuery(sql);

//	    while (rset.next()) {
//	    	logger.info(rset.getString(0));
//	    	results.add(rset.);
//	    }
//	    stmt.close();		
		
		return rset;
	}

}
