/**
 * 
 */
package pl.com.dbs.reports.client.auchan.profile.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.profile.ClientProfile;
import pl.com.dbs.reports.api.profile.ClientProfileAuthority;
import pl.com.dbs.reports.api.profile.ClientProfileFilter;
import pl.com.dbs.reports.api.profile.ClientProfileService;
import pl.com.dbs.reports.api.support.db.SqlExecutor;
import pl.com.dbs.reports.api.support.db.SqlExecutorContext;

/**
 * Auchan profiles service.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Service
public class AuchanProfileService implements ClientProfileService {
	private static final Logger logger = Logger.getLogger(AuchanProfileService.class);
	@Autowired private SqlExecutor<ClientProfile> executor1;
	@Autowired private SqlExecutor<ClientProfileAuthority> executor2;
	
	/* (non-Javadoc)
	 * @see pl.com.dbs.reports.api.profile.ClientProfileService#find()
	 */
	@Override
	public List<ClientProfile> find(ClientProfileFilter filter) throws DataAccessException {
		String SQL = null; 
		final List<Object> params = new LinkedList<Object>();
		//..if login/passwd provide - uery for single result..
		if (!StringUtils.isBlank(filter.getLogin())) {
			SQL = "SELECT TRIM(CDUTIL) CDUTIL, TRIM(CDPRPO) CDPRPO, TRIM(LIUTIL) LIUTIL FROM UC10 WHERE UPPER(TRIM(CDUTIL)) = UPPER(?) AND UPPER(TRIM(CDPASS)) = UPPER(?)";
			params.add(filter.getLogin());
			params.add(filter.getPasswd());
		} else if (filter.getUuid()!=null&&filter.getMax()!=null) {
			//..if uuid/max provided - crop results..
			SQL = "SELECT CDUTIL, CDPRPO, LIUTIL FROM (SELECT TRIM(CDUTIL) CDUTIL, TRIM(CDPRPO) CDPRPO, TRIM(LIUTIL) LIUTIL FROM UC10 WHERE UPPER(TRIM(CDUTIL)) > UPPER(?) ORDER BY CDUTIL ASC) WHERE ROWNUM <= ? ORDER BY ROWNUM";
			params.add(filter.getUuid());
			params.add(filter.getMax());
		} else if (filter.getUuid()!=null&&filter.getMax()==null) {
			SQL = "SELECT TRIM(CDUTIL) CDUTIL, TRIM(CDPRPO) CDPRPO, TRIM(LIUTIL) LIUTIL FROM UC10 WHERE UPPER(TRIM(CDUTIL)) > UPPER(?) ORDER BY CDUTIL ASC";
			params.add(filter.getUuid());
		} else if (filter.getUuid()==null&&filter.getMax()!=null) {
			SQL = "SELECT CDUTIL, CDPRPO, LIUTIL FROM (SELECT TRIM(CDUTIL) CDUTIL, TRIM(CDPRPO) CDPRPO, TRIM(LIUTIL) LIUTIL FROM UC10 ORDER BY CDUTIL ASC) WHERE ROWNUM <= ? ORDER BY ROWNUM";
			params.add(filter.getMax());
		} else {
			SQL = "SELECT TRIM(CDUTIL) CDUTIL, TRIM(CDPRPO) CDPRPO, TRIM(LIUTIL) LIUTILL FROM UC10 ORDER BY CDUTIL ASC";
		}
		
		if (StringUtils.isBlank(SQL)) throw new IllegalStateException("Bad parameters!");
		
		return queryProfiles(SQL, params.toArray());
	}	
	
	private List<ClientProfile> queryProfiles(final String sql, final Object[] params) throws DataAccessException {
		logger.debug("Auchan querying for profiles..");
		
		return (List<ClientProfile>)executor1.query(new SqlExecutorContext<ClientProfile>(sql, params, new RowMapper<ClientProfile>() {
			@Override
			public ClientProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
				final String pid = StringUtils.trim(rs.getString(1));
				final String pname = StringUtils.trim(rs.getString(3));
				final ClientProfileNameConverter names = new ClientProfileNameConverter(pname);
				final String pprofile = StringUtils.trim(rs.getString(2));
				
				final List<ClientProfileAuthority> pauthorities = queryAuthorities(pprofile);
				
				return new ClientProfile() {
					@Override
					public String getId() {
						return pid;
					}
					@Override
					public String getFirstName() {
						return names.getFirstName();
					}
					@Override
					public String getLastName() {
						return names.getLastName();
					}					
					@Override
					public String getLogin() {
						return pid;
					}
					@Override
					public String getProfile() {
						return pprofile;
					}						
					@Override
					public List<ClientProfileAuthority> getAuthorities() {
						return pauthorities;
					}
					@Override
					public String getDescription() {
						return pname;
					}
				};
			}
		}));
		
	}
	
	private List<ClientProfileAuthority> queryAuthorities(final String profile) throws DataAccessException {
		if (StringUtils.isBlank(profile)) return new ArrayList<ClientProfileAuthority>();
			final List<Object> params = new LinkedList<Object>();
			params.add(profile);
			
			return (List<ClientProfileAuthority>)executor2.query(new SqlExecutorContext<ClientProfileAuthority>(
					"SELECT CDPROF, ZOSQLM FROM PF30 WHERE CDSTDO = 'ZA' AND TRIM(CDPROC) IS NULL AND TRIM(CDPROS) IS NULL AND TRIM(CDPROF) = ?", 
					params.toArray(), 
					new RowMapper<ClientProfileAuthority>() {
				@Override
				public ClientProfileAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
					final String aname = StringUtils.trim(rs.getString(1));
					final String ameta = StringUtils.trim(rs.getString(2)).replace("'", "''"); 
					return new ClientProfileAuthority() {
						@Override
						public String getName() {
							return aname;
						}
						@Override
						public String getMetaData() {
							return ameta;
						}
		    		};
				}}));
	}
	
	/**
	 * Try to split name...
	 */
	private class ClientProfileNameConverter {
		private static final String UNDEFINED = "brak";
		private String firstname = UNDEFINED;
		private String lastname = UNDEFINED;
		private final java.util.regex.Pattern NAME_PATTERN = java.util.regex.Pattern.compile("^.*;(.*)$",  java.util.regex.Pattern.CASE_INSENSITIVE);
		
		ClientProfileNameConverter(String value) {
			if (StringUtils.isBlank(value)) return;
				
			Matcher m = NAME_PATTERN.matcher(value);
			if (m.find()) {
			   	String names = StringUtils.trim(m.group(1));
			   	
			   	if (!StringUtils.isBlank(names)) {
			   		String[] splitednames = names.split("\\s");
			   		if (splitednames!=null&&splitednames.length>1) {
					   	firstname = StringUtils.trim(splitednames[0]);
					   	lastname = "";
					   	for (int i=1; i<splitednames.length; i++) {
					   		String lname = StringUtils.trim(splitednames[i]);
					   		if (!StringUtils.isBlank(lname)) 
					   			lastname += (i>1)?(" "+lname):lname; 
					   	}
					   	if (StringUtils.isBlank(lastname)) lastname = UNDEFINED;
			   		}
			   	} else {
				   	firstname = UNDEFINED;
				   	lastname = StringUtils.trim(value);
			   	}
			}
		}

		public String getFirstName() {
			return firstname;
		}

		public String getLastName() {
			return lastname;
		}
	}

}
