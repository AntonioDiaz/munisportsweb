package com.adiaz.forms.utils;

import com.adiaz.entities.Match;
import com.adiaz.entities.SportCenterCourt;
import com.adiaz.entities.Team;
import com.adiaz.forms.MatchForm;
import com.adiaz.utils.ConstantsLegaSport;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Created by toni on 31/07/2017.
 */
@Repository
public class MatchFormUtils implements GenericFormUtils<MatchForm, Match> {

	public void formToEntity(Match match, MatchForm matchForm) throws Exception {
		match.setScoreLocal(matchForm.getScoreLocal());
		match.setScoreVisitor(matchForm.getScoreVisitor());
		match.setDate(null);
		if (StringUtils.isNotBlank(matchForm.getDateStr())) {
			DateFormat dateFormat = new SimpleDateFormat(ConstantsLegaSport.DATE_FORMAT);
			match.setDate(dateFormat.parse(matchForm.getDateStr()));
		}
		match.setSportCenterCourtRef(null);
		if (matchForm.getCourtId()!=null) {
			Key<SportCenterCourt> key = Key.create(SportCenterCourt.class, matchForm.getCourtId());
			Ref<SportCenterCourt> sportCenterCourtRef = Ref.create(key);
			match.setSportCenterCourtRef(sportCenterCourtRef);
		}
		match.setTeamLocalRef(null);
		if (matchForm.getTeamLocalId()!=null) {
			Key<Team> key = Key.create(Team.class, matchForm.getTeamLocalId());
			Ref<Team> refLocal = Ref.create(key);
			match.setTeamLocalRef(refLocal);
		}
		match.setTeamVisitorRef(null);
		if (matchForm.getTeamVisitorId()!=null) {
			Key<Team> key = Key.create(Team.class, matchForm.getTeamVisitorId());
			Ref<Team> refVisitor = Ref.create(key);
			match.setTeamVisitorRef(refVisitor);
		}
	}

	@Override
	public Match formToEntity(MatchForm form) {

		return null;
	}

	@Override
	public MatchForm entityToForm(Match e) {
		MatchForm f = new MatchForm();
		f.setId(e.getId());
		f.setWeek(e.getWeek());
		DateFormat dateFormat = new SimpleDateFormat(ConstantsLegaSport.DATE_FORMAT);
		if (e.getDate()!=null) {
			f.setDateStr(dateFormat.format(e.getDate()));
		}
		f.setScoreLocal(e.getScoreLocal());
		f.setScoreVisitor(e.getScoreVisitor());
		if (e.getTeamLocalEntity()!=null) {
			f.setTeamLocalId(e.getTeamLocalEntity().getId());
			f.setTeamLocalName(e.getTeamLocalEntity().getName());
		}
		if (e.getTeamVisitorEntity()!=null) {
			f.setTeamVisitorId(e.getTeamVisitorEntity().getId());
			f.setTeamVisitorName(e.getTeamVisitorEntity().getName());
		}
		if (e.getSportCenterCourt()!=null) {
			f.setCourtId(e.getSportCenterCourt().getId());
			f.setCourtName(e.getSportCenterCourt().getNameWithCenter());
		}
		if (e.getMatchPublished()!=null) {
			Match matchPublished = e.getMatchPublished();
			if (e.getScoreLocal()!=matchPublished.getScoreLocal() || e.getScoreVisitor()!=matchPublished.getScoreVisitor()) {
				f.setUpdatedScore(true);
			}
			if (!Objects.equals(e.getDate(), matchPublished.getDate())) {
				f.setUpdatedDate(true);
			}
			if (!Objects.equals(e.getSportCenterCourt(), matchPublished.getSportCenterCourt())) {
				f.setUpdatedCourt(true);
			}
			if (!Objects.equals(e.getTeamLocalEntity(), matchPublished.getTeamLocalEntity())) {
				f.setUpdatedTeamLocal(true);
			}
			if (!Objects.equals(e.getTeamVisitorEntity(), matchPublished.getTeamVisitorEntity())) {
				f.setUpdatedTeamVisitor(true);
			}
		}
		return f;
	}
}