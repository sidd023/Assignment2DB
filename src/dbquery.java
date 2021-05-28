import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dbquery {

   private static bplustree _bt = new bplustree();

   // store the page size 
   private static int size;

   public static void setSize(int s) {
      size = s;
   }

   public static int getSize() {
      return size;
   }

   // implements search for heap and bplus
   public static void main(String[] args) throws IOException {

      dbquery query = new dbquery();

      // check the input arguments
      if (args.length == constants.DBQUERY_ARG_COUNT && query.isInteger(args[2])) {
    	  // set the page size
         setSize(Integer.parseInt(args[2]));

         // perform query on heap
         if (args[0].equals("-h")) {
            query.searchHeap(args[1], Integer.parseInt(args[2]));
         } 
         //perform range search on id in heap
         else if (args[0].equals("-hid")) {
            String range1 = "";
            String range2 = "";
            String key = args[1];
            if (key.contains(constants.RANGE_DELIMITER)) {
               String[] searchValues = key.split(constants.RANGE_DELIMITER);
               range1 = searchValues[0];
               range2 = searchValues[1];
            }
            query.rangeHeap(range1, range2, constants.RANGE_KEY_ID, Integer.parseInt(args[2]));
         } 
         //perform range search on date in heap
         else if (args[0].equals("-hdate")) {
            String range1 = "";
            String range2 = "";
            String key = args[1];
            if (key.contains(constants.RANGE_DELIMITER)) {
               String[] searchValues = key.split(constants.RANGE_DELIMITER);
               range1 = searchValues[0];
               range2 = searchValues[1];
            }
            query.rangeHeap(range1, range2, constants.RANGE_KEY_DATE, Integer.parseInt(args[2]));
         } 
      // perform query on bplus
         else if (args[0].equals("-b")) {
        	 
            System.out.println("Inserting into B+ tree.........");
            // insert the tree 
            readBTree();
            System.out.println("Insert done");
            long startTime = 0;
            long finishTime = 0;
            startTime = System.nanoTime();
            // search within the tree
            _bt.searchTree(args[1]);
            finishTime = System.nanoTime();
            long timeInMilliseconds = (finishTime - startTime) / constants.MILLISECONDS_PER_SECOND;
            System.out.println("Time taken: " + timeInMilliseconds + " ms");

         } 
       //perform range search on id in bplus
         else if (args[0].equals("-bid")) {
            String range1 = "";
            String range2 = "";
            String key = args[1];
            if (key.contains(constants.RANGE_DELIMITER)) {
               String[] searchValues = key.split(constants.RANGE_DELIMITER);
               range1 = searchValues[0];
               range2 = searchValues[1];
            }
            System.out.println("Inserting into B+ tree.........");
            readBTree();
            System.out.println("Insert done");
            long startTime = 0;
            long finishTime = 0;
            startTime = System.nanoTime();
            _bt.rangeSearch(range1, range2, constants.RANGE_KEY_ID);
            finishTime = System.nanoTime();
            long timeInMilliseconds = (finishTime - startTime) / constants.MILLISECONDS_PER_SECOND;
            System.out.println("Time taken: " + timeInMilliseconds + " ms");
         } 
         
       //perform range search on date in bplus
         else if (args[0].equals("-bdate")) {
            String range1 = "";
            String range2 = "";
            String key = args[1];
            if (key.contains(constants.RANGE_DELIMITER)) {
               String[] searchValues = key.split(constants.RANGE_DELIMITER);
               range1 = searchValues[0];
               range2 = searchValues[1];
            }
            System.out.println("Inserting into B+ tree.........");
            readBTree();
            System.out.println("Insert done");
            long startTime = 0;
            long finishTime = 0;
            startTime = System.nanoTime();
            _bt.rangeSearch(range1, range2, constants.RANGE_KEY_DATE);
            finishTime = System.nanoTime();
            long timeInMilliseconds = (finishTime - startTime) / constants.MILLISECONDS_PER_SECOND;
            System.out.println("Time taken: " + timeInMilliseconds + " ms");
         }
      } else {
         System.out.println("Error: Incorrect number of arguments were input");
      }
   }

   // file stored in disk is loaded into tree 
   public static void readBTree() {
      File btreeFilename = new File(constants.TREE_FILE_NAME);
      try (FileInputStream fis = new FileInputStream(btreeFilename)) {
         boolean haveNextRecord = true;
         while (haveNextRecord) {
            byte[] buf = new byte[constants.TREE_RECORD_SIZE];
            int i = fis.read(buf, 0, constants.TREE_RECORD_SIZE);
            String rec = new String(buf);
            if (i != -1) {
               String key = rec.split("[,]")[0];
               String val = rec.split("[,]")[1];
               _bt.insert(key, val);
            } else
               haveNextRecord = false;
         }
         fis.close();
      } catch (FileNotFoundException e) {
         System.out.println("File " + btreeFilename + " not found.");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   // functions in support to convert and check string to integer 
   public boolean isInteger(String s) {
      boolean isValidInt = false;
      try {
         Integer.parseInt(s);
         isValidInt = true;
      } catch (NumberFormatException e) {
         e.printStackTrace();
      }
      return isValidInt;
   }

   // seek into the position in the heap
   public static void readSeek(String offset, String data) {
      int pageSize = getSize();
      String[] parts = offset.split("_");
      // offset consist of pagenumber_recordNumber
      // eg, 027362_12
      
      long pageNum = Long.valueOf(parts[0]);
      int i = Integer.parseInt(parts[1])-1;
    
      
      //int i = Integer.parseInt(parts[1])-1;
      if (i < 0)
         i = 0;
      
      pageNum = pageNum* pageSize;
      String datafile = "heap." + pageSize;
      int numBytesInOneRecord = constants.TOTAL_SIZE;
      int numBytesInSdtnameField = constants.STD_NAME_SIZE;
      int numBytesIntField = Integer.BYTES;
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
      byte[] page = new byte[pageSize];
      FileInputStream inStream = null;

      try {
         inStream = new FileInputStream(datafile);
         // Create byte arrays for each field
         byte[] sdtnameBytes = new byte[numBytesInSdtnameField];
         byte[] idBytes = new byte[constants.ID_SIZE];
         byte[] dateBytes = new byte[constants.DATE_SIZE];
         byte[] yearBytes = new byte[constants.YEAR_SIZE];
         byte[] monthBytes = new byte[constants.MONTH_SIZE];
         byte[] mdateBytes = new byte[constants.MDATE_SIZE];
         byte[] dayBytes = new byte[constants.DAY_SIZE];
         byte[] timeBytes = new byte[constants.TIME_SIZE];
         byte[] sensorIdBytes = new byte[constants.SENSORID_SIZE];
         byte[] sensorNameBytes = new byte[constants.SENSORNAME_SIZE];
         byte[] countsBytes = new byte[constants.COUNTS_SIZE];

         // place the head position to the specific position
         // passed into the function 
         RandomAccessFile raf = null;
         try {
            raf = new RandomAccessFile(datafile, "rw");
            raf.seek((long) pageNum);
            raf.read(page);
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } finally {
            raf.close();
         }
         

         // Copy record's SdtName (field is located at multiples of the total record byte length)
         System.arraycopy(page, (i * numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);

         String sdtNameString = new String(sdtnameBytes);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.ID_OFFSET), idBytes, 0, numBytesIntField);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.DATE_OFFSET), dateBytes, 0, constants.DATE_SIZE);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.YEAR_OFFSET), yearBytes, 0, numBytesIntField);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.MONTH_OFFSET), monthBytes, 0, constants.MONTH_SIZE);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.MDATE_OFFSET), mdateBytes, 0, numBytesIntField);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.DAY_OFFSET), dayBytes, 0, constants.DAY_SIZE);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.TIME_OFFSET), timeBytes, 0, numBytesIntField);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.SENSORID_OFFSET), sensorIdBytes, 0, numBytesIntField);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.SENSORNAME_OFFSET), sensorNameBytes, 0, constants.SENSORNAME_SIZE);
         System.arraycopy(page, ((i * numBytesInOneRecord) + constants.COUNTS_OFFSET), countsBytes, 0, numBytesIntField);

         // Convert long data into Date object
         Date date = new Date(ByteBuffer.wrap(dateBytes).getLong());

         // Get a string representation of the record for printing to stdout
         String record = sdtNameString.trim() + "," + ByteBuffer.wrap(idBytes).getInt() +
            "," + dateFormat.format(date) + "," + ByteBuffer.wrap(yearBytes).getInt() +
            "," + new String(monthBytes).trim() + "," + ByteBuffer.wrap(mdateBytes).getInt() +
            "," + new String(dayBytes).trim() + "," + ByteBuffer.wrap(timeBytes).getInt() +
            "," + ByteBuffer.wrap(sensorIdBytes).getInt() + "," +
            new String(sensorNameBytes).trim() + "," + ByteBuffer.wrap(countsBytes).getInt();
         System.out.println(record);

      } catch (FileNotFoundException e) {
         System.err.println("File not found " + e.getMessage());
      } catch (IOException e) {
         System.err.println("IO Exception " + e.getMessage());
      } finally {

         if (inStream != null) {
            try {
               inStream.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }

   }

   public void rangeHeap(String key1, String key2, int search_Type, int size) throws IOException {
      int pageSize = size;

      String datafile = "heap." + pageSize;
      long startTime = 0;
      long finishTime = 0;
      int numBytesInOneRecord = constants.TOTAL_SIZE;
      int numBytesInSdtnameField = constants.STD_NAME_SIZE;
      int numBytesIntField = Integer.BYTES;
      int numRecordsPerPage = pageSize / numBytesInOneRecord;
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
      byte[] page = new byte[pageSize];
      FileInputStream inStream = null;

      try {
         inStream = new FileInputStream(datafile);
         int numBytesRead = 0;
         startTime = System.nanoTime();
         // Create byte arrays for each field
         byte[] sdtnameBytes = new byte[numBytesInSdtnameField];
         byte[] idBytes = new byte[constants.ID_SIZE];
         byte[] dateBytes = new byte[constants.DATE_SIZE];
         byte[] yearBytes = new byte[constants.YEAR_SIZE];
         byte[] monthBytes = new byte[constants.MONTH_SIZE];
         byte[] mdateBytes = new byte[constants.MDATE_SIZE];
         byte[] dayBytes = new byte[constants.DAY_SIZE];
         byte[] timeBytes = new byte[constants.TIME_SIZE];
         byte[] sensorIdBytes = new byte[constants.SENSORID_SIZE];
         byte[] sensorNameBytes = new byte[constants.SENSORNAME_SIZE];
         byte[] countsBytes = new byte[constants.COUNTS_SIZE];

         if (search_Type == constants.RANGE_KEY_ID) {
            int range1 = Integer.parseInt(key1);
            int range2 = Integer.parseInt(key2);

            while ((numBytesRead = inStream.read(page)) != -1) {
               // Process each record in page
               for (int i = 0; i < numRecordsPerPage; i++) {

                  // Copy record's SdtName (field is located at multiples of the total record byte length)
                  System.arraycopy(page, (i * numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);

                  // Check if field is empty; if so, end of all records found (packed organisation)
                  if (sdtnameBytes[0] == 0) {
                     // can stop checking records
                     break;
                  }

                  // Check for match to "text"
                  String sdtNameString = new String(sdtnameBytes);

                  String[] search = sdtNameString.split("_");
                  int searchKey = Integer.parseInt(search[0]);

                  // range search on id
                  if (searchKey >= range1 && searchKey <= range2) {

                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.ID_OFFSET), idBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.DATE_OFFSET), dateBytes, 0, constants.DATE_SIZE);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.YEAR_OFFSET), yearBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.MONTH_OFFSET), monthBytes, 0, constants.MONTH_SIZE);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.MDATE_OFFSET), mdateBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.DAY_OFFSET), dayBytes, 0, constants.DAY_SIZE);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.TIME_OFFSET), timeBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.SENSORID_OFFSET), sensorIdBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.SENSORNAME_OFFSET), sensorNameBytes, 0, constants.SENSORNAME_SIZE);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.COUNTS_OFFSET), countsBytes, 0, numBytesIntField);

                     // Convert long data into Date object
                     Date date = new Date(ByteBuffer.wrap(dateBytes).getLong());

                     // Get a string representation of the record for printing to stdout
                     String record = sdtNameString.trim() + "," + ByteBuffer.wrap(idBytes).getInt() +
                        "," + dateFormat.format(date) + "," + ByteBuffer.wrap(yearBytes).getInt() +
                        "," + new String(monthBytes).trim() + "," + ByteBuffer.wrap(mdateBytes).getInt() +
                        "," + new String(dayBytes).trim() + "," + ByteBuffer.wrap(timeBytes).getInt() +
                        "," + ByteBuffer.wrap(sensorIdBytes).getInt() + "," +
                        new String(sensorNameBytes).trim() + "," + ByteBuffer.wrap(countsBytes).getInt();
                     System.out.println(record);
                  }
               }
            }
         } else if (search_Type == constants.RANGE_KEY_DATE) {
            Date search_date = null;
            Date range_date1 = null;
            Date range_date2 = null;

            try {
               range_date1 = new SimpleDateFormat("MM/dd/yyyy").parse(key1);
               range_date2 = new SimpleDateFormat("MM/dd/yyyy").parse(key2);
            } catch (ParseException e) {
               e.printStackTrace();
            }

            while ((numBytesRead = inStream.read(page)) != -1) {
               // Process each record in page
               for (int i = 0; i < numRecordsPerPage; i++) {

                  // Copy record's SdtName (field is located at multiples of the total record byte length)
                  System.arraycopy(page, (i * numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);

                  // Check if field is empty; if so, end of all records found (packed organisation)
                  if (sdtnameBytes[0] == 0) {
                     // can stop checking records
                     break;
                  }

                  // Check for match to "text"
                  String sdtNameString = new String(sdtnameBytes);
                  String space_split[] = sdtNameString.split(" ");
                  String date_split[] = space_split[0].split("_");
                  String date = date_split[1];
                  try {
                     search_date = new SimpleDateFormat("MM/dd/yyyy").parse(date);
                  } catch (ParseException e) {
                     e.printStackTrace();
                  }

                  // range search on date
                  if (search_date.after(range_date1) && search_date.before(range_date2)) {

                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.ID_OFFSET), idBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.DATE_OFFSET), dateBytes, 0, constants.DATE_SIZE);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.YEAR_OFFSET), yearBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.MONTH_OFFSET), monthBytes, 0, constants.MONTH_SIZE);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.MDATE_OFFSET), mdateBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.DAY_OFFSET), dayBytes, 0, constants.DAY_SIZE);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.TIME_OFFSET), timeBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.SENSORID_OFFSET), sensorIdBytes, 0, numBytesIntField);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.SENSORNAME_OFFSET), sensorNameBytes, 0, constants.SENSORNAME_SIZE);
                     System.arraycopy(page, ((i * numBytesInOneRecord) + constants.COUNTS_OFFSET), countsBytes, 0, numBytesIntField);

                     // Convert long data into Date object
                     Date date1 = new Date(ByteBuffer.wrap(dateBytes).getLong());

                     // Get a string representation of the record for printing to stdout
                     String record = sdtNameString.trim() + "," + ByteBuffer.wrap(idBytes).getInt() +
                        "," + dateFormat.format(date1) + "," + ByteBuffer.wrap(yearBytes).getInt() +
                        "," + new String(monthBytes).trim() + "," + ByteBuffer.wrap(mdateBytes).getInt() +
                        "," + new String(dayBytes).trim() + "," + ByteBuffer.wrap(timeBytes).getInt() +
                        "," + ByteBuffer.wrap(sensorIdBytes).getInt() + "," +
                        new String(sensorNameBytes).trim() + "," + ByteBuffer.wrap(countsBytes).getInt();
                     System.out.println(record);
                  }
               }
            }
         }

         finishTime = System.nanoTime();
      } catch (FileNotFoundException e) {
         System.err.println("File not found " + e.getMessage());
      } catch (IOException e) {
         System.err.println("IO Exception " + e.getMessage());
      } finally {

         if (inStream != null) {
            inStream.close();
         }
      }

      long timeInMilliseconds = (finishTime - startTime) / constants.MILLISECONDS_PER_SECOND;
      System.out.println("Time taken: " + timeInMilliseconds + " ms");

   }

   public void searchHeap(String search_text, int size) throws IOException {
      String text = search_text;
      int pageSize = size;

      String datafile = "heap." + pageSize;
      long startTime = 0;
      long finishTime = 0;
      int numBytesInOneRecord = constants.TOTAL_SIZE;
      int numBytesInSdtnameField = constants.STD_NAME_SIZE;
      int numBytesIntField = Integer.BYTES;
      int numRecordsPerPage = pageSize / numBytesInOneRecord;
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
      byte[] page = new byte[pageSize];
      FileInputStream inStream = null;

      try {
         inStream = new FileInputStream(datafile);
         int numBytesRead = 0;
         startTime = System.nanoTime();
         // Create byte arrays for each field
         byte[] sdtnameBytes = new byte[numBytesInSdtnameField];
         byte[] idBytes = new byte[constants.ID_SIZE];
         byte[] dateBytes = new byte[constants.DATE_SIZE];
         byte[] yearBytes = new byte[constants.YEAR_SIZE];
         byte[] monthBytes = new byte[constants.MONTH_SIZE];
         byte[] mdateBytes = new byte[constants.MDATE_SIZE];
         byte[] dayBytes = new byte[constants.DAY_SIZE];
         byte[] timeBytes = new byte[constants.TIME_SIZE];
         byte[] sensorIdBytes = new byte[constants.SENSORID_SIZE];
         byte[] sensorNameBytes = new byte[constants.SENSORNAME_SIZE];
         byte[] countsBytes = new byte[constants.COUNTS_SIZE];
        

         // until the end of the binary file is reached
         while ((numBytesRead = inStream.read(page)) != -1) {
            
            // Process each record in page
            for (int i = 0; i < numRecordsPerPage; i++) {

               // Copy record's SdtName (field is located at multiples of the total record byte length)
               System.arraycopy(page, (i * numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);

               // Check if field is empty; if so, end of all records found (packed organisation)
               if (sdtnameBytes[0] == 0) {
                  // can stop checking records
                  break;
               }

               // Check for match to "text"
               String sdtNameString = new String(sdtnameBytes);

               // if match is found, copy bytes of other fields and print out the record
               if (sdtNameString.contains(text)) {

                  //System.out.println(sdtNameString);
                  /*
                   * Fixed Length Records (total size = 112 bytes):
                   * SDT_NAME field = 24 bytes, offset = 0
                   * id field = 4 bytes, offset = 24
                   * date field = 8 bytes, offset = 28
                   * year field = 4 bytes, offset = 36
                   * month field = 9 bytes, offset = 40
                   * mdate field = 4 bytes, offset = 49
                   * day field = 9 bytes, offset = 53
                   * time field = 4 bytes, offset = 62
                   * sensorid field = 4 bytes, offset = 66
                   * sensorname field = 38 bytes, offset = 70
                   * counts field = 4 bytes, offset = 108
                   *
                   * Copy the corresponding sections of "page" to the individual field byte arrays
                   */
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.ID_OFFSET), idBytes, 0, numBytesIntField);
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.DATE_OFFSET), dateBytes, 0, constants.DATE_SIZE);
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.YEAR_OFFSET), yearBytes, 0, numBytesIntField);
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.MONTH_OFFSET), monthBytes, 0, constants.MONTH_SIZE);
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.MDATE_OFFSET), mdateBytes, 0, numBytesIntField);
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.DAY_OFFSET), dayBytes, 0, constants.DAY_SIZE);
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.TIME_OFFSET), timeBytes, 0, numBytesIntField);
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.SENSORID_OFFSET), sensorIdBytes, 0, numBytesIntField);
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.SENSORNAME_OFFSET), sensorNameBytes, 0, constants.SENSORNAME_SIZE);
                  System.arraycopy(page, ((i * numBytesInOneRecord) + constants.COUNTS_OFFSET), countsBytes, 0, numBytesIntField);

                  // Convert long data into Date object
                  Date date = new Date(ByteBuffer.wrap(dateBytes).getLong());

                  // Get a string representation of the record for printing to stdout
                  String record = sdtNameString.trim() + "," + ByteBuffer.wrap(idBytes).getInt() +
                     "," + dateFormat.format(date) + "," + ByteBuffer.wrap(yearBytes).getInt() +
                     "," + new String(monthBytes).trim() + "," + ByteBuffer.wrap(mdateBytes).getInt() +
                     "," + new String(dayBytes).trim() + "," + ByteBuffer.wrap(timeBytes).getInt() +
                     "," + ByteBuffer.wrap(sensorIdBytes).getInt() + "," +
                     new String(sensorNameBytes).trim() + "," + ByteBuffer.wrap(countsBytes).getInt();
                  System.out.println(record);
                  break;

               }

            }
         }

         finishTime = System.nanoTime();
      } catch (FileNotFoundException e) {
         System.err.println("File not found " + e.getMessage());
      } catch (IOException e) {
         System.err.println("IO Exception " + e.getMessage());
      } finally {

         if (inStream != null) {
            inStream.close();
         }
      }

      long timeInMilliseconds = (finishTime - startTime) / constants.MILLISECONDS_PER_SECOND;
      System.out.println("Time taken: " + timeInMilliseconds + " ms");
   }
}