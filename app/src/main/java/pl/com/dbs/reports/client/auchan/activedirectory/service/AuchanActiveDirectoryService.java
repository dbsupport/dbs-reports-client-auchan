/**
 * 
 */
package pl.com.dbs.reports.client.auchan.activedirectory.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.activedirectory.ClientActiveDirectoryProfile;
import pl.com.dbs.reports.api.activedirectory.ClientActiveDirectoryProfileFilter;
import pl.com.dbs.reports.api.activedirectory.ClientActiveDirectoryService;
import pl.com.dbs.reports.api.support.db.SqlExecutor;
import pl.com.dbs.reports.api.support.db.SqlExecutorContext;

/**
 * Auchan AD service.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2014
 */
@Service
public class AuchanActiveDirectoryService implements ClientActiveDirectoryService {
	private static final Logger logger = Logger.getLogger(AuchanActiveDirectoryService.class);
	@SuppressWarnings("rawtypes")
	@Autowired private SqlExecutor executor;
	
	/* (non-Javadoc)
	 * @see pl.com.dbs.reports.api.activedirectory.ActiveDirectoryService#find(pl.com.dbs.reports.api.activedirectory.ClientActiveDirectoryProfileFilter)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ClientActiveDirectoryProfile> find(ClientActiveDirectoryProfileFilter filter) throws DataAccessException {
		String sql = "select level from dual connect by level <= 123";
		final List<Object> params = new LinkedList<Object>();
		
		String csql = "select 123 from dual";
		
		logger.debug("Auchan querying for AD profiles..");
		return (List<ClientActiveDirectoryProfile>)executor.execute(new SqlExecutorContext<ClientActiveDirectoryProfile>(sql, params.toArray(), 
			new RowMapper<ClientActiveDirectoryProfile>() {
			@Override
			public ClientActiveDirectoryProfile mapRow(final ResultSet rs, final int rowNum) throws SQLException {
//				final String pid = StringUtils.trim(rs.getString(1));
//				final String pname = StringUtils.trim(rs.getString(3));
//				final String pprofile = StringUtils.trim(rs.getString(2));
				
				return new ClientActiveDirectoryProfile() {
					@Override
					public String getNumber() {
						// TODO Auto-generated method stub
						return String.valueOf(rowNum);
					}

					@Override
					public String getLocationCode() {
						// TODO Auto-generated method stub
						return "CCC";
					}

					@Override
					public String getLocationName() {
						// TODO Auto-generated method stub
						return "CCNCNC";
					}

					@Override
					public String getFirstName() {
						// TODO Auto-generated method stub
						return "firstname";
					}

					@Override
					public String getLastName() {
						// TODO Auto-generated method stub
						return "lastname";
					}

					@Override
					public String getUnitCode() {
						// TODO Auto-generated method stub
						return "UC";
					}

					@Override
					public String getUnitName() {
						// TODO Auto-generated method stub
						return "UN";
					}

					@Override
					public Date getEmploymentDate() {
						// TODO Auto-generated method stub
						return new Date();
					}

					@Override
					public Date getDismissalDate() {
						// TODO Auto-generated method stub
						return new Date();
					}
					
				};
			}
		}).counting(csql, filter));
	}
}
