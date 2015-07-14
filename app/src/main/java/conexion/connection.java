package conexion;

/**
 * Created by arleckk on 11/07/2015.
 */

import android.content.Context;
import android.widget.Toast;

import com.arleckk.s_cool.R;

import java.sql.*;

public class connection {

    public String usuario = "arleck_SQLLogin_1";
    public String password = "iwpipmqe86";
    public String url = "jdbc:sqlserver://scooldatabase.mssql.somee.com;databaseName=scooldatabase";
    public Connection cn = null;
    public Statement st = null;
    public Context context;


    public Statement Conectar()
    {
        try
        {
            Connection cn = DriverManager.getConnection(url,usuario,password);
            st=cn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException i)
        {

        }
        return st;
    }





}

