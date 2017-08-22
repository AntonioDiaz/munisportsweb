package com.adiaz.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.adiaz.entities.*;
import com.adiaz.entities.Competition;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;

public class MuniSportsUtils {

	private static final Logger logger = Logger.getLogger(MuniSportsUtils.class.getName());

	public static List<Match> parseCalendar(String lines, long idCompetition, Ref<SportCenterCourt> sportCourtRef,Map<String, Ref<Team>> teamsMap) {
		List<Match> matchesList = new ArrayList<>();
		String[] split = lines.split("\\r\\n");
		int week = 0;
		for (int i = 0; i < split.length; i++) {
			String line = split[i];
			if (line.startsWith("Jornada")) {
				week++;
			} else {
				String[] strings = line.split("\\t");
				Match match = new Match();
				match.setWeek(week);
				match.setTeamLocalRef(teamsMap.get(strings[2]));
				match.setTeamVisitorRef(teamsMap.get(strings[3]));


				match.setWorkingCopy(false);
				if (strings.length >= 5) {
					match.setSportCenterCourtRef(sportCourtRef);
				}
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				Date myDate;
				try {
					myDate = dateFormat.parse(strings[0] + " " + strings[1]);
					match.setDate(myDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Key<Competition> competitionKey = Key.create(Competition.class, idCompetition);
				match.setCompetitionRef(Ref.create(competitionKey));
				matchesList.add(match);
			}
		}
		return matchesList;
	}

	public static Set<String> parseCalendarGetTeams() {
		Set<String> teamsNames = new HashSet<>();
		String lines = parseLinesTextFile("static_calendar.txt");
		String[] split = lines.split("\\r\\n");
		for (int i = 0; i < split.length; i++) {
			String[] strings = split[i].split("\\t");
			if (strings.length>=3) {
				teamsNames.add(strings[2]);
				teamsNames.add(strings[3]);
			}
		}
		return teamsNames;
	}

	public static List<Match> parseCalendar(Long idCompetition, Ref<SportCenterCourt> sportCourtRef, Map<String, Ref<Team>> teamsMap) {
		String lines = parseLinesTextFile("static_calendar.txt");
		return  parseCalendar(lines, idCompetition, sportCourtRef, teamsMap);
	}
	
	public static List<ClassificationEntry> parseClassification(String lines, Long competitionId) {
		List<ClassificationEntry> classificationList = new ArrayList<ClassificationEntry>();
		String[] split = lines.split("\\r\\n");
		for (int i = 0; i < split.length; i++) {
			ClassificationEntry classificationEntry = new ClassificationEntry();
			String[] strings = split[i].split("\\t");
			//1	AD CEPA SPORT	22	18	1	3	85	24	0	0	0	55	0
			classificationEntry.setPosition(Integer.valueOf(strings[0]));
			classificationEntry.setTeam(strings[1]);
			classificationEntry.setPoints(Integer.valueOf(strings[11]));
			classificationEntry.setMatchesPlayed(Integer.valueOf(strings[2]));
			classificationEntry.setMatchesWon(Integer.valueOf(strings[3]));
			classificationEntry.setMatchesDrawn(Integer.valueOf(strings[4]));
			classificationEntry.setMatchesLost(Integer.valueOf(strings[5]));
			Key<Competition> competitionKey = Key.create(Competition.class, competitionId);
			classificationEntry.setCompetitionRef(Ref.create(competitionKey));
			classificationList.add(classificationEntry);
		}
		return classificationList;
	}
	public static List<ClassificationEntry> parseClassification(Long competitionId) {
		String lines = parseLinesTextFile("static_classification.txt");
		return  parseClassification(lines, competitionId);
	}
	
	public static void main(String[] args) {
		ObjectifyService.register(Sport.class);
		ObjectifyService.register(Category.class);
		ObjectifyService.register(Competition.class);
		ObjectifyService.register(Match.class);
		System.out.println("parse calendar....");
		System.out.println(MuniSportsUtils.parseClassification(1L).size());
	}

	public static String sha256Encode(String text) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
		byte[] digest = md.digest();
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			hexString.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
		}
		return hexString.toString();
	}

	/**
	 * Validate the format of a field and if is null.
	 *
	 * @param errors
	 * @param fieldToCheckValue
	 * @param fieldToCheckName
	 * @param keyErrorMsg
	 * @param patternToCheck
	 */
	public static void validateNotEmptyAndFormat(
			Errors errors, String fieldToCheckValue, String fieldToCheckName, String keyErrorMsg, String patternToCheck) {
		if (StringUtils.isEmpty(fieldToCheckValue)) {
			errors.rejectValue(fieldToCheckName, "field_required");
		} else {
			Pattern pattern = Pattern.compile(patternToCheck);
			Matcher matcher = pattern.matcher(fieldToCheckValue);
			if (!matcher.matches()){
				errors.rejectValue(fieldToCheckName, keyErrorMsg);
			}
		}
	}

	public static User getActiveUser(){
		User user = null;
		try {
			user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return user;
	}

	private static String parseLinesTextFile(String fileName) {
		String calendarTxt = "";
		ClassLoader classLoader = MuniSportsUtils.class.getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null ){
				calendarTxt += line + "\r\n";
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return calendarTxt;
	}

}