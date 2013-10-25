/**
 * 
 */
package pl.com.dbs.reports.client.auchan.report.pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.report.pattern.Pattern;
import pl.com.dbs.reports.api.report.pattern.PatternFactory;
import pl.com.dbs.reports.api.report.pattern.PatternFactoryContext;
import pl.com.dbs.reports.api.report.pattern.PatternValidationException;

/**
 * TODO
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @coptyright (c) 2013
 */
@Service("report.pattern.factory.auchan")
public class PatternFactoryAuchan implements PatternFactory {
	private static final Log logger = LogFactory.getLog(PatternFactoryAuchan.class);

	@Override
	public String getName() {
		return PatternFactoryAuchan.class.getCanonicalName();
	}

	@Override
	public Pattern produce(PatternFactoryContext context) throws PatternValidationException {
		logger.info("DUMMY AUCHAN PATTERN FACTORY");
		return null;
	}

}
