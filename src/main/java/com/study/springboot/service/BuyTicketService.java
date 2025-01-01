package com.study.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.study.springboot.dao.ITransaction1Dao;
import com.study.springboot.dao.ITransaction2Dao;

@Service
public class BuyTicketService implements IBuyTicketService {
	
	@Autowired
	ITransaction1Dao transaction1;
	
	@Autowired
	ITransaction2Dao transaction2;
	
	
	/* 1) PlatformTransactionManager 사용하는 방식
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	TransactionDefinition definition;
	*/

	// 2) TransactionTemplate 사용하는 방식
	@Autowired
	TransactionTemplate transactionTemplate;
	
	
	@SuppressWarnings("unused")
	@Override
	public int buy(String consumerId, int amount, String error) {
		
		/* 스프링에서 트랜잭션 사용하기
		 * 1. 선언적 방법
		 * 	빈의 퍼블릭 메서드에 어노테이션 이용해서 추가 -> 미리 선언된 룰에 따라 트랜잭션 제어하고 예외발생시 자동 롤백처리
		 * 	트랜잭션 처리를 비즈니스 로직안에 기술할 필요 x
		 * 
		 * 2. 프로그램적 방법
		 * 	명시적으로 commit & rollback 선언
		 * 	메서드단위보다 더 작은 단위로 트랜잭션 처리 가능
		 * 	- 1) PlatformTransactionManager 사용하는 방식
		 * 	- 2) TransactionTemplate 사용하는 방식
		 * 
		 * 
		*/
		
		// 1)트랜잭션 설정
		//TransactionStatus status = transactionManager.getTransaction(definition);
		
		try {
			// 트랜잭션 적용할 부분을 템플릿으로 감싸기
			transactionTemplate.execute(new TransactionCallbackWithoutResult()
					 {
						@Override
						protected void doInTransactionWithoutResult(TransactionStatus arg0) {
							
							transaction1.pay(consumerId, amount);
							// 에러발생시 insert되었다가 rollback
							
							//의도적 에러 발생
							if(error.equals("1")) {
								int n = 10/0;
							}
							
							transaction2.pay(consumerId, amount);
							// 에러 발생시 insert 자체가 실행 X
						}
					
					});
			
			
			
			
			// 1)트랜잭션 커밋
			//transactionManager.commit(status);
			
			return 1;
			
		} catch(Exception e) {
			System.out.println("[PlatformTransactionManager] Rollback");
			
			// 1)트랜잭션 롤백
			//transactionManager.rollback(status);
			return 0;
		}
	}
}
