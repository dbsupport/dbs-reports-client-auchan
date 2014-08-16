/**
 * 
 */
package pl.com.dbs.reports.client.auchan.support.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import pl.com.dbs.reports.api.support.db.ClientDataSource;
import pl.com.dbs.reports.api.support.db.ConnectionContext;

/**
 * Proxy of CLIENT datasource paramterized by connection context.
 * 
 * http://127.0.0.1/apex/f?p=4550:1:1494491542417640
 * http://daust.blogspot.com/2006/01/xe-changing-default-http-port.html
 * http://infotechinspiration.blogspot.com/2012/07/how-to-unlock-hr-schema-in-oracle.html
 * mvn install:install-file -Dfile=ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0 -Dpackaging=jar
 * 
 * select value from nls_database_parameters where parameter='NLS_CHARACTERSET'
 * 
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Component(ClientDataSource.DATASOURCE)
public class AuchanDataSource implements ClientDataSource {
	private static final Logger logger = Logger.getLogger(AuchanDataSource.class);
	private DataSource datasource;
	
	public AuchanDataSource() {
		logger.info("auchan ds");
	}
	
	/**
	 * Make new target datasource.
	 */
	@Override
	public void reconnect(ConnectionContext context) throws SQLException {
		try {
			if (datasource!=null) datasource.getConnection().close();
		} catch (Exception e) {
			logger.error("Error closing previous Auchan datacource connection!", e);
		}
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(context.getDriver());
		ds.setUrl(context.getUrl());
		ds.setUsername(context.getUser());
		ds.setPassword(context.getPassword());
		ds.setMinEvictableIdleTimeMillis(120000);
		ds.setTestOnBorrow(true);
		ds.setTimeBetweenEvictionRunsMillis(120000);
		ds.setMaxActive(context.getMaxActive());
		
//		if (!StringUtils.isBlank(context.getCharacterEncoding())) {
//			/**
//			 * http://stackoverflow.com/questions/5296257/how-to-convert-characterset-of-column-in-oracle
//			 * http://stackoverflow.com/questions/4401043/oracle-text-will-not-work-with-nvarchar2-what-else-might-be-unavailable
//			 * 
//			 * https://groups.google.com/forum/#!topic/comp.databases.oracle.server/yVfnv_42pCQ
//			 * 
//			 * http://blogs.warwick.ac.uk/kieranshaw/entry/utf-8_internationalisation_with
//			 * 
//			 * https://community.oracle.com/thread/1101841?start=0&tstart=0
//			 */
//			ds.addConnectionProperty("useUnicode", "true");
//			ds.addConnectionProperty("defaultNChar", "true");
//			ds.addConnectionProperty("characterEncoding", context.getCharacterEncoding());
//			//System.setProperty("file.encoding", context.getCharacterEncoding());
//			ds.addConnectionProperty("connection.useUnicode", "true");
//			ds.addConnectionProperty("connection.characterEncoding", context.getCharacterEncoding());
//			ds.addConnectionProperty("connection.charset", context.getCharacterEncoding());
//		}
		
		this.datasource = ds;
		this.datasource.getConnection();
	}
	
	/* (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getLogWriter()
	 */
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return datasource.getLogWriter();
	}

	/* (non-Javadoc)
	 * @see javax.sql.CommonDataSource#getLoginTimeout()
	 */
	@Override
	public int getLoginTimeout() throws SQLException {
		return datasource.getLoginTimeout();
	}

	/* (non-Javadoc)
	 * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
	 */
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		datasource.setLogWriter(out);
	}

	/* (non-Javadoc)
	 * @see javax.sql.CommonDataSource#setLoginTimeout(int)
	 */
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		datasource.setLoginTimeout(seconds);
	}

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return datasource.isWrapperFor(iface);
	}

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return datasource.unwrap(iface);
	}

	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	/* (non-Javadoc)
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return datasource.getConnection(username, password);
	}

}
