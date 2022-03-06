package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        //트랜잭션 시작
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Member member = em.find(Member.class, 150L);
            member.setName("ZZZZZ");
            System.out.println("===========================");



            //쿼리 발생
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
