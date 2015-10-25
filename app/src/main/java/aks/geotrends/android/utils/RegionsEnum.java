package aks.geotrends.android.utils;

import java.util.HashMap;
import java.util.Map;

public enum RegionsEnum {
	
	SouthAfrica("South Africa","ZA",40),
	Germany("Germany","DE",15),
	SaudiArabia("Saudi Arabia","SA",36),
	Argentina("Argentina","AR",30),
	Australia("Australia","AU",8),
	Austria("Austria","AT",44),
	Belgium("Belgium","BE",41),
	Brazil("Brazil","BR",18),
	Canada("Canada","CA",13),
	Chile("Chile","CL",38),
	Colombia("Colombia","CO",32),
	SouthKorea("South Korea","KR",23),
	Denmark("Denmark","DK",49),
	Egypt("Egypt","EG",29),
	Spain("Spain","ES",26),
	UnitedStates("United States","US",1),
	Finland("Finland","FI",50),
	France("France","FR",16),
	Greece("Greece","GR",48),
	HongKong("Hong Kong","HK",10),
	Hungary("Hungary","HU",45),
	India("India","IN",3),
	Indonesia("Indonesia","ID",19),
	Israel("Israel","IL",6),
	Italy("Italy","IT",27),
	Japan("Japan","JP",4),
	Kenya("Kenya","KE",37),
	Malaysia("Malaysia","MY",34),
	Mexico("Mexico","MX",21),
	Nigeria("Nigeria","NG",52),
	Norway("Norway","NO",51),
	Netherlands("Netherlands","NL",17),
	Philippines("Philippines","PH",25),
	Poland("Poland","PL",31),
	Portugal("Portugal","PT",47),
	CzechRepublic("Czech Republic","CZ",43),
	Romania("Romania","RO",39),
	UnitedKingdom("United Kingdom","GB",9),
	Russia("Russia","RU",14),
	Singapore("Singapore","SG",5),
	Sweden("Sweden","SE",42),
	Switzerland("Switzerland","CH",46),
	Taiwan("Taiwan","TW",12),
	Thailand("Thailand","TH",33),
	Turkey("Turkey","TR",24),
	Ukraine("Ukraine","UA",35),
	Vietnam("Vietnam","VN",28);
	
	private String printName;
	private String region;
	private int code;
	
	private static Map<Integer, RegionsEnum> regionIntegerCodeMap = null;
	private static Map<String, RegionsEnum> regionShortCodeMap = null;
	
	private RegionsEnum(String printName, String region, int code)
	{
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

}
