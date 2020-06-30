package edu.agh.zp.objects;

import edu.agh.zp.classes.TimeProvider;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;


@Entity
public class Log{
    public enum Status{ SUCCESS, FAILURE}
    public enum Operation{ ADD, EDIT, LOGIN}
    public enum ElementType{ DOCUMENT, VOTING, VOTE, USER }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name= "logid")
    private long id;

    @Column(name= "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "operation")
    private String operation;

    @Column(name = "logdescription", nullable = false)
    @NotEmpty
    private String logDescription;

    @Column(name= "elementType")
    private String elementType;

    @Column(name= "elementID")
    private long elementID;

    @ManyToOne
    @JoinColumn(name="citizenID")
    private CitizenEntity user;

    private String status;

    public Log(){}


    // constructor for all accepted operations on existing element, performed by signed in users (or accepted registration or sign in)
    public Log(Operation operation,
               @NotEmpty String logDescription,
               ElementType elementType,
               long elementID,
               CitizenEntity user,
               Status status) {
        this.operation = operation.toString();
        this.logDescription = logDescription;
        this.elementType = elementType.toString();
        this.elementID = elementID;
        this.user = user;
        this.status = status.toString();
        this.time = TimeProvider.now();
    }

    // failed sign in or sign up
    public Log(Operation operation, @NotEmpty String logDescription, ElementType elementType, Status status) {
        this.operation = operation.toString();
        this.logDescription = logDescription;
        this.elementType = elementType.toString();
        this.user = null;
        this.status = status.toString();
        this.time = TimeProvider.now();
    }

    public Log(Operation operation, @NotEmpty String logDescription, ElementType elementType, CitizenEntity user, Status status) {
        this.operation = operation.toString();
        this.logDescription = logDescription;
        this.elementType = elementType.toString();
        this.user = user;
        this.status = status.toString();
        this.time = TimeProvider.now();
    }

    static public Log failedSignInOrSignUp(Operation operation, @NotEmpty String logDescription){
        return new Log(operation, logDescription, ElementType.USER, Status.FAILURE);
    }

    static public Log successSignInOrSignUp(Operation operation, @NotEmpty String logDescription, CitizenEntity user){
        return new Log(operation, logDescription, ElementType.USER, user.getCitizenID(), user, Status.SUCCESS);
    }

    static public Log successAddVoting(@NotEmpty String logDescription, VotingEntity voting, CitizenEntity user){
        return new Log(Operation.ADD, logDescription, ElementType.VOTING, voting.getVotingID(), user, Status.SUCCESS);
    }

    static public Log failedAddVoting(@NotEmpty String logDescription, CitizenEntity user){
        return new Log(Operation.ADD, logDescription, ElementType.VOTING, user, Status.FAILURE);
    }

    static public Log successEditVoting(@NotEmpty String logDescription, VotingEntity voting, CitizenEntity user){
        return new Log(Operation.EDIT, logDescription, ElementType.VOTING, voting.getVotingID(), user, Status.SUCCESS);
    }

    static public Log failedEditVoting(@NotEmpty String logDescription,  VotingEntity voting, CitizenEntity user){
        return new Log(Operation.EDIT, logDescription, ElementType.VOTING, voting.getVotingID(), user, Status.FAILURE);
    }

    static public Log successAddVoteParlam(@NotEmpty String logDescription, VoteEntity vote){
        return new Log(Operation.ADD, logDescription, ElementType.VOTE, vote.getVoteID(), vote.getCitizenID(), Status.SUCCESS);
    }

    static public Log failedAddVoteParlam(@NotEmpty String logDescription, CitizenEntity user){
        return new Log(Operation.ADD, logDescription, ElementType.VOTE, user, Status.FAILURE);
    }

    static public Log successAddVoteCitizen(@NotEmpty String logDescription, VotingEntity voting,  CitizenEntity user){
        return new Log(Operation.ADD, logDescription, ElementType.VOTING, voting.getVotingID(), user, Status.SUCCESS);
    }

    static public Log failedAddVoteCitizen(@NotEmpty String logDescription, VotingEntity voting, CitizenEntity user){
        return new Log(Operation.ADD, logDescription, ElementType.VOTING,  voting.getVotingID(), user, Status.FAILURE);
    }

    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getLogDescription() {
        return logDescription;
    }

    public void setLogDescription(String logDescription) {
        this.logDescription = logDescription;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public long getElementID() {
        return elementID;
    }

    public void setElementID(long elementID) {
        this.elementID = elementID;
    }

    public CitizenEntity getUser() {
        return user;
    }

    public void setUser(CitizenEntity user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}