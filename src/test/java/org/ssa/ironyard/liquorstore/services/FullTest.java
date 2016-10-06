package org.ssa.ironyard.liquorstore.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ssa.ironyard.liquorstore.crypto.BCryptSecurePassword;
import org.ssa.ironyard.liquorstore.dao.DAOAdmin;
import org.ssa.ironyard.liquorstore.dao.DAOAdminImpl;
import org.ssa.ironyard.liquorstore.dao.DAOCoreProduct;
import org.ssa.ironyard.liquorstore.dao.DAOCoreProductImpl;
import org.ssa.ironyard.liquorstore.dao.DAOCustomer;
import org.ssa.ironyard.liquorstore.dao.DAOCustomerImpl;
import org.ssa.ironyard.liquorstore.dao.DAOOrder;
import org.ssa.ironyard.liquorstore.dao.DAOOrderImpl;
import org.ssa.ironyard.liquorstore.dao.DAOProduct;
import org.ssa.ironyard.liquorstore.dao.DAOProductImpl;
import org.ssa.ironyard.liquorstore.model.Address;
import org.ssa.ironyard.liquorstore.model.Address.State;
import org.ssa.ironyard.liquorstore.model.Address.ZipCode;
import org.ssa.ironyard.liquorstore.model.CoreProduct.Tag;
import org.ssa.ironyard.liquorstore.model.CoreProduct.Type;
import org.ssa.ironyard.liquorstore.model.Order.OrderDetail;
import org.ssa.ironyard.liquorstore.model.Product.BaseUnit;
import org.ssa.ironyard.liquorstore.model.Admin;
import org.ssa.ironyard.liquorstore.model.CoreProduct;
import org.ssa.ironyard.liquorstore.model.Customer;
import org.ssa.ironyard.liquorstore.model.Order;
import org.ssa.ironyard.liquorstore.model.Password;
import org.ssa.ironyard.liquorstore.model.Product;

import com.mysql.cj.jdbc.MysqlDataSource;

public class FullTest
{
    
    AdminServiceImpl adminService;
    CustomerServiceImpl custService;
    AnalyticsServiceImpl anService;
    CoreProductServiceImpl cpService;
    OrdersServiceImpl orderService;
    ProductServiceImpl prodService;
    SalesServiceImpl salesService;
    
    DAOAdmin daoAdmin;
    DAOCoreProduct daoCoreProduct;
    DAOCustomer daoCustomer;
    DAOOrder daoOrder;
    DAOProduct daoProduct;
    
    Customer c;
    Admin ad;
    CoreProduct cp;
    Order ord;
    Product prod;
    Product prod2;
    
    Customer cAdd;
    Admin aAdd;
    CoreProduct cpAdd;
    Order ordAdd;
    Product prodAdd;
    Product prodAdd2;
    
    
    static String URL = "jdbc:mysql://localhost/liquor_store?user=root&password=root&useServerPrpStmts=true";
    static DataSource dataSource;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        MysqlDataSource mysqlDdataSource = new MysqlDataSource();
        mysqlDdataSource.setURL(URL);

        dataSource = mysqlDdataSource;
        
    }
    
    @Before
    public void setup()
    {
      //also used as addTest
        daoAdmin = new DAOAdminImpl(dataSource);
        daoCoreProduct = new DAOCoreProductImpl(dataSource);
        daoCustomer = new DAOCustomerImpl(dataSource);
        daoOrder = new DAOOrderImpl(dataSource);
        daoProduct = new DAOProductImpl(dataSource);

        adminService = new AdminServiceImpl(daoAdmin);
        custService = new CustomerServiceImpl(daoCustomer);
        cpService = new CoreProductServiceImpl(daoCoreProduct);
        orderService = new OrdersServiceImpl(daoOrder);
        prodService = new ProductServiceImpl(daoProduct);
   
        daoAdmin.clear();
        daoCoreProduct.clear();
        daoCustomer.clear();
        daoOrder.clear();
        daoProduct.clear();
        
        String userName = "username";
        Password p = new BCryptSecurePassword().secureHash("password");
        String firstName = "Michael";
        String lastName = "Patrick";
        String street = "111 Road";
        String city = "Pasadena";
        ZipCode zip = new ZipCode("21122");
        State state = State.ALABAMA;
        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setZip(zip);
        LocalDate ld = LocalDate.of(1992,12,24);
        LocalTime lt = LocalTime.of(12, 00);
        LocalDateTime ldt = LocalDateTime.of(ld, lt);
        
        Customer c = new Customer(userName,p,firstName,lastName,address,ldt);
        
        cAdd = custService.addCustomer(c);
        c = new Customer(cAdd.getId(),c.getUserName(),c.getPassword(),c.getFirstName(),c.getLastName(),c.getAddress(),c.getBirthDate());
        assertTrue(cAdd.deeplyEquals(c));
        
        String aUserName = "adminuser";
        Password ap = new BCryptSecurePassword().secureHash("adminpassword");
        String aFirstName = "John";
        String aLastName = "Jacob";
        Integer role = 1;
        Admin a = new Admin(aUserName,ap,aFirstName,aLastName,role);
        
        aAdd = adminService.addAdmin(a);
        a = new Admin(aAdd.getId(),a.getUsername(),a.getPassword(),a.getFirstName(),a.getLastName(),a.getRole());
        assertTrue(aAdd.deeplyEquals(a));
        
        String cpName = "Bud Light";
        Tag tag = new Tag("light beer");
        Tag tag2 = new Tag("beer");
        List<Tag> tagList = new ArrayList();
        tagList.add(tag);
        tagList.add(tag2);
        Type type = Type.BEER;
        String cpSubType = "Light Beer";
        String cpDes = "Tastes Great";
        CoreProduct cp = new CoreProduct(cpName,tagList,type,cpSubType,cpDes);
        
        cpAdd = cpService.addCoreProduct(cp);
        cp = new CoreProduct(cpAdd.getId(),cp.getName(),cp.getTags(),cp.getType(),cp.getSubType(),cp.getDescription());
        assertTrue(cpAdd.deeplyEquals(cp));
        
        BaseUnit bu = BaseUnit._12OZ_CAN;
        Integer q = 12;
        Integer inv = 100;
        BigDecimal price = BigDecimal.valueOf(12.00);
        Product prod = new Product(cpAdd,bu,q,inv,price);
        
        prodAdd = prodService.addProduct(prod);
        prod = new Product(prodAdd.getId(),prod.getCoreProduct(),prod.getBaseUnit(),prod.getQuantity(),prod.getInventory(),prod.getPrice());
        assertTrue(prodAdd.deeplyEquals(prod));
        
        BaseUnit bu2 = BaseUnit._12OZ_BOTTLE;
        Integer q2 = 6;
        Integer inv2 = 50;
        BigDecimal price2 = BigDecimal.valueOf(10.00);
        Product prod2 = new Product(cpAdd,bu2,q2,inv2,price2);
        
        prodAdd2 = prodService.addProduct(prod2);
        prod2 = new Product(prodAdd2.getId(),prod2.getCoreProduct(),prod2.getBaseUnit(),prod2.getQuantity(),prod2.getInventory(),prod2.getPrice());
        assertTrue(prodAdd2.deeplyEquals(prod2));
        
        LocalDate old = LocalDate.of(2016, 11, 15);
        LocalTime olt = LocalTime.of(12, 00);
        LocalDateTime oldt = LocalDateTime.of(old, olt);
        BigDecimal tot = BigDecimal.valueOf(45.00);
        OrderDetail od = new OrderDetail(prodAdd, 1, prodAdd.getPrice());
        OrderDetail od2 = new OrderDetail(prodAdd2,2,prodAdd2.getPrice());
        List<OrderDetail> odList = new ArrayList();
        odList.add(od);
        odList.add(od2);
        Order ord = new Order(c,oldt,tot,odList);
        ordAdd = orderService.addOrder(ord);
        ord = new Order(ordAdd.getId(),ord.getCustomer(),ord.getDate(),ord.getTotal(),ord.getoD());
        
        assertTrue(ordAdd.deeplyEquals(ord));
        
    }


    
    @Test
    public void readTest()
    {
        Customer cRead = custService.readCustomer(cAdd.getId());
        assertTrue(cRead.deeplyEquals(cAdd));
        
        Admin aRead = adminService.readAdmin(aAdd.getId());
        assertTrue(aRead.deeplyEquals(aAdd));
        
        CoreProduct cpRead = cpService.readCoreProduct(cpAdd.getId());
        //assertTrue(cpRead.deeplyEquals(cpAdd));
        
      
        assertEquals(cpRead.getId(), cpAdd.getId());
        assertEquals(cpRead.getName(), cpAdd.getName());
        assertEquals(cpRead.getType(), cpAdd.getType());
        assertEquals(cpRead.getSubType(), cpAdd.getSubType());
        //assertEquals(cpRead.getTags(), cpAdd.getTags()); doesnt grab tags yet
        assertEquals(cpRead.getDescription(), cpAdd.getDescription());
        
        
        Product pRead = prodService.readProduct(prodAdd.getId());
        assertTrue(pRead.deeplyEquals(prodAdd));
        
//        Order oRead = orderService.readOrder(ordAdd.getId());
//        assertTrue(oRead.deeplyEquals(ordAdd));
        
        
        
    }
    
    @Test
    public void readAllTest()
    {
        
        
    }
    
    //@Test
    public void editTest()
    {
       
    }
    
    //@Test
    public void testDelete()
    {
       
    }
    @After
    public void clear()
    {
        
    }

}
