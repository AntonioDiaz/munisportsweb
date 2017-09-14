package com.adiaz.daos;

import com.adiaz.entities.Issue;
import com.adiaz.entities.Match;

import java.util.List;

/**
 * Created by toni on 14/09/2017.
 */

public interface IssueDAO extends GenericDAO<Issue> {

	List<Issue> findByCompetition(Long competitionId);
	List<Issue> findByTown(Long townId);
	List<Issue> findAll();
	Issue findById(Long id);

}
