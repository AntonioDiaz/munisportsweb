package com.adiaz.controllers;

import static com.adiaz.utils.UtilsLegaSport.getActiveUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.adiaz.entities.*;
import com.adiaz.forms.TeamFilterForm;
import com.adiaz.services.*;
import com.adiaz.utils.ConstantsLegaSport;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.adiaz.entities.Sport;

@RestController
@RequestMapping("server")
public class RESTController {

	@Autowired
	SportsManager sportsManager;
	@Autowired
	CategoriesManager categoriesManager;
	@Autowired
	CompetitionsManager competitionsManager;
	@Autowired
	MatchesManager matchesManager;
	@Autowired
	ClassificationManager classificationManager;
	@Autowired
	SportCenterCourtManager sportCenterCourtManager;
	@Autowired
	TeamManager teamManager;


	private static final Logger logger = Logger.getLogger(RESTController.class);

	@RequestMapping(value = "/sports/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Sport> getSportsById(@PathVariable("id") long id) {
		ResponseEntity<Sport> response;
		Sport sport = sportsManager.querySportsById(id);
		if (sport == null) {
			response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			response = new ResponseEntity<>(sport, HttpStatus.OK);
		}
		return response;
	}

	@RequestMapping(value = "/sports_name/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Sport> getSportsById(@PathVariable("name") String name) {
		ResponseEntity<Sport> response;
		Sport sport = sportsManager.querySportsByName(name);
		if (sport == null) {
			response = new ResponseEntity<Sport>(HttpStatus.NOT_FOUND);
		} else {
			response = new ResponseEntity<Sport>(sport, HttpStatus.OK);
		}
		return response;
	}

	@RequestMapping(value = "/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Category> getCategories() {
		List<Category> queryCategories = categoriesManager.queryCategories();
		return queryCategories;
	}

	@RequestMapping(value = "/competitions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Competition> competitions() {
		logger.debug("*competitions");
		List<Competition> competitions = competitionsManager.queryCompetitions();
		for (Competition competition : competitions) {
			List<Match> matchesList = matchesManager.queryMatchesByCompetitionPublished(competition.getId());
			competition.setMatches(matchesList);
			List<ClassificationEntry> classification = classificationManager.queryClassificationBySport(competition.getId());
			competition.setClassification(classification);
		}
		return competitions;
	}

	@RequestMapping(value = "/search_competitions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Competition> searchCompetitions(
			@RequestParam(value = "idSport", required = false) Long idSport,
			@RequestParam(value = "idCategory", required = false) Long idCategory,
			@RequestParam(value = "idTown", required = false) Long idTown) {
		if (!getActiveUser().isAdmin()) {
			idTown = getActiveUser().getTownEntity().getId();
		}
		List<Competition> competitions = competitionsManager.queryCompetitions(idSport, idCategory, idTown);
		return competitions;
	}

	@RequestMapping(value = "/matches", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Match> getMatches() {
		List<Match> matches = matchesManager.queryMatches();
		return matches;
	}

	@RequestMapping(value = "/matches/{competition_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Match> getMatches(@PathVariable("competition_id") Long competitionId) {
		List<Match> matches = matchesManager.queryMatchesByCompetitionWorkingCopy(competitionId);
		return matches;
	}

	@RequestMapping(value = "/sports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Sport> listSports() {
		List<Sport> sportsList = sportsManager.querySports();
		return sportsList;
	}

	@RequestMapping(value = "/match/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Match> getMatch(@PathVariable("id") Long id) {
		Match match = matchesManager.queryMatchesById(id);
		if (match == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(match, HttpStatus.OK);
	}

	// TODO: 10/07/2017 IMPORTAN protect this call in production environment. 
	@RequestMapping(value = "/match/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Match> updateMatchScore(@PathVariable("id") Long id, @RequestBody Match newMatchVO) {
		Match match = matchesManager.queryMatchesById(id);
		if (match == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		match.setScoreLocal(newMatchVO.getScoreLocal());
		match.setScoreVisitor(newMatchVO.getScoreVisitor());
		try {
			match.setDate(null);
			if (StringUtils.isNotBlank(newMatchVO.getDateStr())) {
				match.setDateStr(newMatchVO.getDateStr());
				DateFormat dateFormat = new SimpleDateFormat(ConstantsLegaSport.DATE_FORMAT);
				match.setDate(dateFormat.parse(newMatchVO.getDateStr()));
			}
			match.setSportCenterCourtRef(null);
			if (newMatchVO.getCourtId()!=null) {
				Key<SportCenterCourt> key = Key.create(SportCenterCourt.class, newMatchVO.getCourtId());
				Ref<SportCenterCourt> sportCenterCourtRef = Ref.create(key);
				match.setSportCenterCourtRef(sportCenterCourtRef);
			}
			matchesManager.update(match);
		} catch (Exception e) {
			logger.error(e.getMessage() , e);
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		}
		return new ResponseEntity<>(match, HttpStatus.OK);
	}

	@RequestMapping(value = "/courts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SportCenterCourt> courts(
			@RequestParam(value = "idTown") Long idTown,
			@RequestParam(value = "idSport") Long idSport) {
		return sportCenterCourtManager.querySportCourtsByTownAndSport(idTown, idSport);
	}

	@RequestMapping(value = "/teams", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Team> teams(
			@RequestParam(value = "idTown") Long idTown,
			@RequestParam(value = "idSport") Long idSport,
			@RequestParam(value = "idCategory") Long idCategory) {
		TeamFilterForm teamFilterForm = new TeamFilterForm(idTown, idSport, idCategory);
		return teamManager.queryByFilter(teamFilterForm);
	}
}