package com.icfolson.aem.groovy.console.response

import com.icfolson.aem.groovy.console.audit.AuditRecord
import com.icfolson.aem.groovy.console.table.Table
import groovy.json.JsonBuilder
import groovy.transform.Immutable
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.sling.jcr.resource.JcrPropertyMap

import javax.jcr.Node

import static com.icfolson.aem.groovy.console.audit.AuditRecord.PROPERTY_EXCEPTION_STACK_TRACE
import static com.icfolson.aem.groovy.console.audit.AuditRecord.PROPERTY_SCRIPT

@Immutable
class RunScriptResponse {

    static RunScriptResponse fromResult(String script, Object result, String output, String runningTime) {
        def resultString

        if (result instanceof Table) {
            resultString = new JsonBuilder([table: result]).toString()
        } else {
            resultString = result as String
        }

        new RunScriptResponse(script, resultString, output, "", runningTime)
    }

    static RunScriptResponse fromException(String script, Throwable throwable) {
        def exceptionStackTrace = ExceptionUtils.getStackTrace(throwable)

        new RunScriptResponse(script, "", "", exceptionStackTrace, "")
    }

    static RunScriptResponse fromAuditRecordNode(Node node) {
        def properties = new JcrPropertyMap(node)

        def script = properties.get(PROPERTY_SCRIPT, "")
        def exceptionStackTrace = properties.get(PROPERTY_EXCEPTION_STACK_TRACE, "")

        def response

        if (exceptionStackTrace) {
            response = new RunScriptResponse(script, "", "", exceptionStackTrace, "")
        } else {
            def result = properties.get(AuditRecord.PROPERTY_RESULT, "")
            def output = properties.get(AuditRecord.PROPERTY_OUTPUT, "")
            def runningTime = properties.get(AuditRecord.PROPERTY_RUNNING_TIME, "")

            response = new RunScriptResponse(script, result, output, "", runningTime)
        }

        response
    }

    String script

    String result

    String output

    String exceptionStackTrace

    String runningTime
}
