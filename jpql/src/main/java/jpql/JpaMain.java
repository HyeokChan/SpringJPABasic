package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("관리자");
            member.setAge(10);
            member.changeTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            member2.setAge(10);
            member2.changeTeam(team);
            member2.setType(MemberType.ADMIN);
            em.persist(member2);

            em.flush();
            em.clear();

//            String query = "select concat('a', 'b') from Member m";
//            String query = "select substring(m.username, 2, 1) from Member m";
//            String query = "select locate('de', 'abcdefg') from Member m";
//            String query = "select size(t.members) from Team t";
            // 실무에서는 묵시적 조인 사용하지 않는다. 운영/튜닝 상황에서 어려움 있음
            String query = "select m.username from Team t join t.members m";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();
            for (String s : result) {
                System.out.println("s = " + s);
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
