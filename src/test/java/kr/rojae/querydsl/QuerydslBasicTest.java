package kr.rojae.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.rojae.querydsl.entity.Member;
import kr.rojae.querydsl.entity.QMember;
import kr.rojae.querydsl.entity.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static kr.rojae.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory query;

    @BeforeEach
    public void setup() {
        query = new JPAQueryFactory(em);

        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        //when
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() throws Exception {
        // find member1
        Member findMember = em.createQuery("select m from Member m where m.username= : username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        Member findMember = query.select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assert findMember != null;
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = query.selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10))
                )
                .fetchOne();

        assert findMember != null;
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam(){
        Member findMember = query.selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assert findMember != null;
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultFetch() throws Exception {
        log.info("==== fetch ==== ");
        List<Member> fetch = query.selectFrom(member).fetch();

        log.info("==== fetchOne ==== ");
        Member fetchOne = query.selectFrom(member).where(member.username.eq("member1")).fetchOne();

        log.info("==== fetchFirst ==== ");
        Member fetchFirst = query.selectFrom(member).fetchFirst();

        log.info("==== fetchResults ==== ");
        QueryResults<Member> results = query.selectFrom(member).fetchResults();
        long total = results.getTotal();
        List<Member> results1 = results.getResults();
        log.info("total : " + total);
        log.info("results1 : " + results1);

        log.info("==== fetchCount =====");
        long count = query.selectFrom(member).fetchCount();
        log.info("fetchCount : " + count);


    }
}
