package br.com.oauth2.mongo.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Table(name = "EUSER")
public class User implements UserDetails{
	
	private static final long serialVersionUID = 6092895629054887881L;
	
	
	@Id
    @GeneratedValue( strategy = GenerationType.AUTO)
	private Long id;
	private String grantedAuthority;
	private String username;
	private String password;
	private boolean accountNonExpired;
	private boolean credentialsNonExpired;
	private boolean enabled;
	private boolean accountNonLocked;
	@Transient
	private transient  Function<String, String> passwordEncoder = password -> password;
	
	public User passwordEncoder(Function<String, String> encoder) {
		this.setPasswordEncoder(encoder);
		return this;
	}

	public String getGrantedAuthority() {
		return grantedAuthority;
	}

	public void setGrantedAuthority(String grantedAuthority) {
		this.grantedAuthority = grantedAuthority;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new LinkedList<>();
		Arrays.stream(grantedAuthority.split(",")).collect(Collectors.toList()).forEach(grante ->authorities.add(new SimpleGrantedAuthority(grante)));
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return this.username ;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public Function<String, String> getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(Function<String, String> passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}


}
