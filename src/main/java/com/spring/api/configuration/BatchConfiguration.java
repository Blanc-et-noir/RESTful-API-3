package com.spring.api.configuration;

import java.io.File;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
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

import com.spring.api.entity.ItemEntity;
import com.spring.api.entity.ItemImageEntity;
import com.spring.api.entity.UserEntity;
import com.spring.api.rowMapper.ItemEntityRowMapper;
import com.spring.api.rowMapper.ItemImageEntityRowMapper;
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
	
	private final String SEP = File.separator;
	private final String BASE_DIRECTORY_OF_IMAGE_FILES = "C:"+SEP+"georaesangeo"+SEP+"items"+SEP+"images"+SEP;
	
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
				//메세지 id 조회 및 삭제 스텝
				.next(deleteItemStep())
				.next(deleteItemImageStep())
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
	@JobScope
	public Step deleteItemStep() {
		return this.stepBuilderFactory
				.get("deleteItemStep")
				.<ItemEntity, ItemEntity>chunk(10)
				.reader(itemEntityReader(this.dataSource))
				.writer(itemEntityWriter(this.dataSource))
				.build();
	}
	
	@Bean
	@JobScope
	public Step deleteItemImageStep() {
		return this.stepBuilderFactory
				.get("deleteItemStep")
				.<ItemImageEntity, ItemImageEntity>chunk(10)
				.reader(itemImageEntityReader(this.dataSource))
				.processor(new ItemImageEntityProcessor())
				.writer(itemImageEntityWriter(this.dataSource))
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
			factoryBean.setWhereClause("WHERE user_status = 'Y' AND TIMESTAMPDIFF(DAY, user_withdraw_time, NOW()) >= 0");
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
	
	@Bean
	@StepScope
	public JdbcPagingItemReader<ItemEntity> itemEntityReader(DataSource dataSource){
		try {
			SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
			
			factoryBean.setDataSource(dataSource);
			factoryBean.setSelectClause("SELECT *");
			factoryBean.setFromClause("FROM items");
			factoryBean.setWhereClause("WHERE user_id IS NULL OR item_status = 'Y'");
			factoryBean.setSortKey("item_id");
			
			PagingQueryProvider pagingQueryProvider = factoryBean.getObject();
			
			JdbcPagingItemReaderBuilder itemReaderBuilder = new JdbcPagingItemReaderBuilder();
			
			itemReaderBuilder.name("itemReaderBuilder");
			itemReaderBuilder.dataSource(dataSource);
			itemReaderBuilder.queryProvider(pagingQueryProvider);
			itemReaderBuilder.pageSize(10);
			itemReaderBuilder.parameterValues(new HashMap());
			itemReaderBuilder.rowMapper(new ItemEntityRowMapper());
			
			return itemReaderBuilder.build(); 
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ItemEntity> itemEntityWriter(DataSource dataSource){
		JdbcBatchItemWriterBuilder itemWriterBuilder = new JdbcBatchItemWriterBuilder();
		
		return itemWriterBuilder
			.dataSource(dataSource)
			.sql("DELETE FROM items WHERE item_id = :item_id")
			.beanMapped()
			.build();
	}
	
	@Bean
	@StepScope
	public JdbcPagingItemReader<ItemImageEntity> itemImageEntityReader(DataSource dataSource){
		try {
			SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
			
			factoryBean.setDataSource(dataSource);
			factoryBean.setSelectClause("SELECT *");
			factoryBean.setFromClause("FROM item_images");
			factoryBean.setWhereClause("WHERE item_id IS NULL OR item_image_status = 'Y'");
			factoryBean.setSortKey("item_image_id");
			
			PagingQueryProvider pagingQueryProvider = factoryBean.getObject();
			
			JdbcPagingItemReaderBuilder itemImageReaderBuilder = new JdbcPagingItemReaderBuilder();
			
			itemImageReaderBuilder.name("itemImageReaderBuilder");
			itemImageReaderBuilder.dataSource(dataSource);
			itemImageReaderBuilder.queryProvider(pagingQueryProvider);
			itemImageReaderBuilder.pageSize(10);
			itemImageReaderBuilder.parameterValues(new HashMap());
			itemImageReaderBuilder.rowMapper(new ItemImageEntityRowMapper());
			
			return itemImageReaderBuilder.build(); 
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Bean
	@StepScope
	public JdbcBatchItemWriter<ItemImageEntity> itemImageEntityWriter(DataSource dataSource){
		JdbcBatchItemWriterBuilder itemWriterBuilder = new JdbcBatchItemWriterBuilder();
		
		return itemWriterBuilder
			.dataSource(dataSource)
			.sql("DELETE FROM item_images WHERE item_image_id = :item_image_id")
			.beanMapped()
			.build();
	}
	
	class ItemImageEntityProcessor implements ItemProcessor<ItemImageEntity,ItemImageEntity>{

		@Override
		public ItemImageEntity process(ItemImageEntity itemImageEntity) throws Exception {
			
			String path = BASE_DIRECTORY_OF_IMAGE_FILES+itemImageEntity.getItem_image_stored_name()+"."+itemImageEntity.getItem_image_extension();
			File file = new File(path);
			
			if(file.exists()) {
				FileUtils.delete(file);
			}			
			
			return itemImageEntity;
		}
	}
}