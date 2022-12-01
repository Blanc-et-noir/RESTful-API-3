package com.spring.api.configuration;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.spring.api.entity.UserEntity;
import com.spring.api.rowMapper.UserEntityRowMapper;

@Configuration
public class BatchConfiguration {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private JobRepository jobRepository;
	
	@Bean("asyncJobLauncher")
	public JobLauncher asyncJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        
		jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        
        return jobLauncher;
	}
	
	@Bean
	public Job deleteJob() {
		return this.jobBuilderFactory
				.get("deleteJob")
				.start(deleteUserStep())
				.build();
	}
	
	@Bean
	@JobScope
	public Step deleteUserStep() {
		return this.stepBuilderFactory
				.get("deleteUserStep")
				.<UserEntity, UserEntity>chunk(10)
				.reader(userEntityReader(this.dataSource))
				.writer(userEntityWriter(this.dataSource))
				.build();
	}
	
	@Bean
	@StepScope
	public JdbcPagingItemReader<UserEntity> userEntityReader(DataSource dataSource){
		try {
			SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
			
			factoryBean.setDataSource(dataSource);
			factoryBean.setSelectClause("SELECT *");
			factoryBean.setFromClause("FROM users NATURAL JOIN questions NATURAL JOIN user_times");
			factoryBean.setWhereClause("WHERE user_status = 'Y' AND TIMESTAMPDIFF(DAY, user_withdraw_time, NOW()) >= 30");
			factoryBean.setSortKey("user_id");
			
			PagingQueryProvider pagingQueryProvider = factoryBean.getObject();
			
			JdbcPagingItemReaderBuilder itemReaderBuilder = new JdbcPagingItemReaderBuilder();
			
			itemReaderBuilder.name("itemReaderBuilder");
			itemReaderBuilder.dataSource(dataSource);
			itemReaderBuilder.queryProvider(pagingQueryProvider);
			itemReaderBuilder.pageSize(10);
			itemReaderBuilder.parameterValues(new HashMap());
			itemReaderBuilder.rowMapper(new UserEntityRowMapper());
			
			return itemReaderBuilder.build(); 
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Bean
	@StepScope
	public JdbcBatchItemWriter<UserEntity> userEntityWriter(DataSource dataSource){
		JdbcBatchItemWriterBuilder itemWriterBuilder = new JdbcBatchItemWriterBuilder();
		
		return itemWriterBuilder
			.dataSource(dataSource)
			.sql("DELETE FROM users WHERE user_id = :user_id")
			.beanMapped()
			.build();
	}
}