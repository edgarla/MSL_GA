/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import msl.ga.db.DbInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author edgarloraariza
 */
public class PropertiesTest {
    
    public PropertiesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     public void hello() {
        try {
            DbInfo db = new DbInfo("/Applications/NetBeans/glassfish-4.1/glassfish/domains/domain1/lib/classes/");
        } catch (Exception ex) {
            Logger.getLogger(PropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
}
