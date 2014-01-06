/**
 * 
 */
package pl.com.dbs.reports.client.auchan.security.service;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.profile.ClientProfile;
import pl.com.dbs.reports.api.profile.ClientProfileFilter;
import pl.com.dbs.reports.api.security.SecurityAuthenticatinException;
import pl.com.dbs.reports.api.security.SecurityContext;
import pl.com.dbs.reports.api.security.SecurityService;
import pl.com.dbs.reports.client.auchan.profile.service.AuchanProfileService;

/**
 * Authenticates user in CLIENT database.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Service
public class AuchanSecurityService implements SecurityService {
	private static final Logger logger = Logger.getLogger(AuchanSecurityService.class);
	@Autowired private AuchanProfileService profileService;
	
	
	@Override
	public ClientProfile authenticate(final SecurityContext context) throws SecurityAuthenticatinException, DataAccessException {
		logger.info("Auchan authentication of "+context.getLogin());
		Validate.notNull(context, "Context cant be null!");
		Validate.notEmpty(context.getLogin(), "Login cant be empty!");
		Validate.notEmpty(context.getPassword(), "Password cant be empty!");
		
		ClientProfileFilter filter = new ClientProfileFilter() {
			@Override
			public String getLogin() {
				return context.getLogin();
			}
			@Override
			public String getPasswd() {
				return context.getPassword();
			}
			@Override
			public String getUuid() {
				return null;
			}
			@Override
			public Integer getMax() {
				return 1;
			}
		};
		
		//..query..
		List<ClientProfile> profiles = profileService.find(filter);
		//..validate..
		if (profiles==null||profiles.isEmpty()) {
			logger.error("User "+context.getLogin()+" NOT found!");
			throw new SecurityAuthenticatinException("User not found!");
		}
		if (profiles.size()>1) {
			logger.error("Too many ("+profiles.size()+") users "+context.getLogin()+" found!");
			throw new SecurityAuthenticatinException("Too many ("+profiles.size()+") users found!");
		}
		
		return profiles.get(0);
	}
}
