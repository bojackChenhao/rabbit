/**
 * Created on 2017年12月19日 by james
 */
package com.dongnao.james.rabbit.mybatis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Title 
 * @Description 
 * @Copyright <p>Copyright (c) 2017</p>
 * @Company <p>dongnao Co., Ltd.</p>
 * @author james
 * @version 1.0
 * @修改记录
 * @修改序号，修改日期，修改人，修改内容
 */
@ConfigurationProperties(prefix=DataSourceProperties.DS, ignoreUnknownFields = false)
public class DataSourceProperties {

	//对应配置文件里的配置键
		public final static String DS="mysqldb.datasource";	
		private String driverClassName ="com.mysql.jdbc.Driver";
		
		private String url="jdbc:mysql://192.168.62.17:3306/mas-party-1?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
		private String username="root";
		private String password="123456";
		private int minIdle=2;
		private int initialSize;
		private int maxActive=2;
		private int maxWait=6000;
		private boolean removeAbandoned;
		private String validationQuery;
		private int removeAbandonedTimeout;
		private String filters;
		private boolean testOnBorrow;
		private boolean testOnReturn;
		private boolean testWhileIdle;
		private int timeBetweenEvictionRunsMillis;
	    private int minEvictableIdleTimeMillis;
	    private boolean poolPreparedStatements;
	    private int maxOpenPreparedStatements;

		public void setMaxWait(int maxWait) {
			this.maxWait = maxWait;
		}
		public int getMaxWait() {
			return maxWait;
		}
		public void setRemoveAbandoned(boolean removeAbandoned) {
			this.removeAbandoned = removeAbandoned;
		}
		public boolean getRemoveAbandoned() {
			return removeAbandoned;
		}
		public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
			this.removeAbandonedTimeout = removeAbandonedTimeout;
		}
		public int getRemoveAbandonedTimeout() {
			return removeAbandonedTimeout;
		}
		public void setMaxActive(int maxActive) {
			this.maxActive = maxActive;
		}
		
		public int getMaxActive() {
			return maxActive;
		}
		public void setFilters(String filters) {
			this.filters = filters;
		}
		public String getFilters() {
			return filters;
		}
			
		public String getDriverClassName() {
			return driverClassName;
		}

		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public int getMinIdle() {
			return minIdle;
		}

		public void setMinIdle(int minIdle) {
			this.minIdle = minIdle;
		}

		public int getInitialSize() {
			return initialSize;
		}

		public void setInitialSize(int initialSize) {
			this.initialSize = initialSize;
		}

		public String getValidationQuery() {
			return validationQuery;
		}

		public void setValidationQuery(String validationQuery) {
			this.validationQuery = validationQuery;
		}

		public boolean isTestOnBorrow() {
			return testOnBorrow;
		}

		public void setTestOnBorrow(boolean testOnBorrow) {
			this.testOnBorrow = testOnBorrow;
		}

		public boolean isTestOnReturn() {
			return testOnReturn;
		}

		public void setTestOnReturn(boolean testOnReturn) {
			this.testOnReturn = testOnReturn;
		}

		public boolean isTestWhileIdle() {
			return testWhileIdle;
		}

		public void setTestWhileIdle(boolean testWhileIdle) {
			this.testWhileIdle = testWhileIdle;
		}
		public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
			this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
		}
		public int getTimeBetweenEvictionRunsMillis() {
			return timeBetweenEvictionRunsMillis;
		}
	    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
			this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
		}
		public int getMinEvictableIdleTimeMillis() {
			return minEvictableIdleTimeMillis;
		}
		public void setPoolPreparedStatements(boolean poolPreparedStatements) {
			this.poolPreparedStatements = poolPreparedStatements;
		}
		public boolean getPoolPreparedStatements() {
			return poolPreparedStatements;
		}
		public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
			this.maxOpenPreparedStatements = maxOpenPreparedStatements;
		}
		public int getMaxOpenPreparedStatements() {
			return maxOpenPreparedStatements;
		}
}

