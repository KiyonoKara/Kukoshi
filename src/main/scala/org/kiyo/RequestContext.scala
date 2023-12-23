package org.kiyo

import Utility.Constants

/**
 * Created 2023/12/22
 * File RequestContext.scala
 */

/**
 * Request context for abstracted request functions
 * @param url            The URL
 * @param method         The request method
 * @param hasBody        Whether request has a body to send
 * @param data           The data for the body
 * @param headers        Request headers
 * @param readTimeout    Request reading timeout
 * @param connectTimeout Request connection timeout
 */
protected case class RequestContext(url: String,
                                    method: String = Constants.GET,
                                    hasBody: Boolean = true,
                                    data: String = new String(),
                                    headers: Iterable[(String, String)] = Iterable.empty[(String, String)],
                                    readTimeout: Int,
                                    connectTimeout: Int)