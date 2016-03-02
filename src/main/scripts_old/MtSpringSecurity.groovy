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

includeTargets << new File(multiTenantSingleDbPluginDir, "scripts/_MTCommon.groovy")

USAGE = """
Usage: grails mt-spring-security <package> <tenant-domain-class-name>

This script will generate
 * A tenant resolver that integrates with Spring Securty
 * Domain class to represent tenants
 * A tenant repository implementation that integrates with Spring Security

Example: grails mt-spring-security com.yourapp Customer
"""

packageName = ''
tenantResolverClassName = 'SpringSecurityTenantResolver'
tenantRepositoryClassName = 'SpringSecurityTenantRepository'
tenantDomainClassName = ''

target(mtSpringSecurity: 'MultiTenant - SingleDB Spring Security Core integration') {
    depends(checkVersion, configureProxy, packageApp, classpath)

    configure()
    createTenantResolver()
    createTenantDomainClass()
    createTenantRepository()
    updateSpringResources()
    updateConfig()
    messageUserChanges()
}

private void configure() {
    ( packageName, tenantDomainClassName ) = parseArgs()

    templateAttributes = [ packageName: packageName,
                           tenantResolverClassName: tenantResolverClassName,
                           tenantRepositoryClassName: tenantRepositoryClassName,
                           tenantDomainClassName: tenantDomainClassName ]
}

private void createTenantResolver() {
    String dir = packageToDir(packageName)

    String templatePath = "$templateDir/SpringSecurityTenantResolverSkeleton.groovy.template"
    String destinationPath = "$basedir/src/groovy/${dir}${tenantResolverClassName}.groovy"
    generateFile templatePath, destinationPath

    printMessage "Created a Spring Security implementation of TenantResolver: ${destinationPath}"
}

private void createTenantDomainClass() {
    String dir = packageToDir(packageName)

    String templatePath = "$templateDir/TenantDomainClass.groovy.template"
    String destinationPath = "$basedir/grails-app/domain/${dir}${tenantDomainClassName}.groovy"
    generateFile templatePath, destinationPath

    printMessage "Created a tenant domain class: ${destinationPath}"
}

private void createTenantRepository() {
    String dir = packageToDir(packageName)

    String templatePath = "$templateDir/SpringSecurityTenantRepository.groovy.template"
    String destinationPath = "$basedir/src/groovy/${dir}${tenantRepositoryClassName}.groovy"
    generateFile templatePath, destinationPath

    printMessage "Created a Spring Security implementation of TenantRepository: ${destinationPath}"
}

private void updateSpringResources() {
    String resolverBeanDefinitionLine = """tenantResolver(${packageName}.${tenantResolverClassName}) {
//	springSecurityService = ref('springSecurityService')
// }"""
    String repositoryBeanDefinitionLine = "tenantRepository(${packageName}.${tenantRepositoryClassName})"

    String springResourcesPath = "$basedir/grails-app/conf/spring/resources.groovy"
    File springResourcesFile = new File(springResourcesPath)
    if (springResourcesFile.exists()) {
        springResourcesFile << "\n\n// Activate these bean definitions\n"
        springResourcesFile << "// Documentation http://grails.org/doc/latest/guide/single.html#14.2%20Configuring%20Additional%20Beans\n"
        springResourcesFile << "// ${resolverBeanDefinitionLine}\n"
        springResourcesFile << "// ${repositoryBeanDefinitionLine}\n"

        printMessage "--------------------- IMPORTANT ---------------------"
        printMessage "I've added some lines to: ${springResourcesPath}"
        printMessage "Open it and follow the instructions added to the bottom of file\n"
    } else {
        errorMessage "ERROR: Could not find: ${springResourcesPath}"
        errorMessage "You'll have to create this file manually and add the following Spring beans:"
        errorMessage " => ${resolverBeanDefinitionLine}"
        errorMessage " => ${repositoryBeanDefinitionLine}"
    }
}

private void updateConfig() {
    String configLines = "multiTenant {\n    tenantClass = ${packageName}.${tenantDomainClassName}\n}"

    String configPath = "$basedir/grails-app/conf/Config.groovy"
    File configFile = new File(configPath)
    if (configFile.exists()) {
        configFile << "\n\n// Added by the MultiTenant plugin\n// TODO: Verify that this is correct\n"
        configFile << "${configLines}\n"

        printMessage "--------------------- IMPORTANT ---------------------"
        printMessage "I've added some lines to: ${configPath}"
        printMessage "Open it and follow the instructions added to the bottom of file\n"
    } else {
        errorMessage "ERROR: Could not find: ${configPath}"
        errorMessage "You'll have to create this file manually and add the following lines:"
        errorMessage "${configLines}"
    }
}
private void messageUserChanges() {
	printMessage "--------------------- IMPORTANT ---------------------"
	printMessage "You must update your Spring Security User domain"
	printMessage "You need to add 'Integer userTenantId' as a field\n"
}

private parseArgs() {
    args = args ? args.split('\n') : []
    switch (args.size()) {
        case 2:
            printMessage "Creating tenant classes in package ${args[0]}"
            return args
        default:
            errorMessage USAGE
            System.exit(1)
            break
    }
}

setDefaultTarget 'mtSpringSecurity'