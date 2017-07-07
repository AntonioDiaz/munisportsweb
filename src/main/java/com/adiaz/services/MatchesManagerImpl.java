package com.adiaz.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adiaz.daos.MatchesDAO;
import com.adiaz.entities.MatchesVO;


@Service ("matchesManager")
public class MatchesManagerImpl implements MatchesManager {

	@Autowired
	MatchesDAO matchesDAO;
	
	@Override
	public void add(MatchesVO item) throws Exception {
		matchesDAO.create(item);
	}

	@Override
	public boolean remove(MatchesVO item) throws Exception {
		return matchesDAO.remove(item);
	}

	@Override
	public boolean update(MatchesVO item) throws Exception {
		return matchesDAO.update(item);
	}

	@Override
	public List<MatchesVO> queryMatchesByCompetition(Long competitionId) {
		return matchesDAO.queryMatchesByCompetition(competitionId);
	}

	@Override
	public void add(List<MatchesVO> matchesList) throws Exception {
		for (MatchesVO matchesVO : matchesList) {
			this.add(matchesVO);
		}
	}

	@Override
	public List<MatchesVO> queryMatches() {
		return matchesDAO.queryAllMatches();
	}

	@Override
	public void removeAll() throws Exception {
		List<MatchesVO> queryAllMatches = matchesDAO.queryAllMatches();
		for (MatchesVO matchesVO : queryAllMatches) {
			matchesDAO.remove(matchesVO);
		}		
	}
}