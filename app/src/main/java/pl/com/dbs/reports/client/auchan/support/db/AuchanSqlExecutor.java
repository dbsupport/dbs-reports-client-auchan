/**
 * 
 */
package pl.com.dbs.reports.client.auchan.support.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.support.db.ClientDataSource;
import pl.com.dbs.reports.api.support.db.SqlExecutor;

/**
 * Just connect to JDBC and run SQLs.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Service
public class AuchanSqlExecutor implements SqlExecutor {
	private static final Logger logger = Logger.getLogger(AuchanSqlExecutor.class);
	@Autowired @Qualifier(ClientDataSource.DATASOURCE)
	private ClientDataSource datasource;
	
	@Override
	public List<Map<String, Object>> execute(final String sql) throws DataAccessException {
		logger.debug("Querying Auchan: "+sql);
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		return jdbcTemplate.queryForList(sql);
	}

	@Override
	public List<?> execute(final String sql, final RowMapper<?> mapper) throws DataAccessException {
		logger.debug("Querying Auchan(RowMapper): "+sql);
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		return jdbcTemplate.query(sql, mapper);
	}

	@Override
	public List<?> execute(final String sql, final Object[] params, final RowMapper<?> mapper) throws DataAccessException {
		logger.debug("Querying Auchan(RowMapper, params): "+sql);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		
		//return jdbcTemplate.query(sql, params, mapper);
		return jdbcTemplate.query(
			new PreparedStatementCreator() {
				@Override
			    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql);
			        for (int idx=0; idx<params.length; idx++)
			        	ps.setObject(idx+1, params[idx]);
			        return ps;
			    }
			}, mapper);
	}
}
