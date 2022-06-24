package es.uv.adiez.domain;


public class User {

    //private String id;
    private String email;
    private String password;
    private String nif;
    private String name;
    private UserType userType;
    private PersonType personType;
    private Status status;
    private String[] roles;
    private int quantity;
    
    
	public User() {}
    
	/*public User(String id, String email, String password, String nif, String name, PersonType type, String[] roles) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.nif = nif;
		this.name = name;
		this.personType = type;
		this.roles = roles;
	}*/
	
	public User(String email, String password,String nif, String name, PersonType type, int quantity, String[] roles) {
		this.email = email;
		this.password = password;
		this.nif = nif;
		this.name = name;
		this.personType = type;
		this.roles = roles;
		this.quantity = quantity;
	}

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType type) {
		this.userType = type;
	}
	public PersonType getPersonType() {
		return personType;
	}

	public void setPersonType(PersonType type) {
		this.personType = type;
	}
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status s) {
		this.status = s;
	}

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String[] getRoles() {
		String[] roles = new String[] {};
		if(this.userType == UserType.P) roles = new String[] {"ROLE_USER"};
		else if(this.userType == UserType.V) roles = new String[] {"ROLE_ADMIN"};
		
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}
	
	public AuthenticatedUser toAuthUser() {
		this.roles = getRoles();
    	return new AuthenticatedUser(this.email, this.password, this.roles);
    }
}


