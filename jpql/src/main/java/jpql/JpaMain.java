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
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();
//            String query = "select m from Member m left outer join m.team t on t.name = 'teamA'";
//            String query = "select m from Member m left outer join Team t on m.username = t.name and t.name = 'teamA'";
//            String query = "select (select avg(m1.age) from Member m1) as avgAge, m from Member m";
            String query = "select m.username, 'HELLO', true, m.type from Member m " +
                            "where m.type = :userType";
            List<Object[]> resultList = em.createQuery(query)
                    .setParameter("userType", MemberType.ADMIN)
                    .getResultList();
            for(Object[] objects : resultList){
                for(Object o : objects){
                    System.out.println("objects = " + o);
                }
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
