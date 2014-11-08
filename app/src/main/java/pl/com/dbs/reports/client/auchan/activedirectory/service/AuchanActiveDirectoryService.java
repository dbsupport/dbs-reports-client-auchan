/**
 * 
 */
package pl.com.dbs.reports.client.auchan.activedirectory.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import pl.com.dbs.reports.api.activedirectory.ClientActiveDirectoryProfile;
import pl.com.dbs.reports.api.activedirectory.ClientActiveDirectoryProfileFilter;
import pl.com.dbs.reports.api.activedirectory.ClientActiveDirectoryProfileUpdateContext;
import pl.com.dbs.reports.api.activedirectory.ClientActiveDirectoryService;
import pl.com.dbs.reports.api.support.db.ClientSorterField;
import pl.com.dbs.reports.api.support.db.SqlExecutor;
import pl.com.dbs.reports.api.support.db.SqlExecutorContext;
import pl.com.dbs.reports.client.auchan.support.tech.Separator;

/**
 * Auchan AD service.
 *
 * @author Krzysztof Kaziura | krzysztof.kaziura@gmail.com | http://www.lazydevelopers.pl
 * @copyright (c) 2014
 */
@Service
public class AuchanActiveDirectoryService implements ClientActiveDirectoryService {
	private static final Logger logger = LoggerFactory.getLogger(AuchanActiveDirectoryService.class);
	@Autowired private SqlExecutor<ClientActiveDirectoryProfile> executor1;
	@Autowired private SqlExecutor<List<Map<String, String>>> executor2;
	
	private static final String TABLE = "TOMEK.REAL_CURRENTLY_EMPLOYED";
	private static final String EMP_NO = "EMP_NO";
	private static final String LOCATION_CODE = "LOCATION_CODE";
	private static final String LOCATION = "LOCATION";
	private static final String FIRST_NAME = "FIRST_NAME";
	private static final String LAST_NAME = "LAST_NAME";
	private static final String UNIT_CODE = "UNIT_CODE";
	private static final String UNIT = "UNIT";
	private static final String DATE_OF_EMPLOYMENT = "DATE_OF_EMPLOYMENT";
	private static final String DATE_OF_DISMISSAL = "DATE_OF_DISMISSAL";
	private static final String DATE_FORMAT1 = "yyyy-MM-dd";
	
	
	/* (non-Javadoc)
	 * @see pl.com.dbs.reports.api.activedirectory.ActiveDirectoryService#find(pl.com.dbs.reports.api.activedirectory.ClientActiveDirectoryProfileFilter)
	 */
	@Override
	public List<ClientActiveDirectoryProfile> find(ClientActiveDirectoryProfileFilter filter) throws DataAccessException {
		StringBuffer sql = resolveSQL();
		StringBuffer csql = resolveCounterSQL();
		
		StringBuffer where = resolveWhere(filter);
		sql.append(where);
		csql.append(where);
		
		StringBuffer order = resolveOrder(filter);
		sql.append(order);
		
		logger.debug("sql:" +sql.toString());
		logger.debug("csql:" +csql.toString());
		
		final List<Object> params = new LinkedList<Object>();
		
		logger.debug("Auchan querying for AD profiles..");
		return (List<ClientActiveDirectoryProfile>)executor1.query(new SqlExecutorContext<ClientActiveDirectoryProfile>(sql.toString(), params.toArray(), 
			new RowMapper<ClientActiveDirectoryProfile>() {
			@Override
			public ClientActiveDirectoryProfile mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				final String number = StringUtils.trim(rs.getString(1));
				final String locationCode = StringUtils.trim(rs.getString(2));
				final String location = StringUtils.trim(rs.getString(3));
				final String fname = StringUtils.trim(rs.getString(4));
				final String lname = StringUtils.trim(rs.getString(5));
				final String unitCode = StringUtils.trim(rs.getString(6));
				final String unit = StringUtils.trim(rs.getString(7));
				final Date edate = resolveDate(rs.getString(8));
				final Date ddate = resolveDate(rs.getString(9));
				
				return new ClientActiveDirectoryProfile() {
					@Override
					public String getNumber() {
						return number;
					}

					@Override
					public String getLocationCode() {
						return locationCode;
					}

					@Override
					public String getLocationName() {
						return location;
					}

					@Override
					public String getFirstName() {
						return fname;
					}

					@Override
					public String getLastName() {
						return lname;
					}

					@Override
					public String getUnitCode() {
						return unitCode;
					}

					@Override
					public String getUnitName() {
						return unit;
					}

					@Override
					public Date getEmploymentDate() {
						return edate;
					}

					@Override
					public Date getDismissalDate() {
						return ddate;
					}
				};
		}}).counting(csql.toString(), filter));
	}
	
	@Override
	public void update(ClientActiveDirectoryProfileUpdateContext context) throws DataAccessException {
		DateTime dateTime = new DateTime(context.getDismissalDate());
		StringBuffer sql = new StringBuffer("UPDATE ")
			.append(TABLE)
			.append(" SET ")
			.append(DATE_OF_DISMISSAL)
			.append("='")
			.append(dateTime.toString(DATE_FORMAT1))
			.append("' WHERE ")
			.append(EMP_NO)
			.append(" IN (")
			.append(resolveProfileNumbers(context.getProfilesNumbers()))
			.append(")");
		
		logger.debug("Auchan update dismissal date:"+sql.toString());
		executor2.update(sql.toString(), new LinkedList<Object>().toArray()); 
	}
	
	private StringBuffer resolveProfileNumbers(final List<String> numbers) {
		Separator sep = new Separator(", ");
		StringBuffer sb = new StringBuffer();
		for (String no : numbers)
			sb.append(sep).append("'").append(no).append("'");
		return sb;
	}
	

	private Date resolveDate(final String value) {
		try {
			return DateTimeFormat.forPattern(DATE_FORMAT1).parseDateTime(StringUtils.trim(value)).toDate();
		} catch (Exception e) {}
		return null;
	}
	
	private StringBuffer resolveCounterSQL() {
		return new StringBuffer("SELECT COUNT(*) FROM ").append(TABLE);
	}

	private StringBuffer resolveSQL() {
		Separator s = new Separator(", ");
		StringBuffer sb = new StringBuffer("SELECT ").
		append(s).append(EMP_NO).
		append(s).append(LOCATION_CODE).
		append(s).append(LOCATION).
		append(s).append(FIRST_NAME).
		append(s).append(LAST_NAME).
		append(s).append(UNIT_CODE).
		append(s).append(UNIT).
		append(s).append("to_char("+DATE_OF_EMPLOYMENT+", 'yyyy-mm-dd')").
		append(s).append("to_char("+DATE_OF_DISMISSAL+", 'yyyy-mm-dd')").
		append(" FROM ").append(TABLE);
		return sb;
	}

	private StringBuffer resolveOrder(ClientActiveDirectoryProfileFilter filter) {
		if (filter.getSorter().isOn()) {
			StringBuffer sb = new StringBuffer();
			Separator s = new Separator(", ");
			for (ClientSorterField field : filter.getSorter().getFields()) {
				String column = resolveColumn(field);
				if (!StringUtils.isBlank(column)) {
					sb.append(s).append(column);
					sb.append(field.isAsc()?" ASC":" DESC");
				}
			}
			if (!StringUtils.isBlank(sb.toString())) {
				sb.insert(0, " ORDER BY ");
			}
			return sb;
		}
		return new StringBuffer();
	}

	private StringBuffer resolveWhere(ClientActiveDirectoryProfileFilter filter) {
		if (!StringUtils.isBlank(filter.getValue())) {
			final String value = filter.getValue().trim().toLowerCase();
			Separator s = new Separator(" OR ");
			StringBuffer sb = new StringBuffer(" WHERE ").
			append(s).append("lower(").append(EMP_NO).append(") LIKE '%"+value+"%'").
			append(s).append("lower(").append(LOCATION_CODE).append(") LIKE '%"+value+"%'").
			append(s).append("lower(").append(LOCATION).append(") LIKE '%"+value+"%'").
			append(s).append("lower(").append(FIRST_NAME).append(") LIKE '%"+value+"%'").
			append(s).append("lower(").append(LAST_NAME).append(") LIKE '%"+value+"%'").
			append(s).append("lower(").append(UNIT_CODE).append(") LIKE '%"+value+"%'").
			append(s).append("lower(").append(UNIT).append(") LIKE '%"+value+"%'").
			append(s).append("lower(").append(DATE_OF_EMPLOYMENT).append(") LIKE '%"+value+"%'").
			append(s).append("lower(").append(DATE_OF_DISMISSAL).append(") LIKE '%"+value+"%'");
			
			return sb;
		}
		return new StringBuffer();
	}


	
	/**
	 * Probuje przetlumaczyc pola sortowania na nazwy kolumn.
	 * Jesli nie umie przetlumaczyc zwraca null.
	 */
	private String resolveColumn(ClientSorterField field) {
		final String name = field.getName().trim().toLowerCase();
		if (name.contains(EMP_NO)||name.contains("number"))
			return EMP_NO;
		
		if (name.contains(LOCATION_CODE)||name.contains("locationcode"))
			return LOCATION_CODE;
		
		if (name.contains(LOCATION)||name.contains("location"))
			return LOCATION;
		
		if (name.contains(FIRST_NAME)||name.contains("firstname"))
			return FIRST_NAME;	
		
		if (name.contains(LAST_NAME)||name.contains("lastname"))
			return LAST_NAME;	
		
		if (name.contains(UNIT_CODE)||name.contains("unitcode"))
			return UNIT_CODE;	
		
		if (name.contains(UNIT)||name.contains("unitname"))
			return UNIT;			

		if (name.contains(DATE_OF_EMPLOYMENT)||name.contains("employmentdate"))
			return DATE_OF_EMPLOYMENT;		

		if (name.contains(DATE_OF_DISMISSAL)||name.contains("dismissaldate"))
			return DATE_OF_DISMISSAL;
		
		return null;
	}
}
