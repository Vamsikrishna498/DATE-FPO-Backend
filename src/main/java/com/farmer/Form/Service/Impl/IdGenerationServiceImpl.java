package com.farmer.Form.Service.Impl;

import com.farmer.Form.Entity.IdCard;
import com.farmer.Form.Repository.IdCardRepository;
import com.farmer.Form.Service.IdGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdGenerationServiceImpl implements IdGenerationService {
    
    @Autowired
    private IdCardRepository idCardRepository;
    
    // State code mapping
    private static final Map<String, String> STATE_CODES = new HashMap<>();
    static {
        STATE_CODES.put("TAMIL NADU", "TN");
        STATE_CODES.put("KERALA", "KL");
        STATE_CODES.put("KARNATAKA", "KA");
        STATE_CODES.put("ANDHRA PRADESH", "AP");
        STATE_CODES.put("TELANGANA", "TG");
        STATE_CODES.put("MAHARASHTRA", "MH");
        STATE_CODES.put("GUJARAT", "GJ");
        STATE_CODES.put("RAJASTHAN", "RJ");
        STATE_CODES.put("MADHYA PRADESH", "MP");
        STATE_CODES.put("UTTAR PRADESH", "UP");
        STATE_CODES.put("BIHAR", "BR");
        STATE_CODES.put("WEST BENGAL", "WB");
        STATE_CODES.put("ODISHA", "OR");
        STATE_CODES.put("ASSAM", "AS");
        STATE_CODES.put("PUNJAB", "PB");
        STATE_CODES.put("HARYANA", "HR");
        STATE_CODES.put("HIMACHAL PRADESH", "HP");
        STATE_CODES.put("UTTARAKHAND", "UK");
        STATE_CODES.put("JAMMU AND KASHMIR", "JK");
        STATE_CODES.put("DELHI", "DL");
        STATE_CODES.put("CHANDIGARH", "CH");
        STATE_CODES.put("PUDUCHERRY", "PY");
        STATE_CODES.put("GOA", "GA");
        STATE_CODES.put("MEGHALAYA", "ML");
        STATE_CODES.put("MANIPUR", "MN");
        STATE_CODES.put("MIZORAM", "MZ");
        STATE_CODES.put("NAGALAND", "NL");
        STATE_CODES.put("TRIPURA", "TR");
        STATE_CODES.put("SIKKIM", "SK");
        STATE_CODES.put("ARUNACHAL PRADESH", "AR");
        STATE_CODES.put("LADAKH", "LA");
        STATE_CODES.put("ANDAMAN AND NICOBAR ISLANDS", "AN");
        STATE_CODES.put("DAMAN AND DIU", "DD");
        STATE_CODES.put("DADRA AND NAGAR HAVELI", "DN");
        STATE_CODES.put("LAKSHADWEEP", "LD");
    }
    
    // Country code mapping
    private static final Map<String, String> COUNTRY_CODES = new HashMap<>();
    static {
        COUNTRY_CODES.put("INDIA", "IN");
        COUNTRY_CODES.put("UNITED STATES", "US");
        COUNTRY_CODES.put("UNITED KINGDOM", "UK");
        COUNTRY_CODES.put("CANADA", "CA");
        COUNTRY_CODES.put("AUSTRALIA", "AU");
        COUNTRY_CODES.put("GERMANY", "DE");
        COUNTRY_CODES.put("FRANCE", "FR");
        COUNTRY_CODES.put("JAPAN", "JP");
        COUNTRY_CODES.put("CHINA", "CN");
        COUNTRY_CODES.put("BRAZIL", "BR");
        COUNTRY_CODES.put("RUSSIA", "RU");
        COUNTRY_CODES.put("SOUTH AFRICA", "ZA");
        COUNTRY_CODES.put("MEXICO", "MX");
        COUNTRY_CODES.put("ITALY", "IT");
        COUNTRY_CODES.put("SPAIN", "ES");
        COUNTRY_CODES.put("NETHERLANDS", "NL");
        COUNTRY_CODES.put("BELGIUM", "BE");
        COUNTRY_CODES.put("SWITZERLAND", "CH");
        COUNTRY_CODES.put("AUSTRIA", "AT");
        COUNTRY_CODES.put("SWEDEN", "SE");
        COUNTRY_CODES.put("NORWAY", "NO");
        COUNTRY_CODES.put("DENMARK", "DK");
        COUNTRY_CODES.put("FINLAND", "FI");
        COUNTRY_CODES.put("POLAND", "PL");
        COUNTRY_CODES.put("CZECH REPUBLIC", "CZ");
        COUNTRY_CODES.put("HUNGARY", "HU");
        COUNTRY_CODES.put("ROMANIA", "RO");
        COUNTRY_CODES.put("BULGARIA", "BG");
        COUNTRY_CODES.put("CROATIA", "HR");
        COUNTRY_CODES.put("SLOVENIA", "SI");
        COUNTRY_CODES.put("SLOVAKIA", "SK");
        COUNTRY_CODES.put("ESTONIA", "EE");
        COUNTRY_CODES.put("LATVIA", "LV");
        COUNTRY_CODES.put("LITHUANIA", "LT");
        COUNTRY_CODES.put("MALTA", "MT");
        COUNTRY_CODES.put("CYPRUS", "CY");
        COUNTRY_CODES.put("IRELAND", "IE");
        COUNTRY_CODES.put("PORTUGAL", "PT");
        COUNTRY_CODES.put("GREECE", "GR");
        COUNTRY_CODES.put("TURKEY", "TR");
        COUNTRY_CODES.put("ISRAEL", "IL");
        COUNTRY_CODES.put("SAUDI ARABIA", "SA");
        COUNTRY_CODES.put("UNITED ARAB EMIRATES", "AE");
        COUNTRY_CODES.put("QATAR", "QA");
        COUNTRY_CODES.put("KUWAIT", "KW");
        COUNTRY_CODES.put("BAHRAIN", "BH");
        COUNTRY_CODES.put("OMAN", "OM");
        COUNTRY_CODES.put("JORDAN", "JO");
        COUNTRY_CODES.put("LEBANON", "LB");
        COUNTRY_CODES.put("SYRIA", "SY");
        COUNTRY_CODES.put("IRAQ", "IQ");
        COUNTRY_CODES.put("IRAN", "IR");
        COUNTRY_CODES.put("AFGHANISTAN", "AF");
        COUNTRY_CODES.put("PAKISTAN", "PK");
        COUNTRY_CODES.put("BANGLADESH", "BD");
        COUNTRY_CODES.put("SRI LANKA", "LK");
        COUNTRY_CODES.put("NEPAL", "NP");
        COUNTRY_CODES.put("BHUTAN", "BT");
        COUNTRY_CODES.put("MALDIVES", "MV");
        COUNTRY_CODES.put("MYANMAR", "MM");
        COUNTRY_CODES.put("THAILAND", "TH");
        COUNTRY_CODES.put("LAOS", "LA");
        COUNTRY_CODES.put("CAMBODIA", "KH");
        COUNTRY_CODES.put("VIETNAM", "VN");
        COUNTRY_CODES.put("MALAYSIA", "MY");
        COUNTRY_CODES.put("SINGAPORE", "SG");
        COUNTRY_CODES.put("INDONESIA", "ID");
        COUNTRY_CODES.put("PHILIPPINES", "PH");
        COUNTRY_CODES.put("BRUNEI", "BN");
        COUNTRY_CODES.put("EAST TIMOR", "TL");
        COUNTRY_CODES.put("PAPUA NEW GUINEA", "PG");
        COUNTRY_CODES.put("FIJI", "FJ");
        COUNTRY_CODES.put("SOLOMON ISLANDS", "SB");
        COUNTRY_CODES.put("VANUATU", "VU");
        COUNTRY_CODES.put("NEW CALEDONIA", "NC");
        COUNTRY_CODES.put("NEW ZEALAND", "NZ");
        COUNTRY_CODES.put("SAMOA", "WS");
        COUNTRY_CODES.put("TONGA", "TO");
        COUNTRY_CODES.put("KIRIBATI", "KI");
        COUNTRY_CODES.put("TUVALU", "TV");
        COUNTRY_CODES.put("NAURU", "NR");
        COUNTRY_CODES.put("PALAU", "PW");
        COUNTRY_CODES.put("MARSHALL ISLANDS", "MH");
        COUNTRY_CODES.put("MICRONESIA", "FM");
        COUNTRY_CODES.put("COOK ISLANDS", "CK");
        COUNTRY_CODES.put("NIUE", "NU");
        COUNTRY_CODES.put("TOKELAU", "TK");
        COUNTRY_CODES.put("AMERICAN SAMOA", "AS");
        COUNTRY_CODES.put("GUAM", "GU");
        COUNTRY_CODES.put("NORTHERN MARIANA ISLANDS", "MP");
        COUNTRY_CODES.put("PUERTO RICO", "PR");
        COUNTRY_CODES.put("VIRGIN ISLANDS", "VI");
        COUNTRY_CODES.put("ANGUILLA", "AI");
        COUNTRY_CODES.put("ANTIGUA AND BARBUDA", "AG");
        COUNTRY_CODES.put("ARUBA", "AW");
        COUNTRY_CODES.put("BAHAMAS", "BS");
        COUNTRY_CODES.put("BARBADOS", "BB");
        COUNTRY_CODES.put("BELIZE", "BZ");
        COUNTRY_CODES.put("BERMUDA", "BM");
        COUNTRY_CODES.put("BONAIRE", "BQ");
        COUNTRY_CODES.put("BRITISH VIRGIN ISLANDS", "VG");
        COUNTRY_CODES.put("CAYMAN ISLANDS", "KY");
        COUNTRY_CODES.put("COSTA RICA", "CR");
        COUNTRY_CODES.put("CUBA", "CU");
        COUNTRY_CODES.put("CURACAO", "CW");
        COUNTRY_CODES.put("DOMINICA", "DM");
        COUNTRY_CODES.put("DOMINICAN REPUBLIC", "DO");
        COUNTRY_CODES.put("EL SALVADOR", "SV");
        COUNTRY_CODES.put("GRENADA", "GD");
        COUNTRY_CODES.put("GUATEMALA", "GT");
        COUNTRY_CODES.put("HAITI", "HT");
        COUNTRY_CODES.put("HONDURAS", "HN");
        COUNTRY_CODES.put("JAMAICA", "JM");
        COUNTRY_CODES.put("MARTINIQUE", "MQ");
        COUNTRY_CODES.put("MONTSERRAT", "MS");
        COUNTRY_CODES.put("NICARAGUA", "NI");
        COUNTRY_CODES.put("PANAMA", "PA");
        COUNTRY_CODES.put("SABA", "BQ");
        COUNTRY_CODES.put("SAINT BARTHELEMY", "BL");
        COUNTRY_CODES.put("SAINT KITTS AND NEVIS", "KN");
        COUNTRY_CODES.put("SAINT LUCIA", "LC");
        COUNTRY_CODES.put("SAINT MARTIN", "MF");
        COUNTRY_CODES.put("SAINT PIERRE AND MIQUELON", "PM");
        COUNTRY_CODES.put("SAINT VINCENT AND THE GRENADINES", "VC");
        COUNTRY_CODES.put("SINT EUSTATIUS", "BQ");
        COUNTRY_CODES.put("SINT MAARTEN", "SX");
        COUNTRY_CODES.put("TRINIDAD AND TOBAGO", "TT");
        COUNTRY_CODES.put("TURKS AND CAICOS ISLANDS", "TC");
        COUNTRY_CODES.put("US VIRGIN ISLANDS", "VI");
    }
    
    // Thread-safe counters for each state-country combination
    private final Map<String, AtomicLong> counters = new HashMap<>();
    
    @Override
    public String generateFarmerId(String state, String district) {
        String state2 = normalizeTwoLetters(state);
        String district2 = normalizeTwoLetters(district);
        return generateId("FAM", state2, district2);
    }
    
    @Override
    public String generateEmployeeId(String state, String district) {
        System.out.println("🔄 Generating employee ID for state: " + state + ", district: " + district);
        String state2 = normalizeTwoLetters(state);
        String district2 = normalizeTwoLetters(district);
        System.out.println("📍 Normalized state: " + state2 + ", district: " + district2);
        String generatedId = generateId("EMP", state2, district2);
        System.out.println("✅ Generated employee ID: " + generatedId);
        return generatedId;
    }
    
    private String generateId(String prefix, String stateCode, String districtCode) {
        String key = prefix + stateCode + districtCode;
        String cardId;
        
        do {
            long nextNumber = getNextNumber(key);
            cardId = prefix + stateCode + districtCode + String.format("%04d", nextNumber);
        } while (!isIdUnique(cardId));
        
        return cardId;
    }
    
    private long getNextNumber(String key) {
        return counters.computeIfAbsent(key, k -> new AtomicLong(1)).getAndIncrement();
    }
    
    @Override
    public boolean isIdUnique(String cardId) {
        return !idCardRepository.findByCardId(cardId).isPresent();
    }
    
    @Override
    public String getStateCode(String stateName) {
        if (stateName == null) return "XX";
        return STATE_CODES.getOrDefault(stateName.toUpperCase(), "XX");
    }

    private String normalizeTwoLetters(String value) {
        if (value == null || value.isBlank()) return "XX";
        String cleaned = value.replaceAll("[^A-Za-z]", "").toUpperCase();
        return cleaned.length() >= 2 ? cleaned.substring(0, 2) : (cleaned + "X").substring(0, 2);
    }
    
    @Override
    public String getCountryCode(String countryName) {
        if (countryName == null) return "XX";
        return COUNTRY_CODES.getOrDefault(countryName.toUpperCase(), "XX");
    }
}
