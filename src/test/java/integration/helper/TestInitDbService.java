package integration.helper;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.opinta.entity.TariffGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestInitDbService {
    private final TestHelper testHelper;
    private List<TariffGrid> populatedTariffGrids;

    @Autowired
    public TestInitDbService(TestHelper testHelper) {
        this.testHelper = testHelper;
    }

    @PostConstruct
    public void init() {
        populateDb();
    }
    
    @PreDestroy
    public void destroy() {
        cleanDb();
    }

    private void populateDb() {
        populatedTariffGrids = testHelper.populateTariffGrid();
    }
    
    private void cleanDb() {
        testHelper.deleteTariffGrids(populatedTariffGrids);
    }
}
