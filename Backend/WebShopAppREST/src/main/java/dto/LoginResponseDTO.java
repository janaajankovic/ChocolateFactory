package dto;

public class LoginResponseDTO {

	private String username;
	private String role;
	private String token;
	public LoginResponseDTO(String username, String role, String token) {
		super();
		this.username = username;
		this.role = role;
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
}
