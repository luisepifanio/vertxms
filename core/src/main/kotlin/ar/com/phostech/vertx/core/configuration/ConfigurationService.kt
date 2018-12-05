package ar.com.phostech.vertx.core.configuration

import io.vertx.core.json.JsonObject

interface ConfigurationService {
    /**
     * Sets the property with the specified name to the specified value. Calling
     * this method would first trigger a PropertyChangeEvent that will
     * be dispatched to all VetoableChangeListeners. In case no complaints
     * (PropertyVetoException) have been received, the property will be actually
     * changed and a PropertyChangeEvent will be dispatched.
     *
     *
     * @param propertyName the name of the property to change.
     * @param property the new value of the specified property.
     * @throws ConfigPropertyVetoException in case the changed has been refused
     * by at least one propertychange listener.
     */
    fun setProperty(propertyName: String, property: Any): Boolean

    /**
     * Sets the property with the specified name to the specified value. Calling
     * this method would first trigger a PropertyChangeEvent that will
     * be dispatched to all VetoableChangeListeners. In case no complaints
     * (PropertyVetoException) have been received, the property will be actually
     * changed and a PropertyChangeEvent will be dispatched. This method also
     * allows the caller to specify whether or not the specified property is a
     * system one.
     *
     *
     * @param propertyName the name of the property to change.
     * @param property the new value of the specified property.
     * @param isSystem specifies whether or not the property being set is a
     * System property and should be resolved against the system property set.
     *
     * @throws ConfigPropertyVetoException in case the changed has been refused by
     * at least one propertychange listener.
     */
    fun setProperty(propertyName: String,
                    property: Any,
                    isSystem: Boolean): Boolean

    /**
     * Sets a set of specific properties to specific values as a batch operation
     * meaning that first `VetoableChangeListener`s are asked to
     * approve the modifications of the specified properties to the specified
     * values, then the modifications are performed if no complaints have been
     * raised in the form of `PropertyVetoException` and finally
     * `PropertyChangeListener`s are notified about the changes of
     * each of the specified properties. The batch operations allows the
     * `ConfigurationService` implementations to optimize, for
     * example, the saving of the configuration which in this case can be
     * performed only once for the setting of multiple properties.
     *
     * @param properties a [Map] of property names to their new values to
     * be set.
     * @throws ConfigPropertyVetoException if a change in at least one of the
     * properties has been refused by at least one of the [ ]s.
     */
    fun setProperties(properties: Map<String, Any>): Boolean

    /**
     * Returns the value of the property with the specified name or null if no
     * such property exists.
     * @param propertyName the name of the property that is being queried.
     * @return the value of the property with the specified name.
     */
    fun <V> getProperty(propertyName: String): V?

    /**
     * Removes the property with the specified name. Calling
     * this method would first trigger a PropertyChangeEvent that will
     * be dispatched to all VetoableChangeListeners. In case no complaints
     * (PropertyVetoException) have been received, the property will be actually
     * changed and a PropertyChangeEvent will be dispatched.
     * All properties with prefix propertyName will also be removed.
     *
     *
     * @param propertyName the name of the property to change.
     * @throws ConfigPropertyVetoException in case the changed has been refused by
     * at least one propertychange listener.
     */
    fun removeProperty(propertyName: String): Boolean

    /**
     * Returns a [List] of <tt>String</tt>s containing all
     * property names.
     *
     * @return a [List] containing all property names.
     */
    fun getAllPropertyNames(): List<String>

    /**
     * Returns a [List] of <tt>String</tt>s containing all property names
     * that have the specified prefix. The return value will include property
     * names that have prefixes longer than specified, unless the
     * <tt>exactPrefixMatch</tt> parameter is <tt>true</tt>.
     *
     * Example:
     *
     * Imagine a configuration service instance containing 2 properties only:<br></br>
     *
     * `
     * net.java.sip.communicator.PROP1=value1<br>
     * net.java.sip.communicator.service.protocol.PROP1=value2
     * `
     *
     * A call to this method with a prefix="net.java.sip.communicator" and
     * exactPrefixMatch=true would only return the first property -
     * net.java.sip.communicator.PROP1, whereas the same call with
     * exactPrefixMatch=false would return both properties as the second prefix
     * includes the requested prefix string.
     *
     * @param prefix a String containing the prefix (the non dotted non-caps
     * part of a property name) that we're looking for.
     * @param exactPrefixMatch a boolean indicating whether the returned
     * property names should all have a prefix that is an exact match of the
     * the <tt>prefix</tt> param or whether properties with prefixes that
     * contain it but are longer than it are also accepted.
     * @return a <tt>java.util.List</tt>containing all property name String-s
     * matching the specified conditions.
     */
    fun getPropertyNamesByPrefix(
        prefix: String,
        exactPrefixMatch: Boolean
    ): List<String>


    /**
     * Returns a <tt>List</tt> of <tt>String</tt>s containing the property names
     * that have the specified suffix. A suffix is considered to be everything
     * after the last dot in the property name.
     *
     * For example, imagine a configuration service instance containing two
     * properties only:
     *
     * `
     * net.java.sip.communicator.PROP1=value1<br>
     * net.java.sip.communicator.service.protocol.PROP1=value2
     * `
     *
     * A call to this method with <tt>suffix</tt> equal to "PROP1" will return
     * both properties, whereas the call with <tt>suffix</tt> equal to
     * "communicator.PROP1" or "PROP2" will return an empty <tt>List</tt>. Thus,
     * if the <tt>suffix</tt> argument contains a dot, nothing will be found.
     *
     *
     * @param suffix the suffix for the property names to be returned
     * @return a <tt>List</tt> of <tt>String</tt>s containing the property names
     * which contain the specified <tt>suffix</tt>
     */
    fun getPropertyNamesBySuffix(suffix: String): List<String>

    /**
     * Returns the String value of the specified property and null in case no
     * property value was mapped against the specified propertyName, or in
     * case the returned property string had zero length or contained
     * whitespaces only.
     *
     * @param propertyName the name of the property that is being queried.
     * @return the result of calling the property's toString method and null in
     * case there was no value mapped against the specified
     * <tt>propertyName</tt>, or the returned string had zero length or
     * contained whitespaces only.
     */
    fun getString(propertyName: String): String?

    /**
     * Returns the String value of the specified property and null in case no
     * property value was mapped against the specified propertyName, or in
     * case the returned property string had zero length or contained
     * whitespaces only.
     *
     * @param propertyName the name of the property that is being queried.
     * @param defaultValue the value to be returned if the specified property
     * name is not associated with a value in this
     * `ConfigurationService`
     * @return the result of calling the property's toString method and
     * `defaultValue` in case there was no value mapped against
     * the specified <tt>propertyName</tt>, or the returned string had zero
     * length or contained whitespaces only.
     */
    fun getString(propertyName: String, defaultValue: String): String

    /**
     * Gets the value of a specific property as a boolean. If the specified
     * property name is associated with a value in this
     * `ConfigurationService`, the string representation of the value
     * is parsed into a boolean according to the rules of
     * [Boolean.parseBoolean] . Otherwise,
     * `defaultValue` is returned.
     *
     * @param propertyName
     * the name of the property to get the value of as a boolean
     * @param defaultValue
     * the value to be returned if the specified property name is not
     * associated with a value in this
     * `ConfigurationService`
     * @return the value of the property with the specified name in this
     * `ConfigurationService` as a boolean;
     * `defaultValue` if the property with the specified name
     * is not associated with a value in this
     * `ConfigurationService`
     */
    fun getBoolean(propertyName: String, defaultValue: Boolean): Boolean

    /**
     * Gets the value of a specific property as a signed decimal integer. If the
     * specified property name is associated with a value in this
     * <tt>ConfigurationService</tt>, the string representation of the value is
     * parsed into a signed decimal integer according to the rules of
     * [Integer.parseInt] . If parsing the value as a signed
     * decimal integer fails or there is no value associated with the specified
     * property name, <tt>defaultValue</tt> is returned.
     *
     * @param propertyName the name of the property to get the value of as a
     * signed decimal integer
     * @param defaultValue the value to be returned if parsing the value of the
     * specified property name as a signed decimal integer fails or there is no
     * value associated with the specified property name in this
     * <tt>ConfigurationService</tt>
     * @return the value of the property with the specified name in this
     * <tt>ConfigurationService</tt> as a signed decimal integer;
     * <tt>defaultValue</tt> if parsing the value of the specified property name
     * fails or no value is associated in this <tt>ConfigurationService</tt>
     * with the specified property name
     */
    fun getInt(propertyName: String, defaultValue: Int): Int

    /**
     * Gets the value of a specific property as a double. If the specified
     * property name is associated with a value in this
     * <tt>ConfigurationService</tt>, the string representation of the value is
     * parsed into a double according to the rules of [ ][Double.parseDouble]. If there is no value, or parsing of the
     * value fails, <tt>defaultValue</tt> is returned.
     *
     * @param propertyName the name of the property.
     * @param defaultValue the default value to be returned.
     * @return the value of the property with the specified name in this
     * <tt>ConfigurationService</tt> as a double, or <tt>defaultValue</tt>.
     */
    fun getDouble(propertyName: String, defaultValue: Double): Double

    /**
     * Gets the value of a specific property as a signed decimal long integer.
     * If the specified property name is associated with a value in this
     * <tt>ConfigurationService</tt>, the string representation of the value is
     * parsed into a signed decimal long integer according to the rules of
     * [Long.parseLong] . If parsing the value as a signed
     * decimal long integer fails or there is no value associated with the
     * specified property name, <tt>defaultValue</tt> is returned.
     *
     * @param propertyName the name of the property to get the value of as a
     * signed decimal long integer
     * @param defaultValue the value to be returned if parsing the value of the
     * specified property name as a signed decimal long integer fails or there
     * is no value associated with the specified property name in this
     * <tt>ConfigurationService</tt>
     * @return the value of the property with the specified name in this
     * <tt>ConfigurationService</tt> as a signed decimal long integer;
     * <tt>defaultValue</tt> if parsing the value of the specified property name
     * fails or no value is associated in this <tt>ConfigurationService</tt>
     * with the specified property name
     */
    fun getLong(propertyName: String, defaultValue: Long): Long

    fun getJsonObject(propertyName: String): JsonObject?

    fun getJsonObject(propertyName: String, defaultValue: JsonObject): JsonObject

}
