package aks.geotrends.android.utils;

import java.util.HashMap;
import java.util.Map;

import aks.geotrends.android.R;

public enum RegionsEnum {
	
	SouthAfrica("South Africa","ZA",40, R.drawable.ic_south_africa),
	Germany("Germany","DE",15,R.drawable.ic_germany),
	SaudiArabia("Saudi Arabia","SA",36,R.drawable.ic_saudi_arabia),
	Argentina("Argentina","AR",30, R.drawable.ic_argentina),
	Australia("Australia","AU",8, R.drawable.ic_australia),
	Austria("Austria","AT",44,R.drawable.ic_austria),
	Belgium("Belgium","BE",41,R.drawable.ic_belgium),
	Brazil("Brazil","BR",18,R.drawable.ic_brazil),
	Canada("Canada","CA",13,R.drawable.ic_canada),
	Chile("Chile","CL",38,R.drawable.ic_chile),
	Colombia("Colombia","CO",32,R.drawable.ic_colombia),
	SouthKorea("South Korea","KR",23,R.drawable.ic_south_korea),
	Denmark("Denmark","DK",49,R.drawable.ic_denmark),
	Egypt("Egypt","EG",29,R.drawable.ic_egypt),
	Spain("Spain","ES",26,R.drawable.ic_spain),
	UnitedStates("United States","US",1,R.drawable.ic_united_states),
	Finland("Finland","FI",50,R.drawable.ic_finland),
	France("France","FR",16,R.drawable.ic_france),
	Greece("Greece","GR",48,R.drawable.ic_greece),
	HongKong("Hong Kong","HK",10,R.drawable.ic_hong_kong),
	Hungary("Hungary","HU",45,R.drawable.ic_hungary),
	India("India","IN",3,R.drawable.ic_india),
	Indonesia("Indonesia","ID",19,R.drawable.ic_indonesia),
	Israel("Israel","IL",6,R.drawable.ic_israel),
	Italy("Italy","IT",27,R.drawable.ic_italy),
	Japan("Japan","JP",4,R.drawable.ic_japan),
	Kenya("Kenya","KE",37,R.drawable.ic_kenya),
	Malaysia("Malaysia","MY",34,R.drawable.ic_malaysia),
	Mexico("Mexico","MX",21,R.drawable.ic_mexico),
	Nigeria("Nigeria","NG",52,R.drawable.ic_nigeria),
	Norway("Norway","NO",51,R.drawable.ic_norway),
	Netherlands("Netherlands","NL",17,R.drawable.ic_netherlands),
	Philippines("Philippines","PH",25,R.drawable.ic_philippines),
	Poland("Poland","PL",31,R.drawable.ic_poland),
	Portugal("Portugal","PT",47,R.drawable.ic_portugal),
	CzechRepublic("Czech Republic","CZ",43,R.drawable.ic_czech_republic),
	Romania("Romania","RO",39,R.drawable.ic_romania),
	UnitedKingdom("United Kingdom","GB",9,R.drawable.ic_united_kingdom),
	Russia("Russia","RU",14,R.drawable.ic_russia),
	Singapore("Singapore","SG",5,R.drawable.ic_singapore),
	Sweden("Sweden","SE",42,R.drawable.ic_sweden),
	Switzerland("Switzerland","CH",46,R.drawable.ic_switzerland),
	Taiwan("Taiwan","TW",12,R.drawable.ic_taiwan),
	Thailand("Thailand","TH",33,R.drawable.ic_thailand),
	Turkey("Turkey","TR",24,R.drawable.ic_turkey),
	Ukraine("Ukraine","UA",35,R.drawable.ic_ukraine),
	Vietnam("Vietnam","VN",28,R.drawable.ic_vietnam);
	
	private String printName;
	private String region;
	private int code;
	private int flag;
	
	private static Map<Integer, RegionsEnum> regionIntegerCodeMap = null;
	private static Map<String, RegionsEnum> regionShortCodeMap = null;
	
	private RegionsEnum(String printName, String region, int code,int flag)
	{
		this.flag = flag;
		this.setPrintName(printName);
		this.region = region;
		this.code = code;		
	}
	
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	public String getPrintName() {
		return printName;
	}

	public void setPrintName(String printName) {
		this.printName = printName;
	}

	public static RegionsEnum getRegionForCode(int code)
	{
		if(regionIntegerCodeMap==null)
		{
			populateMaps();
		}
		
		RegionsEnum regionItem = regionIntegerCodeMap.get(code);
		
		return regionItem;
	}

	public static RegionsEnum getRegionByShortCode(String regionShort) {
		
		if(regionShortCodeMap==null)
		{
			populateMaps();
		}
		
		RegionsEnum regionItem = regionShortCodeMap.get(regionShort);
		
		return regionItem;
		
	}
	
	private static void populateMaps()
	{
		regionIntegerCodeMap = new HashMap<Integer, RegionsEnum>();
		regionShortCodeMap = new HashMap<String, RegionsEnum>();
		
		RegionsEnum[] values = RegionsEnum.values();
		for (RegionsEnum regionsEnum : values) {
			regionIntegerCodeMap.put(regionsEnum.getCode(), regionsEnum);
			regionShortCodeMap.put(regionsEnum.getRegion(), regionsEnum);
		}
	}

	public int getFlag() {
		return flag;
	}
}
