package org.ssa.ironyard.liquorstore.dao.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssa.ironyard.liquorstore.model.CoreProduct;
import org.ssa.ironyard.liquorstore.model.CoreProduct.Tag;
import org.ssa.ironyard.liquorstore.model.CoreProduct.Type;

public class ORMCoreProductImpl extends AbstractORM<CoreProduct> implements ORM<CoreProduct>
{    
    static Logger LOGGER = LogManager.getLogger(ORMCoreProductImpl.class);
    
    public ORMCoreProductImpl()
    {
        this.primaryKeys.add("id");

        this.fields.add("name");
        this.fields.add("type");
        this.fields.add("subtype");
        this.fields.add("description");
    }

    @Override
    public String table()
    {
        return "core_product";
    }

    @Override
    public CoreProduct map(ResultSet results) throws SQLException
    {
        Integer id = results.getInt(table() + ".id");
        String name = results.getString(table() + ".name");
        Type type = Type.getInstance(results.getString(table() + ".type"));
        String subType = results.getString(table() + ".subtype");
        String description = results.getString(table() + ".description");
        List<Tag> tags = new ArrayList<>();
        
        CoreProduct coreProduct = new CoreProduct(id, name, tags, type, subType, description);
        
        coreProduct.setLoaded(true);
        
        return coreProduct;
    }
    
    public Tag mapTag(ResultSet results) throws SQLException
    {
        return new Tag(results.getString("product_tags.name"));
    }
    
    public String prepareInsertTag()
    {
        return " INSERT INTO product_tags (core_product_id, name) values (?, ?)";
        
    }
    
    private String joinProductTags()
    {
        return " JOIN product_tags ON " + this.table() + "." + this.primaryKeys.get(0) + " = product_tags.core_product_id ";
    }

    @Override
    public String prepareReadByIds(Integer numberOfIds)
    {
        String readByIds = " SELECT " + this.projection() +  " FROM " + this.table()
                + " WHERE " + this.table() + "." + this.primaryKeys.get(0) + " IN ( ";
        
        for(int i = 0; i < numberOfIds; i++)
        {
            readByIds = readByIds + " ?, ";
        }
        
        readByIds = readByIds.substring(0, readByIds.length() - 2) + " ) ";
        
        LOGGER.debug(this.getClass().getSimpleName());
        LOGGER.debug("Read By IDs prepared Statement: {}", readByIds);
        
        return readByIds;
    }
  

}
