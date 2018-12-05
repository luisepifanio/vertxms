package ar.com.phostech.vertx.core.configuration

import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

// TODO: Provide a dynamic configuration service based on service

class InMemoryConfigurationService(
    systemMap: Map<String, String> = HashMap(),
    readOnlyProperties: Map<String, String> = HashMap()
) : ConfigurationService {

    private val log = LoggerFactory.getLogger(InMemoryConfigurationService::class.java)

    private val defaultProperties = HashMap<String, Any>()
    private val immutableDefaultProperties: Map<String, Any>
    private val systemPropertyNames = HashSet<String>()

    init {
        //TODO: Instatiate system map here
        immutableDefaultProperties = Collections.unmodifiableMap(readOnlyProperties)
        systemMap.forEach { key, value -> setProperty(key, value, true) }
    }

    override fun setProperty(propertyName: String, property: Any): Boolean {
        return if (property is JsonObject) setJsonProperty(propertyName, property, false)
        else setProperty(propertyName, property, false)
    }

    private fun setJsonProperty(propertyName: String, propertyValue: JsonObject, isSystem: Boolean): Boolean {
        log.info("Trying to store JsonObject")
        return this.setProperty(propertyName, propertyValue.encode(), isSystem)
    }

    override fun setProperty(propertyName: String, propertyValue: Any, isSystem: Boolean): Boolean {
        val oldValue = getProperty<Any>(propertyName)
        if (null != oldValue && oldValue.equals(propertyValue)) {
            log.info("Abortig change of property '{0}' from {1} to {2}", propertyName, oldValue, propertyValue)
            return false
        }
        val returnValue = if (propertyValue is JsonObject) commitPropertyWrite(propertyName, propertyValue.encode(), isSystem)
        else commitPropertyWrite(propertyName, propertyValue, isSystem)

        log.info("Property {0} changed from {1} to {2}", propertyName, oldValue, propertyValue)
        return returnValue
    }

    private fun commitPropertyWrite(propertyName: String, propertyValue: Any?, system: Boolean): Boolean {
        //once set system, a property remains system even if the user
        //specified sth else
        var isSystem = if (isSystemProperty(propertyName)) true else system

        // ignore requests to override immutable properties:
        if (immutableDefaultProperties.containsKey(propertyName)) {
            return false
        }

        if (propertyValue == null) { // we are deleting a property
            defaultProperties.remove(propertyName)

            if (isSystem) {
                //we can't remove or nullset a sys prop so let's "empty" it.
                System.setProperty(propertyName, "")
            }
        } else {
            if (isSystem) {
                //in case this is a system property, we must only store it
                //in the System property set and keep only a ref locally.
                System.setProperty(propertyName, propertyValue.toString())
                systemPropertyNames.add(propertyName)
            }
            defaultProperties[propertyName] = propertyValue
        }
        return true
    }

    private fun isSystemProperty(propertyName: String): Boolean {
        return systemPropertyNames.contains(propertyName) || System.getProperties().keys.any { any: Any? -> propertyName.equals(any) }
    }

    override fun setProperties(properties: Map<String, Any>): Boolean {
        val result = ArrayList<Boolean>()
        properties.forEach { s, any ->
            result.add(setProperty(s, any, false))
        }
        return result.all { it }
    }

    override fun <V> getProperty(propertyName: String): V? {
        var result = immutableDefaultProperties[propertyName]

        if (result != null) {
            return result as? V
        }

        result = defaultProperties[propertyName]

        return if (result != null) {
            result as? V
        } else {
            System.getProperty(propertyName) as? V
        }
    }

    override fun removeProperty(propertyName: String): Boolean {
        return commitPropertyWrite(propertyName, null, false)
    }

    override fun getAllPropertyNames(): List<String> {
        val propertyNames = defaultProperties.keys
        propertyNames.addAll(immutableDefaultProperties.keys)
        propertyNames.addAll(System.getProperties().stringPropertyNames())

        return ArrayList(propertyNames)
    }

    override fun getPropertyNamesByPrefix(prefix: String, exactPrefixMatch: Boolean): List<String> {
        val oo = getAllPropertyNames().filter { it.startsWith(prefix) }
        return if (exactPrefixMatch && oo.isNotEmpty()) oo.subList(0, 1) else oo
    }

    override fun getPropertyNamesBySuffix(suffix: String): List<String> {
        return getAllPropertyNames().filter { it.endsWith(suffix) }
    }

    override fun getString(propertyName: String): String? {
        return getProperty(propertyName)
    }

    override fun getString(propertyName: String, defaultValue: String): String {
        return getBoxed(propertyName, defaultValue)
    }

    fun <V> getBoxed(propertyName: String, defaultValue: V): V {
        val nullable = getProperty(propertyName) as V?

        return Optional.ofNullable(nullable)
            .orElseGet {
                commitPropertyWrite(propertyName, defaultValue, false)
                defaultValue
            }
    }

    override fun getBoolean(propertyName: String, defaultValue: Boolean): Boolean {
        return getBoxed(propertyName, defaultValue)
    }

    override fun getInt(propertyName: String, defaultValue: Int): Int {
        return getBoxed(propertyName, defaultValue)
    }

    override fun getDouble(propertyName: String, defaultValue: Double): Double {
        return getBoxed(propertyName, defaultValue)
    }

    override fun getLong(propertyName: String, defaultValue: Long): Long {
        return getBoxed(propertyName, defaultValue)
    }

    override fun getJsonObject(propertyName: String, defaultValue: JsonObject): JsonObject { //JsonValues are stored as String and back then
        val nullable: String = getString(propertyName, defaultValue.encode())
        return JsonObject(nullable)
    }

    override fun getJsonObject(propertyName: String): JsonObject? {
        val nullable = getString(propertyName)
        return if (null != nullable) JsonObject(nullable) else null
    }

    fun debugPrintSystemProperties() {
        defaultProperties.isNotEmpty() && log.info("DEFAULT PROPERTIES").equals(null)
        defaultProperties.forEach { key, value -> log.info("$key=$value") }
        immutableDefaultProperties.isNotEmpty() && log.info("READONLY PROPERTIES").equals(null)
        immutableDefaultProperties.forEach { key, value -> log.info("$key=$value") }
        systemPropertyNames.isNotEmpty() && log.info("SYSTEM PROPERTIES").equals(null)
        systemPropertyNames.forEach { log.info("$it=${System.getProperty(it)}") }
    }
}
