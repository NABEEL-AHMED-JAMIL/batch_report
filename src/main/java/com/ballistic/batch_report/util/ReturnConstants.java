package com.ballistic.batch_report.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ReturnConstants {

	public Integer ReturnCodeInsert = 1000;
	public Integer ReturnCodeGet = 1001;
	public Integer ReturnCodeUpdate = 1002;
	public Integer ReturnCodeDelete = 1003;
	public Integer ReturnCodeFailure = 1005;
	public Integer ReturnCodeNoDataFound = 1006;

	// Messages
	public String API_ACCESS_DENIED = "API Access Denied";
	public String INVALID_INFO = "Invalid information";
	public String TECHNICAL_ISSUE = "There is a technical issue. Please try again later." + "If you get the same issue again then report the issue with detail at support@ballistic.com";

}