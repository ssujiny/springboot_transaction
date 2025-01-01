package com.study.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class BuyAndLogService {
	@Autowired
	BuyTicketService2 buyTicket;
	
	@Autowired
	LogWriteService logWrite;
	
	@Autowired
	TransactionTemplate transactionTemplate;
	
	
	public int buy(String consumerId, int amount, String error) {
		
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult()
			 {
				@Override
				@SuppressWarnings("unused")
				protected void doInTransactionWithoutResult(TransactionStatus arg0) {
					
					buyTicket.buy(consumerId, amount, error);
					
					//의도적 에러 발생
					if(error.equals("2")) {
						int n = 10/0;
					}
					
					logWrite.write(consumerId, amount);
				}
			
			});
	
			return 1;
			
		} catch(Exception e) {
			System.out.println("[Transaction Propagation #1] Rollback");
			
			return 0;
		}
	}
}
