package edu.depaul.secmail;

//note: enums are automatically serializable in java.
public enum Command {
	CLOSE, 
	GET_NOTIFICATION, 
	END_NOTIFICATION, 
	SEND_NOTIFICATION, 
	LOGIN, 
	PASSWORD, 
	SEND_EMAIL, 
	RECEIVE_EMAIL, 
	ERROR, 
	CONNECT_TEST, 
	CONNECT_SUCCESS,
	LOGIN_SUCCESS,
	LOGIN_FAIL
}
