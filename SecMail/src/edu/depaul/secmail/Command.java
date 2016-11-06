package edu.depaul.secmail;

//note: enums are automatically serializable in java.
public enum Command {
	CLOSE, GET_NOTIFICATION, SEND_NOTIFICATION, LOGIN, PASSWORD, EMAIL, ERROR, CONNECT_TEST, CONNECT_SUCCESS
}
