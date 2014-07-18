import demo.*
def tenant = new DemoTenant(name:'novadge1',domain:'novadg1e.com')

tenant.save(failOnError:true,flush:true)
def tenant2 = new DemoTenant(name:'Rocitus1',domain:'rocitu1s.com')
tenant2.save(failOnError:true,flush:true)

def dog
DemoTenant.withTenantId(tenant.tenantId()){
    dog = new demo.DemoDog(name:'Omasiri').save(flush:true)
}

println tenant.getProperties()
print tenant.getProperties()
println "dog tenant id = ${dog.tenantId}"
print dog.tenantId == tenant.tenantId()
dog.delete()

tenant.delete()
tenant2.delete()