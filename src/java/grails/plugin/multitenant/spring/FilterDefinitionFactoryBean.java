/*
 * Copyright 2002-2013 the original author or authors.
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
 */

package grails.plugin.multitenant.spring;

import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Convenient FactoryBean for defining Hibernate FilterDefinitions.
 * Exposes a corresponding Hibernate FilterDefinition object.
 * <p>
 * <p>Typically defined as an inner bean within a LocalSessionFactoryBean
 * definition, as the list element for the "filterDefinitions" bean property.
 * For example:
 * <p>
 * <pre class="code">
 * &lt;bean id="sessionFactory" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean"&gt;
 * ...
 * &lt;property name="filterDefinitions"&gt;
 * &lt;list&gt;
 * &lt;bean class="grails.plugin.multitenant.spring.FilterDefinitionFactoryBean"&gt;
 * &lt;property name="filterName" value="myFilter"/&gt;
 * &lt;property name="parameterTypes"&gt;
 * &lt;map&gt;
 * &lt;entry key="myParam" value="string"/&gt;
 * &lt;entry key="myOtherParam" value="long"/&gt;
 * &lt;/map&gt;
 * &lt;/property&gt;
 * &lt;/bean&gt;
 * &lt;/list&gt;
 * &lt;/property&gt;
 * ...
 * &lt;/bean&gt;</pre>
 * <p>
 * Alternatively, specify a bean id (or name) attribute for the inner bean,
 * instead of the "filterName" property.
 *
 * @author Juergen Hoeller
 * @see org.hibernate.engine.spi.FilterDefinition
 * @see LocalSessionFactoryBean#setFilterDefinitions
 * @since 1.2
 */
public class FilterDefinitionFactoryBean implements FactoryBean<FilterDefinition>, BeanNameAware, InitializingBean {

    private final TypeResolver typeResolver = new TypeResolver();

    private String filterName;

    private Map<String, Type> parameterTypeMap = new HashMap<String, Type>();

    private String defaultFilterCondition;

    private FilterDefinition filterDefinition;


    /**
     * Set the name of the filter.
     */
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    /**
     * Set the parameter types for the filter,
     * with parameter names as keys and type names as values.
     * See {@code org.hibernate.type.TypeResolver#heuristicType(String)}.
     */
    public void setParameterTypes(Map<String, String> parameterTypes) {
        if (parameterTypes != null) {
            this.parameterTypeMap = new HashMap<String, Type>(parameterTypes.size());
            for (Map.Entry<String, String> entry : parameterTypes.entrySet()) {
                this.parameterTypeMap.put(entry.getKey(), this.typeResolver.heuristicType(entry.getValue()));
            }
        } else {
            this.parameterTypeMap = new HashMap<String, Type>();
        }
    }

    /**
     * Specify a default filter condition for the filter, if any.
     */
    public void setDefaultFilterCondition(String defaultFilterCondition) {
        this.defaultFilterCondition = defaultFilterCondition;
    }

    /**
     * If no explicit filter name has been specified, the bean name of
     * the FilterDefinitionFactoryBean will be used.
     *
     * @see #setFilterName
     */
    @Override
    public void setBeanName(String name) {
        if (this.filterName == null) {
            this.filterName = name;
        }
    }

    @Override
    public void afterPropertiesSet() {
        this.filterDefinition =
                new FilterDefinition(this.filterName, this.defaultFilterCondition, this.parameterTypeMap);
    }


    @Override
    public FilterDefinition getObject() {
        return this.filterDefinition;
    }

    @Override
    public Class<FilterDefinition> getObjectType() {
        return FilterDefinition.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
