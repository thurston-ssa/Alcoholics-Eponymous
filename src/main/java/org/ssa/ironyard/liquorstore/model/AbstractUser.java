package org.ssa.ironyard.liquorstore.model;

public abstract class AbstractUser extends AbstractDomainObject
{
    
    Boolean isAdmin;

    public AbstractUser(Integer id)
    {
        super(id);
    }

    protected Boolean isAdmin()
    {
        return this.isAdmin;
    }

}
