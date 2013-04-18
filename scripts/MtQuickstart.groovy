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
Usage: grails mt-quickstart <package> <tenant-resolver-name> <tenant-repository-name> <tenant-domain-class-name>

This script will generate
 * The skeleton of a tenant resolver
 * Domain class to represent tenants
 * Simple tenant repository implementation

Example: grails mt-quickstart com.yourapp DomainTenantResolver CachingTenantRepository Customer
"""

packageName = ''
tenantResolverClassName = ''
tenantRepositoryClassName = ''
tenantDomainClassName = ''

target(mtQuickstart: 'MultiTenant - SingleDB plugin quickstart') {
    depends(checkVersion, configureProxy, packageApp, classpath)

    configure()
    createTenantResolver()
    createTenantDomainClass()
    createTenantRepository()
    updateSpringResources()
    updateConfig()
}

private void configure() {
    ( packageName, tenantResolverClassName, tenantRepositoryClassName, tenantDomainClassName ) = parseArgs()

    templateAttributes = [ packageName: packageName,
                           tenantResolverClassName: tenantResolverClassName,
                           tenantRepositoryClassName: tenantRepositoryClassName,
                           tenantDomainClassName: tenantDomainClassName ]
}

private void createTenantResolver() {
    String dir = packageToDir(packageName)

    String templatePath = "$templateDir/TenantResolverSkeleton.groovy.template"
    String destinationPath = "$basedir/src/groovy/${dir}${tenantResolverClassName}.groovy"
    generateFile templatePath, destinationPath

    ant.echo message: "Created a skeleton implementation of TenantResolver: ${destinationPath}"
}

private void createTenantDomainClass() {
    String dir = packageToDir(packageName)

    String templatePath = "$templateDir/TenantDomainClass.groovy.template"
    String destinationPath = "$basedir/grails-app/domain/${dir}${tenantDomainClassName}.groovy"
    generateFile templatePath, destinationPath

    ant.echo message: "Created a tenant domain class: ${destinationPath}"
}

private void createTenantRepository() {
    String dir = packageToDir(packageName)

    String templatePath = "$templateDir/DefaultTenantRepository.groovy.template"
    String destinationPath = "$basedir/src/groovy/${dir}${tenantRepositoryClassName}.groovy"
    generateFile templatePath, destinationPath

    ant.echo message: "Created a default implementation of TenantRepository: ${destinationPath}"
}

private void updateSpringResources() {
    String resolverBeanDefinitionLine = "tenantResolver(${packageName}.${tenantResolverClassName})"
    String repositoryBeanDefinitionLine = "tenantRepository(${packageName}.${tenantRepositoryClassName})"

    String springResourcesPath = "$basedir/grails-app/conf/spring/resources.groovy"
    File springResourcesFile = new File(springResourcesPath)
    if (springResourcesFile.exists()) {
        springResourcesFile << "\n\n// Activate these bean definitions\n"
        springResourcesFile << "// Documentation http://grails.org/doc/latest/guide/single.html#14.2%20Configuring%20Additional%20Beans\n"
        springResourcesFile << "// ${resolverBeanDefinitionLine}\n"
        springResourcesFile << "// ${repositoryBeanDefinitionLine}\n"

        ant.echo message: "--------------------- IMPORTANT ---------------------"
        ant.echo message: "I've added some lines to: ${springResourcesPath}"
        ant.echo message: "Open it and follow the instructions added to the bottom of file\n"
    } else {
        ant.echo message: "ERROR: Could not find: ${springResourcesPath}", level: "error"
        ant.echo message: "You'll have to create this file manually and add the following Spring beans:", level: "error"
        ant.echo message: " => ${resolverBeanDefinitionLine}", level: "error"
        ant.echo message: " => ${repositoryBeanDefinitionLine}", level: "error"
    }
}

private void updateConfig() {
    String configLines = "multiTenant {\n    tenantClass = ${packageName}.${tenantDomainClassName}\n}"

    String configPath = "$basedir/grails-app/conf/Config.groovy"
    File configFile = new File(configPath)
    if (configFile.exists()) {
        configFile << "\n\n// Added by the MultiTenant plugin\n// TODO: Verify that this is correct\n"
        configFile << "${configLines}\n"

        ant.echo message: "--------------------- IMPORTANT ---------------------"
        ant.echo message: "I've added some lines to: ${configPath}"
        ant.echo message: "Open it and follow the instructions added to the bottom of file\n"
    } else {
        ant.echo message: "ERROR: Could not find: ${configPath}", level: "error"
        ant.echo message: "You'll have to create this file manually and add the following lines:", level: "error"
        ant.echo message: "${configLines}", level: "error"
    }
}

private parseArgs() {
    args = args ? args.split('\n') : []
    switch (args.size()) {
        case 4:
            ant.echo message: "Creating tenant classes in package ${args[0]}"
            return args
        default:
            ant.echo message: USAGE, level: "error"
            System.exit(1)
            break
    }
}

setDefaultTarget 'mtQuickstart'
