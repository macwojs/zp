package edu.agh.zp.controller;

import edu.agh.zp.classes.Event;
import edu.agh.zp.classes.Votes;
import edu.agh.zp.objects.*;
import edu.agh.zp.repositories.*;
import edu.agh.zp.services.CitizenService;
import edu.agh.zp.services.ParliamentarianService;
import edu.agh.zp.services.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping ( value = { "/wydarzenie" } )
public class EventController {


	@Autowired
	private VotingRepository vr;

	@Autowired
	private VoteService voteS;

	@Autowired
	private ParliamentarianService parlS;

	@Autowired
	private CitizenService cS;

	@Autowired
	private OptionSetRepository osR;

	@Autowired
	private OptionRepository oR;

	@Autowired
	private VoteRepository voteRepository;

	@Autowired
	private PoliticianRepository politicianRepository;

	@Autowired
	private ParliamentarianRepository parliamentarianRepository;


	@GetMapping ( value = "/kalendarz" )
	public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName("Calendar/kalendarz.html");
		modelAndView.addObject( "voting", parseVotingsToEvents( ) );

		return modelAndView;
	}

	@RequestMapping ( value = "/allevents", method = RequestMethod.GET )
	public List< Event > allEvents() {
		return parseVotingsToEvents( );
	}

	@GetMapping ( "/{num}" )
	public ModelAndView index( @PathVariable Long num ) {
		VotingEntity voting = vr.findByVotingID( num );
		if ( voting == null ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Voting not found");
		}
		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.setViewName("Calendar/wydarzenie.html");
		modelAndView.addObject( "voting", voting );
		//System.out.println("\n\n\n"+num+"\n\n\n");
		String link = "";
		if ( voting.getVotingType( ) == VotingEntity.TypeOfVoting.PREZYDENT ) {
			link = "/prezydent/vote/" + voting.getVotingID( );
		} else if ( voting.getVotingType( ) == VotingEntity.TypeOfVoting.REFERENDUM ) {
			link = "/obywatel/wyboryReferenda/" + voting.getVotingID( );
		} else if ( voting.getVotingType( ) == VotingEntity.TypeOfVoting.SEJM || voting.getVotingType( ) == VotingEntity.TypeOfVoting.SENAT ) {
			link = "/parlament/vote/" + voting.getVotingID( );
		}
		modelAndView.addObject( "link", link );
		Time time = java.sql.Time.valueOf( LocalTime.now( ) );
		Date date = java.sql.Date.valueOf( LocalDate.now( ) );
		boolean vs = ( voting.getVotingDate( ).equals( date ) && voting.getOpenVoting( ).before( time ) && voting.getCloseVoting( ).after( time ) );
		modelAndView.addObject( "visibility", vs );
		boolean ended = ( voting.getVotingDate( ).before( date ) || ( voting.getVotingDate( ).equals( date ) && voting.getCloseVoting( ).before( time ) ) );
		modelAndView.addObject( "ended", ended );
		boolean during = ( voting.getVotingDate( ).equals( date ) && voting.getOpenVoting( ).before( time ) && voting.getCloseVoting( ).after( time ) );
		modelAndView.addObject( "during", during );
		return modelAndView;
	}

	@GetMapping("/{num}/wyniki")
	public ModelAndView results(@PathVariable Long num) {
		VotingEntity voting = vr.findByVotingID(num);
		if(voting==null) {
			return new ModelAndView(String.valueOf(HttpStatus.NOT_FOUND));
		}
		Statistics stats = new Statistics();
		Chart pieChart = new Chart( "Rozkład głosów");
		List<Chart> multiChart = new ArrayList<>();
		List<String> politicalGroups = parlS.findPoliticalGroups();
		if(voting.getVotingType() == VotingEntity.TypeOfVoting.SEJM ||  voting.getVotingType() == VotingEntity.TypeOfVoting.SENAT  ) {
			for (String group : politicalGroups) {
				multiChart.add(new Chart(group));
			}
		}
		List< OptionSetEntity > tempOptions = osR.findAllBySetIDcolumn( voting.getSetID_column() );
		for( OptionSetEntity i : tempOptions ){
			Optional< OptionEntity > temp = oR.findByOptionID( i.getOptionID().getOptionID() );
			if(temp.isPresent()){
				OptionEntity option = temp.get(); // option
				if(voting.getVotingType() == VotingEntity.TypeOfVoting.SEJM ||  voting.getVotingType() == VotingEntity.TypeOfVoting.SENAT  ) {
					for (int j = 0; j < politicalGroups.size(); ++j) { // iterate through political groups to get information about votes in each of them
						long voteCount = voteS.findByVotingAndOptionAndPoliticalGroup(voting, option, politicalGroups.get(j));
						multiChart.get(j).data.add(new StatisticRecord(option.getOptionDescription(), voteCount));
					}
				}
				long voteCount = voteS.countByVotingAndOption(voting, option);
				pieChart.data.add(new StatisticRecord(option.getOptionDescription(), voteCount));
			}
		}
		ModelAndView modelAndView = new ModelAndView( );
		switch(voting.getVotingType()){
			case SEJM:
				stats = new Statistics(voteS.countAllByVoting(voting), parlS.countMemberOfSejm(), pieChart, VotingEntity.TypeOfVoting.SEJM);
				modelAndView.addObject("multichart",  multiChart);
				break;
			case SENAT:
				stats = new Statistics(voteS.countAllByVoting(voting), parlS.countMemberOfSenat(), pieChart, VotingEntity.TypeOfVoting.SENAT);
				modelAndView.addObject("multichart",  multiChart);
				break;
			case PREZYDENT:
			case REFERENDUM:
				stats = new Statistics(voteS.countAllByVoting(voting), cS.countEntitledToVote(), pieChart, VotingEntity.TypeOfVoting.REFERENDUM);
				break;
		}

		modelAndView.setViewName("Votings/votingResults.html");
		modelAndView.addObject("voting", voting);
		modelAndView.addObject("statistics",  stats);

		return modelAndView;
	}

	@GetMapping ( value = { "/{id}/votesList" } )
	public ModelAndView parlamentVotesList( @PathVariable long id, HttpServletRequest request ) {
//		Pagination control
		int page = 0;
		int size = 10;
		if ( request.getParameter( "page" ) != null && !request.getParameter( "page" ).isEmpty( ) ) {
			page = Integer.parseInt( request.getParameter( "page" ) ) - 1;
		}
		if ( request.getParameter( "size" ) != null && !request.getParameter( "size" ).isEmpty( ) ) {
			size = Integer.parseInt( request.getParameter( "size" ) );
		}


		List< VoteEntity > votes = voteRepository.findAllByVotingId( id );
		List< Votes > votesTh = new ArrayList<>( );
		for ( VoteEntity i : votes ) {
			CitizenEntity citizen = i.getCitizenID( );
			Optional< PoliticianEntity > politicianEntity = politicianRepository.findByCitizenID( i.getCitizenID( ) );
			long politicID = 0;
			String party = "-";
			if ( politicianEntity.isPresent( ) ) {
				politicID = politicianEntity.get( ).getPoliticianID( );
				try {
					ParliamentarianEntity parliamentarianEntity = parliamentarianRepository.findByPoliticianID( politicianEntity.get( ) );
					party = parliamentarianEntity.getPoliticalGroup( );
				} catch ( Exception e ) {
					e.printStackTrace( );
				}
			}

			votesTh.add( new Votes( citizen.getSurname( ), citizen.getName( ), party, i.getOptionID( ).getOptionDescription( ), politicID ) );
		}

//		Convert list to page
		PageRequest pageable = PageRequest.of( page, size );
		long start = pageable.getOffset( );
		long end = ( start + pageable.getPageSize( ) ) > votesTh.size( ) ? votesTh.size( ) : ( start + pageable.getPageSize( ) );
		Page< Votes > pageTh = new PageImpl<>( votesTh.subList( (int) start, (int) end ), pageable, votesTh.size( ) );

		ModelAndView modelAndView = new ModelAndView( );
		modelAndView.addObject( "votes", pageTh );
		modelAndView.addObject( "vote_id", id );
		modelAndView.setViewName( "Votings/votesList" );
		return modelAndView;
	}

//	ADDITIONAL METHODS
	public List< Event > parseVotingsToEvents() {
		List< VotingEntity > votings = vr.findAll( );
		List< Event > events = new ArrayList<>( );
		for ( VotingEntity i : votings ) {
			events.add( new Event( i.getVotingID( ), dateAndTimeToLocalDateTime( i.getVotingDate( ), i.getOpenVoting( ) ), dateAndTimeToLocalDateTime( i.getVotingDate( ), i.getCloseVoting( ) ), i.getDocumentID( ) != null ? i.getDocumentID( ).getDocName( ) : i.getVotingDescription( ) != null ? i.getVotingDescription( ) : i.getVotingType( ).toString( ), "/wydarzenie/" + i.getVotingID( ) ) );
		}
		return events;
	}

	public LocalDateTime dateAndTimeToLocalDateTime( Date date, Time time ) {
		String myDate = date + "T" + time;
		return LocalDateTime.parse( myDate );
	}

}



