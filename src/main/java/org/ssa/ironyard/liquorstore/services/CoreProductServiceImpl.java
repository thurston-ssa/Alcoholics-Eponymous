package org.ssa.ironyard.liquorstore.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ssa.ironyard.liquorstore.dao.DAOCoreProduct;
import org.ssa.ironyard.liquorstore.model.CoreProduct;
import org.ssa.ironyard.liquorstore.model.CoreProduct.Type;
import org.ssa.ironyard.liquorstore.model.Product;

@Service
public class CoreProductServiceImpl implements CoreProductService
{
    
    DAOCoreProduct daoCP;
    
    @Autowired
    public CoreProductServiceImpl(DAOCoreProduct daoCP)
    {
        this.daoCP = daoCP;
    }

    @Override
    public CoreProduct readCoreProduct(Integer id)
    {
        return daoCP.read(id);
    }

    @Override
    public List<CoreProduct> readAllCoreProduct()
    {
       return daoCP.readAll();
    }

    @Override
    public CoreProduct editCoreProduct(CoreProduct coreproduct)
    {
        if(daoCP.read(coreproduct.getId()) == null)
            return null;
        
        CoreProduct cp = new CoreProduct(coreproduct.getId(),coreproduct.getName(),coreproduct.getTags(),coreproduct.getType(),coreproduct.getSubType(),coreproduct.getDescription());
        return daoCP.update(cp);
    }

    @Override
    public CoreProduct addCoreProduct(CoreProduct coreproduct)
    {
        CoreProduct cp = new CoreProduct(coreproduct.getName(),coreproduct.getTags(),coreproduct.getType(),coreproduct.getSubType(),coreproduct.getDescription());
        return daoCP.insert(cp);
    }

    @Override
    public boolean deleteCoreProduct(Integer id)
    {
        if(daoCP.read(id) == null)
            return false;
        return daoCP.delete(id);
    }

    @Override
    public List<Product> searchCoreProduct(Product product)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Product> searchType(Type cpTypea)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Product> searchDateTimeFrame(LocalDate date1, LocalDate date2)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    

}
