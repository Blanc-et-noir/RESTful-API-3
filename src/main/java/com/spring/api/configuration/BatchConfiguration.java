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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import com.spring.api.entity.ItemEntity;
import com.spring.api.entity.ItemImageEntity;
import com.spring.api.entity.MessageEntity;
import com.spring.api.entity.UserEntity;
import com.spring.api.rowMapper.ItemEntityRowMapper;
import com.spring.api.rowMapper.ItemImageEntityRowMapper;
import com.spring.api.rowMapper.MessageEntityRowMapper;
import com.spring.api.rowMapper.UserEntityRowMapper;

@Configuration
public class BatchConfiguration {
	private JobBuilderFactory jobBuilderFactory;
	private StepBuilderFactory stepBuilderFactory;
	private DataSource dataSource;
	private JobRepository jobRepository;
	
	private final String SEP = File.separator;
	private final String BASE_DIRECTORY_OF_IMAGE_FILES = "C:"+SEP+"georaesangeo"+SEP+"items"+SEP+"images"+SEP;
	private final long BATCH_FREQUENCY;
	
	@Autowired
	BatchConfiguration(
		JobBuilderFactory jobBuilderFactory,
		StepBuilderFactory stepBuilderFactory,
		DataSource dataSource,
		JobRepository jobRepository,
		@Value("${frequency.batch}") long BATCH_FREQUENCY
	){
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.dataSource = dataSource;
		this.jobRepository = jobRepository;
		this.BATCH_FREQUENCY = BATCH_FREQUENCY;
	}
	
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
				.next(deleteMessageStep())
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
				.get("deleteItemImageStep")
				.<ItemImageEntity, ItemImageEntity>chunk(10)
				.reader(itemImageEntityReader(this.dataSource))
				.processor(new ItemImageEntityProcessor())
				.writer(itemImageEntityWriter(this.dataSource))
				.build();
	}
	
	@Bean
	@JobScope
	public Step deleteMessageStep() {
		return this.stepBuilderFactory
				.get("deleteMessageStep")
				.<MessageEntity, MessageEntity>chunk(10)
				.reader(messageEntityReader(this.dataSource))
				.writer(messageEntityWriter(this.dataSource))
				.build();
	}
	
	@Bean
	@StepScope
	public JdbcPagingItemReader<MessageEntity> messageEntityReader(DataSource dataSource){
		try {
			SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
			
			factoryBean.setDataSource(dataSource);
			factoryBean.setSelectClause("SELECT *");
			factoryBean.setFromClause("FROM messages m");
			factoryBean.setWhereClause(
				"WHERE "+
				"NOT EXISTS(SELECT 1 FROM message_receivers r WHERE m.message_id = r.message_id AND (r.message_receiver_status = 'N' OR (r.message_receiver_status = 'Y' AND TIMESTAMPDIFF(SECOND,r.message_receiver_delete_time, NOW()) <= "+BATCH_FREQUENCY+")))"+
				" AND "+
				"NOT EXISTS(SELECT 1 FROM message_senders s WHERE m.message_id = s.message_id AND (s.message_sender_status = 'N' OR (s.message_sender_status = 'Y' AND TIMESTAMPDIFF(SECOND,s.message_sender_delete_time,NOW()) <= "+BATCH_FREQUENCY+")))"
			);
			factoryBean.setSortKey("message_id");
			
			PagingQueryProvider pagingQueryProvider = factoryBean.getObject();
			
			JdbcPagingItemReaderBuilder messageReaderBuilder = new JdbcPagingItemReaderBuilder();
			
			messageReaderBuilder.name("messageReaderBuilder");
			messageReaderBuilder.dataSource(dataSource);
			messageReaderBuilder.queryProvider(pagingQueryProvider);
			messageReaderBuilder.pageSize(10);
			messageReaderBuilder.parameterValues(new HashMap());
			messageReaderBuilder.rowMapper(new MessageEntityRowMapper());
			
			return messageReaderBuilder.build(); 
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Bean
	@StepScope
	public JdbcBatchItemWriter<MessageEntity> messageEntityWriter(DataSource dataSource){
		JdbcBatchItemWriterBuilder messageWriterBuilder = new JdbcBatchItemWriterBuilder();
		
		return messageWriterBuilder
			.dataSource(dataSource)
			.sql("DELETE FROM messages WHERE message_id = :message_id")
			.beanMapped()
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
			factoryBean.setWhereClause("WHERE user_status = 'Y' AND TIMESTAMPDIFF(DAY, user_withdraw_time, NOW()) >= "+BATCH_FREQUENCY);
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
	
	private class ItemImageEntityProcessor implements ItemProcessor<ItemImageEntity,ItemImageEntity>{

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