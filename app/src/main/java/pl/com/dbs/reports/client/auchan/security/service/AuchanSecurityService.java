/**
 * 
 */
package pl.com.dbs.reports.client.auchan.security.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.security.SecurityAuthenticatinException;
import pl.com.dbs.reports.api.security.SecurityAuthority;
import pl.com.dbs.reports.api.security.SecurityContext;
import pl.com.dbs.reports.api.security.SecurityService;
import pl.com.dbs.reports.api.security.SecurityUser;
import pl.com.dbs.reports.api.support.db.SqlExecuteException;
import pl.com.dbs.reports.api.support.db.SqlExecutor;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * TODO
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Service
public class AuchanSecurityService implements SecurityService {
	private static final Logger logger = Logger.getLogger(AuchanSecurityService.class);
	@Autowired private SqlExecutor executor;
	
	@Override
	public SecurityUser authenticate(final SecurityContext context) throws SecurityAuthenticatinException {
		final String sql1 = "SELECT cdutil, cdprpo, liutil FROM uc10 WHERE cdutil = '" + context.getLogin() + "' AND cdpass = '" + context.getPassword() + "'";
		try {
			final ResultSet result1 = executor.execute(context.getConnectionContext(), sql1);
			
			if (result1 ==null) return null;
			//if (result.isClosed()) return null;
		    result1.next();
	    	final String pid = StringUtils.trim(result1.getString(1));
	    	final String pname = StringUtils.trim(result1.getString(3));
	    	final String pprofile = StringUtils.trim(result1.getString(2));		
	    	
		    final List<SecurityAuthority> authorities = new ArrayList<SecurityAuthority>();
		    
		    if (!StringUtils.isBlank(pprofile)) {
		    	//..select profiles..
		    	final String sql2 = "SELECT CDPROF, ZOSQLM FROM pf30 WHERE CDPROF = '" + pprofile + "'";
		    	final ResultSet result2 = executor.execute(context.getConnectionContext(), sql2);
		    	while(result2.next()) {
		    		final String aname = StringUtils.trim(result2.getString(1));
		    		final String ameta = StringUtils.trim(result2.getString(2));
		    		if (Iterables.find(authorities, new Predicate<SecurityAuthority>() {
						@Override
						public boolean apply(SecurityAuthority input) {
							return input.getName().equals(aname);
						}
		    		}, null)==null) {
			    		authorities.add(new SecurityAuthority() {
							@Override
							public String getName() {
								return aname;
							}
							@Override
							public String getMetaData() {
								return ameta;
							}
			    		});
		    		}
		    	}
		    }
		    
		    logger.info("Authentication done.");
		    return new SecurityUser() {
				@Override
				public String getId() {
					return pid;
				}

				@Override
				public String getName() {
					return pname;
				}

				@Override
				public String getLogin() {
					return context.getLogin();
				}

				@Override
				public List<SecurityAuthority> getAuthorities() {
					return authorities;
				}
		    };
		    	

		} catch (ClassNotFoundException e) {
			throw new SecurityAuthenticatinException(e);
		} catch (SQLException e) {
			throw new SecurityAuthenticatinException(e);
		} catch (SqlExecuteException e) {
			throw new SecurityAuthenticatinException(e);
		} catch (Exception e) {
			throw new SecurityAuthenticatinException(e);
		}
	}
}
