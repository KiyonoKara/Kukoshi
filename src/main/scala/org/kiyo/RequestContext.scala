package org.kiyo

import org.kiyo.Constants

import scala.annotation.targetName

/**
 * Created 2023/12/22
 * File RequestContext.scala
 */

/**
 * Request context for abstracted request functions
 * @param url                The URL
 * @param method             The request method
 * @param `sendsPayload?`    If the request has data to send
 * @param `receivesPayload?` If the response expects a payload
 * @param data               The data for the body
 * @param headers            Request headers
 * @param readTimeout        Request reading timeout
 * @param connectTimeout     Request connection timeout
 */
protected case class RequestContext(url: String,
                                    method: String = Constants.GET,
                                    @targetName("sendsPayload") `sendsPayload?`: Boolean = false,
                                    @targetName("receivesPayload") `receivesPayload?`: Boolean = true,
                                    data: String = new String(),
                                    headers: Iterable[(String, String)] = Iterable.empty,
                                    readTimeout: Int,
                                    connectTimeout: Int)