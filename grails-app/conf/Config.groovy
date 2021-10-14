log4j = {
    error 'org.codehaus.groovy.grails',
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'
    info 'grails.app'

    debug 'grails.plugin.multitenant'
}

multiTenant {
    perTenantBeans = ["demoService"]
    tenantClass = demo.DemoTenant
}

grails.doc.authors = "Kim A. Betti"
grails.doc.license = "Apache 2.0"
grails.doc.copyright = ""
grails.doc.footer = "Have a nice day!"
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"

hibernate {
    hijacker {
        datasource = 'secondary'
    }
}
// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
