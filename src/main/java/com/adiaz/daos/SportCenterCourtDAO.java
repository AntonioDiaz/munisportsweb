package com.adiaz.daos;

import java.util.List;

import com.adiaz.entities.Sport;
import com.adiaz.entities.SportCenter;
import com.adiaz.entities.SportCenterCourt;
import com.adiaz.entities.Town;
import com.googlecode.objectify.Ref;

public interface SportCenterCourtDAO extends GenericDAO<SportCenterCourt> {
	
	public Ref<SportCenterCourt> createReturnRef(SportCenterCourt item) throws Exception;
	public List<SportCenterCourt> findAllSportCourt();
	public SportCenterCourt findBySportCenter(Long idCourt);
	public List<SportCenterCourt> findBySportCenter(Ref<SportCenter> sportCenterRef);
}