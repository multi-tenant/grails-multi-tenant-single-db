package demo

import org.springframework.beans.factory.InitializingBean;

class DemoService implements InitializingBean {

    static transactional = false
	
	public void afterPropertiesSet() {
	}

	def touchedByTenant = "none"
		
}