package server;

import java.io.Serializable;

/**
 * Created by Gia Bao Nguyen on 12/6/2017.
 */
public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String userName;
	String password;
	public UserInfo(){
		userName = "Nguyen Gia Bao";
		password = "ABCZZ";
	}

	public UserInfo(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}
}
