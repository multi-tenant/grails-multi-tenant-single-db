package grails.plugin.multitenant.singledb.hibernate

import grails.plugin.multitenant.core.Tenant
import grails.plugin.multitenant.core.exception.TenantSecurityException
import grails.plugin.spock.IntegrationSpec

import org.hibernate.Session
import org.hibernate.SessionFactory

import spock.lang.FailsWith
import demo.DemoProduct

/**
 * These tests are also good for highlighting various aspects
 * and side effects of the Hibernate filters and the plugin.
 * @author Kim A. Betti
 */
class TenantHibernateEventListenerIntegrationSpec extends IntegrationSpec {
    
    SessionFactory sessionFactory
    
    def "tenant id should be injected"() {
        when:
        Tenant.withTenantId(2) {
            new DemoProduct(name: "iPhone").save(flush: true, failOnError: true)
        }
        
        then:
        Tenant.withTenantId(2) {
            DemoProduct.findByName("iPhone").properties['tenantId']
        } == 2
    }
    
    def "should not be allowed to load entities owned by other tenants"() {
        when: "we create a new product iBone"
        int iBoneId = Tenant.withTenantId(2) {
            DemoProduct iBone = new DemoProduct(name: "iBone").save(flush: true, failOnError: true);
            return iBone.id
        }
        
        then: "the id should not be negative"
        iBoneId > -1
        
        and: "iBone should not be viewable by other tenants"
        Tenant.withTenantId(1) {
            DemoProduct.withNewSession {
                DemoProduct.get(iBoneId);
            }
        } == null
    }
    
    def "should be allowed to load own entities"() {
        when:
        int entityId = Tenant.withTenantId(2) {
            def productInstance = new DemoProduct(name: "product-name-goes-here").save(flush: true, failOnError: true)
            return productInstance.id
        }
        
        then:
        Tenant.withTenantId(2) {
            DemoProduct.withNewSession {
                DemoProduct.get(entityId);
            }
        } != null
    }
    
    @FailsWith(TenantSecurityException)
    def "should not be able to change tenant id"() {
        given:
        def productInstance = Tenant.withTenantId(2) {
            new DemoProduct(name: "another-product-name").save(flush: true, failOnError: true)
        }
        
        expect:
        Tenant.withTenantId(3) {
            productInstance.name = "another-product-name-version-2"
            productInstance.save(flush: true, failOnError: true)
            throw new Exception("Should not be able to do update entity with another tenant id")
        }
    }
    
    def "should be able to update with current id"() {
        when:
        def productInstance = Tenant.withTenantId(2) {
            new DemoProduct(name: "yet-another-product-name").save(flush: true, failOnError: true)
        }
        
        then:
        Tenant.withTenantId(2) {
            productInstance.name = "yet-another-product-name-version-2";
            productInstance.save(flush: true, failOnError: true)
        }
    }
    
    def "should not be able to delete another tenants entities"() {
        given: "a random product"
        DemoProduct jaguar = Tenant.withTenantId(100) {
            createAndPersistProduct("Jaguar")
        }
        
        when: "we try to create it in the namespace of another tenant"
        Tenant.withTenantId(200) {
            jaguar.delete();
        }

        then: "the product still exists in the database"
        Tenant.withTenantId(100) {
            DemoProduct.findByName("Jaguar")
        } != null
    
        when: "we try to delete it in the namespace of the owning tenant"
        Tenant.withTenantId(100) {
            jaguar.delete();
        }
        
        then: "it is deleted from the database"
        Tenant.withTenantId(200) {
            jaguar.delete();
        } == null
    }
    
    def "filters should override where clause"() {
        given: "a product"
        DemoProduct hammer = Tenant.withTenantId(123) {
            createAndPersistProduct("Hammer")
        }
        
        expect: "the owning tenant should be able to find it"
        Tenant.withTenantId(123) {
            DemoProduct.findByNameAndTenantId("Hammer", 123)
        } != null
    
        and: "other tenants should be able to fetch"
        Tenant.withTenantId(321) {
            DemoProduct.findByNameAndTenantId("Hammer", 123)
        } == null
    }
    
    def "plain old sql will not be filtered"() {
        given: "the current Hibernate session to execute sql" 
        Session currentSession = sessionFactory.getCurrentSession()
        
        and: "a product"
        DemoProduct nail = Tenant.withTenantId(123) {
            createAndPersistProduct("Nail")
        }
        
        expect: "we're able to look it up by sql even in another tenant namespace"
        Tenant.withTenantId(312) {
            currentSession.createSQLQuery("select * from demo_product where tenant_id = 123").list()
        }.size() > 0
    }
    
    protected DemoProduct createAndPersistProduct(String productName) {
        DemoProduct product = new DemoProduct(name: productName)
        product.save(flush: true, failOnError: true)
        return product
    }
    
//    TODO: Detect and implement this in a graceful way
//    def "one of a collection is not his"() {
//        given:
//        Session session = sessionFactory.getCurrentSession()
//        assert session.createSQLQuery("insert into demo_pet_owner(version, name, tenant_id) values(0, 'Mickey Mouse', 567)").executeUpdate() == 1
//        
//        when:
//        Integer mickeyId = Tenant.withTenantId(567) {
//            DemoPetOwner.findByName("Mickey Mouse")?.id
//        }
//        
//        then:
//        mickeyId != null
//        mickeyId > 0
//    
//        when:
//        String base = "insert into demo_animal(version, class, name, cats_killed, owner_id, tenant_id) "
//        assert session.createSQLQuery(base + "values(0, 'demo.DemoDog', 'Pluto', 0, $mickeyId, 567)").executeUpdate() == 1
//        assert session.createSQLQuery(base + "values(0, 'demo.DemoDog', 'Bolivar', 2, $mickeyId, 111)").executeUpdate() == 1
//        session.clear()
//        
//        then: "as Hibernate hasn't fetched the actual data yet so we don't know that one doesn't belong to Mickey"
//        Tenant.withTenantId(567) {
//            DemoPetOwner.findByName("Mickey Mouse").pets.size()
//        } == 2
//    
//        when: "we force Hibernate to load the entities"
//        def mickeysDogs = Tenant.withTenantId(567) {
//            DemoPetOwner.findByName("Mickey Mouse").pets.collect { it.name } 
//        } 
//        
//        then: "a tenant security exception is thrown"
//        !mickeysDogs.contains("Bolivar")
//        mickeysDogs.size() == 1
//    }
    
}
