/* Copyright 2006-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * --------------
 * This script is based on the quickstart script in the 
 * excellent Spring Security Plugin by Burt Beckwith: 
 * http://grails.org/plugin/spring-security-core
 */

import grails.util.GrailsNameUtils

includeTargets << new File("$multiTenantSingleDbPluginDir/scripts/_MTCommon.groovy")

USAGE = """
Usage: grails mt-create-tenant-resolver <package> <tenant-resolver-class-name>

Creates a skeleton tenant resolver

Example: grails mt-create-tenant-resolver com.yourapp DNSTenantResolver
"""

includeTargets << grailsScript('_GrailsBootstrap')

packageName = ''
tenantResolverClassName = ''

target(mtCreateTenantResolver: 'Creates a tenant resolver skeleton for the MultiTenant - SingleDB plugin') {
    depends(checkVersion, configureProxy, packageApp, classpath)

    configure()
    createTenantResolver()
    updateSpringResources()
}

private void configure() {
    def argValues = parseArgs()
    if (argValues.size() == 2) {
        ( packageName, tenantResolverClassName ) = argValues
    } else {
        ( packageName, userClassName, roleClassName ) = argValues
    }

    templateAttributes = [ packageName: packageName,
                           tenantResolverClassName: tenantResolverClassName ]
}

private void createTenantResolver() {
    String dir = packageToDir(packageName)
    
    String templatePath = "$templateDir/TenantResolverSkeleton.groovy.template"
    String destinationPath = "$basedir/src/groovy/${dir}${tenantResolverClassName}.groovy"
    generateFile templatePath, destinationPath
    
    ant.echo message: "Created a skeleton implementation of TenantResolver: ${destinationPath}"
}

private void updateSpringResources() {
    String beanDefinitionLine = "tenantResolver(${packageName}.${tenantResolverClassName})"
    
    String springResourcesPath = "$basedir/grails-app/conf/spring/resources.groovy"
    File springResourcesFile = new File(springResourcesPath)
    if (springResourcesFile.exists()) {
        springResourcesFile << "\n\n// Activate this bean definition\n"
        springResourcesFile << "// Documentation http://grails.org/doc/latest/guide/single.html#14.2%20Configuring%20Additional%20Beans\n"
        springResourcesFile << "// ${beanDefinitionLine}"
        
        ant.echo message: " "
        ant.echo message: "--------------- IMPORTANT ---------------"
        ant.echo message: "I've added some lines to: ${springResourcesPath}"
        ant.echo message: "Open it and follow the instructions added to the bottom of file\n"
    } else {
        ant.echo message: "ERROR: Could not find: ${springResourcesPath}", level: "error"
        ant.echo message: "You'll have to create this file manually and add the following Spring bean:", level: "error"
        ant.echo message: " => ${beanDefinitionLine}", level: "error"
    }
}

private parseArgs() {
    args = args ? args.split('\n') : []
    switch (args.size()) {
        case 2:
            ant.echo message: "Creating TenantResolver class ${args[1]} in package ${args[0]}"
            return args
        default:
            ant.echo message: USAGE, level: "error"
            System.exit(1)
            break
    }
}

setDefaultTarget 'mtCreateTenantResolver'