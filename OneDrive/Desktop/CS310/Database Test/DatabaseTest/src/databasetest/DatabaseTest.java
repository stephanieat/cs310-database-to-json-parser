package databasetest;

import java.sql.*;
import java.util.ArrayList;
import com.opencsv.*;
import org.json.simple.*;

public class DatabaseTest {

    public static void main(String[] args) {
        
        System.out.println("***************************");
        System.out.println("DATABASE TO JSON PARSER");
        System.out.println("***************************");
        System.out.println(getJSONData().toString());
    }    
    public static JSONArray getJSONData()
    {                          
        Connection conn = null;
        PreparedStatement pstSelect = null, pstUpdate = null;
        ResultSet resultset = null;
        ResultSetMetaData metadata = null;
        
        String query, value;
        ArrayList <String> key = new ArrayList <>();
        JSONArray data = new JSONArray();
        
        boolean hasresults;
        int resultCount, columnCount, updateCount = 0;
        
        try {
            
            /* Identify the Server */
            
            String server = ("jdbc:mysql://localhost/p2_test");
            String username = "root";
            String password = "root";
            System.out.println("Connecting to " + server + "...");
            
            /* Load the MySQL JDBC Driver */
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            /* Open Connection */

            conn = DriverManager.getConnection(server, username, password);

            /* Test Connection */
            
            if (conn.isValid(0)) {
                
                /* Connection Open! */
                
                System.out.println("Connected Successfully!");
                
                // Prepare Select Query
                
                query = "SELECT * FROM people";
                pstSelect = conn.prepareStatement(query);
                
                // Execute Select Query
                
                hasresults = pstSelect.execute();
                
                // Get Results
                
                if (updateCount > 0) {
            
                    resultset = pstUpdate.getGeneratedKeys();

                    if (resultset.next()) {

                        System.out.print("Update Successful!  New Key: ");
                        System.out.println(resultset.getInt(1));

                    }

                }                
                
                /* Prepare Select Query */
                
                query = "SELECT * FROM people";
                pstSelect = conn.prepareStatement(query);
                
                /* Execute Select Query */
                
                System.out.println("Submitting Query ...");
                
                hasresults = pstSelect.execute();                
                
                /* Get Results */
                
                System.out.println("Getting Results ...");
                
                while ( hasresults || pstSelect.getUpdateCount() != -1 ) {

                    if ( hasresults ) {
                        
                        /* Get ResultSet Metadata */
                        
                        resultset = pstSelect.getResultSet();
                        metadata = resultset.getMetaData();
                        columnCount = metadata.getColumnCount();
                        
                        /* Get Column Names; Print as Table Header */
                        
                        for (int i = 2; i <= columnCount ; i++) {

                            key.add(metadata.getColumnLabel(i));

                            System.out.format("%20s", key);

                        }
                        
                        /* Get Data; Print as Table Rows */
                        
                        while(resultset.next()) {
                            
                            /* Begin Next ResultSet Row */

                            JSONObject object = new JSONObject();
                            
                            /* Loop Through ResultSet Columns; Print Values */

                            for (int i = 2; i <=columnCount; i++) {

                                JSONObject Jobject = new JSONObject();
                                value = resultset.getString(i);

                                if (resultset.wasNull()) {
                                    Jobject.put(key.get(i - 2), "NULL");
                                    Jobject.toJSONString();
                                }

                                else {
                                    Jobject.put(key.get(i - 2), value);
                                    Jobject.toString();
                                }
                            object.putAll(Jobject);
                            }
                        data.add(object);
                        }
                        
                    }

                    else {

                        resultCount = pstSelect.getUpdateCount();  

                        if ( resultCount == -1 ) {
                            break;
                        }

                    }
                    
                    /* Check for More Data */

                    hasresults = pstSelect.getMoreResults();

                }
                
            }
            
            System.out.println();
            
            /* Close Database Connection */
            
            conn.close();
            
        }
        
        catch (Exception e) {
            System.err.println(e.toString());
        }
        
        /* Close Other Database Objects */
        
        finally {
            
            if (resultset != null) { try { resultset.close(); resultset = null; } catch (Exception e) {} }
            
            if (pstSelect != null) { try { pstSelect.close(); pstSelect = null; } catch (Exception e) {} }
            
            if (pstUpdate != null) { try { pstUpdate.close(); pstUpdate = null; } catch (Exception e) {} }
            
        }
        
    return data;
        
    }
    
}
        