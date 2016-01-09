package org.superbiz.arqpersistence;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//tag::doc[]
@RunWith(Arquillian.class)
public class PersistenceTest {

    @Deployment
    public static WebArchive deploy() throws Exception {       // <1>
        return ShrinkWrap.create(WebArchive.class, "PersistenceTest.war")
                        .addClasses(MyEntity.class)
                        .addAsWebInfResource("test-persistence.xml", "persistence.xml");
    }

    @PersistenceContext(name = "myPU")                         // <2>
    private EntityManager em;

    @Test
    @InSequence(1)
    public void initEntityManager() {                          // <3>
        em.getMetamodel();
    }

    @Test
    @ShouldMatchDataSet("datasets/after_create.xml")
    @InSequence(2)
    public void shouldCreateEntity() throws Exception {        // <4>
        MyEntity myentity = new MyEntity("Some Key", "Some Value");
        em.persist(myentity);
    }

    @Test
    @UsingDataSet("datasets/before_update.xml")
    @ShouldMatchDataSet("datasets/after_update.xml")
    @InSequence(2)
    public void shouldUpdateEntity() throws Exception {        // <5>
        MyEntity myentity = em.find(MyEntity.class, "Key 1");
        myentity.setValue("Another Value 1");
    }
}
//end::doc[]
