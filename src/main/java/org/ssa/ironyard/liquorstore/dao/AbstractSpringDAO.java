package org.ssa.ironyard.liquorstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.ssa.ironyard.liquorstore.dao.orm.ORM;
import org.ssa.ironyard.liquorstore.dao.orm.ORMProductImpl;
import org.ssa.ironyard.liquorstore.model.Customer;
import org.ssa.ironyard.liquorstore.model.DomainObject;
import org.ssa.ironyard.liquorstore.model.Product;

/**
 *
 * @author thurston
 * @param <T>
 */
public abstract class AbstractSpringDAO<T extends DomainObject> implements DAO<T>
{
    protected final ORM<T> orm;
    protected final DataSource dataSource;
    protected final JdbcTemplate springTemplate;
    protected ResultSetExtractor<T> extractor;
    protected ResultSetExtractor<List<T>> listExtractor;

    protected AbstractSpringDAO(ORM<T> orm, DataSource dataSource)
    {
        this.orm = orm;
        this.dataSource = dataSource;
        this.springTemplate = new JdbcTemplate(dataSource);
        this.extractor = (ResultSet cursor) ->
        {
            if (cursor.next())
                return this.orm.map(cursor);
            return null;
        };
        
        this.listExtractor = (ResultSet cursor) ->
        {
            List<T> resultList = new ArrayList<>();
            while (cursor.next())
            {
                resultList.add(this.orm.map(cursor));
            }

            return resultList;
        };
    }

    @Override
    public T read(Integer id)
    {
        if (null == id)
            return null;
        return this.springTemplate.query(this.orm.prepareRead(), (PreparedStatement ps) -> ps.setInt(1, id),
                this.extractor);

    }
    
    @Override
    public List<T> readByIds(List<Integer> ids)
    {
        List<T> t = new ArrayList<>();

        if (ids.size() == 0)
            return t;
        return this.springTemplate.query(this.orm.prepareReadByIds(ids.size()),
                (PreparedStatement ps) ->
                {
                    for (int i = 0; i < ids.size(); i++)
                    {
                        ps.setInt(i + 1, ids.get(i));
                    }

                }, this.listExtractor);
    }
    
    @Override
    public List<T> readAll()
    {
        return this.springTemplate.query(this.orm.prepareReadAll(), this.listExtractor);
    }

    @Override
    public T insert(T domain)
    {
        if (null == domain)
            return domain;
        KeyHolder generatedId = new GeneratedKeyHolder();
        if (this.springTemplate.update((Connection conn) ->
        {
            PreparedStatement statement = conn.prepareStatement(this.orm.prepareInsert(),
                    Statement.RETURN_GENERATED_KEYS);
            insertPreparer(statement, domain);
            return statement;
        }, generatedId) == 1)
        {
            T copy = (T) domain.clone(); // necessary to maintain 'immutable'
                                         // semantics
            return afterInsert(copy, generatedId.getKey().intValue());
        }
        return null;
    }

    @Override
    public T update(T domain)
    {
        if (null == domain || null == domain.getId())
            return null;

        if (1 == this.springTemplate.update(this.orm.prepareUpdate(), updatePreparer(domain)))
            return afterUpdate((T) domain.clone());
        return null;
    }

    @Override
    public boolean delete(Integer id)
    {
        if (null == id)
            return false;
        return 1 == this.springTemplate.update(this.orm.prepareDelete(), (PreparedStatement ps) -> ps.setInt(1, id));
    }

    @Override
    public void clear()
    {
        this.springTemplate.update("DELETE FROM " + this.orm.table());
    }

    /**
     * Set the parameters on the
     * <dd>insertStatement</dd> from
     * <dd>domainToInsert</dd>
     * 
     * @param insertStatement
     *            the {@link PreparedStatement}
     * @param domainToInsert
     * @throws SQLException
     */
    protected abstract void insertPreparer(PreparedStatement insertStatement, T domainToInsert) throws SQLException;

    /**
     * Note: set id and loaded to true
     * 
     * @param copy
     *            safe to modify, if using mutable {@link DomainObject domain
     *            objects}
     * @param id
     * @return the domain object with its id properly set
     */
    protected abstract T afterInsert(T copy, Integer id);

    /**
     * In most cases, is just used to ensure {@link DomainObject#isLoaded() }
     * 
     * @param copy
     *            safe to modify, if using mutable {@link DomainObject domain
     *            objects}
     * @return
     */
    protected abstract T afterUpdate(T copy);

    protected abstract PreparedStatementSetter updatePreparer(T domainToUpdate);
}
