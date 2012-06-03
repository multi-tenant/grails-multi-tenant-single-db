dataSource {
    pooled = true
/*    driverClassName = 'com.mysql.jdbc.Driver'
    username = 'root'
    password = 'xxx'*/
  
  driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
    format_sql = false
	//naming_strategy = 'org.hibernate.cfg.DefaultNamingStrategy'
	show_sql = false
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            url = "jdbc:h2:mem:devDB;MVCC=TRUE"
        }
    }
    test {
        dataSource {
            dbCreate = "create-drop"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE"
            //url = 'jdbc:mysql://127.0.0.1/multi'
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:file:prodDb;shutdown=true;MVCC=TRUE"
        }
    }
}
