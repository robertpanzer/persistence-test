package org.superbiz.arqpersistence;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import static org.junit.Assert.assertEquals;

@Transactional(TransactionMode.DISABLED)
//tag::doc[]
@RunWith(Arquillian.class)
public class OldSchoolTest {

    @Deployment
    public static WebArchive deploy() throws Exception {       // <1>
        return ShrinkWrap.create(WebArchive.class, "OldSchoolTest.war")
                .addClasses(MyEntity.class)
                .addAsWebInfResource("test-persistence.xml", "persistence.xml");
    }

    @PersistenceContext(name = "myPU")
    private EntityManager em;                                  // <2>

    @Resource
    private UserTransaction trx;                               // <3>

    @Test
    public void shouldCreateEntity() throws Exception {        // <4>
        try {
            // Given: Initial DB setup with empty tables
            trx.begin();
            em.createQuery("delete from MyEntity").executeUpdate();
            trx.commit();

            // When: an entity is created
            trx.begin();
            MyEntity myentity = new MyEntity("Some Key", "Some Value");
            em.persist(myentity);
            trx.commit();

            // Then: only that one new entity is in the database
            trx.begin();
            MyEntity myEntity2 = em.find(MyEntity.class, "Some Key");
            assertEquals("Some Value", myEntity2.getValue());
            Number count = (Number) em.createQuery("select count(e) from MyEntity e").getSingleResult();
            assertEquals(1, count.intValue());
            trx.commit();
        } finally {
            // Rollback transaction in case it is open due to some error
            if (trx.getStatus() == Status.STATUS_ACTIVE) {
                trx.rollback();
            }
        }
    }

    @Test
    public void shouldUpdateEntity() throws Exception {        // <5>
        try {
            // Given: Initial DB setup with two entities
            trx.begin();
            em.createQuery("delete from MyEntity").executeUpdate();
            em.persist(new MyEntity("Key 1", "Value 1"));
            em.persist(new MyEntity("Key 2", "Value 2"));
            trx.commit();

            // When: one entity is updated
            trx.begin();
            MyEntity myentity = em.find(MyEntity.class, "Key 1");
            myentity.setValue("Another Value 1");
            trx.commit();

            // Then: the two entities are still available and only one of them has the new value
            trx.begin();
            assertEquals("Another Value 1", em.find(MyEntity.class, "Key 1").getValue());
            assertEquals("Value 2", em.find(MyEntity.class, "Key 2").getValue());

            Number count = (Number) em.createQuery("select count(e) from MyEntity e").getSingleResult();
            assertEquals(2, count.intValue());
            trx.commit();
        } finally {
            // Rollback transaction in case it is open due to some error
            if (trx.getStatus() == Status.STATUS_ACTIVE) {
                trx.rollback();
            }
        }
    }
}
//end::doc[]