package org.ssa.ironyard.liquorstore.dao.orm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.ssa.ironyard.liquorstore.model.Admin;
import org.ssa.ironyard.liquorstore.model.Password;

public class ORMAdminImpl extends AbstractORM<Admin> implements ORM<Admin>
{
    public ORMAdminImpl()
    {
        this.primaryKeys.add("id");

        this.fields.add("userName");
        this.fields.add("salt");
        this.fields.add("hash");
        this.fields.add("firstName");
        this.fields.add("lastName");
        this.fields.add("role");
    }

    @Override
    public String table()
    {
        return "admin";
    }

    @Override
    public Admin map(ResultSet results) throws SQLException
    {
        Integer id = results.getInt("admin.id");
        String userName = results.getString("admin.userName");
        Password password = new Password(results.getString("admin.salt"), results.getString("admin.hash"));
        String firstName = results.getString("admin.firstName");
        String lastName = results.getString("admin.lastName");
        Integer role = results.getInt("admin.role");
        
        return new Admin(id, userName, password, firstName, lastName, role);
    }

}
