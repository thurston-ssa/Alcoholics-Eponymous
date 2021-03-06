package org.ssa.ironyard.liquorstore.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.ssa.ironyard.liquorstore.dao.orm.ORMOrderImpl;
import org.ssa.ironyard.liquorstore.model.Order;
import org.ssa.ironyard.liquorstore.model.Order.OrderDetail;

import com.mysql.cj.api.jdbc.Statement;

@Repository
public class DAOOrderImpl extends AbstractDAOOrder implements DAOOrder
{
    @Autowired
    public DAOOrderImpl(DataSource dataSource)
    {
        super(new ORMOrderImpl(), dataSource);

        this.extractor = (ResultSet cursor) ->
        {
            Order currentOrder = null;

            List<OrderDetail> orderDetail = new ArrayList<>();

            while (cursor.next())
            {
                currentOrder = this.orm.map(cursor);

                orderDetail.addAll(currentOrder.getoD());
            }

            if (currentOrder != null)
            {
                currentOrder.setoD(orderDetail);
                return currentOrder;
            }

            return null;
        };

        this.listExtractor = (ResultSet cursor) ->
        {
            Map<Order, Order> orderResults = new HashMap<>();
            List<Order> orderList = new ArrayList<>();

            while (cursor.next())
            {
                Order currentOrder;
                Order o = this.orm.map(cursor);
                if ((currentOrder = orderResults.get(o)) != null)
                {
                    List<OrderDetail> orderDetail = o.getoD();

                    orderDetail.addAll(currentOrder.getoD());
                    o.setoD(orderDetail);
                }

                orderResults.put(o, o);
            }

            orderList = orderResults.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());

            return orderList;
        };
    }

    @Override
    protected void insertPreparer(PreparedStatement insertStatement, Order domainToInsert) throws SQLException
    {
        insertStatement.setInt(1, domainToInsert.getCustomer().getId());
        insertStatement.setTimestamp(2, Timestamp.valueOf(domainToInsert.getDate()));
        insertStatement.setString(3, domainToInsert.getOrderStatus().toString());
        insertStatement.setBigDecimal(4, domainToInsert.getTotal());
        insertStatement.setTimestamp(5, Timestamp.valueOf(domainToInsert.getTimeOfOrder()));
    }

    private void insertDetailPreparer(PreparedStatement insertStatement, OrderDetail orderDetail, Order order)
            throws SQLException
    {
        insertStatement.setInt(1, order.getId());
        insertStatement.setInt(2, orderDetail.getProduct().getId());
        insertStatement.setInt(3, orderDetail.getQty());
        insertStatement.setBigDecimal(4, orderDetail.getUnitPrice());
    }

    @Override
    public Order insertDetail(Order domain)
    {
        if (domain.getId() != null)
        {
            List<OrderDetail> orderDetails = new ArrayList<>();

            for (OrderDetail od : domain.getoD())
            {

                KeyHolder generatedId = new GeneratedKeyHolder();
                if (this.springTemplate.update((Connection conn) ->
                {
                    PreparedStatement statement = conn.prepareStatement(((ORMOrderImpl) this.orm).prepareInsertDetail(),
                            Statement.RETURN_GENERATED_KEYS);
                    insertDetailPreparer(statement, od, domain);
                    return statement;
                }, generatedId) == 1)
                {
                    orderDetails.add(new OrderDetail(od.getProduct(), od.getQty(), od.getUnitPrice()));
                }
            }

            Order copy = (Order) domain.clone();

            copy.setoD(orderDetails);

            return copy;
        }

        return null;
    }

    @Override
    public Order insert(Order domain)
    {
        if(domain == null)
            return domain;
        
        domain.setTimeOfOrder(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Order order;

        if ((order = super.insert(domain)) != null)
        {
            return this.insertDetail(order);
        }

        return null;
    }

    @Override
    protected Order afterInsert(Order copy, Integer id)
    {
        Order order;
        order = new Order(id, copy.getCustomer(), copy.getDate(), copy.getTotal(), copy.getoD(), copy.getOrderStatus());
        order.setLoaded(true);
        order.setTimeOfOrder(copy.getTimeOfOrder());

        return order;
    }

    @Override
    protected Order afterUpdate(Order copy)
    {
        copy.setLoaded(true);

        return copy;
    }

    @Override
    protected PreparedStatementSetter updatePreparer(Order domainToUpdate)
    {
        return new PreparedStatementSetter()
        {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException
            {
                ps.setInt(1, domainToUpdate.getCustomer().getId());
                ps.setTimestamp(2, Timestamp.valueOf(domainToUpdate.getDate()));
                ps.setString(3, domainToUpdate.getOrderStatus().toString());
                ps.setBigDecimal(4, domainToUpdate.getTotal());
                ps.setTimestamp(5, Timestamp.valueOf(domainToUpdate.getTimeOfOrder()));
                ps.setInt(6, domainToUpdate.getId());
            }

        };

    }

    @Override
    public List<Order> readOrdersByCustomers(List<Integer> customerIDs)
    {
        List<Order> orders = new ArrayList<>();

        if (customerIDs.size() == 0)
            return orders;
        return this.springTemplate.query(((ORMOrderImpl) this.orm).prepareReadByCustomers(customerIDs.size()),
                (PreparedStatement ps) ->
                {
                    for (int i = 0; i < customerIDs.size(); i++)
                    {
                        ps.setInt(i + 1, customerIDs.get(i));
                    }

                }, this.listExtractor);
    }

    @Override
    public List<Order> readOrdersByProduct(List<Integer> productIDs)
    {
        List<Order> orders = new ArrayList<>();

        if (productIDs.size() == 0)
            return orders;
        return this.springTemplate.query(((ORMOrderImpl) this.orm).prepareReadByProducts(productIDs.size()),
                (PreparedStatement ps) ->
                {
                    for (int i = 0; i < productIDs.size(); i++)
                    {
                        ps.setInt(i + 1, productIDs.get(i));
                    }

                }, this.listExtractor);
    }

    @Override
    public List<Order> readOrderByCoreProduct(List<Integer> coreProductIDs)
    {
        List<Order> orders = new ArrayList<>();

        if (coreProductIDs.size() == 0)
            return orders;
        return this.springTemplate.query(((ORMOrderImpl) this.orm).prepareReadByCoreProducts(coreProductIDs.size()),
                (PreparedStatement ps) ->
                {
                    for (int i = 0; i < coreProductIDs.size(); i++)
                    {
                        ps.setInt(i + 1, coreProductIDs.get(i));
                    }

                }, this.listExtractor);
    }
    
    
    @Override
    public List<Order> readOrdersInTimeFrame(LocalDate start, LocalDate end)
    {
        List<Order> orders = new ArrayList<>();

        if (start == null && end == null)
            return orders;
        return this.springTemplate.query(((ORMOrderImpl) this.orm).prepareReadInTimeFrame(), (PreparedStatement ps) ->
        {
            if (start == null)
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(LocalDate.MIN, LocalTime.of(0, 0))));
            else
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(start, LocalTime.of(0, 0))));
            
            if(end == null)
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(LocalDate.MAX, LocalTime.of(0, 0))));
            else
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(end, LocalTime.of(0, 0))));
        }, this.listExtractor);
    }

    @Override
    public List<Order> readOrdersInThePast(LocalDate end)
    {
        return this.readOrdersInTimeFrame(null, end);
    }

    @Override
    public List<Order> readOrdersInTheFuture(LocalDate start)
    {
        return this.readOrdersInTimeFrame(start, null);
    }

    @Override
    public List<Order> readMostRecentOrders(Integer numberOfOrders)
    {
        List<Order> orders = new ArrayList<>();

        if (numberOfOrders == 0)
            return orders;
        return this.springTemplate.query(((ORMOrderImpl) this.orm).prepareReadMostRecent(), (PreparedStatement ps) ->
        {
            ps.setInt(1, numberOfOrders);
        }, this.listExtractor);
    }

}
