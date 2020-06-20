package edu.agh.zp.classes;

public class Votes {
	String surname;
	String name;
	String party;
	String voteValue;
	long politicID;

	public String getSurname() {
		return surname;
	}

	public void setSurname( String surname ) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getParty() {
		return party;
	}

	public void setParty( String party ) {
		this.party = party;
	}

	public String getVoteValue() {
		return voteValue;
	}

	public void setVoteValue( String voteValue ) {
		this.voteValue = voteValue;
	}

	public long getPoliticID() {
		return politicID;
	}

	public void setPoliticID( long politicID ) {
		this.politicID = politicID;
	}

	public Votes( String surname, String name, String party, String voteValue, long politicID ) {
		this.surname = surname;
		this.name = name;
		this.party = party;
		this.voteValue = voteValue;
		this.politicID = politicID;
	}
}
