package hellojpa;

public class ValueMain {
    public static void main(String[] args) {
        Address address1 = new Address("city", "stt", "11233");
        Address address2 = new Address("city", "stt", "11233");

        // 오버라이드한 equals
        System.out.println("address1 equal address2 " + address1.equals(address2));

    }
}
