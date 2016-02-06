package Biogeme;

import java.util.ArrayList;

public class UtilsTS {
	
	public static String city;
	//Constants
    public static String id;
    public static String mNumero;
    public static String domSdr;
    public static String domAd;
    public static String cars;
    public static String pers;
    public static String kids;
    public static String pDebut;
    public static String pRang;
    public static String noDep;
    public static String sex;
    public static String ageGroup;
    public static String age;
    public static String occupation;
    public static String licence;
    public static String weigth;
    public static String groupHour;
    public static String hour;
    public static String dMotif;
    public static String mtmX;
    public static String mtmY;
    


    
    //new
    public static String chainLength;
    public static String firstDep;
    public static String lastDep;
    public static String minDist;
    public static String maxDist;
    public static String maxActTime;
    public static String fidelPtRange;
    public static String fidelPt;
    public static String nAct;
    public static String tourType;
    public static String pStatut;
    public static String motor;
    
    public static String xOrigin;
    public static String yOrigin;
    public static String xDest;
    public static String yDest;
    
    //dummies
    public static String dummyInactiveWomen;
    public static String dummyInactiveMen;
    public static String dummyActiveMen;
    public static String dummyStudent;
    public static String dummyRetire;
    public static String dummyWorker;
    public static String dummyTempWorker;
    public static String mother34;
    public static String dummyFemale;
    public static String dummyMale;
    public static String dummyYoung;
    public static String dummyPTuser;
    public static String dummyPartialPT;
    public static String dummyFullPT;
    public static String dummyUnder15;
    public static String dummyUnder19;
    public static String dummyInactive;
    public static String dummyKids;
    public static String dummyKids01;
    public static String dummyKids2;
    
    public static String sim;
	public static String choice;
	public static String stayedHome;
	public static String noPT;
	
	
	//###########DICO pour le recensement
	public static String dauid = "dauid";
	public static String noPt = "C_NOT_PT_RIDER";
	public static String biogemeStayedHome = "C_HOME";
	public static String alternative = "alternative";
	public static String var = "_var";
	
	
	
	
	public UtilsTS(String area){
		if(area.equals("Montreal")){
			city = "Montreal";
			
			//Constants
		     id = "IPERE";
		     mNumero = "M_NUMERO";
		     domSdr = "M_DOMSDR";
		     domAd = "M_DOMAD";
		     cars = "M_AUTO";
		     pers = "M_PERS";
		     kids = "KIDS";
		     pDebut = "P_DEBUT";
		     pRang = "P_RANG";
		     sex = "P_SEXE";
		     ageGroup = "P_GRAGE";
		     age = "P_AGE";
		     occupation = "P_STATUT";
		     licence = "P_PERMIS";
		     weigth = "P_FEXP";
		     groupHour = "D_GRHRE";
		     dMotif = "D_MOTIF";
		     mtmX = "M_DOMXCOOR";
		     mtmY = "M_DOMYCOOR";
		    
		     xOrigin ="D_ORIXCOOR";
		     yOrigin="D_ORIYCOOR";
		     xDest="D_DESXCOOR";
		     yDest="D_DESYCOOR";
		    
		    //new
		     chainLength = "DIST";
		     firstDep = "FIRST_DEP";
		     lastDep = "LAST_DEP";
		     minDist = "MIN_DIST";
		     maxDist = "MAX_DIST";
		     maxActTime = "MAX_ACT_TIME";
		     fidelPtRange = "FIDEL_PT_RANGE";
		     fidelPt = "FIDEL_PT";
		     nAct = "N_ACT";
		     tourType = "TOUR_TYPE";
		     pStatut = "P_STATUT";
		     motor = "MOTOR";
		    
		    //dummies
		     dummyInactiveWomen = "INACTIVEWOMEN";
		     dummyInactiveMen = "INACTIVEMEN";
		     dummyActiveMen = "ACTIVEMEN";
		     dummyStudent = "STUDENT";
		     dummyRetire = "RETIRE";
		     dummyWorker = "WORKER";
		     dummyTempWorker = "TEMPWORKER";
		     mother34 = "MOTHER34";
		     dummyFemale = "FEMALE";
		     dummyMale = "MALE";
		     dummyYoung = "YOUNG";
		     dummyPTuser = "PTUSER";
		     dummyPartialPT = "PARTIALPT";
		     dummyFullPT = "FULLPT";
		     dummyUnder15 = "UNDER15";
		     dummyUnder19 = "UNDER19";
		     dummyInactive = "INACTIVE";
		     dummyKids = "BOOLKIDS";
		     dummyKids01 = "KIDS01";
		     dummyKids2 = "KIDS2";
		    
		     sim = "sim";
			 choice = "CHOICE";
			 stayedHome = "HOME";
			 noPT = "NO_PT";
		}
		
		if(area.equals("Gatineau")){
			city = "Gatineau";
			
			//Constants
		     id = "IPERE";
		     mNumero = "DEPLACEMENT_CLELOGIS";
		     domSdr = "sdrlogis05";
		     domAd = "sdrlogis05";
		     cars = "NBVEH";
		     pers = "NBPERS";
		     kids = "KIDS";
		     pDebut = "P_DEBUT";
		     pRang = "PERSONNES_CLEPERSONNE";
		     noDep = "NDEP";
		     sex = "SEXE";
		     ageGroup = "GRPAGE";
		     age = "AGE";
		     occupation = "OCCUP";
		     licence = "PERMIS";
		     weigth = "FacPer";
		     groupHour = "GRPH";
		     hour = "HEURE";
		     dMotif = "MOTIF";
		     mtmX = "XMTM83Z9LG";
		     mtmY = "LOGIS_CLELOGIS";
		    
		     xOrigin ="XMTM83Z9OR";
		     yOrigin="YMTM83Z9OR";
		     xDest="XMTM83Z9D";
		     yDest="YMTM83Z9LG";

		    
		    //new
		     chainLength = "DIST_2";
		     firstDep = "FIRST_DEP";
		     lastDep = "LAST_DEP";
		     minDist = "MIN_DIST";
		     maxDist = "MAX_DIST";
		     maxActTime = "MAX_ACT_TIME";
		     fidelPtRange = "FIDEL_PT_RANGE";
		     fidelPt = "FIDEL_PT";
		     nAct = "N_ACT";
		     tourType = "TOUR_TYPE";
		     pStatut = "OCCUP";
		     motor = "MOTOR";
		    
		    //dummies
		     dummyInactiveWomen = "INACTIVEWOMEN";
		     dummyInactiveMen = "INACTIVEMEN";
		     dummyActiveMen = "ACTIVEMEN";
		     dummyStudent = "STUDENT";
		     dummyRetire = "RETIRE";
		     dummyWorker = "WORKER";
		     dummyTempWorker = "TEMPWORKER";
		     mother34 = "MOTHER34";
		     dummyFemale = "FEMALE";
		     dummyMale = "MALE";
		     dummyYoung = "YOUNG";
		     dummyPTuser = "PTUSER";
		     dummyPartialPT = "PARTIALPT";
		     dummyFullPT = "FULLPT";
		     dummyUnder15 = "UNDER15";
		     dummyUnder19 = "UNDER19";
		     dummyInactive = "INACTIVE";
		     dummyKids = "BOOLKIDS";
		     dummyKids01 = "KIDS01";
		     dummyKids2 = "KIDS2";
		    
		     sim = "sim";
			 choice = "CHOICE";
			 stayedHome = "HOME";
			 noPT = "NO_PT";
			 
		}
	}

	
    
    
}
