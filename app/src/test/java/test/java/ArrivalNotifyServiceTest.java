package test.java;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.remulasce.lametroapp.ArrivalNotifyService;

import junit.framework.TestCase;

public class ArrivalNotifyServiceTest extends ServiceTestCase<ArrivalNotifyService> {

    public ArrivalNotifyServiceTest() {
        super(ArrivalNotifyService.class);
    }

    public void testSanity() {
        assert(true);
    }

    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), ArrivalNotifyService.class);
        
        startService(startIntent);
    }
}