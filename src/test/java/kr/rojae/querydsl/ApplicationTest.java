package kr.rojae.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.rojae.querydsl.entity.Hello;
import kr.rojae.querydsl.entity.QHello;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional(readOnly = true)
@Slf4j
 class ApplicationTest {

	@Autowired
	private EntityManager em;

	/**
	 * QueryDSL 설정, 테스트
 	 */
	@Test
	@Transactional
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = QHello.hello;

		Hello result = query.selectFrom(qHello).fetchOne();

		assertThat(hello).isEqualTo(result);
		assertThat(hello.getId()).isEqualTo(result.getId());

		log.info("hello : " + hello.getId());
		log.info("result : " + result.getId());
	}

}
