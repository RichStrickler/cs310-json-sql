package databasetest;

import java.sql.*;
import org.json.simple.*;

public class DatabaseTest {
    
    private String server = null;
    private String username = null;
    private String password = null;
    
    public DatabaseTest(){
        server = ("jdbc:mysql://localhost/p2_test");
        username = "root";
        password = "CS488";
    }
    
    public JSONArray getJSONData(){
        
        Connection conn = null;
        PreparedStatement pstSelect = null;
        ResultSet resultset = null;
        ResultSetMetaData metadata = null;
        
        JSONObject jsonFile = new JSONObject();
        JSONArray headData = new JSONArray();
        JSONArray finArray = new JSONArray();
        
        String query, key, value;
        boolean hasresults;
        int resultCount, columnCount, updateCount = 0;
        
        try {
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            conn = DriverManager.getConnection(server, username, password);
        
            if (conn.isValid(0)) {
                
                query = "SELECT * FROM people";
                pstSelect = conn.prepareStatement(query);
                hasresults = pstSelect.execute();   
                
                while ( hasresults || pstSelect.getUpdateCount() != -1 ) {

                    if ( hasresults ) {
                        
                        resultset = pstSelect.getResultSet();
                        metadata = resultset.getMetaData();
                        columnCount = metadata.getColumnCount();
                        
                        for (int i = 1; i <= columnCount; i++) {
                            key = metadata.getColumnLabel(i);
                            headData.add(key);
                        }
                        
                        while(resultset.next()) { 
                            for (int i = 1; i <= columnCount; i++) {

                                value = resultset.getString(i);

                                if (!resultset.wasNull()) {
                                    if(i != 1){
                                        jsonFile.put(headData.get(i-1),value);
                                    }
                                }
                            }
                            finArray.add(jsonFile.clone());
                            jsonFile.clear();
                        }   
                    }
                    else {
                        resultCount = pstSelect.getUpdateCount();  
                        if ( resultCount == -1 ) {
                            break;
                        }
                    }
                    hasresults = pstSelect.getMoreResults();
                }
            
                
            }
            conn.close();
        }
        catch(Exception e) { return finArray; }
        
        return finArray;
    }
    
}