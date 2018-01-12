package co.com.m4h.seido.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.com.m4h.seido.model.security.Authority;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user", schema = "public")
public class User extends AbstractEntity {

	// @Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	// @Column(name = "ID")
	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	// @SequenceGenerator(name = "user_seq", sequenceName = "user_seq",
	// allocationSize = 1)
	// private Long id;

	// @Column(name = "USERNAME", length = 50, unique = true)
	// @NotNull
	// @Size(min = 4, max = 50)
	private String username;

	// @Column(name = "PASSWORD", length = 100)
	// @NotNull
	// @Size(min = 4, max = 100)
	private String password;

	// @Column(name = "FIRSTNAME", length = 50)
	// @NotNull
	// @Size(min = 4, max = 50)
	private String firstname;

	// @Column(name = "LASTNAME", length = 50)
	// @NotNull
	// @Size(min = 4, max = 50)
	private String lastname;

	// @Column(name = "EMAIL", length = 50)
	// @NotNull
	// @Size(min = 4, max = 50)
	private String email;

	// @Column(name = "ENABLED")
	// @NotNull
	private int enabled;

	@Column(name = "LASTPASSWORDRESETDATE")
	@Temporal(TemporalType.TIMESTAMP)
	// @NotNull
	@JsonIgnore
	private Date lastPasswordResetDate;

	@ManyToOne // (fetch = FetchType.EAGER)
	// @JoinColumn(name = "authority_id")
	private Authority authority;

	@ManyToOne // (fetch = FetchType.EAGER)
	// @JoinColumn(name = "company_id")
	private Company company;
}