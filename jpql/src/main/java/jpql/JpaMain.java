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
            member.setUsername("teamA");
            member.setAge(10);
            member.changeTeam(team);
            em.persist(member);

            em.flush();
            em.clear();
//            String query = "select m from Member m left outer join m.team t on t.name = 'teamA'";
            String query = "select m from Member m left outer join Team t on m.username = t.name and t.name = 'teamA'";
            List<Member> resultList = em.createQuery(query, Member.class)
                    .getResultList();
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
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
