package ar.com.phostech.demo.usecases.services.insights.presenter

import ar.com.phostech.demo.entities.InsigthsCredentials
import ar.com.phostech.functional.Callback
import ar.com.phostech.vertx.response.Response

interface ShowInsightsCredentialsOutputBoundaries : Callback<Response<InsigthsCredentials>>
