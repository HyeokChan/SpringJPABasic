# SpringJPABasic
### - JPA
#### JPA 구동방식
1. Persistence : META-INF/persistence.xml 에서 설정 정보 조회
2. EntityManagerFactory 생성
3. EntityManager 생성
4. 주의 사항
	- 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유
	- 엔티티 매니저는 쓰레드간 공유하지 않아야 함
	- JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
***
### - 영속성 관리
#### 엔티티 매니저 팩토리와 엔티티 매니저 관계
1. 웹 애플리케이션 내 하나의 엔티티 매니저 팩토리가 각 요청에 대한 엔티티 매니저를 생성하고 요청은 엔티티 매니저를 사용하여 db에 접근한다.

#### 엔티티의 생명 주기
1. 비영속(new/transient) : 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
	 - 객체 생성만 한 상태
2. 영속(managed) : 영속성 컨텍스트에 의해 관리되는 상태
	 - 객체 생성 후 persist() 를 통해 영속성 컨텍스트에 관리되는 상태
3. 준영속(detached) : 영속성 컨텍스트에 저장되었다가 분리된 상태
	 - detach(), clear(), close() 를 통해 객체를 영속성 컨텍스트에서 분리
	 - merge() 를 통해 다시 영속성 컨텍스트에서 관리 가능
4. 삭제(removed) : 삭제된 상태
	 - 영속성 컨텍스트에서 관리되는 객체에 remove() 하여 삭제
	 - 다시 영속성 컨텍스트에서 관리하기 위해 persist() 사용
#### 영속성 컨텍스트의 이점
1. 1차 캐시
	 - em.persist()를 통해 객체를 영속성 컨텍스트에 관리하면서 1차 캐시에 저장함
	 - 이후 해당 객체를 조회할 때 1차 캐시에 있으면 1차 캐시에서 조회함 (db조회 안함)
	 - 1차 캐시에 없는 경우 db에서 조회 하고 조회한 결과를 1차 캐시에 저장
2. 동일성(identity) 보장
	 - 조회된 같은 객체에 대한 동일성 보장
3. 트랜잭션을 지원하는 쓰기 지연
	 - em.persist() 등을 통해 생성된 sql 쿼리를 transaction.commit() 시점에 db에 반영한다.
 (1차 캐시에는 persist()시점에 저장해 둔다)
4. 변경 감지(Dirty Checking)
	 - 객체의 데이터를 수정하는 경우 em.update()와 같은 코드가 없어도 객체의 변경을 감지하고 변경에 해당하는 sql 쿼리를 commit() 시점에 전송한다.
5. 지연 로딩

#### 플러시
1. 영속성 컨텍스트의 변경내용을 데이터베이스에 반영한다.
2. 발생 시점
	 - 변경 감지로 인해 발생
	 - 수정된 엔티티 쓰기 지연 sql 저장소에 등록
	 - em.flush()
	 - transaction.commit()
	 - jpql 쿼리 실행
3. 주의사항
	 - 영속성 컨텍스트를 비우는것이 아님
	 - 영속성 컨텍스트의 변경내용을 db에 동기화
	 - 트랜잭션 단위

#### 준영속 상태 (detached)
1. 영속 상태의 엔티티를 영속성 컨텍스트에서 분리
2. 영속성 컨텍스트가 제공하는 기능 사용 x
3. 준영속 상태로 변경하는 방법
	 - em.detach(entity) : 특정 엔티티만 준영속상태로 변경
	 - em.clear() : 영속성 컨텍스트를 완전히 초기화
	 - em.close() : 영속성 컨텍스트 종료
***
### 엔티티 매핑
#### 객체와 테이블 매핑
1. @Entity
	 - JPA 를 통해 테이블과 매핑할 클래스는 위 어노테이션 필수
	 - 기본 생성자 필수

#### 데이터베이스 스키마 자동 생성
1. ddl을 애플리케이션 실행 시점에 자동으로 생성한다.
2. 데이터베이스 방언을 활용하여 데이터베이스에 맞는 ddl 생성
3. 운영서버에서는 사용 x
4. 스키마 자동 생성 속성 (persistence.xml)
	```
	<property name="hibernate.hbm2ddl.auto" value="create"/>
	```
	 - create : 기존 테이블 삭제 후 재생성
	 - create-drop : create와 같으나 종료시점에 drop
	 - update : 변경분만 반영
	 - validate : 엔티티와 테이블이 정상 매핑되었는지만 확인
	 - none : 스키마 자동생성 사용하지 않음
5. 주의사항
	 - 운영장비에는 절대 create, create-drop, update 를 사용하면 안된다.
	 - 개발 초기 단계는 create, update 사용
	 - 테스트 서버는 update, validate
	 - 스테이징과 운영서버는 validate, none 사용
 
#### 필드와 컬럼 매핑
1. 매핑 어노테이션 정리
	 - @Column : 컬럼 매핑
		  - name : 필드와 매핑할 테이블 컬럼 이름
		  - insertable/updatable : 등록, 변경 가능 여부 설정
		  - nullalbe : false로 설정하면 ddl 생성 시에 not null 제약조건 추가
		  - unique : ddl 생성 시에 컬럼에 유니크 제약조건 추가
		  - columnDefinition : 데이터 베이스 컬럼 정보를 직접 줄 수 있다.
		  - length : 문자 길이 제약조건, String 타입에만 사용
		  - precision, scale : BigDecimal 타입에 사용, precision은 전체자릿수, scale은 소수 자릿수를 제약한다.
	 - @Temporal : 날짜 타입 매핑
		  - java.util.Date, Calendar 과 매핑
		  - LocalDate, LocaDateTime을 사용할때는 생략가능
	 - @Enumerated : enum 타입 매핑
		  - value : EnumType.ORDINAL / EnumType.STRING 두가지 분류, ORDINAL은 소스 코드의 순서가 바뀌면 db와 싱크를 맞출 수 없기에 사용하면 안된다 (STRING 사용)
	 - @Lob : BLOB, CLOB 매핑
	 - @Transient : 특정 필드를 컬럼에 매핑하지 않음
 
#### 기본 키 매핑
1. 직접할당 : @Id 만 사용
2. 자동생성 : @GeneratedValue
	- IDENTITY : 데이터베이스에 위임, MYSQL
	- SEQUENCE : 데이터베이스 시퀀스 오브젝트 사용, ORACLE
		- @SequenceGenerator 필요
			- name : 식별자 생성기 이름
			- sequenceName : 데이터베이스에 등록되어 있는 시퀀스 이름
			- initalValue : DDL 생성 시에만 사용됨, 시퀀스 DDL을 생성할 때 처음 시작하는 수 지정
			- allocationSize : 시퀀스 한 번 호출에 증가하는 수
	- TABLE : 키 생성용 테이블 사용, 모든 DB에서 사용
		- @TableGenerator 필요
	- AUTO : 방언에 따라 자동 지정, 기본값
***
### 연관관계 매핑 기초
#### 단반향 연관관계
~~~	
@Entity  
public class Member extends BaseEntity {	  
    @Id  
    @GeneratedValue @Column(name = "MEMBER_ID")  
    private Long id;  
  
    @Column(name = "USERNAME")  
    private String username;  
  
    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "TEAM_ID")  
    private Team team;
}	
~~~
~~~
Team team = new Team();  
team.setName("TeamA");  
em.persist(team);  
Member member = new Member();  
member.setUsername("KKK");  
member.changeTeam(team);  
em.persist(member);  
Member findMember = em.find(Member.class, member.getId());  
// member -> team 조회
Team findTeam = findMember.getTeam();    
System.out.println("findTeam.name = " + findTeam.getName());  
tx.commit();
~~~
참조 관계를 이용해서 team 객체 조회

#### 양방향 연관관계와 연관관계의 주인
~~~	
@Entity  
public class Member extends BaseEntity {	  
    @Id  
    @GeneratedValue @Column(name = "MEMBER_ID")  
    private Long id;  
  
    @Column(name = "USERNAME")  
    private String username;  
  
    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "TEAM_ID")  
    private Team team;
}	

@Entity  
public class Team extends BaseEntity{  
    @Id  
	@GeneratedValue @Column(name = "TEAM_ID")  
    private Long id;  
	private String name;  
  
	@OneToMany(mappedBy = "team")  
    private List<Member> members = new ArrayList<>();  
~~~
~~~
Team team = new Team();  
team.setName("TeamA");  
em.persist(team);  
Member member = new Member();  
member.setUsername("KKK");  
member.changeTeam(team);  
em.persist(member);  
Member findMember = em.find(Member.class, member.getId());  
Team findTeam = findMember.getTeam();  
// team -> member 조회
System.out.println("findTeam.member = " + findTeam.getMembers().get(0).getUsername());    
tx.commit();
~~~

#### 연관관계의 주인과 mappedBy
1. 객체의 양방향 관계
	- 두 테이블의 관계는 양방향 관계 하나로 동작한다. (외래키 하나)
	- 두 객체의 관계로 양방향 관계를 만들기 위해서는 단방향 관계 두개로 동작한다.
	- 테이블과 객체의 격차를 해결하기 위해 두 객체 중 하나로 외래키를 관리해야 한다.
2. 연관관계의 주인
	- 객체의 두 관계중 하나를 연관관계의 주인으로 지정
	- 연관관계의 주인만이 외래키를 관리 (등록, 수정)
	- 주인이 아닌쪽은 읽기만 가능
	- 주인은 mappedBy 속성 사용 X  / JoinColumn 사용
	- 주인이 아니면 mappedBy 속성으로 다른 객체를 주인으로 지정
	- 테이블 기준으로 외래키를 가지고 있는 객체를 연관관계의 주인으로 지정한다 (일반적으로 1:N 관계에서 N인 테이블/객체)
***
### 다양한 연관관계 매핑
#### 매핑 관계
1.  다대일 [N:1]
	- 다대일 단방향
		- 가장 많이 사용하는 연관관계
	- 다대일 양방향
		- 외래키가 있는 쪽이 연관관계의 주인
		- 양쪽을 서로 참조하도록 개발
2. 일다대 [1:N]
	- 일대다 단방향
		- 일대다 단방향은 일대다에서 일이 연관관계의 주인
		- 테이블의 일대다 관계는 항상 다 쪽에 외래키가 있음
		- 일대다 단방향 매핑보다는 다대일 양방향 매핑을 사용 권장
3. 일대일 [1:1]
4. 다대다 [N:M]
	- 실제로 사용해서는 안됨
#### 주요 어노테이션 설명
1. @JoinColumn
	- 외래키를 매핑할 때 사용
	- name : 매핑할 외래키 이름
2. @ManyToOne
	- fetch : 글로벌 페치 전략을 설정한다. (EAGER / LAZY)
3. @OneToMany
	- mappedBy : 연관관계의 주인필드를 선택한다. 
	- fetch : 글로벌 페치 전략을 설정한다. (EAGER / LAZY)
***
### 고급매핑
#### 상속관계 매핑
1. 객체의 상속 구조와 db의 슈퍼타입 서브타입 관계를 매핑
2. 매핑 방법
	- 각각 테이블로 변환 : 조인 전략
		- 장점 : 테이블 정규화, 외래키 참조 무결성 제약조건 활용가능, 저장공간 효율화
		- 단점 : 조회시 조인을 많이 사용, 성능 저하, 조회 쿼리가 복잡함, 테이터 저장시 insert 쿼리 2번 호출
	- 통합 테이블로 변환 : 단일 테이블 전략
		- 장점 : 조인하지 않으므로 일반적으로 조회 성능이 빠름, 조회 쿼리가 단순함
		- 단점 : 자식 엔티티가 매핑한 컬럼은 모두 null 허용 해야 함, 단일 테이블에 모든것을 저장하므로 테이블이 커질 수 있음
	- 서브타입 테이블로 변환 : 구현 클래스마다 테이블 전략
		- 추천하지 않음
3. 주요 어노테이션
	- @Inheritance(strategy=InheritanceType.XXXX)
		- JOINED : 조인전략
		- SINGLE_TABLE : 단일 테이블 전략
			- @DiscriminatorColumn(name="DTYPE") : 단일된 테이블을 구분하는 컬럼명
			- @DiscriminatorValue("XXX") : DTYPE 컬럼에 구분을 위해 저장될 값
		- TABLE_PER_CLASS : 구현클래스마다 테이블 전략

#### @MappedSuperclass
1. 공통 매핑 정보가 필요할 때 사용
	- 최초작업일자, 최초작업자, 최종작업일자, 최종작업자 등 테이블/객체 마다 공통적으로 사용하는 필드 있을 때 활용가능
	- 부모 클래스를 상속받는 자식클래스에 매핑 정보ㅁ나 제공
	- 조회, 검색 불가능
	- 직접 생성해서 사용할 일이 없으므로 추상 클래스 권장