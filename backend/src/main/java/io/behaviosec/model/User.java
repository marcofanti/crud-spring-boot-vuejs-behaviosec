package io.behaviosec.model;

import javax.persistence.*;

@Entity
@Table(name = "usertable")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "email")
	private String email;

	@Column(name = "trainingphrase")
	private String trainingPhrase;

	@Column(name = "repetitions")
	private int repetitions;

	public User() {

	}

	public User(String email, String trainingPhrase, int repetitions) {
		this.email = email;
		this.trainingPhrase = trainingPhrase;
		this.repetitions = repetitions;
	}

	public long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTrainingPhrase() {
		return trainingPhrase;
	}

	public void setTrainingPhrase(String trainingPhrase) {
		this.trainingPhrase = trainingPhrase;
	}

	public int getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(int repetitions) {
		this.repetitions = repetitions;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", email='" + email + '\'' +
				", trainingPhrase='" + trainingPhrase + '\'' +
				", repetitions=" + repetitions +
				'}';
	}
}
