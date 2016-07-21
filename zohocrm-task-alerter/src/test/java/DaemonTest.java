
import java.util.List;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.scandilabs.apps.zohocrm.service.zoho.ZohoCrmApiService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:zoho-api-spring.xml")
public class DaemonTest {

	private static Logger logger = LoggerFactory
	        .getLogger(DaemonTest.class);

	@Autowired
	private ZohoCrmApiService zohoCrmApiService;

	@Before
	public void setUp() {
	}

	@Test
	public void testListContacts() {
		
		logger.info("Waiting while daemon runs...");
		try {
	        Thread.sleep(40000);
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		logger.info("Stopping.");

	}

}
