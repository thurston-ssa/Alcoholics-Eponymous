package org.ssa.ironyard.liquorstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order extends AbstractDomainObject implements DomainObject
{
    private Customer customer;
    private final LocalDateTime date;
    private final BigDecimal total;
    private List<OrderDetail> oD;
    private OrderStatus status;
    private LocalDateTime timeOfOrder;

    public Order(Integer id, Customer customer, LocalDateTime date, BigDecimal total, List<OrderDetail> oD, OrderStatus status)
    {
        super(id);
        this.customer = customer;
        this.date = date;
        this.total = total;
        this.oD = oD;
        this.status = status;
    }

    public Order(Customer customer, LocalDateTime date, BigDecimal total, List<OrderDetail> oD, OrderStatus status)
    {
        this(null, customer, date, total, oD, status);
    }
    
    public Order(Customer customer, LocalDateTime date, BigDecimal total, OrderStatus status)
    {   
        this(customer, date, total, new ArrayList<>(), status);
    }
    
    public enum OrderStatus
    {
        APPROVED("approved"), PENDING("pending"), REJECTED("rejected");
        
        private String status;
        
        private OrderStatus(String status)
        {
            this.status = status;
        }
        
        public static OrderStatus getInstance(String status)
        {
            for(OrderStatus s : values())
            {
                if(s.status.equals(status))
                    return s;
            }
            
            return null;
        }
        
    }

    public static class OrderDetail
    {
        Product product;
        Integer qty;
        BigDecimal unitPrice;

        public OrderDetail(Product product, Integer qty, BigDecimal unitPrice)
        {
            this.product = product;
            this.qty = qty;
            this.unitPrice = unitPrice;
        }


        public Product getProduct()
        {
            return product;
        }

        public void setProduct(Product product)
        {
            this.product = product;
        }

        public int getQty()
        {
            return qty;
        }

        public void setQty(int qty)
        {
            this.qty = qty;
        }

        public BigDecimal getUnitPrice()
        {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice)
        {
            this.unitPrice = unitPrice;
        }


        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((product == null) ? 0 : product.hashCode());
            result = prime * result + ((qty == null) ? 0 : qty.hashCode());
            result = prime * result + ((unitPrice == null) ? 0 : unitPrice.hashCode());
            return result;
        }


        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            OrderDetail other = (OrderDetail) obj;
            if (product == null)
            {
                if (other.product != null)
                    return false;
            }
            else if (!product.equals(other.product))
                return false;
            if (qty == null)
            {
                if (other.qty != null)
                    return false;
            }
            else if (!qty.equals(other.qty))
                return false;
            if (unitPrice == null)
            {
                if (other.unitPrice != null)
                    return false;
            }
            else if (unitPrice.compareTo(other.unitPrice) != 0)
                return false;
            return true;
        }
        
        
    }
    
    public void addOrderDetail(OrderDetail oD)
    {
        this.oD.add(oD);
    }

    public Customer getCustomer()
    {
        return customer;
    }
    
    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public LocalDateTime getDate()
    {
        return date;
    }

    public BigDecimal getTotal()
    {
        return total;
    }

    public List<OrderDetail> getoD()
    {
        return oD;
    }

    public void setoD(List<OrderDetail> oD)
    {
        this.oD = oD;
    }
    
    public OrderStatus getOrderStatus()
    {
        return status;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.getId();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        if (this.getId() == null)
        {
            if (other.getId() != null)
                return false;
        }
        else if (!this.getId().equals(other.getId()))
            return false;
        return true;
    }

    public boolean deeplyEquals(DomainObject obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        
        if(this.customer == null)
        {
            if(other.customer != null)
                return false;
        }    
        else if (!customer.equals(other.customer))
            return false;
        
        if (date == null)
        {
            if (other.date != null)
                return false;
        }
        else if (!date.equals(other.date))
            return false;
        
        if (this.getId() == null)
        {
            if (other.getId() != null)
                return false;
        }
        else if (!this.getId().equals(other.getId()))
            return false;
        
        if (oD == null)
        {
            if (other.oD != null)
                return false;
        }
        else if (oD.size() != (other.oD.size()))
            return false;
        else if (!oD.containsAll(other.oD))
            return false;
        else if (!other.oD.containsAll(oD))
            return false;
        
        if(this.total == null)
        {
            if(other.total != null)
                return false;
        }
        else if (this.total.compareTo(other.total) != 0)
            return false;
        
        if(this.status == null)
        {
            if(other.status != null)
                return false;
        }
        else if(this.status != other.status)
            return false;
        
        if(this.getTimeOfOrder() == null)
        {
            if(other.getTimeOfOrder() != null)
                return false;
        }
        else if(!this.getTimeOfOrder().equals(other.getTimeOfOrder()))
            return false;
        return true;
    }

    public Order clone()
    {

        Order copy;

        copy = (Order) super.clone();
        copy.setoD(this.getoD());
        copy.setCustomer(this.getCustomer());
        copy.status = this.getOrderStatus();
        return copy;

    }

    @Override
    public String toString()
    {
        return "Order [id=" + this.getId() + ", customerID=" + customer + ", date=" + date + ", total=" + total
                + ", oD=" + oD + "]";
    }

    public LocalDateTime getTimeOfOrder()
    {
        return timeOfOrder;
    }

    public void setTimeOfOrder(LocalDateTime timeOfOrder)
    {
        this.timeOfOrder = timeOfOrder;
    }

}
