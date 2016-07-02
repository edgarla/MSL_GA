/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import msl.ga.util.SendMailTLS;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author edgarloraariza
 */
public class SendEmailNotificationTest {
    
    public SendEmailNotificationTest() {
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
//            SendMailTLS sendMailTLS = new SendMailTLS();
//            sendMailTLS.sendEmail("elora@msl.com.co", "Prueba" , "<h3>Esto es una prueba</h3>");
        } catch (Exception ex) {
            Logger.getLogger(SendEmailNotificationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
}
