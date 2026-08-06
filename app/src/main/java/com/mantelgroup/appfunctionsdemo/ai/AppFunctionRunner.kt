package com.mantelgroup.appfunctionsdemo.ai

import android.content.Context
import android.util.Log
import androidx.appfunctions.AppFunctionData
import androidx.appfunctions.AppFunctionManager
import androidx.appfunctions.AppFunctionSearchSpec
import androidx.appfunctions.ExecuteAppFunctionRequest
import androidx.appfunctions.ExecuteAppFunctionResponse
import androidx.appfunctions.metadata.AppFunctionBooleanTypeMetadata
import androidx.appfunctions.metadata.AppFunctionDoubleTypeMetadata
import androidx.appfunctions.metadata.AppFunctionFloatTypeMetadata
import androidx.appfunctions.metadata.AppFunctionIntTypeMetadata
import androidx.appfunctions.metadata.AppFunctionLongTypeMetadata
import androidx.appfunctions.metadata.AppFunctionMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.double
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

private const val TAG = "AppFunctionRunner"

class AppFunctionRunner @javax.inject.Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val packageName: String = context.packageName

    suspend fun execute(simpleName: String, args: Map<String, JsonElement>): String {
        val manager = AppFunctionManager.getInstance(context)
            ?: return "AppFunctionManager is not available on this device."

        val functions = discoverFunctions()
        Log.d(TAG, "Discovered ${functions.size} functions: ${functions.map { it.id }}")

        val metadata = functions
            .firstOrNull { it.id.substringAfterLast('#') == simpleName }
            ?: return "Function '$simpleName' was not found in the registry."

        Log.d(TAG, "Executing function '$simpleName' with args: $args")

        val request = ExecuteAppFunctionRequest(
            packageName,
            metadata.id,
            buildParameters(metadata, args),
        )

        return try {
            when (val response = manager.executeAppFunction(request)) {
                is ExecuteAppFunctionResponse.Success -> {
                    val result = readReturnValue(metadata, response.returnValue)
                    Log.d(TAG, "Function '$simpleName' returned: $result")
                    result
                }
                is ExecuteAppFunctionResponse.Error -> {
                    val error = "Error: ${response.error.errorMessage}"
                    Log.e(TAG, "Function '$simpleName' error: $error")
                    error
                }
            }
        } catch (e: Exception) {
            val error = "Exception: ${e.message}"
            Log.e(TAG, "Function '$simpleName' threw: $error", e)
            error
        }
    }

    private suspend fun discoverFunctions(): List<AppFunctionMetadata> {
        val manager = AppFunctionManager.getInstance(context) ?: return emptyList()
        val spec = AppFunctionSearchSpec(packageNames = setOf(packageName))
        return manager.searchAppFunctions(spec)
    }

    private fun buildParameters(metadata: AppFunctionMetadata, args: Map<String, JsonElement>): AppFunctionData {
        val builder = AppFunctionData.Builder(metadata.parameters, metadata.components)
        metadata.parameters.forEach { parameter ->
            val value = args[parameter.name]?.jsonPrimitive ?: return@forEach
            when (parameter.dataType) {
                is AppFunctionIntTypeMetadata -> builder.setInt(parameter.name, value.int)
                is AppFunctionLongTypeMetadata -> builder.setLong(parameter.name, value.long)
                is AppFunctionFloatTypeMetadata -> builder.setFloat(parameter.name, value.float)
                is AppFunctionDoubleTypeMetadata -> builder.setDouble(parameter.name, value.double)
                is AppFunctionBooleanTypeMetadata -> builder.setBoolean(parameter.name, value.boolean)
                else -> builder.setString(parameter.name, value.content)
            }
        }
        return builder.build()
    }

    private fun readReturnValue(metadata: AppFunctionMetadata, data: AppFunctionData): String {
        val key = ExecuteAppFunctionResponse.Success.PROPERTY_RETURN_VALUE
        return when (metadata.response.valueType) {
            is AppFunctionIntTypeMetadata -> data.getInt(key).toString()
            is AppFunctionLongTypeMetadata -> data.getLong(key).toString()
            is AppFunctionFloatTypeMetadata -> data.getFloat(key).toString()
            is AppFunctionDoubleTypeMetadata -> data.getDouble(key).toString()
            is AppFunctionBooleanTypeMetadata -> data.getBoolean(key).toString()
            else -> data.getString(key) ?: "Done."
        }
    }
}
