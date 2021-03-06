package com.stats.restservice.internal.services;

import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.stats.restservice.external.services.ITransactionService;
import com.stats.restservice.transaction.ITransaction;

import io.vertx.core.json.JsonObject;


public class TestTransactionService {
	
	/**
	 * Given - Statistics Service spied object and Transactionservice 
	 * When  - on addition of set of 2 transactions with invalid inputs  - older timestamp
	 * Then -  Assert that statService.computeTransaction isn't getting called
	 */
	  @Test
	  public void whenNewInvalidTransactionsAreAdded_assertStatServiceExecution(){
		  StatisticsServiceImpl statisticsService = new StatisticsServiceImpl();
		  StatisticsServiceImpl spiedStatisticsService = Mockito.spy(statisticsService);
		  ITransactionService transactionService = new TransactionServiceImpl(statisticsService);
		  
		  Optional<Boolean> status = transactionService.unMarshallTransactionData(getTransactionJson(10.5, System.currentTimeMillis() - 60000));
		  Assert.assertEquals(Optional.empty(), status);

		  status = transactionService.unMarshallTransactionData((getTransactionJson(12.5, System.currentTimeMillis() - 60000)));
		  Assert.assertEquals(Optional.empty(), status);

	      Mockito.verify(spiedStatisticsService, Mockito.never()).computeTransaction(Mockito.any(ITransaction.class));
	  }
	  
	  
	  /**
		 * Given - Statistics Service spied object and Transactionservice 
		 * When  - on addition of set of 2 transactions with valid inputs  
		 * Then -  Assert that statService.computeTransaction isn't getting called
		 */
		  @Test
		  public void whenNewValidTransactionsAreAdded_assertStatServiceExecution(){
			  StatisticsServiceImpl statisticsService = new StatisticsServiceImpl();
			  StatisticsServiceImpl spiedStatisticsService = Mockito.spy(statisticsService);
			  ITransactionService transactionService = new TransactionServiceImpl(spiedStatisticsService);
			  
			  Optional<Boolean> status = transactionService.unMarshallTransactionData((getTransactionJson(10.5, new Date().getTime())));
			  Assert.assertEquals(true, status.get().booleanValue());
			  status = transactionService.unMarshallTransactionData((getTransactionJson(12.5, new Date().getTime())));
			  Assert.assertEquals(true, status.get().booleanValue());
			  
		      Mockito.verify(spiedStatisticsService, Mockito.times(2)).computeTransaction(Mockito.any(ITransaction.class));
		  }
		  
		  /**
			 * Given - Statistics Service and Transactionservice 
			 * When  - on addition of set of 2 transactions with invalid inputs  
			 * Then -  Assert that respone returned has empty optional
			 */
			  @Test
			  public void whenTransactionsWithInvalidINputs_assertResposne(){
				  StatisticsServiceImpl statisticsService = new StatisticsServiceImpl();
				  ITransactionService transactionService = new TransactionServiceImpl(statisticsService);
				  
				  Optional<Boolean> status = transactionService.unMarshallTransactionData((getTransactionJson(null, null)));
				  Assert.assertEquals(Optional.empty(), status);
				  status = transactionService.unMarshallTransactionData(JsonObject::new);
				  Assert.assertEquals(Optional.empty(), status);
				  
			  }
	  
	  private Supplier<JsonObject> getTransactionJson(Double doubleValue, Long time) {
		  JsonObject jsonData = new JsonObject();
		  jsonData.put("amount", doubleValue);
		  jsonData.put("timestamp", time);
		return () -> jsonData;
	}

}
