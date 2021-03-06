/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package njieaccount;

import static java.lang.System.exit;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.derby.drda.NetworkServerControl;
import org.controlsfx.dialog.Dialogs;

/**
 *
 * @author Harvey
 */
public class DatabaseHelperLastWorkingCopy {

    public Connection connection;
    public ResultSet resultSet;
    public ResultSetMetaData metaData;
    public int numberOfRows;
    public Statement statement;
    public boolean connectedToDatabase;
    public ResultSet result;
    public ResultSetMetaData metadata;
     PreparedStatement ps;

    public DatabaseHelperLastWorkingCopy() throws SQLException, Exception {

        this.numberOfRows = 0;
        this.statement = null;
        this.connectedToDatabase = false;
        this.resultSet = null;
        this.metaData = null;
        this.connection = null;

        createDB();//create the database if it does not exist
        // update database connection status
        connectedToDatabase = true;
    }

 
    public final void setQuery(String query)
            throws SQLException, IllegalStateException {
        ps = connection.prepareStatement(query, this.resultSet.TYPE_SCROLL_INSENSITIVE, this.resultSet.CONCUR_READ_ONLY);
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // specify query and execute it WATCH OUT: statement.executequery doesnot 
//        run on queries that return number of rows such as inserts, so I use statement.execute()
        
        //statement.execute(query);
        //resultSet = statement.getResultSet();
        ps.execute();
        resultSet = ps.getResultSet();
        if (resultSet != null) {//a resultset is null if it is an update, count, and I think insert
            // determine number of rows in ResultSet
            resultSet.last(); // move to last row
            numberOfRows = resultSet.getRow(); // get row number
            metaData = resultSet.getMetaData();
        }else{
            numberOfRows = 0;
        }

    }  // end method setQuery

    public int getColumnCount() throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // determine number of columns
        try {
            return metaData.getColumnCount();
        } // end try
        catch (SQLException sqlException) {
        } // end catch

        return 0; // if problems occur above, return 0 for number of columns
    } // end method getColumnCount

    // get name of a particular column in ResultSet
    public String getColumnName(int column) throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // determine column name
        try {
            return metaData.getColumnName(column + 1);
        } // end try
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } // end catch

        return ""; // if problems, return empty string for column name
    } // end method getColumnName
    // return number of rows in ResultSet

    public int getRowCount() throws IllegalStateException {
        // ensure database connection is available

        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        return numberOfRows;
    } // end method getRowCount

    public Object getValueAt(int row, int column)
            throws IllegalStateException {
        // ensure database connection is available
        if (!connectedToDatabase) {
            throw new IllegalStateException("Not Connected to Database");
        }

        // obtain a value at specified ResultSet row and column
        try {
            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);
        } // end try
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } // end catch

        return ""; // if problems, return empty string object
    } // end method getValueAt

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void disconnectFromDatabase() {
        if (connectedToDatabase) {
// close Statement and Connection
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } // end try
            catch (SQLException sqlException) {
            } // end catch
            finally // update database connection status
            {
                connectedToDatabase = false;
            } // end finally
        } // end if
    } // end method disconnectFromDatabase
    
    
    public int Query(String sql)
    {
        try{
            statement = connection.createStatement();
           // System.out.println(sql);
            return  statement.executeUpdate(sql);
        
        }
        catch(SQLException pp)
        {
            System.out.println(sql);
            System.out.println(pp.toString());
        }
        return 0;
    
    
    }
    public ArrayList ExecuteQuery(String sql) throws SQLException
    {
        ArrayList resultSet = new  ArrayList();
       try
       { 
           if(connection!=null)
           {
      statement = connection.createStatement();
        result = statement.executeQuery(sql);
       metadata = result.getMetaData();
     //  String[] res=null;
       int numcolls = metadata.getColumnCount();
       while(result.next())
       {
       
       for ( int i = 1; i <= numcolls; i++ )
       {
            
             System.out.println(i+":"+result.getObject(i));  
            
             resultSet.add(result.getObject(i));
       
            
       }
       }
           }
       }
       
       
       
       catch(SQLException pp)
       {
           System.out.println(pp.toString());
       }
       
       if(resultSet.isEmpty())
           resultSet.add(0);
        return resultSet;
       }

    /**
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    
       
       
       
    
        
    

    private void createDB() throws Exception  {
        try {
            String ip = "192.168.1.244";
//            String ip = "localhost";

            System.out.println("working with the new databasehelper class to try and fix the network problem with wati b.");
            
            NetworkServerControl server = new NetworkServerControl
            (InetAddress.getByName(ip),1527);
            server.start(null);
            
            String userHomeDir = System.getProperty("user.home", ".");
            String systemDir = userHomeDir + "/.NjiebaseUS";
            // Set the db system directory.
            System.setProperty("derby.system.home", systemDir);
            
            String url = "jdbc:derby://" + ip + ":1527/njieDB;create=true";
            Properties props = new Properties();
            props.put("njie_909_UniSys", "__njie909UniSys__");
            
            try {
                //Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                connection = DriverManager.getConnection(url, props);
                statement = connection.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("connection failuer ===---==-");
                Dialogs.create().message("error connecting to the server " + e.getMessage()).showInformation();
                System.out.println(e.getMessage());
                exit(1);
            }
            
            //This is the query string
            String query;
            
            /*-------------------------------------------------------------------------------------------*/
            System.out.println("creating ACCOUNTS table");
            query = "CREATE TABLE njieDB.ACCOUNT ( " +
                        "  ACC_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY " +
                        "  (START WITH 1, INCREMENT BY 1), " +
                        "  USERNAME VARCHAR(20), " +
                        "  PASSWORD VARCHAR(45)," +
                        "  ROLE CHAR(1), " +
                        "  C_TIME TIMESTAMP, " +
                        "  UNIQUE (ACC_ID, C_TIME), " +
                        "  PRIMARY KEY ( USERNAME ) " +
                        "  )";
            statement.execute(query);

            System.out.println("creating EXPENSE TRANS CLASS table");
            query = "CREATE TABLE njieDB.EXP_TRANS_CLASS ( " +
                        "  CODE INTEGER NOT NULL, " +
                        "  NAME VARCHAR(20), " +
                        "  INFO VARCHAR(200), " +
                        "  C_DATE TIMESTAMP, " +
                        "  PRIMARY KEY (CODE)" +
                        "  )";
            statement.execute(query);

            System.out.println("creating EXPENSE CLASS table");
            query = "CREATE TABLE njieDB.EXPENSES (" +
                        "  TRANS_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY " +
                        "  (START WITH 1, INCREMENT BY 1), " +
                        "  CODE INTEGER NOT NULL, " +
                        "  MEMO VARCHAR(300)," +
                        "  AMOUNT INTEGER," +
                        "  DAT DATE, " +
                        "  TIM TIME, " +
                        "  PRIMARY KEY (TRANS_ID), " +
                        "  FOREIGN KEY ( code ) " +
                        "   REFERENCES njiedb.EXP_TRANS_CLASS (code )" +
                        "   on delete no action " +
                        "   on update no action" +
                        "  )";
            statement.execute(query);

            System.out.println("creating INCOME TRANSACTION CLASS table");
            query = "CREATE TABLE njieDB.INC_TRANS_CLASS (" +
                        "  CODE INTEGER NOT NULL," +
                        "  NAME VARCHAR(20)," +
                        "  INFO VARCHAR(200), " +
                        "  C_DATE TIMESTAMP, " +
                        "  PRIMARY KEY (CODE)" +
                        "  )";
            statement.execute(query);

            System.out.println("creating INCOME table");
            query = "CREATE TABLE njieDB.INCOME (" +
                        "  TRANS_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY " +
                        "  (START WITH 1, INCREMENT BY 1), " +
                        "  CODE INTEGER NOT NULL,  " +
                        "  MEMO VARCHAR(300)," +
                        "  AMOUNT INTEGER," +
                        "  DAT DATE, " +
                        "  TIM TIME, " +
                        "  PRIMARY KEY (TRANS_ID), " +
                        "  FOREIGN KEY ( code ) " +
                        "   REFERENCES njiedb.INC_TRANS_CLASS (code )" +
                        "   on delete no action " +
                        "   on update no action" +
                        "  )";
            statement.execute(query);

            System.out.println("creating STAFF table");
            query = "CREATE TABLE njieDB.STAFF (" +
                        "  STAFF_ID INTEGER NOT NULL," +
                        "  NAME VARCHAR(30)," +
                        "  PHONE_NUMBER BIGINT," +
                        "  SALARY BIGINT, " +
                        "  BRANCH VARCHAR(20), " +
                        "  EMP_DATE DATE NOT NULL, " +
                        "  PRIMARY KEY (STAFF_ID)" +
                        "  )";
            statement.execute(query);

            System.out.println("creating PAYROLL table");
            query = "CREATE TABLE njieDB.PAYROLL (" +
                        "   PAYMT_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY " +
                        "   (START WITH 1, INCREMENT BY 1), " +
                        "   STAFF_ID INTEGER," +
                        "   AMOUNT_PAID BIGINT," +
                        "   PAY_DATE DATE," +
                        "   PRIMARY KEY( PAYMT_ID ), " +
                        "   FOREIGN KEY ( staff_id ) " +
                        "   REFERENCES njiedb.STAFF (staff_id )" +
                        "   on delete no action " +
                        "   on update no action " +
                        "  )";
            statement.execute(query);
            
            
            /*This is the most recent table i added. It is to aid the problem of changing salary causing excess salary*/
            System.out.println("creating MONTHS SALARY PAYMENT table");
            query = "CREATE TABLE njieDB.MONTHLY_SALARY_PAYMENT ( " +
                        "   DAT DATE NOT NULL, " +
                        "   SALARY BIGINT, " + 
                        "   TOTAL_PAID BIGINT DEFAULT 0, " +
                        "   STAFF_ID INTEGER NOT NULL, " +
                        "   PRIMARY KEY ( DAT, STAFF_ID ), " +
                        "   FOREIGN KEY ( staff_id ) " +
                        "   REFERENCES njiedb.STAFF (staff_id )" +
                        "   on delete no action " +
                        "   on update no action " +
                        "  )";
            statement.execute(query);

            System.out.println("creating OVERDRAFT table");
            query = "CREATE TABLE njieDB.OVERDRAFT (" +
                        "  OV_ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY " +
                        "  (START WITH 1, INCREMENT BY 1), " +
                        "  STAFF_ID INTEGER," +
                        "  AMOUNT INTEGER," +
                        "  DATE DATE DEFAULT CURRENT_DATE, " +
                        "  TIM_ISSUED TIME DEFAULT CURRENT_TIME,   " +
                        "  DEDUCTION_PER_MONTH INTEGER," +
                        "  EXP_STATUS BOOLEAN DEFAULT false, " +
                        "  DEDUCTION_START_DATE DATE, " +
                        "  DEDUCT_UNTIL DATE, " +
                        "  PAY_STATUS BOOLEAN DEFAULT false , " +
                        "  PRIMARY KEY (OV_ID), " +
                        "  FOREIGN KEY ( staff_id ) " +
                        "   REFERENCES njiedb.STAFF (staff_id )" +
                        "   on delete no action " +
                        "   on update no action" +
                        "  )";
            statement.execute(query);

            System.out.println("creating CUSTUMER table");
            query = "CREATE TABLE njieDB.CUSTUMER ( " +
                        "   CUST_ID VARCHAR(10) NOT NULL, " +
                        "   NAME VARCHAR(30), " +
                        "   PREFERED_GOODS VARCHAR(400), " +
                        "   C_DATE DATE, " +
                        "   PRIMARY KEY (CUST_ID) " +
                        "  )";
            statement.execute(query);

            System.out.println("creating DEBTS table");
            query = "CREATE TABLE njieDB.DEBTS ( " +
                        "   DEBT_ID INT  NOT NULL GENERATED ALWAYS AS IDENTITY " +
                        "   (START WITH 1, INCREMENT BY 1), " +
                        "   CUST_ID VARCHAR(10) NOT NULL, " +
                        "   AMOUNT BIGINT NOT NULL, " +
                        "   DATE_ISSUED TIMESTAMP, " +
                        "   PRIMARY KEY (DEBT_ID), " +
                        "   FOREIGN KEY (cust_id ) " +
                        "   REFERENCES njiedb.CUSTUMER ( cust_id ) " +
                        "   on delete no action" +
                        "   on update no action" +
                        "  )";
            statement.execute(query);

            System.out.println("creating DEBT PAYMENT table");
            query = "CREATE TABLE njieDB.DEBT_PAYMENT (" +
                        "   PAY_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY " +
                        "   (START WITH 1, INCREMENT BY 1), " +
                        "   EXP_ID INTEGER NOT NULL," +
                        "   CUST_ID VARCHAR(10), " +
                        "   AMOUNT_PAID INTEGER," +
                        "   DATE DATE, " +
                        "   PRIMARY KEY(PAY_ID), " +
                        "   FOREIGN KEY (cust_id ) " +
                        "   REFERENCES njiedb.CUSTUMER ( cust_id ) " +
                        "   on delete no action" +
                        "   on update no action, " +
                        "   FOREIGN KEY (exp_id)" +
                        "   REFERENCES njiedb.INCOME (trans_id)" +
                        "   on delete no action " +
                        "   on update no action" +
                        "   )";
            statement.execute(query);

            System.out.println("creating SUPPLIER table");
            query = "CREATE TABLE njieDB.SUPPLIER (" +
                        "  SUP_CODE INTEGER NOT NULL," +
                        "  NAME VARCHAR(45)," +
                        "  SERVICES VARCHAR(200), " +
                        "  PRIMARY KEY (SUP_CODE)" +
                        "  )";
            statement.execute(query);

            System.out.println("creating SUPPLIER PAY ORDER table");
            query = "CREATE TABLE njieDB.SUPPLIER_PAY_ORDER (" +
                        "   ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY " +
                        "   (START WITH 1, INCREMENT BY 1), " +
                        "   SUPPLIER_CODE INTEGER NOT NULL," +
                        "   AMOUNT INTEGER," +
                        "   ORDER_DATE DATE ," +
                        "   PRIMARY KEY (ID), " +
                        "   FOREIGN KEY (supplier_code) " +
                        "   REFERENCES njiedb.SUPPLIER (sup_code) " +
                        "   on delete no action " +
                        "   on update no action" +
                        "  )";
            statement.execute(query);

            System.out.println("creating SUPPLIER PAY table");
            query = "CREATE TABLE njieDB.SUPPLIER_PAY (" +
                        "   PAY_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY " +
                        "   (START WITH 1, INCREMENT BY 1), " +
                        "   EXP_ID INTEGER NOT NULL," +
                        "   SUP_ID INT, " +
                        "   AMOUNT_PAID INTEGER," +
                        "   DATE DATE, " +
                        "   PRIMARY KEY(PAY_ID), " +
                        "   FOREIGN KEY (sup_id) " +
                        "   REFERENCES njiedb.SUPPLIER (sup_code) " +
                        "   on delete no action " +
                        "   on update no action, " +
                        "   FOREIGN KEY (exp_id) " +
                        "   REFERENCES njiedb.EXPENSES (trans_id) " +
                        "   on delete no action " +
                        "   on update no action" +
                        "  )";
            statement.execute(query);

            System.out.println("creating SALARY SUSPENSION table");
            query = "create table njiedb.salary_suspension(" +
                        "   id int not null GENERATED ALWAYS AS IDENTITY " +
                        "   (START WITH 1, INCREMENT BY 1), " +
                        "   sus_amt int not null, " +
                        "   staff_id int not null, " +
                        "   dat date not null, " +
                        "   primary key (id), " +
                        "   foreign key ( staff_id ) " +
                        "   references njiedb.staff ( staff_id ) " +
                        "   on delete no action " +
                        "   on update no action " +
                        "  )";
            statement.execute(query);
            
            System.out.println("end of table creation");
            /*------------------------------------------------------------------------------------------*/
            
            
            
            /***********************************
             *CREATING DEFAULT ACCOUNTS FOR EXPENSE TRANSACTION CLASS: 1->SUPPLIERpAYMENT 2->PAYROLL 3->OVERDRAFT 
             ***********************************
             * NOTE THAT THIS HAS TO BE REMOVED BEFORE DEPLOYMENT AS ITS JUST FOR TESTING AND IS GOING GOING TO CAUSE SOME ERROR
             */
            System.out.println("Creating default expenses transaction classes"
                    + " -> 1 paySupplier, 2 payroll, 3 Overdraft");
            query = "INSERT INTO njieDB.EXP_TRANS_CLASS(njieDB.EXP_TRANS_CLASS.CODE, njieDB.EXP_TRANS_CLASS.NAME, "
                    + " njieDB.EXP_TRANS_CLASS.INFO, njieDB.EXP_TRANS_CLASS.C_DATE ) "
                    + " VALUES (" + 1 + ", 'SUPPLIER PAYMENT', 'PAYMENT MADE TO SUPPLIER FOR GOODS SUPPLIED TO THE COMPANY', CURRENT_TIMESTAMP ) ";
            statement.execute(query);
            query = "INSERT INTO njieDB.EXP_TRANS_CLASS(njieDB.EXP_TRANS_CLASS.CODE, njieDB.EXP_TRANS_CLASS.NAME, "
                    + " njieDB.EXP_TRANS_CLASS.INFO, njieDB.EXP_TRANS_CLASS.C_DATE ) "
                    + " VALUES (" + 2 + ", 'PAYROLL', 'PAYMENT TO STAFFS DURING AND AT THE END OF THE MONTH', CURRENT_TIMESTAMP ) ";
            statement.execute(query);
            query = "INSERT INTO njieDB.EXP_TRANS_CLASS(njieDB.EXP_TRANS_CLASS.CODE, njieDB.EXP_TRANS_CLASS.NAME, "
                    + " njieDB.EXP_TRANS_CLASS.INFO, njieDB.EXP_TRANS_CLASS.C_DATE ) "
                    + " VALUES (" + 3 + ", 'OVERDRAFT', 'OVERDRAFT ISSUED TO WORKERS. PARTICULAR AMOUNTS WILL BE DEDUCTED FROM THEIR SALARIES UNTIL THE TOTAL AMOUNT IS TOTALY PAID', CURRENT_TIMESTAMP ) ";
            statement.execute(query);
            System.out.println("end creating default expense transaction accounts");
            
            /***********************************
             *CREATING DEFAULT ACCOUNTS FOR INCOME TRANSACTION CLASS: 1->DEBTS 2->PAYROLL 3->OVERDRAFT 
             ***********************************
             */
            System.out.println("Creating default income transaction classes"
                    + " -> 1 custumer debts");
            query = "INSERT INTO njieDB.INC_TRANS_CLASS(njieDB.INC_TRANS_CLASS.CODE, njieDB.INC_TRANS_CLASS.NAME, "
                    + " njieDB.INC_TRANS_CLASS.INFO, njieDB.INC_TRANS_CLASS.C_DATE ) "
                    + " VALUES (" + 1 + ", 'CUSTUMER PAYBACK', 'DEBTS PAID BY CUSTUMERS OF THE ENTERPRISE', CURRENT_TIMESTAMP ) ";
            statement.execute(query);
            System.out.println("end of creating the income transaction class");
            
            
            /************************************************
             *      LOADING DEFAULT SETTINGS FOR THE USER
             * **********************************************
             */
            Dialogs.create().title("Default Transaction classes")
                    .masthead("THIS PRODUCT IS LISCENCE FOR ONE(1) YEAR\n AFTER WHICH YOU WILL BE REQUIRED TO RENEW YOUR LISCENCE")
                    .message("Welcome to the accountx soft from Unity Systems."
                    + " DEFAULT SETTINGS. certain accounts have been created by default and are going to be used by various services. It is adviced that they should not be deleted. They fall under 2 classes. INCOME and EXPENSES class which include"
                    + " INCOME.         1->CUSTUMER PAYBACK \n"
                    + " EXPENSES        1->SUPPLIER PAYMENT\n"
                    + "                 2->PAYROLL \n"
                    + "                 3->OVERDRAFT\n"
                    + "\nDETAILS ABOUT THESE TRANSACTION CLASS CAN BE FOUND IN THE MANAGERS SECTION UNDER THE TRANSACTION TAB."
                    + "         \n\n @US").showInformation();
            
            /*Insert default users*/
            System.out.println("Inserting default users : admin admin manager and user user as cashier  ");
            System.out.println("root->root->manager");
            query = "INSERT INTO njieDB.ACCOUNT(njieDB.ACCOUNT.C_TIME, njieDB.ACCOUNT.USERNAME, njieDB.ACCOUNT.PASSWORD, njieDB.ACCOUNT.ROLE) VALUES(CURRENT_TIMESTAMP, 'ADMIN', 'YOURSELF', 'M')";
            statement.execute(query);
            
            System.out.println("user->user->cashier");
            query = "INSERT INTO njieDB.ACCOUNT(njieDB.ACCOUNT.C_TIME, njieDB.ACCOUNT.USERNAME, njieDB.ACCOUNT.PASSWORD, njieDB.ACCOUNT.ROLE) VALUES(CURRENT_TIMESTAMP, 'user', 'user', 'C')";
            statement.execute(query);
            
            
            
            System.out.println("---------------------- end of database modelling -------------------------");
            
            
            
            
         
        }
        catch (SQLException ex) {
            //ex.printStackTrace();
            System.out.println(""+ex.getCause()); 
        
        }
        
    }
}


