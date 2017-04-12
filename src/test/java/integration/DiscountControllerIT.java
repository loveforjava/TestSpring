package integration;

import java.util.List;

import com.opinta.entity.Discount;
import com.opinta.service.DiscountService;
import integration.helper.TestHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class DiscountControllerIT {
    @Autowired
    private DiscountService discountService;
    @Autowired
    private TestHelper testHelper;
    private List<Discount> discounts;
    
    @Before
    public void setUp() {
        discounts = testHelper.createDiscounts();
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void getAllDiscounts() {
        
    }
    
    @Test
    public void getDiscountByUuid() {
        
    }
    
    @Test
    public void createDiscount() {
        
    }
    
    @Test
    public void deleteDiscount() {
        
    }
}
