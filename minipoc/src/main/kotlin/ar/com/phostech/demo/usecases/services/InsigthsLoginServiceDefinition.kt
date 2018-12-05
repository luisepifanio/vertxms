package ar.com.phostech.demo.usecases.services

import ar.com.phostech.demo.entities.InsigthsCredentials
import ar.com.phostech.functional.Callback
import ar.com.phostech.vertx.response.Response

interface InsigthsLoginServiceDefinition {

    fun obtainCredentials(callback: Callback<Response<InsigthsCredentials>>)

}
