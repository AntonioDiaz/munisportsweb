package com.adiaz.daos;

import com.adiaz.entities.CategoriesVO;
import com.adiaz.entities.CompetitionsVO;
import com.adiaz.entities.SportVO;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/** Created by toni on 11/07/2017. */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:web/WEB-INF/applicationContext-testing.xml")
@WebAppConfiguration("file:web")
public class CompetitionsDAOImplTest {
    private static final String COPA_PRIMAVERA = "COPA_PRIMAVERA";
    private static final String COPA_LIGA = "COPA_LIGA";
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Autowired
    CompetitionsDAO competitionsDAO;
    @Autowired
    CategoriesDAO categoriesDAO;
    @Autowired
    SportsDAO sportsDAO;
    
    private Ref<CategoriesVO> refCategory;
    private Ref<SportVO> refSportBasket;
    private Ref<SportVO> refSportFutbol;

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        ObjectifyService.register(CompetitionsVO.class);
        ObjectifyService.register(CategoriesVO.class);
        ObjectifyService.register(SportVO.class);
        CategoriesVO category = new CategoriesVO();
        category.setName("Cadete");
        Key<CategoriesVO> categoriesVOKey = categoriesDAO.create(category);
        refCategory = Ref.create(categoriesVOKey);
        SportVO sport = new SportVO();
        sport.setName("Basket");
        Key<SportVO> sportVOKey = sportsDAO.create(sport);
        refSportBasket = Ref.create(sportVOKey);
        sport = new SportVO();
        sport.setName("Futbol");
        sportVOKey = sportsDAO.create(sport);
        refSportFutbol = Ref.create(sportVOKey);
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }

    @Test
    public void create() throws Exception {
        Key<CompetitionsVO> key = createCompetition(COPA_PRIMAVERA);
        CompetitionsVO competition = Ref.create(key).getValue();
        Assert.assertEquals(competition, competitionsDAO.findCompetitionsById(competition.getId()));
    }

    @Test
    public void updateName() throws Exception {
        Key<CompetitionsVO> key = createCompetition(COPA_PRIMAVERA);
        CompetitionsVO competition = Ref.create(key).getValue();
        competition.setName(COPA_LIGA);
        competitionsDAO.update(competition);
        competition = competitionsDAO.findCompetitionsById(competition.getId());
        Assert.assertEquals(COPA_LIGA, competition.getName());
    }

    @Test
    public void updateSport() throws Exception {
        Key<CompetitionsVO> key = createCompetition(COPA_PRIMAVERA);
        CompetitionsVO competition = Ref.create(key).getValue();
        competition.setSport(refSportFutbol);
        competitionsDAO.update(competition);
        competition = competitionsDAO.findCompetitionsById(competition.getId());
        Assert.assertEquals(refSportFutbol, competition.getSport());
        competition.getRefs();
        Assert.assertEquals("Futbol", competition.getSportEntity().getName());
    }

    @Test
    public void remove() throws Exception {
        Key<CompetitionsVO> key = createCompetition(COPA_PRIMAVERA);
        CompetitionsVO competition = Ref.create(key).getValue();
        competitionsDAO.remove(competition);
        Assert.assertEquals(0, competitionsDAO.findCompetitions().size());
    }

    @Test
    public void findCompetitions() throws Exception {
        createCompetition(COPA_PRIMAVERA);
        createCompetition(COPA_LIGA);
        Assert.assertEquals(2, competitionsDAO.findCompetitions().size());
    }

    @Test
    public void findCompetitionsBySport() throws Exception {
        createCompetition(COPA_PRIMAVERA);
        createCompetition(COPA_LIGA);
        Assert.assertEquals(2, competitionsDAO.findCompetitionsBySport(refSportFutbol.getValue()).size());
    }

    @Test
    public void findCompetitionsBySportAndCategory() throws Exception {
        Key<CompetitionsVO> keyCompetition01 = createCompetition(COPA_PRIMAVERA);
        Key<CompetitionsVO> keyCompetition02 = createCompetition(COPA_LIGA);

        long idCategory = refCategory.getKey().getId();
        long idSport = refSportBasket.getKey().getId();
        Assert.assertEquals(2, competitionsDAO.findCompetitions(idSport, idCategory).size());
    }

    @Test
    public void findCompetitionsById() throws Exception {
        Key<CompetitionsVO> key = createCompetition(COPA_PRIMAVERA);
        CompetitionsVO competition = Ref.create(key).getValue();
        Assert.assertEquals(competition, competitionsDAO.findCompetitionsById(key.getId()));
    }

    private Key<CompetitionsVO> createCompetition(String competitionName) throws Exception {
        CompetitionsVO competition = new CompetitionsVO();
        competition.setName(competitionName);
        competition.setSport(refSportBasket);
        competition.setCategory(refCategory);
        return competitionsDAO.create(competition);
    }


}