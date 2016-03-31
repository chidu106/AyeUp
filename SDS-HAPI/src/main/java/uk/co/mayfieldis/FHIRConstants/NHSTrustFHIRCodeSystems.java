package uk.co.mayfieldis.FHIRConstants;

// 2016-02-05 Added all constants from subprojects

//Added Gp and Practice  19/1/2016 
public class NHSTrustFHIRCodeSystems {
	 public final static String URI_PATIENT_HOSPITAL_NUMBER = "http://fhir.chft.nhs.uk/HospitalNumber";
	 
	 public final static String URI_PATIENT_DISTRICT_NUMBER = "http://fhir.chft.nhs.uk/DistrictNumber";
	 
	 public final static String URI_CHFT_PAS_CONSULTANT_CODE = "http://fhir.chft.nhs.uk/ConsultantCode";
	 
	 public final static String URI_CHFT_WARD_CODE = "http://fhir.chft.nhs.uk/WardCode";
	 
	 public final static String URI_CHFT_HOSPITAL_CODE = "http://fhir.chft.nhs.uk/HospitalCode";
	 
	 public final static String uri_CHFT_EDMS_DocumentId = "http://fhir.chft.nh.uk/DocumentReference/EDMS";
	 public final static String uri_CHFT_DocumentIndex = "http://fhir.chft.nhs.uk/DocumentIndex";
	 public final static String uri_CHFT_Kodak_DocumentId = "http://fhir.chft.nh.uk/DocumentReference/KodakScanning";
	 
	 public final static String uriCHFTActivityId = "http://fhir.chft.nhs.uk/HSI";
	 
	 public final static String uriCHFTEDISActivityId = "http://fhir.chft.nhs.uk/EDISActivityId";
	 
	 public final static String uriCHFTNorthgateDocumentId ="http://fhir.chft.nhs.uk/DocumentReference/NorthgateUUID";
	 
	 public final static String URI_PATIENT_UN1 = "http://fhir.chft.nhs.uk/UN1";
	 
	 public final static String  uriCHFTPASUSer = "http://fhir.chft.nhs.uk/PAS/User";
	 
	 public final static String  uriCHFTClinicalLetter = "http://fhir.chft.nhs.uk/ClinicalLetter";
	 
	 public final static String  uriCHFTDischargeLetter = "http://fhir.chft.nhs.uk/DischargeLetter"; 
	 
	 public final static String URI_CHFT_PAS_GP_CODE = "http://fhir.chft.nhs.uk/GPCode";
	 
	 public final static String uri_CHFT_EDMS_DrawerId = "http://fhir.chft.nhs.uk/DocumentReference/DrawerId";
	 public final static String uri_CHFT_EDMS_DrawerId_Extension = "http://fhir.chft.nhs.uk/Extension/EDMSD/DrawerId";
	 
	 public final static String URI_ENCOUNTER_EDIS_REASON_CODE_SYSTEM = "http://fhir.chft.nhs.uk/EDIS/PresentationType";
	 
	 public final static String URI_CHFT_SPECIALTY = "http://fhir.chft.nhs.uk/Specialty";
	 
	 public final static String URI_CHFT_CLINIC_CODE = "http://fhir.chft.nhs.uk/ClinicCode";
	 
	 public final static String SELECT_PATIENT_SQL = "select  p.ID,"+
				" p.Address1,"+
				" p.Address2,"+
				" p.Address3,"+
				" p.Address4,"+
				" CopyLetter,"+
				" DateOfDeath,"+
				" DistrictNo DistrictNumber,"+
				" DoB DOB,"+
				" Email,"+
				" EthnicOrigin,"+
				" ForeName1,"+
				" ForeName2,"+
				" GP,"+
				" GPPract,"+
				" GenNo,"+
				" HomeTelNo,"+
				" SUBSTRING(HospNo,2,99) HospitalNumber,"+
				" JointNum,"+
				" MobileTelNo,"+
				" SUBSTRING(NHSNum,2,99) NHSNum,"+
				" NHSNumStatus,"+
				" OverVisitStat,"+
				" p.PostCode,"+
				" Religion,"+
				" SMS,"+
				" Sex,"+
				" Site,"+
				" p.Status,"+
				" p.Surname,"+
				" Title,"+
				" UN1,"+
				" '' GPLocalCode,"+
				" gp.Surname GPSurname,"+
				" gp.Initials GPInitials,"+
				" prac.Address1 PracticeName,"+
				" prac.Address2 PracticeAd1,"+
				" prac.Address3 PracticeAd2,"+
				" prac.Address4 PracticeAd3,"+
				" prac.postcode PracticePostCode"+
				" from MPI.Patient p"+
				" left outer join REF.NationalGP gp on p.GP = gp.GPCode"
				+ " left outer join REF.NationalPractice prac on prac.PracCode = p.GPPract";
	 
	 public final static String URI_CHFT_REFERRAL_REASON = "http://fhir.chft.nhs.uk/ReferralRequest/ReferralReason";
	 
	 public final static String URI_CHFT_REFERRAL_REASON_ACCPETED_DATE = "http://fhir.chft.nhs.uk/ReferralRequest/AcceptedDate";
	 /*
	  * 
	  * #define uriDistrictNumber "http://fhir.chft.nhs.uk/DistrictNumber"
#define uriHospitalNumber "http://fhir.chft.nhs.uk/HospitalNumber"

#define uriCHFTConsultantCode "http://fhir.chft.nhs.uk/ConsultantCode"



#define uriCHFTWinscribeLetter "http://fhir.chft.nhs.uk/DocumentReference/Winscribe"


#define uriCHFTWinscribeDocumentType "http://fhir.chft.nhs.uk/DocumentType/Winscribe"
#define uriCHFTWinscribeDocumentClass "http://england.nhs.uk/HL7v2/zu015"
#define uriCHFTADPerson "http://fhir.chft.nhs.uk/ActiveDirectory/Person"
#define uriCHFTWinscribePerson "http://fhir.chft.nhs.uk/Person/Winscribe"


	  */
}

