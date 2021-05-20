/**
 * Created on 2016年6月21日 by james
 */
package com.dongnao.james.rabbit.mybatis;

import java.sql.SQLException;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.miemiedev.mybatis.paginator.OffsetLimitInterceptor;

/**
 * @Title 
 * @Description 
 * @Copyright <p>Copyright (c) 2017</p>
 * @author james
 * @version 1.0
 * @修改记录
 * @修改序号，修改日期，修改人，修改内容
 */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
//mybaits dao 搜索路径
@MapperScan("com.dongnao.james.rabbit.dao")
public class MybatisDataSource {

	@Autowired
	private DataSourceProperties dataSourceProperties;
	//mybaits mapper xml搜索路径
	private final static String mapperLocations="classpath:mapper/*.xml"; 

	private DruidDataSource pool;
	
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {		
		DataSourceProperties config = dataSourceProperties;		
		this.pool = new DruidDataSource();		
		this.pool.setDriverClassName(config.getDriverClassName());
		this.pool.setUrl(config.getUrl());
		if (config.getUsername() != null) {
			this.pool.setUsername(config.getUsername());
		}
		if (config.getPassword() != null) {
			this.pool.setPassword(config.getPassword());
		}
		this.pool.setInitialSize(config.getInitialSize());
		this.pool.setMinIdle(config.getMinIdle());
		this.pool.setTestOnBorrow(config.isTestOnBorrow());
		this.pool.setTestOnReturn(config.isTestOnReturn());
		this.pool.setValidationQuery(config.getValidationQuery());
		this.pool.setMaxWait(config.getMaxWait());
		this.pool.setMaxActive(config.getMaxActive());
		//this.pool.setMaxIdle(config.getMaxIdle());
		this.pool.setRemoveAbandoned(config.getRemoveAbandoned());
		this.pool.setRemoveAbandonedTimeout(config.getRemoveAbandonedTimeout());
		try {
			this.pool.setFilters(config.getFilters());
			this.pool.init();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return this.pool;
	}

	@PreDestroy
	public void close() {
		if (this.pool != null) {
			this.pool.close();
		}
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {		
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());		
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mapperLocations));
		
		Interceptor[] plugins = new Interceptor[1];
		plugins[0] = new OffsetLimitInterceptor();
		((OffsetLimitInterceptor)plugins[0]).setDialectClass("com.github.miemiedev.mybatis.paginator.dialect.MySQLDialect");
		sqlSessionFactoryBean.setPlugins(plugins);
		
		 return sqlSessionFactoryBean.getObject();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}
}

