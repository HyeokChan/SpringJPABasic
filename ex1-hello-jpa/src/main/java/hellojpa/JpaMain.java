package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {

            Address address = new Address("city", "str", "10220");

            Member member = new Member();
            member.setUsername("hello");
            member.setHomeAddress(address);
            member.setWorkPeriod(new Period());
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("hello2");
            member2.setHomeAddress(address);
            member2.setWorkPeriod(new Period());
            em.persist(member2);

            Address newAddress = new Address("newCity", address.getStreet(), address.getZipcode());
            member.setHomeAddress(newAddress);

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
