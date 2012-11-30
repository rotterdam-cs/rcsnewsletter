package com.rcs.newsletter.commons;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.DB2400Dialect;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.dialect.resolver.DialectFactory;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
public class CustomizedSessionFactory extends AnnotationSessionFactoryBean {

    private static final Log _log = LogFactoryUtil.getLog(CustomizedSessionFactory.class);

    @Override
    protected void postProcessConfiguration(Configuration config) throws HibernateException {
        Dialect dialect = getDialect(getDataSource());
        Class<?> clazz = dialect.getClass();
        config.setProperty("hibernate.dialect", clazz.getName());
        super.postProcessConfiguration(config);
    }

    public static Dialect getDialect(DataSource dataSource) {
        String dialectKey = null;
        Dialect dialect = null;

        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            String dbName = databaseMetaData.getDatabaseProductName();
            int dbMajorVersion = databaseMetaData.getDatabaseMajorVersion();

            dialectKey = dbName.concat(":").concat(String.valueOf(dbMajorVersion));

            if (_log.isInfoEnabled()) {
                _log.info("Determine dialect for " + dbName + " " + dbMajorVersion);
            }

            if (dbName.equals("ASE") && (dbMajorVersion == 15)) {
                dialect = new SybaseASE15Dialect();
            } else if (dbName.startsWith("DB2") && (dbMajorVersion == 9)) {
                dialect = new DB2Dialect();
            } else if (dbName.startsWith("Microsoft") && (dbMajorVersion == 9)) {
                dialect = new SQLServerDialect();
            } else if (dbName.startsWith("Microsoft") && (dbMajorVersion == 10)) {
                dialect = new SQLServer2008Dialect();
            } else if (dbName.startsWith("Oracle") && (dbMajorVersion >= 10)) {
                dialect = new Oracle10gDialect();
            } else {
                dialect = DialectFactory.buildDialect(new Properties(), connection);
            }
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getLocalizedMessage() : "";

            if (msg.indexOf("explicitly set for database: DB2") != -1) {
                dialect = new DB2400Dialect();
            } else {
                _log.error(e, e);
            }
        } finally {
            cleanUp(connection);
        }

        if (dialect == null) {
            throw new RuntimeException("No dialect found");
        } else if (dialectKey != null) {
            if (_log.isInfoEnabled()) {
                _log.info("Found dialect " + dialect.getClass().getName());
            }
        }

        return dialect;
    }

    private static void cleanUp(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqle) {
            if (_log.isWarnEnabled()) {
                _log.warn(sqle.getMessage());
            }
        }
    }
}
