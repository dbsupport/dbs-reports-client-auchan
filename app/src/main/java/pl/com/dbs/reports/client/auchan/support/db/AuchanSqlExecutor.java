/**
 * 
 */
package pl.com.dbs.reports.client.auchan.support.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.support.db.ClientDataSource;
import pl.com.dbs.reports.api.support.db.ClientQueryFilter;
import pl.com.dbs.reports.api.support.db.ClientQueryPager;
import pl.com.dbs.reports.api.support.db.SqlExecutor;
import pl.com.dbs.reports.api.support.db.SqlExecutorContext;

/**
 * Just connect to JDBC and run SQLs.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Service
public class AuchanSqlExecutor<T> implements SqlExecutor<T> {
	private static final Logger logger = Logger.getLogger(AuchanSqlExecutor.class);
	@Autowired @Qualifier(ClientDataSource.DATASOURCE)
	private ClientDataSource datasource;

	@Override
	public List<T> execute(final SqlExecutorContext<T> context) throws DataAccessException {
		Validate.notNull(context, "context is null");
		Validate.notNull(context.getSql(), "sql is null");
		Validate.notNull(context.getMapper(), "mapper is null");
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		
		if (!context.hasPaging()) {
			logger.debug("Querying Auchan(without paging)");
			return jdbcTemplate.query(buildPreparedStatement(context.getSql(), context.getParams()), context.getMapper());
		}
		
		logger.debug("Querying Auchan(with paging):"+context.getSql());
		Integer counter = jdbcTemplate.queryForInt(context.getCounterSql(), context.getParams());
		Validate.notNull(counter, "counter is null");

		final ClientQueryFilter filter = context.getFilter();
		final ClientQueryPager pager = filter.getPager();
		pager.setDataSize(counter);
		
		jdbcTemplate = new JdbcTemplate(datasource);
		return jdbcTemplate.query(context.getSql(), context.getParams(),
			new ResultSetExtractor<List<T>>() {
	            public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
	            	List<T> items = new ArrayList<T>();
	                int currentRow = 1;
	                while (rs.next()) {
	                	if (currentRow < pager.getDataStart() + pager.getPageSize() && currentRow >= pager.getDataStart()) 
	                		items.add(context.getMapper().mapRow(rs, currentRow-1));
                        currentRow++;
	                }
	                return items;
	            }
		});					
	}
	
	public List<Map<String, Object>> execute(final String sql, final Object[] params) throws DataAccessException {
		logger.debug("Querying Auchan(): "+sql);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
		return jdbcTemplate.queryForList(sql, params);
	}
	
	private PreparedStatementCreator buildPreparedStatement(final String sql, final Object[] params) {
		return new PreparedStatementCreator() {
			@Override
		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql);
				if (params != null) {
			        for (int idx=0; idx<params.length; idx++)
			        	ps.setObject(idx+1, params[idx]);
				}
		        return ps;
		    }
		};
	}
	
}
