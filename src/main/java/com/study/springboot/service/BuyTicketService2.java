package com.study.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.study.springboot.dao.ITransaction1Dao;
import com.study.springboot.dao.ITransaction2Dao;

@Service
public class BuyTicketService2 {
	
	@Autowired
	ITransaction1Dao transaction1;
	
	@Autowired
	ITransaction2Dao transaction2;
	
	@Autowired
	TransactionTemplate transactionTemplate;
	
	/*	트랜잭션의 전파속성 - 관련 어노테이션
	 * 	required(0) : default, 전체 처리
	 * 	supports(1) : 기존 트랜잭션에 의존
	 * 	mandatory(2) : 트랜잭션에 꼭 포함되어야 함. 트랜잭션이 있는 곳에서 호출해야 함
	 * 	requires_new(3) : 각각 트랜잭션 처리 
	 * 	not_supported(4) : 트랜잭션에 포함 x, 기존 트랜잭션 존재 -> 일시중지, 메소드 실행 끝난 후 계속 진행
	 * 	never(5) : 트랜잭션에 절대 포함 x, 트랜잭션있는 곳에서 호출 -> error
	 */
	
	// 1. 선언적 방법
	//@Transactional(propagation=Propagation.REQUIRED)
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public int buy(String consumerId, int amount, String error) {

		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult()
					 {
						@Override
						@SuppressWarnings("unused")
						protected void doInTransactionWithoutResult(TransactionStatus arg0) {
							
							transaction1.pay(consumerId, amount);
							
							//의도적 에러 발생
							if(error.equals("1")) {
								int n = 10/0;
							}
							transaction2.pay(consumerId, amount);
						}
					
					});
			
			return 1;
			
		} catch(Exception e) {
			System.out.println("[Transaction Propagation #2] Rollback");
			
			return 0;
		}
	}
}
