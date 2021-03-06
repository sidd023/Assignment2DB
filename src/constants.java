public class constants{


	public static final int DBLOAD_ARG_COUNT = 3;
	public static final int DBQUERY_ARG_COUNT = 3;
	public static final int STD_NAME_SIZE = 24;
	public static final int ID_SIZE = 4;
	public static final int DATE_SIZE = 8;
	public static final int YEAR_SIZE = 4;
	public static final int MONTH_SIZE = 9;
	public static final int MDATE_SIZE = 4;
	public static final int DAY_SIZE = 9;
	public static final int TIME_SIZE = 4;
	public static final int SENSORID_SIZE = 4;
	public static final int SENSORNAME_SIZE = 38;
	public static final int COUNTS_SIZE = 4;
	public static final int       TREE_RECORD_SIZE = 36;
	public static final int MAX_NUM_KEYS = 1024;
	public static final String    TREE_FILE_NAME = "bt.dat";
	public static final String    RANGE_DELIMITER = "--"; 
	public static final int       RANGE_KEY_ID = 1;
	public static final int       RANGE_KEY_DATE = 2;



	public static final int TOTAL_SIZE =    STD_NAME_SIZE + 
			ID_SIZE + 
			DATE_SIZE + 
			YEAR_SIZE + 
			MONTH_SIZE + 
			MDATE_SIZE + 
			DAY_SIZE + 
			TIME_SIZE + 
			SENSORID_SIZE + 
			SENSORNAME_SIZE + 
			COUNTS_SIZE;
	public static final int ID_POS = 0;
	public static final int DATETIME_POS = 1;
	public static final int YEAR_POS = 2;
	public static final int MONTH_POS = 3;
	public static final int MDATE_POS = 4;
	public static final int DAY_POS = 5;
	public static final int TIME_POS = 6;
	public static final int SENSORID_POS = 7;
	public static final int SENSORNAME_POS = 8;
	public static final int COUNTS_POS = 9;
	public static final int MILLISECONDS_PER_SECOND = 1000000;

	public static final int ID_OFFSET =   STD_NAME_SIZE;

	public static final int DATE_OFFSET =   STD_NAME_SIZE +
			ID_SIZE;

	public static final int YEAR_OFFSET =  STD_NAME_SIZE +
			ID_SIZE +
			DATE_SIZE;

	public static final int MONTH_OFFSET =  STD_NAME_SIZE +
			ID_SIZE +
			DATE_SIZE +
			YEAR_SIZE;

	public static final int MDATE_OFFSET =  STD_NAME_SIZE +
			ID_SIZE +
			DATE_SIZE +
			YEAR_SIZE +
			MONTH_SIZE;

	public static final int DAY_OFFSET =   STD_NAME_SIZE +
			ID_SIZE +
			DATE_SIZE +
			YEAR_SIZE +
			MONTH_SIZE +
			MDATE_SIZE;

	public static final int TIME_OFFSET =   STD_NAME_SIZE + 
			ID_SIZE + 
			DATE_SIZE +
			YEAR_SIZE +
			MONTH_SIZE +
			MDATE_SIZE +
			DAY_SIZE;

	public static final int SENSORID_OFFSET =   STD_NAME_SIZE + 
			ID_SIZE + 
			DATE_SIZE +
			YEAR_SIZE +
			MONTH_SIZE +
			MDATE_SIZE +
			DAY_SIZE +
			TIME_SIZE;

	public static final int SENSORNAME_OFFSET = STD_NAME_SIZE + 
			ID_SIZE + 
			DATE_SIZE + 
			YEAR_SIZE + 
			MONTH_SIZE + 
			MDATE_SIZE + 
			DAY_SIZE + 
			TIME_SIZE + 
			SENSORID_SIZE; 

	public static final int COUNTS_OFFSET = STD_NAME_SIZE + 
			ID_SIZE + 
			DATE_SIZE + 
			YEAR_SIZE + 
			MONTH_SIZE + 
			MDATE_SIZE + 
			DAY_SIZE + 
			TIME_SIZE + 
			SENSORID_SIZE + 
			SENSORNAME_SIZE;    


}