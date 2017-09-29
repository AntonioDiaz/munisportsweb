package com.adiaz.services;

import com.adiaz.daos.SanctionsDAO;
import com.adiaz.entities.Sanction;
import com.adiaz.forms.SanctionForm;
import com.googlecode.objectify.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by toni on 28/09/2017.
 */
@Service("sanctionsManager")
public class SanctionsManagerImpl implements SanctionsManager {

	@Autowired
	SanctionsDAO sanctionsDAO;

	@Override
	public List<Sanction> querySanctionsByIdCompetition(Long idCompetition) {
		return sanctionsDAO.findByCompetitionId(idCompetition);
	}

	@Override
	public void add(SanctionForm sanctionForm) throws Exception {
		sanctionsDAO.create(sanctionForm.convertToEntity());
	}

	@Override
	public boolean remove(Long id) throws Exception {
		Sanction sanction = new Sanction();
		sanction.setId(id);
		return sanctionsDAO.remove(sanction);
	}
}
